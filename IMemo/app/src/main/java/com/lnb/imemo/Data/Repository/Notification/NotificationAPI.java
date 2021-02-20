package com.lnb.imemo.Data.Repository.Notification;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Model.ResultNotification;
import com.lnb.imemo.Model.Root;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

interface NotificationAPI {
    @GET("notifications")
    Flowable<ResultNotification> getAllNotification(@Header("Authorization") String token,
                                                          @Query("page") int page,
                                                          @Query("pageSize") int pageSize);
}
