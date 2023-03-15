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
import com.tradingview.lightweightcharts.api.chart.models.color.IntColor;
import com.tradingview.lightweightcharts.api.chart.models.color.IntColorKt;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.HistogramData;
import com.tradingview.lightweightcharts.api.series.models.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class ChartViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private BehaviorSubject<Resolution> resolutionSubject = BehaviorSubject.create();
    private APIInterface service = APIClient.getClient().create(APIInterface.class);

    private HashMap<Long, Long> memoVolumes = new HashMap<>();
    private final IntColor colorRed = IntColorKt.toIntColor(Color.argb(204, 255, 82, 82));
    private final IntColor colorGreen = IntColorKt.toIntColor(Color.argb(204, 0, 150, 136));

    @SuppressLint("DefaultLocale")
    public Observable<CombineChartData> getCandlestickDatas(String code) {
        return resolutionSubject.observeOn(Schedulers.io()).switchMap(res -> {
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
                    fromCalendar.add(Calendar.YEAR, -3);
                    break;
            }
            Long from = fromCalendar.getTime().getTime() / 1000;
            Log.e("getHistory", String.format("from:%d, to: %d", from, to));
            return service.getHistory(code, from, to, res.getValue()).map(history -> new Pair<>(res, history));
        }).map(pair -> {
            History history = pair.second;
            Resolution resolution = pair.first;
            Log.e("getCandlestickDatas", history.getT().size() + "");
            List<CandlestickData> data = new ArrayList<>();
            List<HistogramData> histogramData = new ArrayList<>();

            switch (resolution.getId()) {
                case 7:
                    if (history.getT().size() > 0) {
                        Calendar monday = Calendar.getInstance();
                        monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        Long anchorTime = monday.getTime().getTime() / 1000;

                        float sumO = 0;
                        float sumC = history.getC().get(history.getT().size() - 1);
                        float sumH = 0;
                        float sumL = history.getL().get(history.getT().size() - 1);
                        long sumV = 0;

                        for (int i = history.getT().size() - 1; i >= 0; i--) {
                            Long time = history.getT().get(i);
                            if (time < anchorTime) {
                                Time tTime = new Time.Utc(time);
                                CandlestickData candlestickData = new CandlestickData(tTime, sumO, sumH, sumL, sumC, null, null, null);
                                data.add(candlestickData);
                                memoVolumes.put(tTime.getDate().getTime(), sumV);
                                HistogramData temp = new HistogramData(tTime, sumV, sumC - sumO > 0 ? colorGreen :
                                        colorRed);
                                histogramData.add(temp);

                                anchorTime = getAnchor(history.getT().get(i),Calendar.DATE,-7);
                                sumC = history.getC().get(i);
                                sumH = 0;
                                sumL = history.getL().get(i);
                                sumV = 0;

                            } else {
                                sumO = history.getO().get(i);
                                if (sumH < history.getH().get(i))
                                    sumH = history.getH().get(i);
                                if (sumL > history.getL().get(i))
                                    sumL = history.getL().get(i);
                                sumV += history.getV().get(i);

                            }
                        }
                        Collections.reverse(data);
                        Collections.reverse(histogramData);
                    }

                    break;
                case 8:
                    break;
                default:
                    for (int i = 0; i < history.getT().size(); i++) {
                        Time time = new Time.Utc(history.getT().get(i));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(time.getDate());
                        memoVolumes.put(time.getDate().getTime(), history.getV().get(i));
                        CandlestickData candlestickData = new CandlestickData(time, history.getO().get(i), history.getH().get(i), history.getL().get(i), history.getC().get(i), null, null, null);
                        data.add(candlestickData);
                        HistogramData temp = new HistogramData(time, history.getV().get(i), history.getC().get(i) - history.getO().get(i) > 0 ? colorGreen :
                                colorRed);
                        histogramData.add(temp);
                    }
            }

            return new CombineChartData(data, histogramData);
        });
    }

    private long getAnchor(long timeUtc, int type, int t) {

        Time tTime = new Time.Utc(timeUtc);
        Calendar tCalendar = Calendar.getInstance();
        tCalendar.setTime(tTime.getDate());
        tCalendar.add(type, t);
        return tCalendar.getTime().getTime() / 1000;
    }

    public Observable<Resolution> getSelectedResolution() {
        return resolutionSubject;
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
        super.onCleared();
    }

    public void setResolution(Resolution resolution) {
        resolutionSubject.onNext(resolution);
    }

    public HashMap<Long, Long> getVolumes() {
        return memoVolumes;
    }

}
