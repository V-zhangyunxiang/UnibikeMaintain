package me.unibike.unilock.data.lock;

import android.content.Context;

import me.unibike.unilock.api.ApiDataTransformFunc;
import me.unibike.unilock.api.LockService;
import rx.Observable;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class LockRepository implements LockDataSource {

    private static volatile LockRepository INSTANCE;

    public static LockRepository getInstance(Context context) {
        LockRepository instance = INSTANCE;
        if (instance == null) {
            synchronized (LockRepository.class) {
                instance = INSTANCE;
                if (instance == null) {
                    instance = new LockRepository(context);
                    INSTANCE = instance;
                }
            }
        }
        return instance;
    }

    private LockRepository(Context context) {

    }

    @Override
    public Observable<Lock> getLock(int lid) {
        return LockService.getService().getLock(lid, "qrcode")
                .map(new ApiDataTransformFunc<>());
    }

    @Override
    public Observable<Instruction> getInstruction(int lid, String key,String token) {
        return LockService.getService().instruction(lid, key,token)
                .map(new ApiDataTransformFunc<>());
    }

    @Override
    public Observable<Response> parseResponse(int lid, String response, String lnglat) {
        return LockService.getService().response(lid, response, lnglat)
                .map(new ApiDataTransformFunc<>());
    }
}
