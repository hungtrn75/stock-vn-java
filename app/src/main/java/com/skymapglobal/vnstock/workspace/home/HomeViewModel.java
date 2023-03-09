package com.skymapglobal.vnstock.workspace.home;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.skymapglobal.vnstock.models.ListStockResp;
import com.skymapglobal.vnstock.models.Stock;
import com.skymapglobal.vnstock.models.Stock2;
import com.skymapglobal.vnstock.models.StockItem;
import com.skymapglobal.vnstock.models.Tab;
import com.skymapglobal.vnstock.service.APIClient;
import com.skymapglobal.vnstock.service.APIInterface;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

  APIInterface service;

  public HomeViewModel(@NonNull Application application) {
    super(application);
  }

  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private PublishSubject<List<Tab>> tabs;

  public PublishSubject<List<Tab>> getTabs() {
    return tabs;
  }

  private Long counter = 0L;

  public void init() {
    List<Tab> mTabs = new ArrayList<>();
    mTabs.add(new Tab(0L, "VN30", new ArrayList<>()));
    mTabs.add(new Tab(1L, "HOSE", new ArrayList<>()));
    mTabs.add(new Tab(2L, "HNX30", new ArrayList<>()));
    mTabs.add(new Tab(3L, "HNX", new ArrayList<>()));
    mTabs.add(new Tab(4L, "UPCOM", new ArrayList<>()));
    service = APIClient.getClient().create(APIInterface.class);
    tabs = PublishSubject.create();
    counter = 5L;
    tabs.onNext(mTabs);
    getStocks();
  }

  public void getStocks() {
    Observable<List<Stock>> obs1 = service.getStocks(1);
    Observable<List<Stock>> obs2 = service.getStocks(2);
    Observable<List<Stock>> obs9 = service.getStocks(9);
    Observable<ListStockResp> obs = service.getStock2s();
    Observable<List<Tab>> result = Observable.zip(
        obs1.subscribeOn(Schedulers.io()), obs2.subscribeOn(Schedulers.io()),
        obs9.subscribeOn(Schedulers.io()), obs.subscribeOn(Schedulers.io()),
        (resp1, resp2, resp9, resp) -> {
          HashMap<String, Stock> memoStocks = new HashMap<>();

          List<Stock> list = new ArrayList<>();
          list.addAll(resp1);
          list.addAll(resp2);
          list.addAll(resp9);
          list.forEach((stock -> {
            memoStocks.put(stock.a, stock);

          }));
          List<StockItem> upcom = new ArrayList<>();
          List<StockItem> hose = new ArrayList<>();
          List<StockItem> vn30 = new ArrayList<>();
          List<StockItem> hnx = new ArrayList<>();
          List<StockItem> hnx30 = new ArrayList<>();
          resp.getData().forEach((stock2 -> {
            Stock stock = memoStocks.get(stock2.getCode());
            StockItem stockItem = new StockItem(stock2.getCode(), stock2.getCompanyName());
            switch (stock2.getFloor()) {
              case "HNX":
                if (stock2.getIndexCode() != null && stock2.getIndexCode().equals("HNX30")) {
                  hnx30.add(stockItem);
                } else {
                  hnx.add(stockItem);
                }
                break;
              case "UPCOM":
                upcom.add(stockItem);
                break;
              case "HOSE":
                if (stock2.getIndexCode() != null && stock2.getIndexCode().equals("VN30")) {
                  vn30.add(stockItem);
                } else {
                  hose.add(stockItem);
                }
                break;
            }
          }));
          List<Tab> mTabs = new ArrayList<>();
          mTabs.add(new Tab(counter, "VN30", vn30));
          mTabs.add(new Tab(counter + 1, "HOSE", hose));
          mTabs.add(new Tab(counter + 2, "HNX30", hnx30));
          mTabs.add(new Tab(counter + 3, "HNX", hnx));
          mTabs.add(new Tab(counter + 4, "UPCOM", upcom));
          counter += 5;
          return mTabs;
        });

    Disposable disposable = result.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleResponse, this::handleError, this::handleSuccess);
    mCompositeDisposable.add(disposable);
  }

  private void handleResponse(List<Tab> stocks) {
    tabs.onNext(stocks);
  }

  private void handleError(Throwable error) {
  }

  private void handleSuccess() {

  }

  public void clearObservables() {
    mCompositeDisposable.clear();
  }
}
