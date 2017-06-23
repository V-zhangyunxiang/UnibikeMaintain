package me.unibike.citymaintain.di;

import android.content.Context;

import me.unibike.citymaintain.BuildConfig;
import me.unibike.citymaintain.util.BuildUtils;
import me.unibike.modulebase.gson.GsonConfig;
import me.unibike.modulebase.gson.ImmutableGsonConfig;
import me.unibike.modulebase.httpclient.HttpClientConfig;
import me.unibike.modulebase.httpclient.ImmutableHttpClientConfig;
import me.unibike.modulebase.retrofit.ImmutableRetrofitConfig;
import me.unibike.modulebase.retrofit.RetrofitConfig;
import me.unibike.modulebase.sharedpreference.ImmutableSharedPreferenceConfig;
import me.unibike.modulebase.sharedpreference.SharedPreferenceConfig;

public class ModelProviderConfigModule {
  private static final boolean DEBUG = BuildUtils.isDebug();

  private static final String TIME_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public static HttpClientConfig provideHttpClientConfig() {
    return ImmutableHttpClientConfig.builder()
        .clientName("unibike")
        .enableCookie(true)
        .enableLog(DEBUG)
        .build();
  }

  public static RetrofitConfig provideRetrofitConfig() {
    return ImmutableRetrofitConfig.builder().baseUrl(BuildConfig.API_BASE_URL).build();
  }

  public static GsonConfig provideGsonConfig() {
    return ImmutableGsonConfig.builder()
        .dateFormatString(TIME_FORMAT_ISO_8601)
        .build();
  }

  public static SharedPreferenceConfig provideSharedPreferenceConfig() {
    return ImmutableSharedPreferenceConfig.builder()
        .name("unibike_sp")
        .mode(Context.MODE_PRIVATE)
        .build();
  }
}
