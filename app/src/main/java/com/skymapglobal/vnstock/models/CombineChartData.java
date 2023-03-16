package com.skymapglobal.vnstock.models;

import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.HistogramData;

import com.tradingview.lightweightcharts.api.series.models.LineData;
import java.util.List;

public class CombineChartData {

  private List<CandlestickData> candlestickDataList;
  private List<HistogramData> histogramDataList;

  private List<LineData> ema20;
  private List<LineData> ema25;

  public CombineChartData(List<CandlestickData> candlestickDataList,
      List<HistogramData> histogramDataList,
      List<LineData> ema20, List<LineData> ema25) {
    this.candlestickDataList = candlestickDataList;
    this.histogramDataList = histogramDataList;
    this.ema20 = ema20;
    this.ema25 = ema25;
  }

  public List<CandlestickData> getCandlestickDataList() {
    return candlestickDataList;
  }

  public List<HistogramData> getHistogramDataList() {
    return histogramDataList;
  }

  public List<LineData> getEma20() {
    return ema20;
  }

  public List<LineData> getEma25() {
    return ema25;
  }
}
