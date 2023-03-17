package com.skymapglobal.vnstock.utils;

import android.util.Log;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.LineData;
import java.util.ArrayList;
import java.util.List;

public class RSI {

  private int period;

  // EMA for 2*period-1 period in RSI formula
  private EMA ema;

  /**
   * Constructor
   *
   * @param period - set period
   */
  public RSI(final int period) {
    this.period = period;
    this.ema = new EMA(2 * period - 1);
  }

  /*
   * https://github.com/piomin/analyzer/blob/master/analyzer/analyzer-alg/src/main/java/pl/stock/algorithm/core/RSI.java
   * */
  public double[] calculate(final double[] prizes, int smoothing) {
    final double[] up = new double[prizes.length - 1];
    final double[] down = new double[prizes.length - 1];
    for (int i = 0; i < prizes.length - 1; i++) {
      if (prizes[i] > prizes[i + 1]) {
        up[i] = prizes[i] - prizes[i + 1];
        down[i] = 0;
      }
      if (prizes[i] < prizes[i + 1]) {
        down[i] = Math.abs(prizes[i] - prizes[i + 1]);
        up[i] = 0;
      }
    }

    // count EMA for up and down tables
    final int emaLength = prizes.length - 2 * period;
    double[] rsis = new double[0];
    if (emaLength > 0) {
      final double[] emus = new double[emaLength];
      final double[] emds = new double[emaLength];
      ema.calculate(up, smoothing, emus);
      ema.calculate(down, smoothing, emds);

      // count RSI with RSI recursive formula
      rsis = new double[emaLength];
      for (int i = 0; i < rsis.length; i++) {
        rsis[i] = 100 - (100 / (double) (1 + emus[i] / emds[i]));
      }
    }

    return rsis;
  }

  public List<LineData> calculateEmaFromCandlestickData(List<CandlestickData> candlestickDataList,
      Integer smoothing) {
    List<LineData> result = new ArrayList<>();
    double[] rsis = calculate(
        candlestickDataList.stream().mapToDouble(CandlestickData::getClose).toArray(), smoothing);

    for (int i = 0; i < rsis.length; i++) {
      result.add(
          new LineData(candlestickDataList.get(i + 2 * period).getTime(), (float) rsis[i]));
    }
    return result;
  }
}
