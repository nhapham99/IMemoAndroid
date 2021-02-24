package com.lnb.imemo.Data.Repository.UploadFile;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.APIUploadClient;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
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
        Log.d(TAG, "uploadFile: " + body.toString());
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                uploadFileAPI.uploadFile(token, body)
                .onErrorReturn(new Function<Throwable, JsonObject>() {
                    @Override
                    public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                        JsonObject jsonObject = new JsonObject();
                        String message = throwable.getMessage();Log.d(TAG, "apply: " + message);
                        if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                            jsonObject.addProperty(Constant.STATUS_CODE, 409);
                        } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                            jsonObject.addProperty(Constant.STATUS_CODE, -1);
                        } else {
                            jsonObject.addProperty(Constant.STATUS_CODE, -2);
                        }
                        return jsonObject;
                    }
                })
                .subscribeOn(Schedulers.io())
        );

        uploadFileLiveData.addSource(source, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                ResponseRepo<Pair<Utils.State, JsonObject>> responseRepo = new ResponseRepo<>();
                Log.d(TAG, "onChanged: " + jsonObject.toString());
                if (jsonObject.get(Constant.UPLOADED) != null && jsonObject.get(Constant.UPLOADED).getAsBoolean()) {
                    responseRepo.setData(new Pair<>(Utils.State.SUCCESS, jsonObject));
                } else {
                    int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
                    if (statusCode == -1) {
                       responseRepo.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                    } else {
                        responseRepo.setData(new Pair<>(Utils.State.FAILURE, null));
                    }
                }
                responseRepo.setKey(Constant.UPLOAD_FILE_KEY);
                uploadFileLiveData.setValue(responseRepo);
                uploadFileLiveData.removeSource(source);
            }
        });
    }

    public MediatorLiveData<ResponseRepo> observableUploadFile() {
        return uploadFileLiveData;
    }
}
