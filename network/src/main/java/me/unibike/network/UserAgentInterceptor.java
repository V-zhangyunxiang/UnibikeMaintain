package me.unibike.network;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author LuoLiangchen
 * @since 16/11/4
 */

final class UserAgentInterceptor implements Interceptor {
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";
    private final String mUserAgentHeaderValue;

    UserAgentInterceptor() {
        mUserAgentHeaderValue = "";
    }

    public UserAgentInterceptor(String userAgentHeaderValue) {
        mUserAgentHeaderValue = Preconditions.checkNotNull(userAgentHeaderValue);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        String httpAgent = System.getProperty("http.agent");
        String userAgent = "unibike/" + BuildConfig.VERSION_NAME + " ";
        Pattern pattern = Pattern.compile(".*(\\(.*\\))");
        Matcher matcher = pattern.matcher(httpAgent);
        if (matcher.matches()) {
            userAgent += matcher.group(1);
        }
        final Request requestWithUserAgent = originalRequest.newBuilder()
                .removeHeader(USER_AGENT_HEADER_NAME)
                .addHeader(USER_AGENT_HEADER_NAME, userAgent + mUserAgentHeaderValue)
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}
