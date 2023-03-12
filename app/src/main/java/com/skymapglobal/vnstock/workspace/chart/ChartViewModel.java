package com.skymapglobal.vnstock.workspace.chart;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.skymapglobal.vnstock.models.CombineChartData;
import com.skymapglobal.vnstock.models.History;
import com.skymapglobal.vnstock.service.APIClient;
import com.skymapglobal.vnstock.service.APIInterface;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.HistogramData;
import com.tradingview.lightweightcharts.api.series.models.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class ChartViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private BehaviorSubject<CombineChartData> candlestickDatas = BehaviorSubject.create();
    private APIInterface service = APIClient.getClient().create(APIInterface.class);

    public Observable<CombineChartData> getCandlestickDatas() {
        return candlestickDatas;
    }

    private HashMap<String, Long> memoVolumes = new HashMap<>();

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
        super.onCleared();
    }

    public void getDatas(String code) {
        Disposable history = service.getHistory(code, 1647448063L, 1678550623L).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(
                this::handleResponse, this::handleError, this::handleSuccess
        );
        mCompositeDisposable.add(history);
    }

    public HashMap<String, Long> getVolumes(){
        return memoVolumes;
    }

    @SuppressLint("DefaultLocale")
    private void handleResponse(History history) {
        List<CandlestickData> data = new ArrayList<>();
        List<HistogramData> histogramData = new ArrayList<>();
        for (int i = 0; i < history.getT().size(); i++) {
            Time time = new Time.Utc(history.getT().get(i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time.getDate());
            String key = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            memoVolumes.put(key, history.getV().get(i));
            CandlestickData candlestickData = new CandlestickData(time, history.getO().get(i), history.getH().get(i), history.getL().get(i), history.getC().get(i), null, null, null);
            data.add(candlestickData);
            HistogramData temp = new HistogramData(time, history.getV().get(i), null);
            histogramData.add(temp);
        }

        candlestickDatas.onNext(new CombineChartData(data, histogramData));
    }

    private void handleError(Throwable error) {
        Log.e("handleError", error.toString());

    }

    private void handleSuccess() {

    }
}
