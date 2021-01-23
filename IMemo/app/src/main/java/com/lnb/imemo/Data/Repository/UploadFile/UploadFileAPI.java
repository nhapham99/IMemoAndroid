package com.lnb.imemo.Data.Repository.UploadFile;

import com.google.gson.JsonObject;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

interface UploadFileAPI {
    @Multipart
    @POST("upload/aws")
    Flowable<JsonObject> uploadFile(@Header("Authorization") String token, @Part MultipartBody.Part fileData);
}
