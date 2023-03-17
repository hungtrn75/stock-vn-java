package com.skymapglobal.vnstock.workspace.chart;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.ViewModel;

import com.skymapglobal.vnstock.models.CombineChartData;
import com.skymapglobal.vnstock.models.History;
import com.skymapglobal.vnstock.models.Resolution;
import com.skymapglobal.vnstock.service.APIClient;
import com.skymapglobal.vnstock.service.APIInterface;
import com.skymapglobal.vnstock.utils.EMA;
import com.skymapglobal.vnstock.utils.RSI;
import com.skymapglobal.vnstock.utils.Tuple;
import com.tradingview.lightweightcharts.api.chart.models.color.IntColor;
import com.tradingview.lightweightcharts.api.chart.models.color.IntColorKt;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.HistogramData;
import com.tradingview.lightweightcharts.api.series.models.LineData;
import com.tradingview.lightweightcharts.api.series.models.Time;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kotlin.Triple;

public class ChartViewModel extends ViewModel {

  private final BehaviorSubject<Resolution> resolutionSubject = BehaviorSubject.create();
  private Resolution selectedResolution;
  private final BehaviorSubject<Boolean> loading = BehaviorSubject.create();
  private APIInterface service = APIClient.getClient().create(APIInterface.class);

  private final IntColor colorRed = IntColorKt.toIntColor(Color.argb(204, 255, 82, 82));
  private final IntColor colorGreen = IntColorKt.toIntColor(Color.argb(204, 0, 150, 136));
  private HashMap<Time, Float> memoEma20 = new HashMap<>();
  private HashMap<Time, Float> memoEma25 = new HashMap<>();
  private HashMap<Time, Float> memoRsi6 = new HashMap<>();
  private HashMap<Time, Float> memoRsi14 = new HashMap<>();
  private HashMap<Time, Float> memoVol = new HashMap<>();

