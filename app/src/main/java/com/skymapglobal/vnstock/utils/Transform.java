package com.skymapglobal.vnstock.utils;

import android.annotation.SuppressLint;
import java.text.DecimalFormat;

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

  public static String priceWithDecimal (Float price) {
    DecimalFormat formatter = new DecimalFormat("###,###,###.00");
    return formatter.format(price);
  }

  public static String priceWithoutDecimal (Float price) {
    DecimalFormat formatter = new DecimalFormat("###,###,###.##");
    return formatter.format(price);
  }

  public static String priceToString(Float price) {
    String toShow = priceWithoutDecimal(price);
    if (toShow.indexOf(".") > 0) {
      return priceWithDecimal(price);
    } else {
      return priceWithoutDecimal(price);
    }
  }
}
