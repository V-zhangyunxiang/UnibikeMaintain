package me.unibike.citymaintain.tool;

import io.reactivex.Observable;
import me.unibike.citymaintain.model.NearBikeModel;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by VULCAN on 2017/7/11.
 */

public interface NearBikeService {

    @GET("city_bikes/nearby_city_bikes")
    Observable<NearBikeModel> nearBikeMsg(@Header("Authorization") String authorization, @Query("lnglat") String lnglat);
}
