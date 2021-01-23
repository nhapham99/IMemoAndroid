package com.lnb.imemo.Data.Repository.Auth;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/get-token")
    Flowable<JsonObject> getTokenFromGoogleToken(@Body JsonObject body);

    @POST("auth/register")
    Flowable<JsonObject> register(@Body JsonObject body);

    @POST("auth/login")
    Flowable<JsonObject> login(@Body JsonObject body);

    @POST("auth/request-forgot-password")
    Flowable<JsonObject> forgotPassword(@Body JsonObject body);

    @POST("auth/reset-password")
    Flowable<JsonObject> resetPassword(@Body JsonObject body);
}
