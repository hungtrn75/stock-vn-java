package com.skymapglobal.vnstock.utils;

import android.util.Log;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.LineData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.StatUtils;

public class EMA {

  // period in days for count EMA
  private int period;

  /**
   * Constructor
   *
   * @param period - set period
   */
  public EMA(final int period) {
    this.period = period;
  }


  /**
   * Count EMA for period
   *
   * @param prizes - input table with prizes sort from latest day to earliest
   * @param offset - offset in the prizes table
   * @param emas   - empty table where EMA-s count would be inserted
   * @return - EMA
   */
  public double count(final double[] prizes, final int offset, final double[] emas) {

    // check if period is not longest than number of prizes count from period index
    if (prizes.length - offset <= period) {
      final double[] startPrizes = new double[period];
      System.arraycopy(prizes, offset, startPrizes, 0, period);
      // returning simple mean for that period, because it's beginning of count EMA
      return StatUtils.mean(startPrizes, 0, period);

      // if period is longer we use recursion algorithm
    } else {
      // recursion call with offset incremented
      final double previousEma = count(prizes, offset + 1, emas);
      // count from the EMA recursion formula
      final double a = (double) 2 / (period + 1);
      final double actualEma = (prizes[offset] - previousEma) * a + previousEma;
      emas[offset] = actualEma;
      return actualEma;
    }
  }

  public void calculate(final double[] prizes, final int smoothing, final double[] emas) {
    float sum = 0;
    boolean first = true;
    for (int i = 0; i < prizes.length; i++) {
      if (i < period) {
        sum += prizes[i];
      } else {
        if (first) {
          first = false;
          emas[0] = sum / period;
        } else {
          double cEma =
              prizes[i] * smoothing / (1 + period)
                  + emas[i - period - 1] * (1
                  - smoothing / (1f + period));
          emas[i - period] = cEma;
        }
      }
    }
  }

  public List<Float> count(List<Float> prizes, Integer smoothing) {
    List<Float> result = new ArrayList<>();
    float sum = 0;
    for (int i = 0; i < prizes.size(); i++) {
      if (i < period) {
        sum += prizes.get(i);
      } else {
        if (result.size() == 0) {
          result.add(sum / period);
        }
        float cEma =
            prizes.get(i) * smoothing / (1 + period)
                + result.get(result.size() - 1) * (1
                - smoothing / (1f + period));
        result.add(cEma);
      }
    }
    return result;
  }


  /**
   * Count EMA for one day
   *
   * @param prize       - actual prize
   * @param previousEma - previous EMA value
   * @return - EMA for today
   */
  public double single(final double prize, final double previousEma) {
    final double a = (float) 2 / (period + 1);
    final double actualEma = (prize - previousEma) * a + previousEma;
    return actualEma;
  }

  public List<LineData> calculateEmaFromCandlestickData(List<CandlestickData> candlestickDataList,
      Integer smoothing) {
    double[] emas = new double[candlestickDataList.size() - period];
    calculate(candlestickDataList.stream()
            .mapToDouble(CandlestickData::getClose).toArray(),
        smoothing, emas);

    List<LineData> result = new ArrayList<>();
    for (int i = 0; i < emas.length; i++) {
      result.add(new LineData(candlestickDataList.get(i + period).getTime(), (float) emas[i]));
    }

    return result;
  }

  public static List<LineData> calculateEmaFromCandlestickData(
      List<CandlestickData> candlestickDataList,
      Integer period, Integer smoothing) {
    /*
     * https://plainenglish.io/blog/how-to-calculate-the-ema-of-a-stock-with-python
     * */
    List<LineData> result = new ArrayList<>();
    float sum = 0;
    for (int i = 0; i < candlestickDataList.size(); i++) {
      if (i < period) {
        sum += candlestickDataList.get(i).getClose();
      } else {
        if (result.size() == 0) {
          result.add(new LineData(candlestickDataList.get(i - 1).getTime(), sum / period));
        }
        float cEma =
            candlestickDataList.get(i).getClose() * smoothing / (1 + period)
                + result.get(result.size() - 1).getValue() * (1
                - smoothing / (1f + period));
        result.add(new LineData(candlestickDataList.get(i).getTime(), cEma));
      }
    }
    return result;
  }
}
