package com.lnb.imemo.Data.Repository.Auth;


import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Model.ResultSharedEmails;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.Root;

import org.json.JSONObject;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
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

    @GET("auth/me")
    Flowable<Root<PersonProfile>> getUserProfile(@Header("Authorization") String token);

    @PATCH("auth/me/update")
    Flowable<Root<JsonObject>> updateUserProfile(@Header("Authorization") String token, @Body JsonObject body);

    @GET("auth/shared-emails")
    Flowable<Root<ResultSharedEmails>> getAllSharedEmails(@Header("Authorization") String token);
}
