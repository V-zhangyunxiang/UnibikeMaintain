package me.unibike.citymaintain;

import com.google.gson.GsonBuilder;

import android.app.Application;
import android.content.Context;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import java.util.Collections;

import me.unibike.citymaintain.di.ModelProviderConfigModule;
import me.unibike.citymaintain.util.BuildUtils;
import me.unibike.modulebase.ModelProviderModule;
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

  private RxSharedPreferences rxSharedPreferences = ModelProviderModule.provideRxSharedPreferences(
      this, ModelProviderConfigModule.provideSharedPreferenceConfig());

  @Override
  public void onCreate() {
    super.onCreate();

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

  public RxSharedPreferences provideRxSharedPreferences() {
    return rxSharedPreferences;
  }
}
