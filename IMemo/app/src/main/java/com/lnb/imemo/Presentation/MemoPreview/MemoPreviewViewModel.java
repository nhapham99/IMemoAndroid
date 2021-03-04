package com.lnb.imemo.Presentation.MemoPreview;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

public class MemoPreviewViewModel extends ViewModel {
    private static final String TAG = "MemoPreviewViewModel";
    private final DiaryRepository diaryRepository;
    private Observer<ResponseRepo> diaryObserver;
    private User mUser;
    public Diary diary;
    private final MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();

    public MemoPreviewViewModel() {
        diaryRepository = new DiaryRepository();
        mUser = User.getUser();
        subscribeObservable();
    }

    public void getDiaryById(String id) {
        Log.d(TAG, "getDiaryById: " + id);
        diaryRepository.getDiaryById(mUser.getToken(), id);
    }

    private void subscribeObservable() {
        diaryObserver = responseRepo -> {
            String key = responseRepo.getKey();
            if (key.equals(Constant.GET_DIARY_BY_ID_KEY)) {
                Pair<Utils.State, Diary> pair = (Pair<Utils.State, Diary>) responseRepo.getData();
                ResponseRepo<Utils.State> response = new ResponseRepo<>();
                switch (pair.first) {
                    case SUCCESS:
                        diary = pair.second;
                        response.setData(Utils.State.SUCCESS);
                        break;
                    case FAILURE:
                        response.setData(Utils.State.FAILURE);
                        break;
                    case NO_INTERNET:
                        response.setData(Utils.State.NO_INTERNET);
                        break;
                }
                response.setKey(Constant.GET_DIARY_BY_ID_KEY);
                viewModelLiveData.setValue(response);
            }
        };
        diaryRepository.observableDiaryRepo().observeForever(diaryObserver);
    }

    public MediatorLiveData<ResponseRepo> getViewModelLiveData() {
        return viewModelLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        diaryRepository.observableDiaryRepo().removeObserver(diaryObserver);
    }
}
