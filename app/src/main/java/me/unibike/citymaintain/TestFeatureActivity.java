package me.unibike.citymaintain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import me.unibike.citymaintain.util.ToastHelper;
import me.unibike.unilock.LockController;


/**
 * Created by VULCAN on 2017/6/25.
 */

public class TestFeatureActivity extends AppCompatActivity {
    private Button openUnlock_btn, con_ble,bike_ringing,bike_light;
    private String lock_id;
    private LockController mLockController;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    private ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_features);
         init();
        //假如是6.0以上系统，要获取地理位置权限
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isLocationEnable(this)) {
                showDialogZxing();
            }
        }
        //获取锁id
        if (getIntent().getStringExtra("lock_id") != null) {
            lock_id = getIntent().getStringExtra("lock_id");
        }
        //连接蓝牙
        con_ble.setOnClickListener(v -> {
            ProgressDialog pd = ProgressDialog.show(this, "正在连接", "请稍后...", false, false);
            pd.show();
            Handler handler = new Handler();
            if (mLockController != null) {
                if (mLockController.isMapPermissionsGranted()) {
                    mLockController.connect(Integer.parseInt(lock_id));
                } else {
                    mLockController.requestMapPermissions(() -> mLockController.connect(Integer.parseInt(lock_id)), () -> {
                    });
                }
            }
            handler.postDelayed(() -> {
                pd.dismiss();
                if(mLockController.isConnected()) {
                    con_ble.setBackgroundColor(getResources().getColor(R.color.con_ble__color));
                    con_ble.setText("已连接蓝牙");
                }else{
                    con_ble.setBackgroundColor(getResources().getColor(R.color.con_ble__color));
                    con_ble.setText("连接失败，已超时");
                    ToastHelper.showToast(UniBikeCityMaintainApp.get(this),"请检查ID是否存在或距离是否过远");
                }
                con_ble.setEnabled(false);
            }, 5000);
        });
        //开锁
        openUnlock_btn.setOnClickListener(v -> {
            if(mLockController.isConnected()) {
                mLockController.sendUnlock();
            }else{
                ToastHelper.showToast(UniBikeCityMaintainApp.get(this),"蓝牙未连接");
            }
        });
        //响铃
        bike_ringing.setOnClickListener(v -> {
            if(mLockController.isConnected()) {
                mLockController.sendBuzzer();
            }else{
                ToastHelper.showToast(UniBikeCityMaintainApp.get(this),"蓝牙未连接");
            }
        });
        //闪灯
        bike_light.setOnClickListener(v -> {
            if(mLockController.isConnected()) {
                mLockController.sendLed();
            }else{
                ToastHelper.showToast(UniBikeCityMaintainApp.get(this),"蓝牙未连接");
            }
        });
        //返回
        img_back.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLockController.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (!isLocationEnable(this)) {
                ToastHelper.showToast(UniBikeCityMaintainApp.get(this),"未开启定位,无法继续操作!");
                finish();
            }
        }
    }

    //判断是否已开启地理位置权限
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider)
            return true;
        return false;
    }

    //引导用户开启GPS
    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }

    // 扫码查车提示对话框
    public void showDialogZxing() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("Android 6.0以上的手机必须开启定位才能连接蓝牙!");
        builder.setPositiveButton("开启", (arg0, arg1) -> {
            setLocationService();
        });
        builder.setNegativeButton("取消", (arg0, arg1) -> {
            arg0.dismiss();
            finish();
        });
        builder.show();
    }
    public void init(){
        openUnlock_btn = (Button) findViewById(R.id.bike_openLock);
        con_ble = (Button) findViewById(R.id.con_ble);
        bike_ringing = (Button) findViewById(R.id.bike_ringing);
        bike_light = (Button) findViewById(R.id.bike_light);
        img_back= (ImageView) findViewById(R.id.img_back);
        //初始化蓝牙对象
        mLockController = LockController.create(TestFeatureActivity.this);
    }
}
