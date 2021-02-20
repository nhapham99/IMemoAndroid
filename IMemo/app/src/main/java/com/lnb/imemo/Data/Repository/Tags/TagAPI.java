package com.lnb.imemo.Data.Repository.Tags;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Model.ResultTags;
import com.lnb.imemo.Model.Root;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TagAPI {
    @GET("tags/get-all")
    Flowable<Root<ResultTags>> getAllTag(@Header("Authorization") String token);

    @GET("tags/{id}")
    Flowable<JsonObject> getTagById(@Header("Authorization") String token, @Path("id") String id);

    @PATCH("tags/{id}")
    Flowable<JsonObject> updateTag(@Header("Authorization") String token, @Path("id") String id, @Body JsonObject body);

    @DELETE("tags/{id}")
    Flowable<JsonObject> deleteTag(@Header("Authorization") String token, @Path("id") String id);

    @POST("tags")
    Flowable<JsonObject> createTag(@Header("Authorization") String token, @Body JsonObject body);
}
