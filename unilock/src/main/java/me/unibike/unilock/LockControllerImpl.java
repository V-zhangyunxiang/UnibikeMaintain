package me.unibike.unilock;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.luolc.lighter.permission.Permissions;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;

import me.unibike.config.UniLockConfig;
import me.unibike.lbs.location.LocationException;
import me.unibike.lbs.location.LocationFromMockException;
import me.unibike.lbs.location.LocationHelper;
import me.unibike.network.ApiException;
import me.unibike.unilock.data.lock.Instruction;
import me.unibike.unilock.data.lock.Lock;
import me.unibike.unilock.data.lock.LockDataSource;
import me.unibike.unilock.data.lock.LockRepository;
import me.unibike.unilock.util.HexString;
import me.unibike.unilock.util.LogUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

class LockControllerImpl extends LockController implements BleHelper.ConnectionListener, BleHelper.NotifyCallback,
        BleHelper.ReadWriteCallback {

    private static final int COMMAND_BYTE_LENGTH = UniLockConfig.COMMAND_BYTE_LENGTH;

    private static final int CONNECT_TIMEOUT_DEFAULT = 8;

    public static final int INSTRUCTION_NONE = -1;
    public static final int INSTRUCTION_DETECT_POWER = 202;
    public static final int INSTRUCTION_GET_TOKEN = 602;
    public static final int INSTRUCTION_UNLOCK = 502;
    public static final int INSTRUCTION_LOCK_STATUS = 508;
    public static final int INSTRUCTION_GET_LOCK_STATUS = 515;

    public static final int PARSE_DETECT_POWER = 201;
    public static final int PARSE_GET_TOKEN = 601;
    public static final int PARSE_UNLOCK = 501;
    public static final int PARSE_GET_LOCK_STATUS = 514;

    public static final int EXECUTE_SUCCESS = 0;
    public static final int EXECUTE_FAIL = 1;
    public static final int PARSE_SUCCESS = 10;
    public static final int PARSE_FAIL = 11;

    private static String token;

    private Context mContext;

    private BleHelper mBleHelper;

    private LocationHelper mLocationHelper;

    private LockDataSource mLockRepository;

    private CompositeSubscription mSubscriptions;

    private RxBleDevice mBleDevice;

    private int mLockId;

    private String mInstruction;

    private boolean mTokenExist;

    private boolean mLocationEnable;

    private int mConnectTimeout;

    private ConnectionListener mConnectionListener;

    private OnError mOnError;

    private ResponseCallback<Integer> mOnDetectPower;

    private ResponseCallback<Void> mOnGetToken;

    private ResponseCallback<Void> mOnUnlock;

    private ResponseCallback<Boolean> mOnAutoLockStatus;

    private ResponseCallback<Boolean> mOnGetLockStatus;

    private ResponseCallback<Boolean> mOnTerminate;

    public LockControllerImpl(@NonNull Context context, boolean startLocation) {
        mContext = Preconditions.checkNotNull(context);
        mBleHelper = BleHelper.getInstance(context);
        mLocationHelper = LocationHelper.getInstance(context);
        mLockRepository = LockRepository.getInstance(context);
        mSubscriptions = new CompositeSubscription();
        mInstruction = Instruction.NONE;
        mConnectTimeout = CONNECT_TIMEOUT_DEFAULT;
        mConnectionListener = new ConnectionListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected() {
            }
        };
        mTokenExist = true;
        mLocationEnable = startLocation;
        mOnError = Throwable::printStackTrace;
        mOnDetectPower = power -> {
        };
        mOnGetToken = nothing -> {
        };
        mOnUnlock = nothing -> {
        };
        mOnAutoLockStatus = locked -> {
        };
        mOnGetLockStatus = locked -> {
        };
        mOnTerminate = allow -> {
        };
    }

    @Override
    public void requestMapPermissions(Permissions.OnGranted onGranted, Permissions.OnDenied onDenied) {
        if (!mLocationEnable)
            return;
        if (mContext instanceof Activity)
            mLocationHelper.requestPermissions(mContext, onGranted, onDenied);
        else
            throw new IllegalArgumentException("mContext should be an instance of Activity");
    }

    @Override
    public void startMapService() {
        mLocationHelper.start();
    }

    @Override
    public boolean isMapPermissionsGranted() {
        return LocationHelper.isPermissionsGranted(mContext);
    }

    @Override
    public void setConnectTimeout(int time) {
        mConnectTimeout = time;
    }

    @Override
    public int getBondedLockId() {
        return mLockId;
    }

    @Override
    public void setInstructionOnConnected(String ins) {
        mInstruction = ins;
    }

    @Override
    public void setConnectionListener(ConnectionListener listener) {
        mConnectionListener = listener;
    }

    @Override
    public void setOnErrorCallback(OnError onErrorCallback) {
        mOnError = onErrorCallback;
    }

    @Override
    public void setOnDetectPower(ResponseCallback<Integer> callback) {
        mOnDetectPower = callback;
    }

    @Override
    public void setOnGetToken(ResponseCallback<Void> onGetToken) {
        mOnGetToken = onGetToken;
    }

    @Override
    public void setOnUnlock(ResponseCallback<Void> onUnlock) {
        mOnUnlock = onUnlock;
    }

    @Override
    public void setOnAutoLockStatus(ResponseCallback<Boolean> onAutoLockStatus) {
        mOnAutoLockStatus = onAutoLockStatus;
    }

    @Override
    public void setOnGetLockStatus(ResponseCallback<Boolean> onGetLockStatus) {
        mOnGetLockStatus = onGetLockStatus;
    }

    @Override
    public void setOnTerminate(ResponseCallback<Boolean> onTerminate) {
        mOnTerminate = onTerminate;
    }

    @Override
    public void connect(int lid) {
        LogUtils.d(TAG, "start connect");
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (!isConnected() && !isConnecting()) {
            mLockId = lid;
            Subscription subscription = mLockRepository.getLock(lid)
                    .subscribeOn(Schedulers.io())
                    .map(Lock::getMacAddr)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::startScanning,
                            this::onNetworkFailure);
            mSubscriptions.add(subscription);
        }
    }

    private void startScanning(final String macAddress) {
        mBleHelper.scan(new BleHelper.ScanListener() {
            @Override
            public void onScanSuccess(RxBleScanResult bleScanResult) {
                mBleHelper.stopScanning();
                setupBleDeviceConnect(bleScanResult.getBleDevice());
            }

            @Override
            public void onScanFailure(Throwable throwable) {
                mOnError.onError(new LockException(throwable, LockException.CODE_SCAN));
            }

            @Override
            public boolean scanFilter(RxBleScanResult bleScanResult) {
                return bleScanResult.getBleDevice().getMacAddress().equals(macAddress);
            }
        });
    }

    private void setupBleDeviceConnect(RxBleDevice bleDevice) {
        mBleDevice = bleDevice;
        mBleHelper.observeConnectionStateChanges(mBleDevice, this);
        mBleHelper.setupConnectionObservable(mContext, mBleDevice, this);
        new Handler().postDelayed(() -> {
            if (isConnecting()) {
                mBleHelper.triggerDisconnect();
                LogUtils.d(TAG, "Timeout disconnect.");
            }
        }, mConnectTimeout * 1000);
    }

    @Override
    public void disconnect() {
        mConnectionListener = new ConnectionListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected() {
            }
        };
        mBleHelper.triggerDisconnect();
    }

    @Override
    public boolean isConnected() {
        return BleHelper.isConnected(mBleDevice);
    }

    @Override
    public boolean isConnecting() {
        return BleHelper.isConnecting(mBleDevice);
    }

    private void sendGetToken() {
        LogUtils.d(TAG, "start sending getToken instruction");
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (isConnecting())
            return;
        LogUtils.tableLog(TAG, "Start getting Instruction",
                "Lock ID", mLockId,
                "Instruction", Instruction.GET_TOKEN);
        Subscription subscription = mLockRepository.getInstruction(mLockId, Instruction.GET_TOKEN, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        instruction -> {
                            write(instruction.cmd());
//                            BleHelper.WRITE_DATA_UUID=UniLockConfig.WRITE_DATA_UUID_OTA;
//                            sendByteCallBack(new byteCallBack() {
//                                @Override
//                                public void GetByte(byte[] b) {
//                                    write(b);
//                                }
//                            });
                        },
                        this::onNetworkFailure);
        mSubscriptions.add(subscription);
    }

