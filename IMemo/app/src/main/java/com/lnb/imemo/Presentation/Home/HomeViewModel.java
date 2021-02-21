package com.lnb.imemo.Presentation.Home;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Data.Repository.PreviewLink.PreviewLinkRepository;
import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    private MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();
    private Observer<ResponseRepo> tagsObservable;
    private Observer<ResponseRepo> diaryObservable;
    private Boolean isGetDairyForCreate = false;
    public ArrayList<Tags> listTags = new ArrayList<>();
    public ArrayList<String> listFilterTags = new ArrayList<>();
    public HashMap<String, String> mIdTagByNameHashMap = new HashMap<>();
    public HashMap<String, Tags> mTagByNameHashMap = new HashMap<>();
    public String filterTimeName = "";
    public ArrayList<String> filterTagName = new ArrayList<>();
    private String tempEmail;
    private String tempId;

    protected User mUser;
    protected PersonProfile personProfile;
    protected List<Diary> listDiary = new ArrayList<>();
    private Boolean isUpdateForShare = false;

    public HomeViewModel() {
        tagsRepository = new TagsRepository();
        diaryRepository = new DiaryRepository();
        previewLinkRepository = new PreviewLinkRepository();
        mUser = User.getUser();
        personProfile = PersonProfile.getInstance();
        Log.d(TAG, "HomeViewModel: " + personProfile.toString());
        subscribeTagAction();
        subscribeDiary();
    }

    protected void getAllTags() {
        tagsRepository.getAllTagAction(mUser.getToken());
    }

    protected void getTagById(String id) {
        tagsRepository.getTagById(Utils.token, id);
    }

    protected void getPreviewLink(String url) {
        previewLinkRepository.getPreViewLink(url);
    }

    protected void shareDiary(String diaryId, String email) {
        ArrayList<String> emails = new ArrayList<>();
        emails.add(email);
        diaryRepository.shareDiary(mUser.getToken(), diaryId, emails);
    }

    protected void getDiaries(@Nullable String query,
                              @Nullable List<String> tags,
                              @NonNull int page,
                              @NonNull int pageSize,
                              @Nullable String fromDate,
                              @Nullable String toDate,
                              @Nullable String lastId) {
        String tagIds = null;
        if (tags != null) {
            tagIds = "";
            for (int i = 0; i < tags.size() - 1; i++) {
                tagIds = tagIds + tags.get(i) + ",";
            }
            tagIds = tagIds + tags.get(tags.size() - 1);
        }
        diaryRepository.getDiaries(mUser.getToken(), query, tagIds, page, pageSize, fromDate, toDate, lastId);
    }

    protected void createDiary(Diary diary) {
        diaryRepository.createDiary(mUser.getToken(), diary);
    }

    public void deleteDiary(int diaryPosition) {
        diaryRepository.deleteDiary(mUser.getToken(), listDiary.get(diaryPosition).getId());
    }

    public void publicDiary(Diary diary, String email) {
        isUpdateForShare = true;
        tempEmail = email;
        tempId = diary.getId();
        diaryRepository.updateDiary(mUser.getToken(), diary);
    }

    public void getDiaryById(String id) {
        diaryRepository.getDiaryById(mUser.getToken(), id);
    }


    private void subscribeTagAction() {
        tagsObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo response) {
                if (response.getKey().equals(Constant.GET_ALL_TAGS_KEY)) {
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) response.getData();
                    ResponseRepo<Pair<Utils.State, ArrayList<Tags>>> responseRepo = new ResponseRepo<>();
                    responseRepo.setKey(Constant.GET_ALL_TAGS_KEY);
                    responseRepo.setData(pair);
                    viewModelLiveData.setValue(responseRepo);
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
                if (key == Constant.GET_DIARIES_KEY) {
                    Log.d(TAG, "onChanged: " + responseRepo.getData());
                    Pair<Utils.State, List<Diary>> response = (Pair<Utils.State, List<Diary>>) responseRepo.getData();
                    listDiary = response.second;
                    getAllMemoLiveData.setValue(response.first);
                } else if (key == Constant.DELETE_DIARY_KEY) {
                    deleteDiaryLiveData.setValue((Utils.State) responseRepo.getData());
                } else if (key == Constant.CREATE_DIARY_KEY) {
                    Pair<Utils.State, JsonObject> pair = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    ResponseRepo<Pair<Utils.State, Diary>> response = new ResponseRepo<>();
                    switch (pair.first) {
                        case SUCCESS:
                            Log.d(TAG, "onChanged: " + pair.second.toString());
                            String id = pair.second.get("id").getAsString();
                            isGetDairyForCreate = true;
                            getDiaryById(id);
                            break;
                        case FAILURE:
                            response.setData(new Pair<>(Utils.State.FAILURE, null));
                            response.setKey(Constant.CREATE_DIARY_KEY);
                            viewModelLiveData.setValue(response);
                            break;
                        case NO_INTERNET:
                            response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                            response.setKey(Constant.CREATE_DIARY_KEY);
                            viewModelLiveData.setValue(response);
                            break;
                    }
                } else if (key == Constant.GET_DIARY_BY_ID_KEY) {
                    Pair<Utils.State, Diary> pair = (Pair<Utils.State, Diary>) responseRepo.getData();
                    ResponseRepo<Pair<Utils.State, Diary>> response = new ResponseRepo<>();
                    response.setKey(Constant.GET_DIARY_BY_ID_KEY);
                    switch (pair.first) {
                        case SUCCESS:
                            isGetDairyForCreate = false;
                            response.setKey(Constant.CREATE_DIARY_KEY);
                            break;
                    }
                    response.setData(pair);
                    viewModelLiveData.setValue(response);
                } else if (key == Constant.SHARE_DIARY) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    ResponseRepo<Utils.State> response = new ResponseRepo<>();
                    response.setData(state);
                    response.setKey(Constant.SHARE_DIARY);
                    viewModelLiveData.setValue(response);
                } else if (key == Constant.PUBLIC_DIARY) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    if (state == Utils.State.SUCCESS) {
                        shareDiary(tempId, tempEmail);
                    }
                } else if (key == Constant.UPDATE_DIARY_KEY) {
                    Pair<Utils.State, JsonObject> pair = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            if (isUpdateForShare) {
                                shareDiary(tempId, tempEmail);
                            }
                            break;
                    }
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

    public MediatorLiveData<ResponseRepo> getViewModelLiveData() {
        return viewModelLiveData;
    }

    public MediatorLiveData<Utils.State> observableDeleteDiary() {
        return deleteDiaryLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tagsRepository.observableTagLiveData().removeObserver(tagsObservable);
        diaryRepository.observableDiaryRepo().removeObserver(diaryObservable);

    }


}
