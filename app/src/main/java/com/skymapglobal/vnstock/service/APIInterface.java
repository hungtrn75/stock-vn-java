package com.skymapglobal.vnstock.service;

import com.skymapglobal.vnstock.models.ListStockResp;
import com.skymapglobal.vnstock.models.Stock;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface APIInterface {
  @GET("stockhandler.ashx")
  Observable<List<Stock>> getStocks(@Query("center") Integer index);

  @GET("https://finfo-api.vndirect.com.vn/v4/stocks?q=type:STOCK~status:LISTED&fields=code,companyName,companyNameEng,shortName,floor,industryName,indexCode&size=3000")
  Observable<ListStockResp> getStock2s();
}