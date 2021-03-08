package com.lnb.imemo.Data.Repository.Diary;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Model.ResultSharedUser;
import com.lnb.imemo.Data.Repository.Model.SharedUser;
import com.lnb.imemo.Data.Repository.Model.Root;
import com.lnb.imemo.Data.Repository.Model.ResultDiaries;
import com.lnb.imemo.Data.Repository.Model.ResultDiary;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DiaryAPI {
    @GET("diaries")
    Flowable<Root<ResultDiaries>> getDiaries(@Header("Authorization") String token,
                                             @Query("q") String query,
                                             @Query("tag") String tags,
                                             @Query("page") int page,
                                             @Query("pageSize") int pageSize,
                                             @Query("fromDate") String fromDate,
                                             @Query("toDate") String toDate,
                                             @Query("lastId") String lastId,
                                             @Query("pinned") Boolean pinned
    );

    @PATCH("diaries/{id}")
    Flowable<Root<JsonObject>> updateDiary(@Header("Authorization") String token,
                                            @Path("id") String id,
                                            @Body JsonObject body);

    @GET("diaries/{id}")
    Flowable<Root<ResultDiary>> getDiaryById(@Header("Authorization") String token, @Path("id") String id);

    @POST("diaries")
    Flowable<Root<JsonObject>> createDiary(@Header("Authorization") String token, @Body JsonObject body);

    @DELETE("diaries/{id}")
    Flowable<Root<JsonObject>> deleteDiary(@Header("Authorization") String token, @Path("id") String id);

    @POST("diaries/trigger-send-email")
    Flowable<Root<JsonObject>> shareDiary(@Header("Authorization") String token, @Body JsonObject body);

    @GET("diaries/public/{id}")
    Flowable<Root<JsonObject>> publicDiary(@Header("Authorization") String token, @Path("id") String id);

    @GET("diaries/share-with-me")
    Flowable<Root<ResultDiaries>> getDiariesSharedWithMe(@Header("Authorization") String token,
                                             @Query("q") String query,
                                             @Query("tag") String tags,
                                             @Query("page") int page,
                                             @Query("pageSize") int pageSize,
                                             @Query("fromDate") String fromDate,
                                             @Query("toDate") String toDate,
                                             @Query("lastId") String lastId,
                                             @Query("pinned") Boolean pinned
    );

    @GET("diaries/{id}/shares")
    Flowable<Root<ResultSharedUser>> getSharedUser(@Header("Authorization") String token, @Path("id") String id);

}
