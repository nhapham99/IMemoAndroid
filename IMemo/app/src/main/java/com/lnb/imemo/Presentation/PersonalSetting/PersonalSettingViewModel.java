package com.lnb.imemo.Presentation.PersonalSetting;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Auth.AuthRepository;

import com.lnb.imemo.Data.Repository.UploadFile.UploadFileRepository;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.FileUtils;
import com.lnb.imemo.Utils.Utils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


class PersonalSettingViewModel extends ViewModel {
    private static final String TAG = "PersonalSettingViewMode";
    private AuthRepository authRepository;
    private User mUser;
    public PersonProfile personProfile;
    public PersonProfile newPersonProfile;
    private MediatorLiveData<ResponseRepo> personalSettingObservable;
    private MediatorLiveData<ResponseRepo<Pair<Utils.State, String>>> uploadLiveData;
    private UploadFileRepository uploadFileRepository;
    private Observer<ResponseRepo> uploadObservable;
    private Observer<ResponseRepo> authObserver;
    private Context mContext;
    private Boolean isUpdateImage = false;

    public PersonalSettingViewModel(Context mContext) {
        this.mContext = mContext;
        authRepository = new AuthRepository();
        uploadFileRepository = new UploadFileRepository();
        personalSettingObservable = new MediatorLiveData<>();
        uploadLiveData = new MediatorLiveData<>();
        mUser = User.getUser();
        personProfile = PersonProfile.getInstance();
        newPersonProfile = personProfile;
        subscribeAuthObservable();
        subscribeUploadObservable();
    }

    public void getPersonProfile() {
        authRepository.getUserProfile(mUser.getToken());
    }

    public void updatePersonProfile() {
        authRepository.updatePersonProfile(mUser.getToken(), newPersonProfile);
    }

    private void subscribeAuthObservable() {
        authObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                String key = response.getKey();
                if (key == Constant.GET_PERSON_PROFILE) {
                   Pair<Utils.State, PersonProfile> responseRepo = (Pair<Utils.State, PersonProfile>) response.getData();
                   ResponseRepo<Utils.State> viewModelResponse = new ResponseRepo<>();
                   viewModelResponse.setKey(Constant.GET_PERSON_PROFILE);
                   switch (responseRepo.first) {
                        case SUCCESS:
                            viewModelResponse.setData(Utils.State.SUCCESS);
                            personProfile.updateInstance(responseRepo.second);
                            break;
                        case FAILURE:
                            viewModelResponse.setData(Utils.State.FAILURE);
                            break;
                        case NO_INTERNET:
                            viewModelResponse.setData(Utils.State.NO_INTERNET);
                            break;
                    }
                    personalSettingObservable.setValue(viewModelResponse);
                } else if (key == Constant.UPDATE_PERSON_PROFILE) {
                    Log.d(TAG, "onChanged: update person profile");
                    Pair<Utils.State, String> responseRepo = (Pair<Utils.State, String>) response.getData();
                    ResponseRepo<Utils.State> viewModelResponse = new ResponseRepo<>();
                    viewModelResponse.setKey(Constant.UPDATE_PERSON_PROFILE);
                    switch (responseRepo.first) {
                        case SUCCESS:
                            if (isUpdateImage) {
                                viewModelResponse.setKey(Constant.UPDATE_IMAGE_PERSON_PROFILE);
                                viewModelResponse.setData(Utils.State.SUCCESS);
                                isUpdateImage = false;
                            } else {
                                personProfile.updateInstance(newPersonProfile);
                                viewModelResponse.setData(Utils.State.SUCCESS);
                            }
                            break;
                        case FAILURE:
                            viewModelResponse.setData(Utils.State.FAILURE);
                            break;
                        case NO_INTERNET:
                            viewModelResponse.setData(Utils.State.NO_INTERNET);
                            break;
                    }
                    personalSettingObservable.setValue(viewModelResponse);
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObserver);
    }

    protected void uploadFile(Uri uri) {
        File originFile = FileUtils.getFile(mContext, uri);
        String path = FileUtils.getPath(mContext, uri);
        RequestBody filePart = RequestBody.create(MediaType.parse(mContext.getContentResolver().getType(uri)),
                originFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("resource", originFile.getName(), filePart);
        uploadFileRepository.uploadFile(mUser.getToken(), file);
    }

    private void subscribeUploadObservable() {
        uploadObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                ResponseRepo<Pair<Utils.State, String>> response = new ResponseRepo<>();
                // upload file
                if (key.equals(Constant.UPLOAD_FILE_KEY)) {
                    Pair<Utils.State, JsonObject> responseRepoObject = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    Utils.State state = responseRepoObject.first;
                    switch (state) {
                        case SUCCESS:
                            isUpdateImage = true;
                            newPersonProfile.setPicture(responseRepoObject.second.getAsJsonObject(Constant.RESULT).get("url").getAsString());
                            updatePersonProfile();
                            break;
                        case FAILURE:
                            response.setData(new Pair<>(Utils.State.FAILURE, null));
                            uploadLiveData.setValue(response);
                            break;
                        case NO_INTERNET:
                            response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                            uploadLiveData.setValue(response);
                            break;
                    }
                    response.setKey(Constant.UPLOAD_FILE_KEY);

                }
            }
        };
        uploadFileRepository.observableUploadFile().observeForever(uploadObservable);
    }


    public MediatorLiveData<ResponseRepo> getPersonalSettingObservable() {
        return personalSettingObservable;
    }

    public MediatorLiveData<ResponseRepo<Pair<Utils.State, String>>> getUploadLiveData() {
        return uploadLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        authRepository.observableAuthRepo().removeObserver(authObserver);
    }
}
