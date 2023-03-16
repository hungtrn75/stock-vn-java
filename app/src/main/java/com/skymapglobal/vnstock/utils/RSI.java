package com.skymapglobal.vnstock.utils;

import android.util.Log;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.LineData;
import java.util.ArrayList;
import java.util.List;

public class RSI {

  public static List<LineData> calculateEmaFromCandlestickData(
      List<CandlestickData> candlestickDataList, Integer period) {
    /*
     * https://github.com/piomin/analyzer/blob/master/analyzer/analyzer-alg/src/main/java/pl/stock/algorithm/core/RSI.java
     * */
    List<LineData> result = new ArrayList<>();
    final double[] up = new double[candlestickDataList.size() - 1];
    final double[] down = new double[candlestickDataList.size() - 1];
    for (int i = 0; i < candlestickDataList.size() - 1; i++) {
      float from = candlestickDataList.get(i).getClose();
      float to = candlestickDataList.get(i + 1).getClose();
      if (from > to) {
        up[i] = from - to;
        down[i] = 0;
      }
      if (from < to) {
        down[i] = Math.abs(from - to);
        up[i] = 0;
      }
    }
    final int emaLength = candlestickDataList.size() - 2 * period;

    if (emaLength > 0) {
      EMA ema = new EMA(2 * period - 1);
      final double[] emus = new double[emaLength];
      final double[] emds = new double[emaLength];
      ema.count(up, 0, emus);
      ema.count(down, 0, emds);
      Log.e("sadasda", candlestickDataList.size() + "/" + emus.length);
      // count RSI with RSI recursive formula
      for (int i = 0; i < emaLength; i++) {
        result.add(new LineData(candlestickDataList.get(i + 2 * period).getTime(),
            100 - (100 / (float) (1 + emus[i] / emds[i]))));
      }
    }
    return result;
  }
}
