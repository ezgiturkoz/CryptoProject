package com.example.retrofitjava.service;

import com.example.retrofitjava.model.CryptoModel;
import com.example.retrofitjava.model.DetailModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CryptoAPI {
    @GET("/api/v5/market/tickers?instType=SPOT")
    Call<CryptoModel> getPositions();

    @GET("/api/v5/market/candles")
    Call<DetailModel> getDetail();

}