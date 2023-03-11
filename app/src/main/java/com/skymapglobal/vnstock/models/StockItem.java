package com.skymapglobal.vnstock.models;

import java.io.Serializable;

public class StockItem implements Serializable {
  private String code;
  private String name;

  private String floor;

  private String indexCode = null;

  private Double chg;

  private Double ratio;

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getVol() {
    return vol;
  }

  public void setVol(Double vol) {
    this.vol = vol;
  }

  private Double price;
  private  Double vol;


  public StockItem(String code, String name, String floor, String indexCode, Double chg, Double ratio,Double price,Double vol) {
    this.code = code;
    this.name = name;
    this.floor = floor;
    this.indexCode= indexCode;
    this.chg = chg;
    this.ratio = ratio;
    this.price = price;
    this.vol = vol;
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

  public Double getChg() {
    return chg;
  }

  public void setChg(Double chg) {
    this.chg = chg;
  }

  public Double getRatio() {
    return ratio;
  }

  public void setRatio(Double ratio) {
    this.ratio = ratio;
  }
}
