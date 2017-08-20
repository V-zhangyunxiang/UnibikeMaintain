package me.unibike.citymaintain.tool;

import io.reactivex.Observable;
import me.unibike.citymaintain.model.DetailModel;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by VULCAN on 2017/7/13.
 */

public interface DetailService {
    @GET("city_bikes/{city_bike_id}/city_bike_info")
    Observable<DetailModel> detailMessage(@Header("Authorization") String authorization, @Path("city_bike_id") Integer city_bike_id, @Query("lnglat") String lnglat);

}
