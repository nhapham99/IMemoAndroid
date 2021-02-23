package com.lnb.imemo.Presentation.UploadActivity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Model.FileRequestBody;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Data.Repository.UploadFile.UploadFileRepository;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.FileMetaData;
import com.lnb.imemo.Utils.FileUtils;
import com.lnb.imemo.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadViewModel extends ViewModel {
    private static final String TAG = "UploadViewModel";
    private Context context;
    private UploadFileRepository uploadFileRepository;
    private DiaryRepository diaryRepository;
    private Observer<ResponseRepo> uploadObservable;
    private Observer<ResponseRepo> diaryObserver;
    private MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();
    private Diary uploadDiary;
    private User mUser;

    public UploadViewModel(Context context) {
        this.context = context;
        uploadFileRepository = new UploadFileRepository();
        diaryRepository = new DiaryRepository();
        uploadDiary = new Diary();
        mUser = User.getUser();
        subscribeUploadObservable();
        subscribeDiaryObservable();
    }

    protected void uploadFile(Uri uri) {
        FileMetaData fileMetaData = FileMetaData.getFileMetaData(context, uri);
        try {
            FileRequestBody requestBody = new FileRequestBody(context.getContentResolver().openInputStream(uri), fileMetaData.mimeType);
            MultipartBody.Part file = MultipartBody.Part.createFormData("resource", fileMetaData.displayName, requestBody);
            uploadFileRepository.uploadFile(mUser.getToken(), file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateDiary() {
        diaryRepository.updateDiary(mUser.getToken(), getUploadDiary());
    }

    private void subscribeDiaryObservable() {
        diaryObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.UPDATE_DIARY_KEY) {
                    Pair<Utils.State,JsonObject> pair = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    ResponseRepo<Utils.State> response = new ResponseRepo<>();
                    response.setKey(Constant.UPDATE_DIARY_KEY);
                    response.setData(pair.first);
                    viewModelLiveData.setValue(response);
                }
            }
        };

        diaryRepository.observableDiaryRepo().observeForever(diaryObserver);
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
        diaryRepository.observableDiaryRepo().removeObserver(diaryObserver);
    }

}
