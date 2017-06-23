package me.unibike.modulebase.retrofit;

import org.immutables.value.Value;

@Value.Immutable
public interface RetrofitConfig {
  String baseUrl();
}
