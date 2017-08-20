package me.unibike.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.unibike.config.UniNetworkConfig;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author LuoLiangchen
 * @since 16/11/4
 */

public class UniNetwork {

    private static final String TAG = UniNetwork.class.getSimpleName();

    private static final String BASE_URL = UniNetworkConfig.BASE_URL;

    private static volatile UniNetwork INSTANCE;

    private OkHttpClient mOkHttpClient;

    private Converter.Factory mGsonConverterFactory;

    private CallAdapter.Factory mRxJavaCallAdapterFactory;

    private Retrofit mRetrofit;

    public static synchronized void init(String token) {
        INSTANCE = new UniNetwork(token);
    }

    public static void updateToken(String token) {
        INSTANCE.updateAuthorizationInterceptor(token);
    }

    public static <T> T createApi(final Class<T> service) {
        return INSTANCE.mRetrofit.create(service);
    }

    private UniNetwork(String token) {
        updateAuthorizationInterceptor(token);
        mOkHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final Map<String, List<Cookie>> mCookieStore = new ConcurrentHashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        mCookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = mCookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .addInterceptor(new AuthorizationInterceptor())
                .addInterceptor(new UserAgentInterceptor())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        mGsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create());
        mRxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(mRxJavaCallAdapterFactory)
                .addConverterFactory(mGsonConverterFactory)
                .build();
    }

    private synchronized void updateAuthorizationInterceptor(String token) {
        AuthorizationInterceptor.setAuthorization(token);
    }
}
