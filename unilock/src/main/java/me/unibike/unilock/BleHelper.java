package me.unibike.unilock;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;
import com.polidea.rxandroidble.internal.util.RxBleAdapterWrapper;
import com.polidea.rxandroidble.utils.ConnectionSharingAdapter;

import java.util.UUID;

import me.unibike.config.UniLockConfig;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static com.polidea.rxandroidble.internal.RxBleLog.DEBUG;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class BleHelper {

  private static volatile BleHelper INSTANCE;

  private static boolean RELEASE_FLAG;

  /**
   * 请求权限用code
   */
  public static final int REQUEST_CODE_PERMISSION = 0x10;

  /**
   * 请求打开蓝牙用code
   */
  public static final int REQUEST_CODE_OPEN = 0x11;

  private static  UUID READ_DATA_UUID = UniLockConfig.READ_DATA_UUID;

  public static  UUID WRITE_DATA_UUID = UniLockConfig.WRITE_DATA_UUID;

  private RxBleAdapterWrapper mRxBleAdapterWrapper;

  private RxBleClient mRxBleClient;

  private PublishSubject<Void> mDisconnectTriggerSubject;

  private Observable<RxBleConnection> mConnectionObservable;

  private Subscription mConnectionStateSubscription;

  private Subscription mNotifySubscription;

  private Subscription mScanSubscription;

  private ConnectionListener mConnectionListener;

  public interface ScanListener {

    void onScanSuccess(RxBleScanResult bleScanResult);

    void onScanFailure(Throwable throwable);

    boolean scanFilter(RxBleScanResult bleScanResult);
  }

  public interface ConnectionListener {

    void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState);

    void onConnectionSuccess(RxBleConnection rxBleConnection);

    void onConnectionFailure(Throwable throwable);

    void onDisconnected();
  }

  public interface ReadWriteCallback {

    void onReadSuccess(byte[] bytes);

    void onReadFailure(Throwable throwable);

    void onWriteSuccess();

    void onWriteFailure(Throwable throwable);
  }

  public interface NotifyCallback {

    void onNotificationReceived(byte[] bytes);

    void onNotificationSetupFailure(Throwable throwable);

    void notificationHasBeenSetUp();
  }

  public interface EnableCallback {

    void userRefuseEnableBluetooth();

    void blueToothEnabled();
  }

  public static BleHelper getInstance(Context context) {
    BleHelper instance = INSTANCE;
    if (instance == null || RELEASE_FLAG) {
      synchronized (BleHelper.class) {
        instance = INSTANCE;
        if (instance == null || RELEASE_FLAG) {
          instance = new BleHelper(context);
          INSTANCE = instance;
          RELEASE_FLAG = false;
        }
      }
    }
    return instance;
  }

  private BleHelper(Context context) {
    clearSubscriptions();
    mRxBleClient = RxBleClient.create(context);
    RxBleClient.setLogLevel(DEBUG);
    mRxBleAdapterWrapper = new RxBleAdapterWrapper(BluetoothAdapter.getDefaultAdapter());
    mDisconnectTriggerSubject = PublishSubject.create();
  }

  public RxBleDevice getBleDevice(String macAddr) {
    return mRxBleClient.getBleDevice(macAddr);
  }

  public void setupConnectionObservable(Context context, RxBleDevice bleDevice, ConnectionListener listener) {
    synchronized (BleHelper.class) {
      mConnectionListener = listener;
      mConnectionObservable = bleDevice
          .establishConnection(context, false)
          .takeUntil(mDisconnectTriggerSubject)
          .unsubscribeOn(AndroidSchedulers.mainThread())
          .doOnUnsubscribe(() -> {
            clearSubscriptions();
            mConnectionListener.onDisconnected();
            mConnectionListener = null;
          })
          .unsubscribeOn(AndroidSchedulers.mainThread())
          .compose(new ConnectionSharingAdapter());
    }
    connect(bleDevice, listener);
  }

  public void enableBluetooth(Activity activity) {
    if (!isBluetoothAvailable()) return;
    if (isBluetoothEnabled()) return;
    try { //尝试直接打开蓝牙
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(intent, REQUEST_CODE_OPEN);
    } catch (Exception e) {
      try { //尝试打开蓝牙设置界面
        activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }

  public boolean isBluetoothAvailable() {
    return mRxBleAdapterWrapper.hasBluetoothAdapter();
  }

  public boolean isBluetoothEnabled() {
    return mRxBleAdapterWrapper.isBluetoothEnabled();
  }

  public void onEnableResult(int requestCode, int resultCode, Intent data, EnableCallback callback) {
    if (requestCode == REQUEST_CODE_OPEN) {
      if (resultCode == Activity.RESULT_OK) { //打开了
        callback.blueToothEnabled();
      } else if (resultCode == Activity.RESULT_CANCELED) { //点了弹窗的拒绝
        callback.userRefuseEnableBluetooth();
      }
    }
  }

  public void connect(RxBleDevice bleDevice, ConnectionListener listener) {
    if (!isConnected(bleDevice)) {
      mConnectionObservable
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(listener::onConnectionSuccess, listener::onConnectionFailure);
    }
  }

  public static boolean isConnected(RxBleDevice bleDevice) {
    return bleDevice != null && bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
  }

  public static boolean isConnecting(RxBleDevice bleDevice) {
    return bleDevice != null && bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTING;
  }

  public void scan(ScanListener listener) {
    if (isScanning()) {
      mScanSubscription.unsubscribe();
    } else {
      mScanSubscription = mRxBleClient.scanBleDevices()
          .filter(listener::scanFilter)
          .observeOn(AndroidSchedulers.mainThread())
          .doOnUnsubscribe(this::clearScanSubscriptions)
          .subscribe(listener::onScanSuccess, listener::onScanFailure);
    }
  }

  public boolean isScanning() {
    return mScanSubscription != null && !mScanSubscription.isUnsubscribed();
  }

  public void stopScanning() {
    if (isScanning()) mScanSubscription.unsubscribe();
  }

  public void observeConnectionStateChanges(RxBleDevice bleDevice, ConnectionListener listener) {
    if (mConnectionStateSubscription != null && !mConnectionStateSubscription.isUnsubscribed()) {
      mConnectionStateSubscription.unsubscribe();
    }
    mConnectionStateSubscription = bleDevice.observeConnectionStateChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(listener::onConnectionStateChange);
  }

  public void triggerDisconnect() {
    mDisconnectTriggerSubject.onNext(null);
  }

  public void read(RxBleDevice bleDevice, ReadWriteCallback callback) {
    if (isConnected(bleDevice)) {
      mConnectionObservable
          .flatMap(rxBleConnection -> rxBleConnection.readCharacteristic(READ_DATA_UUID))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(callback::onReadSuccess, callback::onReadFailure);
    }
  }

  public void write(RxBleDevice bleDevice, byte[] inputBytes, ReadWriteCallback callback) {
    if (isConnected(bleDevice)) {
      mConnectionObservable
          .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(WRITE_DATA_UUID, inputBytes))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              bytes -> callback.onWriteSuccess(),
              callback::onWriteFailure);
    }
  }

  public void setupNotification(RxBleDevice bleDevice, NotifyCallback callback) {
    if (isConnected(bleDevice)) {
      if (mNotifySubscription != null && !mNotifySubscription.isUnsubscribed()) {
        mNotifySubscription.unsubscribe();
      }
      mNotifySubscription = mConnectionObservable
          .flatMap(rxBleConnection -> rxBleConnection.setupNotification(READ_DATA_UUID))
          .observeOn(AndroidSchedulers.mainThread())
          .doOnNext(notificationObservable -> callback.notificationHasBeenSetUp())
          .flatMap(notificationObservable -> notificationObservable)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(callback::onNotificationReceived, callback::onNotificationSetupFailure);
    }
  }

  private void clearSubscriptions() {
    mConnectionObservable = null;
    if (mConnectionStateSubscription != null && !mConnectionStateSubscription.isUnsubscribed()) {
      mConnectionStateSubscription.unsubscribe();
    }
    if (mNotifySubscription != null && !mNotifySubscription.isUnsubscribed()) {
      mNotifySubscription.unsubscribe();
    }
    RELEASE_FLAG = true;
  }

  private void clearScanSubscriptions() {
    if (mScanSubscription != null && !mScanSubscription.isUnsubscribed()) {
      mScanSubscription.unsubscribe();
    }
    mScanSubscription = null;
  }
}
