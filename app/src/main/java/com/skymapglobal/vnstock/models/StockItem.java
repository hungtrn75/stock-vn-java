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

  private Double tc;
  private Double tran;
  private Double san;
  private Double kl;


//
  private Double a1;
  private Double a2;
  private Double a3;
  private Double b1;
  private Double b2;
  private Double b3;
  private Double c1;
  private Double c2;
  private Double c3;
  private Double d1;
  private Double d2;
  private Double d3;

  private Double cao;
  private Double thap;

  public StockItem(String code, String name, String floor, String indexCode, Double chg, Double ratio, Double price, Double vol, Double tc, Double tran, Double san, Double kl, Double a1,
                   Double a2, Double a3, Double b1, Double b2, Double b3, Double c1, Double c2, Double c3, Double d1, Double d2, Double d3,Double cao,Double thap) {
    this.code = code;
    this.name = name;
    this.floor = floor;
    this.indexCode = indexCode;
    this.chg = chg;
    this.ratio = ratio;
    this.price = price;
    this.vol = vol;
    this.tc = tc;
    this.tran = tran;
    this.san = san;
    this.kl = kl;
    this.a1 = a1;
    this.a2 = a2;
    this.a3 = a3;
    this.b1 = b1;
    this.b2 = b2;
    this.b3 = b3;
    this.c1 = c1;
    this.c2 = c2;
    this.c3 = c3;
    this.d1 = d1;
    this.d2 = d2;
    this.d3 = d3;
    this.cao = cao;
    this.thap = thap;  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public Double getCao() {
    return cao;
  }

  public void setCao(Double cao) {
    this.cao = cao;
  }

  public Double getThap() {
    return thap;
  }

  public void setThap(Double thap) {
    this.thap = thap;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getA1() {
    return a1;
  }

  public void setA1(Double a1) {
    this.a1 = a1;
  }

  public Double getA2() {
    return a2;
  }

  public void setA2(Double a2) {
    this.a2 = a2;
  }

  public Double getA3() {
    return a3;
  }

  public void setA3(Double a3) {
    this.a3 = a3;
  }

  public Double getB1() {
    return b1;
  }

  public void setB1(Double b1) {
    this.b1 = b1;
  }

  public Double getB2() {
    return b2;
  }

  public void setB2(Double b2) {
    this.b2 = b2;
  }

  public Double getB3() {
    return b3;
  }

  public void setB3(Double b3) {
    this.b3 = b3;
  }

  public Double getC1() {
    return c1;
  }

  public void setC1(Double c1) {
    this.c1 = c1;
  }

  public Double getC2() {
    return c2;
  }

  public void setC2(Double c2) {
    this.c2 = c2;
  }

  public Double getC3() {
    return c3;
  }

  public void setC3(Double c3) {
    this.c3 = c3;
  }

  public Double getD1() {
    return d1;
  }

  public void setD1(Double d1) {
    this.d1 = d1;
  }

  public Double getD2() {
    return d2;
  }

  public void setD2(Double d2) {
    this.d2 = d2;
  }

  public Double getD3() {
    return d3;
  }

  public void setD3(Double d3) {
    this.d3 = d3;
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
  public Double getTc() {
    return tc;
  }

  public void setTc(Double tc) {
    this.tc = tc;
  }

  public Double getTran() {
    return tran;
  }

  public void setTran(Double tran) {
    this.tran = tran;
  }

  public Double getSan() {
    return san;
  }

  public void setSan(Double san) {
    this.san = san;
  }

  public Double getKl() {
    return kl;
  }

  public void setKl(Double kl) {
    this.kl = kl;
  }
}
