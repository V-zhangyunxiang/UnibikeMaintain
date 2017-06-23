package me.unibike.modulebase.httpclient;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.compat.BuildConfig;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {

  @VisibleForTesting
  static final String HEADER_NAME = "User-Agent";

  private static String sOriginalUserAgentContent;

  private final String clientName;

  public UserAgentInterceptor(String clientName) {
    this.clientName = clientName;
  }

  @Override
  public Response intercept(Interceptor.Chain chain) throws IOException {
    final Request originalRequest = chain.request();
    final Request request = originalRequest.newBuilder()
        .removeHeader(HEADER_NAME)
        .addHeader(HEADER_NAME, generateUserAgent())
        .build();
    return chain.proceed(request);
  }

  @NonNull
  @VisibleForTesting
  String generateUserAgent() {
    final StringBuilder userAgent = new StringBuilder();
    userAgent.append(generateUserAgentPrefix());
    if (sOriginalUserAgentContent == null) {
      synchronized (UserAgentInterceptor.class) {
        if (sOriginalUserAgentContent == null) {
          final String httpAgent = getOriginalUserAgent();
          final Pattern pattern = Pattern.compile("^[^()]*?(\\(.*?\\)).*$");
          final Matcher matcher = pattern.matcher(httpAgent);
          if (matcher.matches()) {
            sOriginalUserAgentContent = " " + matcher.group(1);
          } else {
            sOriginalUserAgentContent = "";
          }
        }
      }
    }
    userAgent.append(sOriginalUserAgentContent);
    return userAgent.toString();
  }

  @VisibleForTesting
  String generateUserAgentPrefix() {
    return clientName + "/" + BuildConfig.VERSION_NAME;
  }

  @VisibleForTesting
  String getOriginalUserAgent() {
    return System.getProperty("http.agent");
  }
}

