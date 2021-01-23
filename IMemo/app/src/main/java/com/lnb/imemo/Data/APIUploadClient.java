package com.lnb.imemo.Data;

import com.lnb.imemo.Utils.Utils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIUploadClient {
    private static Retrofit mInstance;

    public static Retrofit getInstance() {
        if (mInstance == null) {
            mInstance = new Retrofit.Builder()
                    .baseUrl(Utils.baseUploadUrls)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return mInstance;
    }

    private APIUploadClient() {
    }
}