//    public interface byteCallBack {
//        void GetByte(byte b[]);
//    }
//
//    public byteCallBack byteCallBack;
//
//    public void sendByteCallBack(byteCallBack byteCallBack) {
//        this.byteCallBack = byteCallBack;
//        upgradeOTA();
//    }

    @Override
    public void sendUnlock() {
        LogUtils.d(TAG, "start sending unlock instruction");
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (isConnecting())
            return;
        if (!isConnected()) {
            setInstructionOnConnected(Instruction.UNLOCK);
            connect(getBondedLockId());
            return;
        }
        LogUtils.tableLog(TAG, "Start getting Instruction",
                "Lock ID", mLockId,
                "Instruction", Instruction.UNLOCK);
        Subscription subscription = mLockRepository.getInstruction(mLockId, Instruction.UNLOCK, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        instruction -> write(instruction.cmd()),
                        this::onNetworkFailure);
        mSubscriptions.add(subscription);
    }

    @Override
    public void sendLed(){
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (isConnecting())
            return;
        if (!isConnected()) {
            setInstructionOnConnected(Instruction.LED);
            connect(getBondedLockId());
            return;
        }
        LogUtils.tableLog(TAG, "Start getting Instruction",
                "Lock ID", mLockId,
                "Instruction", Instruction.LED);
        Subscription subscription = mLockRepository.getInstruction(mLockId, Instruction.LED, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        instruction -> write(instruction.cmd()),
                        this::onNetworkFailure);
        mSubscriptions.add(subscription);
    }

    @Override
    public void sendBuzzer(){
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (isConnecting())
            return;
        if (!isConnected()) {
            setInstructionOnConnected(Instruction.BUZZER);
            connect(getBondedLockId());
            return;
        }
        LogUtils.tableLog(TAG, "Start getting Instruction",
                "Lock ID", mLockId,
                "Instruction", Instruction.BUZZER);
        Subscription subscription = mLockRepository.getInstruction(mLockId, Instruction.BUZZER, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        instruction -> write(instruction.cmd()),
                        this::onNetworkFailure);
        mSubscriptions.add(subscription);
    }

    @Override
    public void sendGetLockStatus() {
        LogUtils.d(TAG, "start sending getLockStatus instruction");
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (isConnecting())
            return;
        if (!isConnected()) {
            setInstructionOnConnected(Instruction.GET_LOCK_STATUS);
            connect(getBondedLockId());
            return;
        }
        LogUtils.tableLog(TAG, "Start getting Instruction",
                "Lock ID", mLockId,
                "Instruction", Instruction.GET_LOCK_STATUS);
        Subscription subscription = mLockRepository.getInstruction(mLockId, Instruction.GET_LOCK_STATUS, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        instruction -> write(instruction.cmd()),
                        this::onNetworkFailure);
        mSubscriptions.add(subscription);
    }

    @Override
    public void sendDetectPower() {
        LogUtils.d(TAG, "start sending detectPower instruction");
        if (mLocationEnable && !LocationHelper.isPermissionsGranted(mContext))
            return;
        if (isConnecting())
            return;
        if (!isConnected()) {
            setInstructionOnConnected(Instruction.DETECT_POWER);
            connect(getBondedLockId());
            return;
        }
        LogUtils.tableLog(TAG, "Start getting Instruction",
                "Lock ID", mLockId,
                "Instruction", Instruction.DETECT_POWER);
        Subscription subscription = mLockRepository.getInstruction(mLockId, Instruction.DETECT_POWER, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        instruction -> write(instruction.cmd()),
                        this::onNetworkFailure);
        mSubscriptions.add(subscription);
    }

    @Override
    public void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {
        LogUtils.tableLog(TAG, "onConnectionStateChange", "New State", newState.toString());
        if (newState == RxBleConnection.RxBleConnectionState.CONNECTING) {
            mTokenExist = false;
        }
        if (newState == RxBleConnection.RxBleConnectionState.CONNECTED) {
            setupNotification();
            mConnectionListener.onConnected();
        }
    }

    @Override
    public void onConnectionSuccess(RxBleConnection rxBleConnection) {
        Log.d(TAG, "Hey, connection has been established!");
    }

    @Override
    public void onConnectionFailure(Throwable throwable) {
        LogUtils.e(TAG, "Connection error: " + throwable);
        mOnError.onError(new LockException(throwable, LockException.CODE_CONNECTION));
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Bluetooth disconnected!");
        mSubscriptions.clear();
        mTokenExist = false;
        mConnectionListener.onDisconnected();
    }

    @Override
    public void onReadSuccess(byte[] bytes) {
        String res = HexString.bytesToHex(bytes, COMMAND_BYTE_LENGTH);
        LogUtils.tableLog(TAG, "onReadSuccess", "Read success", res);
    }

    @Override
    public void onReadFailure(Throwable throwable) {
        LogUtils.e(TAG, "Read error: " + throwable);
        mOnError.onError(new LockException(throwable, LockException.CODE_READ));
    }

    @Override
    public void onWriteSuccess() {
        LogUtils.d(TAG, "Write success");
    }

    @Override
    public void onWriteFailure(Throwable throwable) {
        LogUtils.e(TAG, "Write error: " + throwable);
        mOnError.onError(new LockException(throwable, LockException.CODE_WRITE));
    }

    @Override
    public void onNotificationReceived(byte[] bytes) {
//        for (byte b : bytes) {
//            Log.i("zyx", "" + b);
//        }
        String res = HexString.bytesToHex(bytes, COMMAND_BYTE_LENGTH);
        LogUtils.tableLog("onNotificationReceived", "onNotificationReceived", "Notify success", res);
        String lnglat = null;
        try {
            if (mLocationEnable) {
                mLocationHelper.start();
                lnglat = mLocationHelper.getFormatLocation();
            }
        } catch (LocationFromMockException e) {
            if (mLocationEnable)
                Toast.makeText(mContext, e.hint, Toast.LENGTH_SHORT).show();
        } catch (LocationException e) {
            e.printStackTrace();
        }
        Subscription subscription = mLockRepository.parseResponse(mLockId, res, lnglat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.getName().equals("getToken")) {
                        token = response.getToken();
                    }else{
                         //其他指令
                    }
                    if(token!=null){
                        mTokenExist=true;
                        //开锁关锁都会上报app，也就是说开锁会接收通知
                    }
                    //int instructionNumber = response.getInsNum();
                    //int instructionStatus = response.getInsStatus();
                    //LogUtils.tableLog("onNotificationReceived", "BLE Response",
                    //"ins_num", instructionNumber,
                    //"ins_status", instructionStatus);
                    //switch (instructionStatus) {
                    //    case PARSE_FAIL:
                    //     case EXECUTE_FAIL:
                    //      if (instructionNumber == INSTRUCTION_GET_LOCK_STATUS && instructionStatus == EXECUTE_FAIL) {
                    //          mOnGetLockStatus.onSuccess(true);
                    //          mOnTerminate.onSuccess(response.isCanReturn());
                    //        } else if (instructionNumber == INSTRUCTION_LOCK_STATUS && instructionStatus == EXECUTE_FAIL) {
                    //           mOnAutoLockStatus.onSuccess(true);
                    //        } else {
                    //                throw new LockException("解析蓝牙指令失败");
                    //        }
                    //           break;
                    //       case EXECUTE_SUCCESS:
                    //          switch (instructionNumber) {
                    //           case INSTRUCTION_GET_TOKEN:
                    //             mTokenExist = true;
                    //             mOnGetToken.onSuccess(null);
                    //             switch (mInstruction) {
                    //                  case Instruction.DETECT_POWER:
                    //                    sendDetectPower();
                    //                    break;
                    //                  case Instruction.UNLOCK:
                    //                     sendUnlock();
                    //                     break;
                    //                  case Instruction.GET_LOCK_STATUS:
                    //                     sendGetLockStatus();
                    //                     break;
                    //                 }
                    //                  break;
                    //                case INSTRUCTION_DETECT_POWER:
                    //                  mOnDetectPower.onSuccess(response.getBattery());
                    //                  break;
                    //                case INSTRUCTION_LOCK_STATUS:
                    //                  mOnAutoLockStatus.onSuccess(false);
                    //                  break;
                    //                case INSTRUCTION_GET_LOCK_STATUS:
                    //                  mOnGetLockStatus.onSuccess(false);
                    //                  break;
                    //                case INSTRUCTION_UNLOCK:
                    //                  mOnUnlock.onSuccess(null);
                    //                  break;
                    //              }
                    //              break;
                    //          }
                }, throwable -> {
                    LogUtils.e(TAG, "Parse error: " + throwable);
                    if (throwable instanceof ApiException)
                        mOnError.onError(new LockException(throwable, LockException.CODE_NET));
                    else
                        mOnError.onError(new LockException(throwable, LockException.CODE_NOTIFY_HANDLE));
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void onNotificationSetupFailure(Throwable throwable) {
        LogUtils.e(TAG, "Notifications error: " + throwable);
        mOnError.onError(new LockException(throwable, LockException.CODE_NOTIFY_SETUP));
    }

    @Override
    public void notificationHasBeenSetUp() {
        LogUtils.d(TAG, "Notifications has been set up");
        if (!mTokenExist)
            sendGetToken();
        else {
            switch (mInstruction) {
                case Instruction.DETECT_POWER:
                    sendDetectPower();
                    break;
                case Instruction.UNLOCK:
                    sendUnlock();
                    break;
                case Instruction.GET_LOCK_STATUS:
                    sendGetLockStatus();
                    break;
                case Instruction.LED:
                    sendLed();
                    break;
                case Instruction.BUZZER:
                    sendBuzzer();
                    break;
            }
        }
    }

    private void read() {
        mBleHelper.read(mBleDevice, this);
    }

    private void write(byte[] inputBytes) {
        mBleHelper.write(mBleDevice, inputBytes, this);
    }

    private void setupNotification() {
        mBleHelper.setupNotification(mBleDevice, this);
    }

    private void onNetworkFailure(Throwable throwable) {
        LogUtils.e(TAG, "Net error: " + throwable);
        mOnError.onError(new LockException(throwable, LockException.CODE_NET));
    }

//    public void upgradeOTA() {
//        InputStream InputStream = null;
//        ByteArrayOutputStream byteArrayOutputStream = null;
//        BufferedInputStream bis = null;
//        byte[] data = new byte[16];
//        try {
//            InputStream = mContext.getResources().openRawResource(R.raw.evaluation);
//            bis = new BufferedInputStream(InputStream);
//            byteArrayOutputStream = new ByteArrayOutputStream();
//            int i;
//            //转化为字节数组流
//            while ((i = bis.read(data)) != -1) {
//                byteArrayOutputStream.write(data, 0, i);
//                data = byteArrayOutputStream.toByteArray();
//                byteCallBack.GetByte(data);
//            }
//            //把文件存在一个字节数组中
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (InputStream != null)
//                    InputStream.close();
//                if (byteArrayOutputStream != null)
//                    byteArrayOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
