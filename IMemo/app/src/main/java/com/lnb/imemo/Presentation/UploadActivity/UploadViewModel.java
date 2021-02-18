package com.lnb.imemo.Presentation.UploadActivity;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Data.Repository.UploadFile.UploadFileRepository;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.FileUtils;
import com.lnb.imemo.Utils.Utils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadViewModel extends ViewModel {
    private static final String TAG = "UploadViewModel";
    private Context context;
    private UploadFileRepository uploadFileRepository;
    private DiaryRepository diaryRepository;
    private Observer<ResponseRepo> uploadObservable;
    private MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();
    private final Diary uploadDiary;
    private User mUser;

    public UploadViewModel(Context context) {
        this.context = context;
        uploadFileRepository = new UploadFileRepository();
        diaryRepository = new DiaryRepository();
        uploadDiary = new Diary();
        mUser = User.getUser();
        subscribeUploadObservable();
    }

    protected void uploadFile(Uri uri) {
        File originFile = FileUtils.getFile(context, uri);
        String path = FileUtils.getPath(context, uri);
        Log.d(TAG, "onActivityResult: " + path);
        RequestBody filePart = RequestBody.create(MediaType.parse(context.getContentResolver().getType(uri)),
                originFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("resource", originFile.getName(), filePart);
        Log.d(TAG, "onActivityResult: " + FileUtils.getPath(context, uri));
        uploadFileRepository.uploadFile(mUser.getToken(), file);
    }

    private void subscribeUploadObservable() {
        uploadObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                ResponseRepo<Pair<Utils.State, Object>> responseToActivity = new ResponseRepo();
                // upload file
                if (key.equals(Constant.UPLOAD_FILE_KEY)) {
                    Pair<Utils.State, JsonObject> responseRepoObject = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    Utils.State state = responseRepoObject.first;
                    JsonObject responseJsonObject = responseRepoObject.second;
                    switch (state) {
                        case SUCCESS:
                            JsonObject response = responseRepoObject.second.getAsJsonObject(Constant.RESULT);
                            Resource resource = new Resource();
                            resource.setType(response.get("mimetype").getAsString());
                            resource.setName(response.get("key").getAsString());
                            resource.setUrl(response.get("url").getAsString());
                            responseToActivity.setData(new Pair<>(Utils.State.SUCCESS, resource));
                            break;
                        case FAILURE:
                            responseToActivity.setData(new Pair<>(Utils.State.FAILURE, null));
                            break;
                        case NO_INTERNET:
                            responseToActivity.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                            break;
                    }
                    responseToActivity.setKey(Constant.UPLOAD_FILE_KEY);
                }
                viewModelLiveData.setValue(responseToActivity);
            }
        };
        uploadFileRepository.observableUploadFile().observeForever(uploadObservable);
    }


    public Diary getUploadDiary() {
        return uploadDiary;
    }

    public MediatorLiveData<ResponseRepo> getViewModelObservable() {
        return viewModelLiveData;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        uploadFileRepository.observableUploadFile().removeObserver(uploadObservable);
    }

}
