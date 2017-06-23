package me.unibike.modulebase;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import android.content.Context;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.moczul.ok2curl.CurlInterceptor;

import java.util.Set;

import io.reactivex.schedulers.Schedulers;
import java8.util.stream.StreamSupport;
import me.unibike.modulebase.gson.GsonConfig;
import me.unibike.modulebase.httpclient.HttpClientConfig;
import me.unibike.modulebase.httpclient.MemoryCookieJar;
import me.unibike.modulebase.httpclient.UserAgentInterceptor;
import me.unibike.modulebase.retrofit.RetrofitConfig;
import me.unibike.modulebase.sharedpreference.SharedPreferenceConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ModelProviderModule {
  public static GsonBuilder provideGsonBuilder(GsonConfig config, Set<TypeAdapterFactory> factories) {
    final GsonBuilder builder = new GsonBuilder();
    StreamSupport.stream(factories).forEach(builder::registerTypeAdapterFactory);
    return builder
        .setDateFormat(config.dateFormatString())
        .setPrettyPrinting();
  }

  public static OkHttpClient provideHttpClient(HttpClientConfig config) {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (config.enableLog()) {
      builder
          .addNetworkInterceptor(new StethoInterceptor())
          .addInterceptor(new HttpLoggingInterceptor(message -> Timber.tag("OkHttp").d(message))
              .setLevel(HttpLoggingInterceptor.Level.BODY))
          .addInterceptor(new CurlInterceptor(message -> Timber.tag("Ok2Curl").d(message)));
    }
    builder.addInterceptor(new UserAgentInterceptor(config.clientName()));
    if (config.enableCookie()) {
      builder.cookieJar(new MemoryCookieJar());
    }
    return builder.build();
  }

  public static Retrofit provideRetrofit(RetrofitConfig config, OkHttpClient okHttpClient, GsonBuilder gsonBuilder) {
    return new Retrofit.Builder()
        .baseUrl(config.baseUrl())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build();
  }

  public static RxSharedPreferences provideRxSharedPreferences(Context context, SharedPreferenceConfig config) {
    return RxSharedPreferences.create(context.getSharedPreferences(config.name(), config.mode()));
  }
}
