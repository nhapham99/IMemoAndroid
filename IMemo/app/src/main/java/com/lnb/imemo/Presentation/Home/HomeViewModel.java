package com.lnb.imemo.Presentation.Home;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Data.Repository.PreviewLink.PreviewLinkRepository;
import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private TagsRepository tagsRepository;
    private DiaryRepository diaryRepository;
    private PreviewLinkRepository previewLinkRepository;
    private MediatorLiveData<ArrayList<Tags>> allTagsLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Tags> tagLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> updateTagLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> deleteTagLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> getAllMemoLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> deleteDiaryLiveData = new MediatorLiveData<>();
    private Observer<ResponseRepo> tagsObservable;
    private Observer<ResponseRepo> diaryObservable;

    protected User mUser;
    protected static List<Diary> listDiary = new ArrayList<>();

    public HomeViewModel() {
        tagsRepository = new TagsRepository();
        diaryRepository = new DiaryRepository();
        previewLinkRepository = new PreviewLinkRepository();
        mUser = User.getUser();
        subscribeTagAction();
        subscribeDiary();
    }

    protected void getAllTags() {
        tagsRepository.getAllTagAction(Utils.token);
    }

    protected void getTagById(String id) {
        tagsRepository.getTagById(Utils.token, id);
    }

    protected void getPreviewLink(String url) {
        previewLinkRepository.getPreViewLink(url);
    }

    protected void getDiaries(@Nullable String query,
                              @Nullable List<String> tags,
                              @NonNull int page,
                              @NonNull int pageSize,
                              @Nullable String fromDate,
                              @Nullable String toDate,
                              @Nullable String lastId) {
        Log.d(TAG, "getDiaries: " + mUser.getToken());
        diaryRepository.getDiaries(mUser.getToken(), query, tags, page, pageSize, fromDate, toDate, lastId);
    }

    protected void createDiary(Diary diary) {
        diaryRepository.createDiary(Utils.token, diary);
    }

    public void deleteDiary(int diaryPosition) {
        diaryRepository.deleteDiary(mUser.getToken(), listDiary.get(diaryPosition).getId());
    }


    private void subscribeTagAction() {
        tagsObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                if (response.getKey().equals(Constant.GET_ALL_TAGS_KEY)) {
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);
                    allTagsLiveData.setValue(pair.second);
                } else if (response.getKey().equals(Constant.GET_TAGS_BY_ID_KEY)) {
                    Pair<Utils.State, Tags> pair = (Pair<Utils.State, Tags>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);
                    tagLiveData.setValue(pair.second);
                } else if (response.getKey().equals(Constant.UPDATE_TAGS_KEY)) {
                    updateTagLiveData.setValue((Utils.State) response.getData());
                } else if (response.getKey().equals(Constant.DELETE_TAG_KEY)) {
                    deleteTagLiveData.setValue((Utils.State) response.getData());
                } else if (response.getKey().equals(Constant.CREATE_TAG_KEY)) {
                    Pair<Utils.State, String> pair = (Pair<Utils.State, String>) response.getData();
                    Log.d(TAG, "onChanged: " + pair.first);
                    Log.d(TAG, "onChanged: " + pair.second);
                }
            }
        };
        tagsRepository.observableTagLiveData().observeForever(tagsObservable);
    }

    private void subscribeDiary() {
        diaryObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_DIARIES_KEY)) {
                    Log.d(TAG, "onChanged: " + responseRepo.getData());
                    Pair<Utils.State, List<Diary>> response = (Pair<Utils.State, List<Diary>>) responseRepo.getData();
                    listDiary = response.second;
                    getAllMemoLiveData.setValue(response.first);
                } else if (key.equals(Constant.DELETE_DIARY_KEY)) {
                    deleteDiaryLiveData.setValue((Utils.State) responseRepo.getData());
                }
            }
        };
        diaryRepository.observableDiaryRepo().observeForever(diaryObservable);
    }



    public MediatorLiveData<ArrayList<Tags>> observableAllTags() {
        return allTagsLiveData;
    }

    public MediatorLiveData<Tags> observableTag() {
        return tagLiveData;
    }

    public MediatorLiveData<Utils.State> observableGetAllDiary() {
        return getAllMemoLiveData;
    }

    public MediatorLiveData<Utils.State> observableDeleteDiary() { return deleteDiaryLiveData; }

    @Override
    protected void onCleared() {
        super.onCleared();
        tagsRepository.observableTagLiveData().removeObserver(tagsObservable);
        diaryRepository.observableDiaryRepo().removeObserver(diaryObservable);

    }
}
