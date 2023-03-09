package com.skymapglobal.vnstock.service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
  private static Retrofit retrofit = null;

  public static Retrofit getClient() {
    OkHttpClient client = new OkHttpClient.Builder().build();

    retrofit = new Retrofit.Builder()
        .baseUrl("https://banggia.cafef.vn/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build();

    return retrofit;
  }
}
