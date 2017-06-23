package me.unibike.citymaintain.util;

import me.unibike.citymaintain.BuildConfig;

public final class BuildUtils {
  /** Prevent instantiation. */
  private BuildUtils() {
  }

  /**
   * Get whether it is a debug build.
   * @return whether it is a debug build
   */
  public static boolean isDebug() {
    return "debug".equals(BuildConfig.BUILD_TYPE);
  }

  /**
   * Get whether it is a release build.
   * @return whether it is a release build
   */
  public static boolean isRelease() {
    return "release".equals(BuildConfig.BUILD_TYPE);
  }
}

