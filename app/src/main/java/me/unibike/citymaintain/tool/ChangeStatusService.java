package me.unibike.citymaintain.tool;

import io.reactivex.Observable;
import me.unibike.citymaintain.model.ChangeStatusModel;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by VULCAN on 2017/7/20.
 */

public interface ChangeStatusService {
    @FormUrlEncoded
    @PUT("city_bikes/{city_bike_id}/mark_status")
    Observable<ChangeStatusModel> changeStatus(@Header("Authorization") String authorization, @Path("city_bike_id")Integer id, @Field("lnglat")String lnglat,@Field("status")String status);
}
