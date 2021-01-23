package com.lnb.imemo.Presentation.Home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.FileUtils;
import android.util.Base64;
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
import com.lnb.imemo.Data.Entity.ResponseRepo;
import com.lnb.imemo.Data.Repository.UploadFile.UploadFileRepository;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private TagsRepository tagsRepository;
    private DiaryRepository diaryRepository;
    private PreviewLinkRepository previewLinkRepository;
    private MediatorLiveData<ArrayList<Tags>> allTagsLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Tags> tagLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> updateTagLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> deleteTagLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Utils.State> createTagLiveData = new MediatorLiveData<>();
    private Observer<ResponseRepo> tagsObservable;
    private Observer<ResponseRepo> diaryObservable;
    private Observer<ResponseRepo> previewLinkObservable;
    private Context mContext;

    public HomeViewModel(Context mContext) {
        this.mContext = mContext;
        tagsRepository = new TagsRepository();
        diaryRepository = new DiaryRepository();
        previewLinkRepository = new PreviewLinkRepository();
        subscribeTagAction();
        subscribeDiary();
        subscribePreviewLink();
    }

    protected void getAllTags() {
        tagsRepository.getAllTagAction(Utils.token);
    }

    protected void getTagById(String id) {
        tagsRepository.getTagById(Utils.token, id);
    }

    protected void updateTags(String id, String name, String colorHex, Boolean isDefault) {
        tagsRepository.updateTags(Utils.token, id, name, colorHex, isDefault);
    }

    protected void deleteTags(String id) {
        tagsRepository.deleteTags(Utils.token, id);
    }

    protected void createTags(String name, String color, Boolean isDefault) {
        tagsRepository.createTags(Utils.token, name, color, isDefault);
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
        diaryRepository.getDiaries(Utils.token, query, tags, page, pageSize, fromDate, toDate, lastId);
    }

    protected void createDiary(Diary diary) {
        diaryRepository.createDiary(Utils.token, diary);
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
                Log.d(TAG, "onChanged: " + responseRepo.getData());
            }
        };
        diaryRepository.observableDiaryRepo().observeForever(diaryObservable);
    }

    private void subscribePreviewLink() {
        previewLinkObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                Log.d(TAG, "onChanged: " + responseRepo.getData().toString());
            }
        };
        previewLinkRepository.observablePreviewLink().observeForever(previewLinkObservable);
    }

    public MediatorLiveData<ArrayList<Tags>> observableAllTags() {
        return allTagsLiveData;
    }

    public MediatorLiveData<Tags> observableTag() {
        return tagLiveData;
    }

    public MediatorLiveData<Utils.State> observableUpdateTags() { return updateTagLiveData; }

    public MediatorLiveData<Utils.State> observableDeleteTags() { return deleteTagLiveData; }

    @Override
    protected void onCleared() {
        super.onCleared();
        tagsRepository.observableTagLiveData().removeObserver(tagsObservable);
        diaryRepository.observableDiaryRepo().removeObserver(diaryObservable);
        previewLinkRepository.observablePreviewLink().removeObserver(previewLinkObservable);
    }
}
