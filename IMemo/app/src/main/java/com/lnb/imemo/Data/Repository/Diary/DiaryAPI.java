package com.lnb.imemo.Data.Repository.Diary;

import com.google.gson.JsonObject;
import com.lnb.imemo.Model.Root;
import com.lnb.imemo.Model.ResultDiaries;
import com.lnb.imemo.Model.ResultDiary;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DiaryAPI {
    @GET("diaries")
    Flowable<Root<ResultDiaries>> getDiaries(@Header("Authorization") String token,
                                             @Query("query") String query,
                                             @Query("tag") List<String> tags,
                                             @Query("page") int page,
                                             @Query("pageSize") int pageSize,
                                             @Query("fromDate") String fromDate,
                                             @Query("toDate") String toDate,
                                             @Query("lastId") String lastId
    );

    @GET("diaries/{id}")
    Flowable<Root<ResultDiary>> getDiaryById(@Header("Authorization") String token, @Path("id") String id);

    @POST("diaries")
    Flowable<JsonObject> createDiary(@Header("Authorization") String token, @Body JsonObject body);

    @DELETE("diaries/{id}")
    Flowable<Root<JsonObject>> deleteDiary(@Header("Authorization") String token, @Path("id") String id);


}
