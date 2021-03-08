package com.lnb.imemo.Data.Repository.Notification;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Model.ResultNotification;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface NotificationAPI {
    @GET("notifications")
    Flowable<ResultNotification> getAllNotification(@Header("Authorization") String token,
                                                          @Query("page") int page,
                                                          @Query("pageSize") int pageSize);
    @PUT("notifications/mark-seen/{id}")
    Flowable<JsonObject> markReadNotification(@Header("Authorization") String token,
                                              @Path("id") String notificationId);
}
