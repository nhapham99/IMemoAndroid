package com.lnb.imemo.Presentation.PickTag;

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

public class PickTagsViewModel extends ViewModel {
    private static final String TAG = "PickTagsViewModel";
    private TagsRepository tagsRepository;
    private Observer<ResponseRepo> tagsObservable;
    private MediatorLiveData<ArrayList<Tags>> allTagsLiveData = new MediatorLiveData<>();
    private User mUser;

    public PickTagsViewModel() {
        tagsRepository = new TagsRepository();
        mUser = User.getUser();
        subscribeTagAction();
    }

    protected void getAllTags() {
        tagsRepository.getAllTagAction(mUser.getToken());
    }

    private void subscribeTagAction() {
        tagsObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                if (response.getKey().equals(Constant.GET_ALL_TAGS_KEY)) {
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);
                    allTagsLiveData.setValue(pair.second);
                }
            }
        };
        tagsRepository.observableTagLiveData().observeForever(tagsObservable);
    }

    public MediatorLiveData<ArrayList<Tags>> observableAllTags() {
        return allTagsLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tagsRepository.observableTagLiveData().removeObserver(tagsObservable);
    }
}
