package me.unibike.network;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author LuoLiangchen
 * @since 16/11/4
 */

final class AuthorizationInterceptor implements Interceptor {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    @Nullable
    private static String authorization;

    public static void setAuthorization(@Nullable String token) {
        authorization = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        if (authorization == null) return chain.proceed(originalRequest);

        final Request authorizedRequest = originalRequest.newBuilder()
                .removeHeader(AUTHORIZATION_HEADER_NAME)
                .addHeader(AUTHORIZATION_HEADER_NAME, authorization)
                .build();
        return chain.proceed(authorizedRequest);
    }
}
