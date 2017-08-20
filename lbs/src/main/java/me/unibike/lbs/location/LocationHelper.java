package me.unibike.lbs.location;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.luolc.lighter.permission.Permissions;

import java.util.Locale;

import rx.Emitter;
import rx.Observable;
import rx.subjects.PublishSubject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author LuoLiangchen
 * @since 16/10/18
 */

public class LocationHelper {

    private static volatile LocationHelper INSTANCE;

    private static final String LOCATION_PATTERN = "%f,%f";

    private AMapLocationClient mLocationClient;

    private LocationManager mLocationManager;

    private PublishSubject<Void> mStopTriggerSubject;

    public static LocationHelper getInstance(Context context) {
        LocationHelper instance = INSTANCE;
        if (instance == null) {
            synchronized (LocationHelper.class) {
                instance = INSTANCE;
                if (instance == null) {
                    instance = new LocationHelper(context);
                    INSTANCE = instance;
                }
            }
        }
        return instance;
    }

    private LocationHelper(Context context) {
        checkNotNull(context);
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        mLocationManager = ((LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
        mStopTriggerSubject = PublishSubject.create();
    }

    public <V> void requestPermissions(V framework) {
        requestPermissions(framework, () -> {}, () -> {});
    }

    public <V> void requestPermissions(V framework, Permissions.OnGranted onGranted, Permissions.OnDenied onDenied) {
        Permissions.request(
                framework,
                () -> {
                    start();
                    onGranted.call();
                },
                () -> {
                    onDeniedPermissions(framework);
                    onDenied.call();
                },
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean isPermissionsGranted(Context context) {
        return Permissions.isGrantedCurrently(context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }

    public boolean isGpsAvailable() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void start() {
        mLocationClient.startLocation();
    }

    public boolean isStarted() {
        return mLocationClient.isStarted();
    }

    public void stop() {
        mLocationClient.stopLocation();
        mStopTriggerSubject.onNext(null);
    }

    public String getFormatLocation() throws LocationException {
        final AMapLocation aMapLocation = mLocationClient.getLastKnownLocation();
        if (aMapLocation == null) throw new LocationException(-1);
        if (aMapLocation.isFromMockProvider()) throw new LocationFromMockException();
        if (aMapLocation.getErrorCode() != 0) {
            throw new LocationException(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
        }
        return String.format(Locale.US, LOCATION_PATTERN, aMapLocation.getLongitude(), aMapLocation.getLatitude());
    }

    public AMapLocation getLocation() throws LocationException{
        final AMapLocation aMapLocation = mLocationClient.getLastKnownLocation();
        if (aMapLocation == null) throw new LocationException(-1);
        if (aMapLocation.isFromMockProvider()) throw new LocationFromMockException();
        if (aMapLocation.getErrorCode() != 0) {
            throw new LocationException(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
        }
        return aMapLocation;
    }

    @Deprecated
    public void setOnLocationChangedListener(AMapLocationListener listener) {
        mLocationClient.setLocationListener(listener);
    }

    public Observable<AMapLocation> observeLocationChange() {
        return Observable
                .<AMapLocation>fromEmitter(
                        emitter -> mLocationClient.setLocationListener(emitter::onNext),
                        Emitter.BackpressureMode.BUFFER)
                .takeUntil(mStopTriggerSubject);
    }

    public static boolean isMockSettingsOn(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return false;
        else {
            int lomock = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0);
            return lomock != 0;
        }
    }

    private <V> void onDeniedPermissions(V framework) {
        if (framework instanceof Context) {
            Toast.makeText((Context) framework, "你拒绝了定位相关的权限请求，将有部分功能无法正常使用", Toast.LENGTH_SHORT).show();
        } else {
            throw new IllegalArgumentException("framework should be an instance of Context");
        }
    }
}
