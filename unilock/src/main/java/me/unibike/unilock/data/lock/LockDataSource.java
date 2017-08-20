package me.unibike.unilock.data.lock;

import rx.Observable;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public interface LockDataSource {

    Observable<Lock> getLock(int lid);

    Observable<Instruction> getInstruction(int lid, String key,String token);

    Observable<Response> parseResponse(int lid, String response, String lnglat);
}
