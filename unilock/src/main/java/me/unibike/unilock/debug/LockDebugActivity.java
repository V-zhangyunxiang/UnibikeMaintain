package me.unibike.unilock.debug;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.luolc.lighter.permission.PermissionCallback;
import com.luolc.lighter.permission.PermissionHandler;

import me.unibike.network.UniNetwork;
import me.unibike.unilock.LockController;
import me.unibike.unilock.R;
import me.unibike.unilock.data.lock.Instruction;
import me.unibike.unilock.util.AppUtils;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class LockDebugActivity extends AppCompatActivity implements PermissionHandler {

    LockController mLockController;

    Button mGetStatus;

    private PermissionCallback mPermissionCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_debug_act);

        AppUtils.debugging = true;

        String token =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxMywidG9rZW5fc2FsdCI6NDh9.UIJe-H9wH8_EFexbVrQWucs11LMhEYk78qmxJO_OONU";
        UniNetwork.init("abc");
        UniNetwork.updateToken(token);

        mGetStatus = (Button) findViewById(R.id.btn_get_status);
        mGetStatus.setOnClickListener(v -> {
            mLockController.sendGetLockStatus();
        });

        mLockController = LockController.create(this);
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "onResume");
        super.onResume();
        if (mLockController.isMapPermissionsGranted()) {
            Log.d("TAG", "all granted");
            mLockController.startMapService();
            mLockController.setInstructionOnConnected(Instruction.UNLOCK);
            mLockController.connect(10320);
        } else {
            Log.d("TAG", "not granted");
            mLockController.requestMapPermissions(() -> {}, () -> {});
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLockController.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "on request result");
        mPermissionCallback.onResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setPermissionCallback(PermissionCallback callback) {
        mPermissionCallback = callback;
    }
}
