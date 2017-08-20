package me.unibike.citymaintain;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.unibike.citymaintain.model.DetailModel;
import me.unibike.citymaintain.model.NearBikeModel;
import me.unibike.citymaintain.tool.DetailService;
import me.unibike.citymaintain.tool.NearBikeService;

/**
 * Created by VULCAN on 2017/7/10.
 */

public class BikeMapActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {
    MapView mMapView = null;
    AMap aMap;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    OnLocationChangedListener mListener;
    MyLocationStyle myLocationStyle;
    private UiSettings mUiSettings;
    private double Lati, Longi;
    private int id;
    private String bike_status, latest_lnglat, bike_la, bike_lng,mark_id;
    private TextView map_bike_number, map_bike_status, map_see_details;
    private RelativeLayout map_re_msg;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bike_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        init();
        jumpDetail(this);
    }

    //初始化参数
    private void init() {
        if (aMap == null) {
            //初始化地图控制器对象
            aMap = mMapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //设置连续定位模式下的定位间隔
        myLocationStyle.interval(2000);
        //设置是否显示定位小蓝点
        myLocationStyle.showMyLocation(true);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        //设置定位监听,必须实现LocationSource接口
        aMap.setLocationSource(this);
        //置为true表示启动显示定位-蓝点
        aMap.setMyLocationEnabled(true);

        //绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //指南针
        mUiSettings.setCompassEnabled(true);
        //显示默认的定位-按钮
        mUiSettings.setMyLocationButtonEnabled(true);
        //比例尺控件
        mUiSettings.setScaleControlsEnabled(true);

        map_bike_number = (TextView) findViewById(R.id.map_bike_number);
        map_bike_status = (TextView) findViewById(R.id.map_bike_status);
        map_see_details = (TextView) findViewById(R.id.map_see_details);
        map_re_msg = (RelativeLayout) findViewById(R.id.map_re_msg);
        img_back = (ImageView) findViewById(R.id.img_back);
        //返回
        img_back.setOnClickListener(v -> finish());


    }
    public static void jumpDetail(BikeMapActivity activity){
        WeakReference<BikeMapActivity> weakReference;
        weakReference = new WeakReference<>(activity);
        new Thread(() -> {
            BikeMapActivity activity1 = weakReference.get();
            activity1.map_see_details.setOnClickListener(v -> {
                DetailService detailService = UniBikeCityMaintainApp.get(activity1).provideRetrofit().create(DetailService.class);
                Observable<DetailModel> observable = detailService.detailMessage(UniBikeCityMaintainApp.get(activity1).getSPInstance().getString("token", ""), Integer.parseInt(activity1.mark_id), String.valueOf(activity1.Longi) + "," + String.valueOf(activity1.Lati));
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Observer<DetailModel>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }

                            @Override
                            public void onNext(@NonNull DetailModel detailModel) {
                                if (detailModel.getCode() == 0) {
                                    Intent intent = new Intent(activity1, BikeDetailActivity.class);
                                    intent.putExtra("detail", detailModel);
                                    activity1.startActivity(intent);
                                }

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                //Toast.makeText(activity1,"请求数据异常",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            });
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
            mLocationClient.setLocationListener(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //关闭缓存机制
            mLocationOption.setLocationCacheEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(5000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();

        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    //定义 Marker 点击事件监听
    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker对象被点击时回调的接口
        // 返回true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            mark_id=marker.getTitle();
            map_bike_number.setText("单车编号 :" + "\t" + mark_id);
            if (marker.getSnippet().equals("normal")) {
                map_bike_status.setText("当前状态 :" + "\t" + "运营中-正常");
            } else if (marker.getSnippet().equals("silent")) {
                map_bike_status.setText("当前状态 :" + "\t" + "运营中-沉默");
            } else if (marker.getSnippet().equals("intervened")) {
                map_bike_status.setText("当前状态 :" + "\t" + "运营中-待干预");
            } else {
                map_bike_status.setText("当前状态 :" + "\t" + "非运营中");
            }
            map_re_msg.setVisibility(View.VISIBLE);
            return true;
        }
    };

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
//                aMapLocation.getLocationType();
                  Lati = aMapLocation.getLatitude();//获取纬度
                  Longi = aMapLocation.getLongitude();//获取经度
                  // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置

//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码

                NearBikeService nearBikeService = UniBikeCityMaintainApp.get(getApplicationContext()).provideRetrofit().create(NearBikeService.class);
                Observable<NearBikeModel> observable = nearBikeService.nearBikeMsg(UniBikeCityMaintainApp.get(getApplicationContext()).getSPInstance().getString("token", ""), String.valueOf(Longi) + "," + String.valueOf(Lati));
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Observer<NearBikeModel>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }

                            @Override
                            public void onNext(@NonNull NearBikeModel nearBikeModel) {
                                if (nearBikeModel.getCode() == 0) {
                                    ArrayList<NearBikeModel.BikeMsg> list = nearBikeModel.getPayload().getCity_bikes();
                                    for (int i = 0; i < list.size(); i++) {
                                        id = list.get(i).getId();
                                        bike_status = list.get(i).getBike_status();
                                        latest_lnglat = list.get(i).getLatest_lnglat();
                                        String[] sourceStrArray = latest_lnglat.split(",");
                                        for (int j = 0; j < sourceStrArray.length; j++) {
                                            bike_la = sourceStrArray[0];
                                            bike_lng = sourceStrArray[1];
                                        }
                                        LatLng latLng = new LatLng(Double.valueOf(bike_lng), Double.valueOf(bike_la));
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(latLng);
                                        markerOptions.title(String.valueOf(id));
                                        markerOptions.snippet(bike_status);
                                        markerOptions.draggable(false);
                                        if (bike_status.equals("normal")) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                    .decodeResource(getResources(), R.drawable.icon_zhengchang)));
                                        } else if (bike_status.equals("silent")) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                    .decodeResource(getResources(), R.drawable.icon_chenmo)));
                                        } else if (bike_status.equals("intervened")) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                    .decodeResource(getResources(), R.drawable.icon_daiganyu)));
                                        } else {
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                                    .decodeResource(getResources(), R.drawable.icon_feiyunying)));
                                        }
                                        markerOptions.setFlat(false);//设置marker平贴地图效果
                                        final Marker marker = aMap.addMarker(markerOptions);
                                    }
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                //Toast.makeText(BikeMapActivity.this,"请求数据异常",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {
                                if (isFirstLoc) {
                                    //设置缩放级别
                                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                                    //将地图移动到定位点
                                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                                    //点击定位按钮 能够将地图的中心移动到定位点
                                    mListener.onLocationChanged(aMapLocation);
                                    isFirstLoc = false;
                                }
                            }
                        });

            } else {
//                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
//                Log.e("AmapErr", errText);
            }
        }
    }
}
