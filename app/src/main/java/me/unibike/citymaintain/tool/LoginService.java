package me.unibike.citymaintain.tool;


import io.reactivex.Observable;
import me.unibike.citymaintain.model.LoginModel;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



/**
 * Created by VULCAN on 2017/6/24.
 */

public interface LoginService {
    @FormUrlEncoded
    @POST("employee_login")
    Observable<LoginModel> getUserMessage(@Field("account") String account, @Field("password") String password);

}
