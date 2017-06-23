package me.unibike.modulebase.httpclient;

import org.immutables.value.Value;

@Value.Immutable
public interface HttpClientConfig {
  boolean enableLog();

  boolean enableCookie();

  String clientName();
}
