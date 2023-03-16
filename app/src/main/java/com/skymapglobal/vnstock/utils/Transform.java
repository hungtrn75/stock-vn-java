package com.skymapglobal.vnstock.utils;

import android.annotation.SuppressLint;

public class Transform {

  @SuppressLint("DefaultLocale")
  public static String getHumanReadablePriceFromNumber(float number) {

    if (number >= 1000000000) {
      return String.format("%.2fB", number / 1000000000.0);
    }

    if (number >= 1000000) {
      return String.format("%.2fM", number / 1000000.0);
    }

    if (number >= 100000) {
      return String.format("%.2fL", number / 100000.0);
    }

    if (number >= 1000) {
      return String.format("%.2fK", number / 1000.0);
    }
    return String.valueOf(number);

  }
}
