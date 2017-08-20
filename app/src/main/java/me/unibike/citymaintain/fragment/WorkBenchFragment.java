package me.unibike.citymaintain.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.activity.CaptureActivity;

import me.unibike.citymaintain.BikeMapActivity;
import me.unibike.citymaintain.LoginActivity;
import me.unibike.citymaintain.R;
import me.unibike.citymaintain.TestFeatureActivity;
import me.unibike.citymaintain.UniBikeCityMaintainApp;
import me.unibike.citymaintain.util.ToastHelper;

/**
 * Created by VULCAN on 2017/6/23.
 */

public class WorkBenchFragment extends Fragment {
    public static final int REQUEST_CODE = 1000;
    private TextView workBench_username, workBench_loginStates;
    private LinearLayout workBench_la_saoMa, workBench_la_diTu, workBench_la_test, workBench_la_bianHao;
    private View view;
    private BluetoothAdapter mBluetoothAdapter;
    private String id;

    //构造方法也可以传参,接口回调,广播;这里用的是setArgument()
    //public WorkBenchFragment(){
    //
    // }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //初始化控件
        init();
        //登录信息变化
        if (getArguments().getString("name") != null) {
            workBench_username.setText(getArguments().getString("name"));
        }
        if (UniBikeCityMaintainApp.get(getActivity()).getSPInstance().getString("token", "").length() > 0) {
            workBench_loginStates.setText("注销");
        }

        //扫码查车
        workBench_la_saoMa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        //车辆地图
        workBench_la_diTu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocationEnable(getActivity())) {
                    showDialog(1);
                } else {
                    Intent intent = new Intent(getActivity(), BikeMapActivity.class);
                    startActivity(intent);
                }
            }
        });
        //编号查车
        workBench_la_bianHao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocationEnable(getActivity())) {
                    showDialog(2);
                } else {
                      new CheckBikeFragment().show(getFragmentManager(),"CheckBikeFragment");
                }
            }
        });
        //登录控制
        workBench_loginStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog();
            }
        });
        //临时测试
        workBench_la_test.setOnClickListener(v -> {
             new TestFragment().show(getFragmentManager(),"TestFragment");
        });
        return view;
    }

    public static WorkBenchFragment newInstance(String name) {
        WorkBenchFragment f = new WorkBenchFragment();
        Bundle b = new Bundle();
        b.putString("name", name);
        f.setArguments(b);
        return f;
    }

    public void setMenuVisibility(boolean menuVisible) {
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void init() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_workbench, null);
        workBench_username = (TextView) view.findViewById(R.id.workBench_username);
        workBench_loginStates = (TextView) view.findViewById(R.id.workBench_loginStates);
        workBench_la_saoMa = (LinearLayout) view.findViewById(R.id.workBench_la_saoMa);
        workBench_la_diTu = (LinearLayout) view.findViewById(R.id.workBench_la_diTu);
        workBench_la_test = (LinearLayout) view.findViewById(R.id.workBench_la_test);
        workBench_la_bianHao = (LinearLayout) view.findViewById(R.id.workBench_la_bianHao);
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"你的手机不支持蓝牙");
        } else {
            //获取适配器,并打开蓝牙
            openBlutooth();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String result = bundle.getString("qr_scan_result");
                if (result == null) {
                    ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"未获取到车辆ID");
                    return;
                }
                if (result.startsWith("http://download.unibike.me/")) {
                    id = result.replace("http://download.unibike.me/app.html?city_bike_id=", "").trim();
                    Intent intent = new Intent(getActivity(), TestFeatureActivity.class);
                    intent.putExtra("lock_id", id);
                    startActivity(intent);
                } else {
                    ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"无法识别该二维码");
                }
            }
        } else if (requestCode == 1) {
            if (isLocationEnable(getActivity())) {
                Intent intent = new Intent(getActivity(), BikeMapActivity.class);
                startActivity(intent);
            } else {
                ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"需要打开GPS才能使用");
            }
        } else if (requestCode == 2) {
            if (isLocationEnable(getActivity())) {
                new CheckBikeFragment().show(getFragmentManager(),"CheckBikeFragment");
            } else {
                ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"需要打开GPS才能使用");
            }
        }
    }

    // 车辆地图提示对话框
    public void showDialog(int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("开始GPS功能,功能将更完善!");
        builder.setPositiveButton("开启", (arg0, arg1) -> {
            setLocationService(code);
        });
        builder.setNegativeButton("取消", (arg0, arg1) -> {
            arg0.dismiss();
        });
        builder.show();
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
    private void setLocationService(int code) {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(locationIntent, code);
    }

    //打开蓝牙
    public void openBlutooth() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        /*隐式打开蓝牙*/
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }
    //退出提示
    private void exitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("确定要退出吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences.Editor ed = UniBikeCityMaintainApp.get(getActivity()).getSPInstance().edit();
                ed.remove("token");
                ed.remove("name");
                ed.remove("pwd");
                ed.apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        builder.show();
    }
}
