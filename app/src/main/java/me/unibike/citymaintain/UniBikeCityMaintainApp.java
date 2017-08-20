package me.unibike.citymaintain;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.google.gson.GsonBuilder;

import java.util.Collections;

import me.unibike.citymaintain.di.ModelProviderConfigModule;
import me.unibike.citymaintain.util.BuildUtils;
import me.unibike.modulebase.ModelProviderModule;
import me.unibike.network.UniNetwork;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import timber.log.Timber;

public class UniBikeCityMaintainApp extends Application {
  public static UniBikeCityMaintainApp get(Context context) {
    return (UniBikeCityMaintainApp) context.getApplicationContext();
  }
  private GsonBuilder gsonBuilder = ModelProviderModule.provideGsonBuilder(
      ModelProviderConfigModule.provideGsonConfig(), Collections.emptySet());

  private OkHttpClient httpClient = ModelProviderModule.provideHttpClient(
      ModelProviderConfigModule.provideHttpClientConfig());

  private Retrofit retrofit = ModelProviderModule.provideRetrofit(
      ModelProviderConfigModule.provideRetrofitConfig(), httpClient, gsonBuilder);

  @Override
  public void onCreate() {
    super.onCreate();
    UniNetwork.init(UniBikeCityMaintainApp.get(getApplicationContext()).getSPInstance().getString("token",""));
    if (BuildUtils.isDebug()) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  public GsonBuilder provideGson() {
    return gsonBuilder;
  }

  public OkHttpClient provideHttpClient() {
    return httpClient;
  }

  public Retrofit provideRetrofit() {
    return retrofit;
  }

  private SharedPreferences sp;
  public SharedPreferences getSPInstance(){
    sp=getApplicationContext().getSharedPreferences("unibike_sp",MODE_PRIVATE);
    return sp;
  }
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }
}
