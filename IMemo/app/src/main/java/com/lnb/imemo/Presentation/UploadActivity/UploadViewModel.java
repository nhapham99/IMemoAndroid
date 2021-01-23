package com.lnb.imemo.Presentation.UploadActivity;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Entity.ResponseRepo;
import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Data.Repository.UploadFile.UploadFileRepository;
import com.lnb.imemo.Model.Tags;
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
    private TagsRepository tagsRepository;
    private Observer<ResponseRepo> uploadObservable;
    private Observer<ResponseRepo> tagsObservable;
    private MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();

    public UploadViewModel(Context context) {
        this.context = context;
        uploadFileRepository = new UploadFileRepository();
        tagsRepository = new TagsRepository();
        subscribeUploadObservable();
        subscribeTagAction();
    }

    protected void uploadFile(Uri uri) {
        File originFile = FileUtils.getFile(context, uri);
        String path = FileUtils.getPath(context, uri);
        Log.d(TAG, "onActivityResult: " + path);
        RequestBody filePart = RequestBody.create(MediaType.parse(context.getContentResolver().getType(uri)),
                originFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("resource", originFile.getName(), filePart);
        Log.d(TAG, "onActivityResult: " + FileUtils.getPath(context, uri));
        uploadFileRepository.uploadFile(Utils.token, file);
    }

    protected void createTags(String name, String color, Boolean isDefault) {
        tagsRepository.createTags(Utils.token, name, color, isDefault);
    }

    private void subscribeUploadObservable() {
        uploadObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                ResponseRepo<Utils.State> responseToActivity = new ResponseRepo();
                // upload file
                if (key.equals(Constant.UPLOAD_FILE_KEY)) {
                    Pair<Utils.State, JsonObject> responseRepoObject = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    Utils.State state = responseRepoObject.first;
                    switch (state) {
                        case SUCCESS:
                            JsonObject response = responseRepoObject.second;
                            responseToActivity.setData(Utils.State.SUCCESS);
                            break;
                        case FAILURE:
                            responseToActivity.setData(Utils.State.FAILURE);
                            break;
                        case NO_INTERNET:
                            responseToActivity.setData(Utils.State.NO_INTERNET);
                            break;
                    }
                    responseToActivity.setKey(Constant.UPLOAD_FILE_KEY);
                }
                viewModelLiveData.setValue(responseToActivity);
            }
        };
        uploadFileRepository.observableUploadFile().observeForever(uploadObservable);
    }

    private void subscribeTagAction() {
        tagsObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                if (response.getKey().equals(Constant.GET_ALL_TAGS_KEY)) {
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);

                } else if (response.getKey().equals(Constant.GET_TAGS_BY_ID_KEY)) {
                    Pair<Utils.State, Tags> pair = (Pair<Utils.State, Tags>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);

                }
            }
        };
        tagsRepository.observableTagLiveData().observeForever(tagsObservable);
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
