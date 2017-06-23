package me.unibike.modulebase.httpclient;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MemoryCookieJar implements CookieJar {

  private final Map<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

  @Override
  public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
    cookieStore.put(url.host(), cookies);
  }

  @Override
  public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
    List<Cookie> cookies = cookieStore.get(url.host());
    if (cookies == null) {
      cookies = Collections.emptyList();
    }
    return cookies;
  }
}
