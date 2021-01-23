package com.lnb.imemo.Data.Repository.UploadFile;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.APIUploadClient;
import com.lnb.imemo.Data.Entity.ResponseRepo;

import java.io.File;
import java.io.InputStream;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class UploadFileRepository {
    private static final String TAG = "UploadFileRepository";
    private UploadFileAPI uploadFileAPI;
    private Retrofit retrofit;
    private MediatorLiveData<ResponseRepo> uploadFileLiveData;

    public UploadFileRepository() {
        retrofit = APIUploadClient.getInstance();
        uploadFileAPI = retrofit.create(UploadFileAPI.class);
        uploadFileLiveData = new MediatorLiveData<>();
    }

    public void uploadFile(String token, MultipartBody.Part body) {
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                uploadFileAPI.uploadFile(token, body)
                .onErrorReturn(new Function<Throwable, JsonObject>() {
                    @Override
                    public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                        Log.d(TAG, "apply: " + throwable.getMessage());
                        return new JsonObject();
                    }
                })
                .subscribeOn(Schedulers.io())
        );

        uploadFileLiveData.addSource(source, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                ResponseRepo<JsonObject> responseRepo = new ResponseRepo<>();
                Log.d(TAG, "onChanged: " + jsonObject.toString());
                responseRepo.setData(jsonObject);
                uploadFileLiveData.setValue(responseRepo);
                uploadFileLiveData.removeSource(source);
            }
        });
    }

    public MediatorLiveData<ResponseRepo> observableUploadFile() {
        return uploadFileLiveData;
    }
}
