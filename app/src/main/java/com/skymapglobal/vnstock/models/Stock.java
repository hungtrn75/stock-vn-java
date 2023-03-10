package com.skymapglobal.vnstock.models;

import com.google.gson.annotations.SerializedName;

public class Stock {
  public String a;
  public Double b;
  public Double k;
  public Double l;

  @Override
  public String toString() {
    return "Stock{" +
        "a='" + a + '\'' +
        ", b=" + b +
        ", k=" + k +
        ", l=" + l +
        '}';
  }
}
