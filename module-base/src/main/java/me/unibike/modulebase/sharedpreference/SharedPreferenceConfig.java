package me.unibike.modulebase.sharedpreference;

import org.immutables.value.Value;

@Value.Immutable
public interface SharedPreferenceConfig {
  String name();

  int mode();
}
