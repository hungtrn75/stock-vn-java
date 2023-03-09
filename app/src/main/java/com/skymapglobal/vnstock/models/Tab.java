package com.skymapglobal.vnstock.models;

import java.util.List;

public class Tab {
  private Long id;
  private String title;
  private List<StockItem> stocks;

  public Tab(Long id, String title, List<StockItem> stocks) {
    this.title = title;
    this.stocks = stocks;
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public List<StockItem> getStocks() {
    return stocks;
  }
}
