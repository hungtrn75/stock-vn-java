package com.skymapglobal.vnstock.workspace.chart;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.skymapglobal.vnstock.models.History;
import com.skymapglobal.vnstock.models.StockItem;
import com.skymapglobal.vnstock.service.APIClient;
import com.skymapglobal.vnstock.service.APIInterface;
import com.tradingview.lightweightcharts.api.series.models.CandlestickData;
import com.tradingview.lightweightcharts.api.series.models.Time;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class ChartViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private BehaviorSubject<List<CandlestickData>> candlestickDatas = BehaviorSubject.create();
    private APIInterface service = APIClient.getClient().create(APIInterface.class);

    public Observable<List<CandlestickData>> getCandlestickDatas() {
        return candlestickDatas;
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
        super.onCleared();
    }

    public void getDatas(String code) {
        Disposable history = service.getHistory(code, 1672423767L, 1678550623L).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(
                this::handleResponse, this::handleError, this::handleSuccess
        );
        mCompositeDisposable.add(history);
    }

    private void handleResponse(History history) {
        List<CandlestickData> data = new ArrayList<>();
        for (int i = 0; i < history.getT().size(); i++) {
            Time time = new Time.Utc(history.getT().get(i));
            CandlestickData candlestickData = new CandlestickData(time, history.getO().get(i), history.getH().get(i), history.getL().get(i), history.getC().get(i), null, null, null);
            data.add(candlestickData);
        }

        candlestickDatas.onNext(data);
    }

    private void handleError(Throwable error) {
        Log.e("handleError", error.toString());

    }

    private void handleSuccess() {

    }
}
