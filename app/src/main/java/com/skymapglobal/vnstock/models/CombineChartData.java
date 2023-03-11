package com.skymapglobal.vnstock.models;

import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.HistogramData;

import java.util.List;

public class CombineChartData {
    private List<CandlestickData> candlestickDataList;
    private List<HistogramData> histogramDataList;

    public CombineChartData(List<CandlestickData> candlestickDataList, List<HistogramData> histogramDataList) {
        this.candlestickDataList = candlestickDataList;
        this.histogramDataList = histogramDataList;
    }

    public List<CandlestickData> getCandlestickDataList() {
        return candlestickDataList;
    }

    public List<HistogramData> getHistogramDataList() {
        return histogramDataList;
    }
}
