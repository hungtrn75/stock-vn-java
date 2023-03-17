package com.skymapglobal.vnstock.models;

public class StockMarket {
    private String name;
    private String index;
    private String change;
    private String percent;

    public StockMarket(String name, String index, String change, String percent) {
        this.name = name;
        this.index = index;
        this.change = change;
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