  @SuppressLint("DefaultLocale")
  public Observable<CombineChartData> getCandlestickDatas(String code) {
    return resolutionSubject.observeOn(AndroidSchedulers.mainThread()).map(r -> {
      loading.onNext(true);
      return r;
    }).observeOn(Schedulers.io()).switchMap(res -> {
      Date toDate = new Date();
      Calendar fromCalendar = Calendar.getInstance();
      Long to = toDate.getTime() / 1000;
      switch (res.getId()) {
        case 1:
        case 2:
          fromCalendar.add(Calendar.MONTH, -1);
          break;
        case 3:
          fromCalendar.add(Calendar.MONTH, -2);
          break;
        case 4:
          fromCalendar.add(Calendar.MONTH, -3);
          break;
        case 5:
          fromCalendar.add(Calendar.MONTH, -7);
          break;
        case 6:
          fromCalendar.add(Calendar.YEAR, -1);
          break;
        default:
          fromCalendar.add(Calendar.YEAR, -5);
          break;
      }
      Long from = fromCalendar.getTime().getTime() / 1000;
      Log.e("getHistory", String.format("from:%02d, to: %02d", from, to));
      return service.getHistory(code, from, to, res.getValue())
          .map(history -> new Pair<>(res, history));
    }).map(pair -> {
      History history = pair.second;
      Resolution resolution = pair.first;
      List<CandlestickData> data = new ArrayList<>();
      List<HistogramData> histogramData = new ArrayList<>();
      switch (resolution.getId()) {
        case 7:
        case 8:
          if (history.getT().size() > 0) {
            Calendar firstDay = Calendar.getInstance();
            if (resolution.getId() == 7) {
              firstDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            } else {
              firstDay.set(Calendar.DAY_OF_MONTH, 1);
            }
            firstDay.set(Calendar.HOUR_OF_DAY, 0);
            firstDay.set(Calendar.MINUTE, 0);
            firstDay.set(Calendar.SECOND, 0);

            Long anchorTime = firstDay.getTime().getTime() / 1000;

            float sumO = history.getO().get(history.getT().size() - 1);
            float sumC = history.getC().get(history.getT().size() - 1);
            float sumH = history.getH().get(history.getT().size() - 1);
            float sumL = history.getL().get(history.getT().size() - 1);
            long sumV = history.getV().get(history.getT().size() - 1);

            for (int i = history.getT().size() - 1; i >= 0; i--) {
              Long time = history.getT().get(i);
              if (time < anchorTime) {
                Time tTime = new Time.Utc(anchorTime);
                CandlestickData candlestickData = new CandlestickData(tTime, sumO, sumH, sumL, sumC,
                    null, null, null);
                data.add(candlestickData);
                HistogramData temp = new HistogramData(tTime, sumV, sumC - sumO > 0 ? colorGreen :
                    colorRed);
                histogramData.add(temp);

                anchorTime = getAnchor(anchorTime,
                    resolution.getId() == 7 ? Calendar.DATE : Calendar.MONTH,
                    resolution.getId() == 7 ? -7 : -1);
                sumC = history.getC().get(i);
                sumH = history.getH().get(i);
                sumL = history.getL().get(i);
                sumV = history.getV().get(i);

              } else {
                sumO = history.getO().get(i);
                if (sumH < history.getH().get(i)) {
                  sumH = history.getH().get(i);
                }
                if (sumL > history.getL().get(i)) {
                  sumL = history.getL().get(i);
                }
                sumV += history.getV().get(i);
              }
            }
            Collections.reverse(data);
            Collections.reverse(histogramData);

          }

          break;
        default:
          for (int i = 0; i < history.getT().size(); i++) {
            Time time = new Time.Utc(history.getT().get(i));
            CandlestickData candlestickData = new CandlestickData(time, history.getO().get(i),
                history.getH().get(i), history.getL().get(i), history.getC().get(i), null, null,
                null);
            data.add(candlestickData);
            HistogramData temp = new HistogramData(time, history.getV().get(i),
                history.getC().get(i) - history.getO().get(i) > 0 ? colorGreen :
                    colorRed);
            histogramData.add(temp);
          }
      }
      EMA ema20O = new EMA(20);
      EMA ema25O = new EMA(25);
      RSI rsi6O = new RSI(6);
      RSI rsi14O = new RSI(14);

      List<LineData> ema20 = ema20O.calculateEmaFromCandlestickData(data, 2);
      List<LineData> ema25 = ema25O.calculateEmaFromCandlestickData(data, 2);
      List<LineData> rsi6 = rsi6O.calculateEmaFromCandlestickData(data, 2);
      List<LineData> rsi14 = rsi14O.calculateEmaFromCandlestickData(data, 2);

      memoEma20.clear();
      memoEma25.clear();
      memoRsi6.clear();
      memoRsi14.clear();
      memoVol.clear();
      ema20.forEach(ema -> memoEma20.put(ema.getTime(), ema.getValue()));
      ema25.forEach(ema -> memoEma25.put(ema.getTime(), ema.getValue()));
      rsi6.forEach(rsi -> memoRsi6.put(rsi.getTime(), rsi.getValue()));
      rsi14.forEach(rsi -> memoRsi14.put(rsi.getTime(), rsi.getValue()));
      histogramData.forEach(vol -> memoVol.put(vol.getTime(), vol.getValue()));
      return new CombineChartData(data, histogramData, ema20, ema25, rsi6, rsi14);
    }).observeOn(AndroidSchedulers.mainThread()).map(r -> {
      loading.onNext(false);
      return r;
    });
  }

  private long getAnchor(long timeUtc, int type, int t) {
    Time tTime = new Time.Utc(timeUtc);
    Calendar tCalendar = Calendar.getInstance();
    tCalendar.setTime(tTime.getDate());
    tCalendar.set(Calendar.HOUR_OF_DAY, 0);
    tCalendar.set(Calendar.MINUTE, 0);
    tCalendar.set(Calendar.SECOND, 0);
    tCalendar.add(type, t);
    return tCalendar.getTime().getTime() / 1000;
  }

  public Observable<Resolution> getSelectedResolution() {
    return resolutionSubject;
  }

  public Observable<Boolean> getLoadingObs() {
    return loading;
  }

  public Resolution getMemoResolution() {
    return selectedResolution;
  }

  public Tuple.T5<Object, Object, Object, Object, Object> getIndexWithTime(Time time) {
    return new Tuple.T5<>(memoEma20.getOrDefault(time, null), memoEma25.getOrDefault(time, null),
        memoRsi6.getOrDefault(time, null), memoRsi14.getOrDefault(time, null),
        memoVol.getOrDefault(time, null));
  }

  public void setResolution(Resolution resolution) {
    selectedResolution = resolution;
    resolutionSubject.onNext(resolution);
  }
}
