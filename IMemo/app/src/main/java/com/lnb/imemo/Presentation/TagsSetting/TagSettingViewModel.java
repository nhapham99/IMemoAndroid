package com.lnb.imemo.Presentation.TagsSetting;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;

import io.reactivex.Observable;

public class TagSettingViewModel extends ViewModel {
    private static final String TAG = "TagSettingViewModel";
    private TagsRepository tagsRepository;
    private User mUser;
    private Observer<ResponseRepo> tagsObservable;
    private MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();
    public static Tags newTag;


    public TagSettingViewModel() {
        tagsRepository = new TagsRepository();
        mUser = User.getUser();
        subscribeTagRepoObservable();
        newTag = new Tags();
    }

    public void getTags() {
        tagsRepository.getAllTagAction(mUser.getToken());
    }

    public void updateTag(Tags tags) {
        Log.d(TAG, "updateTag: ");
        tagsRepository.updateTags(mUser.getToken(), tags);
    }

    public void deleteTag(Tags tags) {
        tagsRepository.deleteTags(mUser.getToken(), tags.getId());
    }

    private void subscribeTagRepoObservable() {
        tagsObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                String key = response.getKey();
                if (key == Constant.GET_ALL_TAGS_KEY) {
                    ResponseRepo<Pair<Utils.State, ArrayList<Tags>>> responseRepo = new ResponseRepo<>();
                    responseRepo.setKey(Constant.GET_ALL_TAGS_KEY);
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) response.getData();
                    response.setData(pair);
                    viewModelLiveData.setValue(response);
                } else if(key == Constant.CREATE_TAG_KEY) {
                    Pair<Utils.State, String> pair = (Pair<Utils.State, String>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);
                    if (pair.first == Utils.State.SUCCESS) {
                        getTags();
                    }
                    ResponseRepo<Utils.State> responseRepo = new ResponseRepo<>();
                    responseRepo.setData(pair.first);
                    responseRepo.setKey(Constant.CREATE_TAG_KEY);
                    viewModelLiveData.setValue(responseRepo);
                } else if (key == Constant.UPDATE_TAGS_KEY) {
                    Log.d(TAG, "onChanged: update tags");
                    Utils.State state = (Utils.State) response.getData();
                    if (state == Utils.State.SUCCESS) {
                        Log.d(TAG, "onChanged: update tag success");
                        getTags();
                    }
                    ResponseRepo<Utils.State> responseRepo = new ResponseRepo<>();
                    responseRepo.setKey(Constant.UPDATE_TAGS_KEY);
                    responseRepo.setData(state);
                    viewModelLiveData.setValue(responseRepo);
                } else if (key == Constant.DELETE_TAG_KEY) {
                    Utils.State state = (Utils.State) response.getData();
                    if (state == Utils.State.SUCCESS) {
                        getTags();
                    }
                    ResponseRepo<Utils.State> responseRepo = new ResponseRepo<>();
                    responseRepo.setKey(Constant.DELETE_TAG_KEY);
                    responseRepo.setData(state);
                    viewModelLiveData.setValue(responseRepo);
                }
            }
        };
        tagsRepository.observableTagLiveData().observeForever(tagsObservable);
    }

    public MediatorLiveData<ResponseRepo> getViewModelLiveData() {
        return viewModelLiveData;
    }

    public void createNewTag() {
        tagsRepository.createTags(mUser.getToken(), newTag);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tagsRepository.observableTagLiveData().removeObserver(tagsObservable);
    }



}
