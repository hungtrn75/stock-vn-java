package com.skymapglobal.vnstock.service;

import com.skymapglobal.vnstock.models.History;
import com.skymapglobal.vnstock.models.ListStockResp;
import com.skymapglobal.vnstock.models.Stock;
import com.skymapglobal.vnstock.models.StockMarket;


import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface APIInterface {
    @GET("stockhandler.ashx")
    Observable<List<Stock>> getStocks(@Query("center") Integer index);

    @GET("https://finfo-api.vndirect.com.vn/v4/stocks?q=type:STOCK~status:LISTED&fields=code,companyName,companyNameEng,shortName,floor,industryName,indexCode&size=3000")
    Observable<ListStockResp> getStock2s();

 @GET("https://banggia.cafef.vn/stockhandler.ashx?index=true")
  Observable<List<StockMarket>> getStockMarket();

    @GET("https://banggia.cafef.vn/stockhandler.ashx?index=true")
    Observable<List<StockMarket>> getStockmarket();

    @GET("https://dchart-api.vndirect.com.vn/dchart/history")
    Observable<History> getHistory(@Query("symbol") String code, @Query("from") Long from, @Query("to") Long to, @Query("resolution") String resolution);
}
