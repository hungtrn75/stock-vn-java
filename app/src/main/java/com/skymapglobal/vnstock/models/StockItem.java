package com.skymapglobal.vnstock.models;

import java.io.Serializable;

public class StockItem implements Serializable {
  private String code;
  private String name;

  private String floor;

  private String indexCode = null;

  public StockItem(String code, String name, String floor, String indexCode) {
    this.code = code;
    this.name = name;
    this.floor = floor;
    this.indexCode= indexCode;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFloor() {
    return floor;
  }

  public void setFloor(String floor) {
    this.floor = floor;
  }

  public String getIndexCode() {
    return indexCode;
  }

  public void setIndexCode(String indexCode) {
    this.indexCode = indexCode;
  }
}
