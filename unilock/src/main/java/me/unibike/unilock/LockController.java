package me.unibike.unilock;

import android.content.Context;
import android.support.annotation.NonNull;

import com.luolc.lighter.permission.Permissions;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public abstract class LockController {

    String TAG = LockController.class.getSimpleName();

    public static LockController create(@NonNull Context context) {
        return new LockControllerImpl(context, true);
    }

    public static LockController create(@NonNull Context context, boolean startLocation) {
        return new LockControllerImpl(context, startLocation);
    }

    public interface ResponseCallback<T> {

        void onSuccess(T data);
    }

    public interface ConnectionListener {

        void onConnected();

        void onDisconnected();
    }

    public interface OnError {

        void onError(LockException e);
    }

    public abstract void requestMapPermissions(Permissions.OnGranted onGranted, Permissions.OnDenied onDenied);

    public abstract void startMapService();

    public abstract boolean isMapPermissionsGranted();

    public abstract void setConnectTimeout(int time);

    public abstract int getBondedLockId();

    public abstract void setInstructionOnConnected(String instruction);

    public abstract void setConnectionListener(ConnectionListener listener);

    public abstract void setOnErrorCallback(OnError onErrorCallback);

    public abstract void setOnDetectPower(ResponseCallback<Integer> callback);

    public abstract void setOnGetToken(ResponseCallback<Void> callback);

    public abstract void setOnUnlock(ResponseCallback<Void> callback);

    public abstract void setOnAutoLockStatus(ResponseCallback<Boolean> callback);

    public abstract void setOnGetLockStatus(ResponseCallback<Boolean> callback);

    public abstract void setOnTerminate(ResponseCallback<Boolean> callback);

    public abstract void connect(int lid);

    public abstract void disconnect();

    public abstract boolean isConnected();

    public abstract boolean isConnecting();

    public abstract void sendUnlock();

    public abstract void sendLed();

    public abstract void sendBuzzer();

    public abstract void sendGetLockStatus();

    public abstract void sendDetectPower();
}
