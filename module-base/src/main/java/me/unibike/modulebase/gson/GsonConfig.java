package me.unibike.modulebase.gson;

import org.immutables.value.Value;

@Value.Immutable
public interface GsonConfig {
  String dateFormatString();
}
