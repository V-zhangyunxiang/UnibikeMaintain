package me.unibike.unilock.api;

import android.support.annotation.Nullable;

import me.unibike.network.ApiResponse;
import me.unibike.unilock.data.lock.Instruction;
import me.unibike.unilock.data.lock.Lock;
import me.unibike.unilock.data.lock.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public interface UniLockApi {

    @GET("eagle_locks/lock")
    Observable<ApiResponse<Lock>> getLock(@Query("id") int lid, @Query("from") String from);

    @GET("eagle_locks/{lock_id}/getInstruction")
    Observable<ApiResponse<Instruction>> instruction(@Path("lock_id") int lid, @Query("name") String instruction,@Query("token")String token);

    @GET("eagle_locks/{lock_id}/response")
    Observable<ApiResponse<Response>> response(@Path("lock_id") int lid,
                                               @Query("response") String response,
                                               @Query("lnglat") @Nullable String lnglat);
}
