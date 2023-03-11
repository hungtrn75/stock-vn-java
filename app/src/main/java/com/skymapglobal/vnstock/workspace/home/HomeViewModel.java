package com.skymapglobal.vnstock.workspace.home;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.skymapglobal.vnstock.models.ListStockResp;
import com.skymapglobal.vnstock.models.SortBy;
import com.skymapglobal.vnstock.models.Stock;
import com.skymapglobal.vnstock.models.StockItem;
import com.skymapglobal.vnstock.models.Stockmarket;
import com.skymapglobal.vnstock.models.Tab;
import com.skymapglobal.vnstock.service.APIClient;
import com.skymapglobal.vnstock.service.APIInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HomeViewModel extends AndroidViewModel {

  APIInterface service;

  public HomeViewModel(@NonNull Application application) {
    super(application);
  }

  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private BehaviorSubject<List<StockItem>> stocks;
  private BehaviorSubject<String> searchTerm;
  private BehaviorSubject<SortBy> sortBy;
  private BehaviorSubject<Boolean> loading;

  public Observable<List<StockItem>> getFilterStocks() {
    return Observable.combineLatest(stocks,
        searchTerm, (stockList, term) -> {
          if (term == null || term.isEmpty()) {
            return stockList;
          }

          return stockList.stream()
              .filter((stockItem -> stockItem.getCode().toLowerCase().contains(term.toLowerCase())))
              .collect(Collectors.toList());
        });
  }

  public Observable<SortBy> getObsSortBy() {
    return sortBy;
  }

  public Observable<Boolean> getObsLoading() {
    return loading;
  }

  public Observable<List<Tab>> getObsStocks() {
    return Observable.combineLatest(stocks,
        sortBy, (stockItemList, sort) -> {
          Log.e("adas", stockItemList.size() + "");
          if (sort == SortBy.CHG_DE) {
            stockItemList.sort(
                ((t1, t2) -> t2.getRatio().compareTo(t1.getRatio())));
          } else if (sort == SortBy.CHG_IN) {
            stockItemList.sort(
                (Comparator.comparing(StockItem::getRatio)));
          } else {
            stockItemList.sort(Comparator.comparing(StockItem::getCode));
          }
          List<StockItem> upcom = new ArrayList<>();
          List<StockItem> hose = new ArrayList<>();
          List<StockItem> vn30 = new ArrayList<>();
          List<StockItem> hnx = new ArrayList<>();
          List<StockItem> hnx30 = new ArrayList<>();
          stockItemList.forEach((stockItem -> {
            switch (stockItem.getFloor()) {
              case "HNX":
                if (stockItem.getIndexCode() != null && stockItem.getIndexCode().equals("HNX30")) {
                  hnx30.add(stockItem);
                } else {
                  hnx.add(stockItem);
                }
                break;
              case "UPCOM":
                upcom.add(stockItem);
                break;
              case "HOSE":
                if (stockItem.getIndexCode() != null && stockItem.getIndexCode().equals("VN30")) {
                  vn30.add(stockItem);
                } else {
                  hose.add(stockItem);
                }
                break;
            }
          }));
          List<Tab> mTabs = new ArrayList<>();
          mTabs.add(new Tab( "VN30", vn30));
          mTabs.add(new Tab( "HOSE", hose));
          mTabs.add(new Tab( "HNX30", hnx30));
          mTabs.add(new Tab( "HNX", hnx));
          mTabs.add(new Tab( "UPCOM", upcom));
          return mTabs;
        });
  }


  public void init() {
    service = APIClient.getClient().create(APIInterface.class);
    stocks = BehaviorSubject.create();
    searchTerm = BehaviorSubject.create();
    sortBy = BehaviorSubject.create();
    loading = BehaviorSubject.create();
    stocks.onNext(new ArrayList<>());
    sortBy.onNext(SortBy.NAME);
    getStocks(true);
  }

  public void setSearchTerm(String term) {
    Log.e("setSearchTerm", term);
    searchTerm.onNext(term);
  }

  public void setSortType() {
    if (sortBy.getValue() == SortBy.NAME) {
      sortBy.onNext(SortBy.CHG_DE);
    } else if (sortBy.getValue() == SortBy.CHG_DE) {
      sortBy.onNext(SortBy.CHG_IN);
    } else {
      sortBy.onNext(SortBy.NAME);
    }
  }

  public Observable<List<Stockmarket>> getStockmarket(){
     return Observable.interval(0,1, TimeUnit.MINUTES).flatMap(aLong -> service.getStockmarket()).repeat();
  }
  public void getStocks(Boolean... showLoadings) {
    if (showLoadings.length > 0 && showLoadings[0]) {
      loading.onNext(true);
    }
    Observable<List<Stock>> obs1 = service.getStocks(1);
    Observable<List<Stock>> obs2 = service.getStocks(2);
    Observable<List<Stock>> obs9 = service.getStocks(9);
    Observable<ListStockResp> obs = service.getStock2s();
    Observable<List<StockItem>> result = Observable.zip(
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
          List<StockItem> res = new ArrayList<>();

          resp.getData().forEach((stock2 -> {
            Stock stock = memoStocks.get(stock2.getCode());
            if (stock != null) {
              StockItem stockItem = new StockItem(stock2.getCode(), stock2.getCompanyName(),
                  stock2.getFloor(), stock2.getIndexCode(), stock.k,
                  100 * (stock.l - stock.b) / stock.b, stock.l,stock.n
                      );
              res.add(stockItem);
            }
          }));

          return res;
        });

    Disposable disposable = result.observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleResponse, this::handleError, this::handleSuccess);
    mCompositeDisposable.add(disposable);
  }

  private void handleResponse(List<StockItem> list) {
    stocks.onNext(list);
  }

  private void handleError(Throwable error) {
    Log.e("handleError", error.toString());
    loading.onNext(false);
  }

  private void handleSuccess() {
    loading.onNext(false);
  }

  public void clearObservables() {
    mCompositeDisposable.clear();
  }
}
