package com.lnb.imemo.Presentation.Home;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.Repository.Auth.AuthRepository;
import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Data.Repository.Model.ResultDiaries;
import com.lnb.imemo.Data.Repository.PreviewLink.PreviewLinkRepository;
import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Model.Pagination;
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

import io.reactivex.subjects.PublishSubject;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final TagsRepository tagsRepository;
    private final DiaryRepository diaryRepository;
    private final PreviewLinkRepository previewLinkRepository;
    private final AuthRepository authRepository;
    private final MediatorLiveData<ArrayList<Tags>> allTagsLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Tags> tagLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Utils.State> updateTagLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Utils.State> deleteTagLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Utils.State> getAllMemoLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Utils.State> deleteDiaryLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<ResponseRepo> viewModelLiveData = new MediatorLiveData<>();
    private Observer<ResponseRepo> authObservable;
    private Observer<ResponseRepo> tagsObservable;
    private Observer<ResponseRepo> diaryObservable;
    private Boolean isGetDairyForCreate = false;
    public ArrayList<Tags> listTags = new ArrayList<>();
    public ArrayList<String> listFilterTags = new ArrayList<>();
    public HashMap<String, String> mIdTagByNameHashMap = new HashMap<>();
    public HashMap<String, Tags> mTagByNameHashMap = new HashMap<>();
    public String filterTimeName = "";
    public String filterHighLight = "";
    public ArrayList<String> filterTagName = new ArrayList<>();
    private static HomeViewModel mInstance;
    public Boolean getTotalMemoInStart = true;
    private int totalMemo;
    private int totalFilterMemo;

    protected User mUser;
    protected PersonProfile personProfile;
    protected ArrayList<Diary> listDiary = new ArrayList<>();
    private Boolean isUpdateForPublic = null;
    public ArrayList<String> listSharedEmail = new ArrayList<>();
    public PublishSubject<ArrayList<String>> sharedEmailPublishSubject = PublishSubject.create();
    private Boolean isUpdateForPinMemo = null;


    private HomeViewModel() {
        tagsRepository = new TagsRepository();
        diaryRepository = new DiaryRepository();
        previewLinkRepository = new PreviewLinkRepository();
        authRepository = new AuthRepository();
        mUser = User.getUser();
        personProfile = PersonProfile.getInstance();
        subscribeTagAction();
        subscribeDiary();
        subscribeAuthObservable();
    }

    public static HomeViewModel getHomeViewModel(Boolean isStart) {
        if (mInstance == null || isStart) {
            mInstance = new HomeViewModel();
        }
        return mInstance;
    }

    protected void getAllTags() {
        tagsRepository.getAllTagAction(mUser.getToken());
    }

    protected void getTagById(String id) {
        tagsRepository.getTagById(mUser.getToken(), id);
    }

    protected void getPreviewLink(String url) {
        previewLinkRepository.getPreViewLink(url);
    }

    public void getSharedUser() {
        
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
                              @Nullable String lastId,
                              @Nullable Boolean pinned) {
        String tagIds = null;
        if (tags != null) {
            tagIds = "";
            for (int i = 0; i < tags.size() - 1; i++) {
                tagIds = tagIds + tags.get(i) + ",";
            }
            tagIds = tagIds + tags.get(tags.size() - 1);
        }
        diaryRepository.getDiaries(mUser.getToken(), query, tagIds, page, pageSize, fromDate, toDate, lastId, pinned);
    }

    protected void getDiariesSharedWithMe(@Nullable String query,
                              @Nullable List<String> tags,
                              @NonNull int page,
                              @NonNull int pageSize,
                              @Nullable String fromDate,
                              @Nullable String toDate,
                              @Nullable String lastId,
                              @Nullable Boolean pinned) {
        String tagIds = null;
        if (tags != null) {
            tagIds = "";
            for (int i = 0; i < tags.size() - 1; i++) {
                tagIds = tagIds + tags.get(i) + ",";
            }
            tagIds = tagIds + tags.get(tags.size() - 1);
        }
        diaryRepository.getDiariesSharedWithMe(mUser.getToken(), query, tagIds, page, pageSize, fromDate, toDate, lastId, pinned);
    }

    protected void createDiary(Diary diary) {
        diaryRepository.createDiary(mUser.getToken(), diary);
    }

    public void deleteDiary(int diaryPosition) {
        diaryRepository.deleteDiary(mUser.getToken(), listDiary.get(diaryPosition).getId());
    }

    public void publicDiary(Diary diary) {
        isUpdateForPublic = true;
        diary.setStatus("public");
        diaryRepository.updateDiary(mUser.getToken(), diary);
    }

    public void privateDiary(Diary diary) {
        isUpdateForPublic = false;
        diary.setStatus("private");
        diaryRepository.updateDiary(mUser.getToken(), diary);
    }

    public void pinDiary(int position) {
        isUpdateForPinMemo = true;
        Diary diary = listDiary.get(position);
        diary.setPinned(true);
        diaryRepository.updateDiary(mUser.getToken(), diary);
    }

    public void unpinDiary(int position) {
        isUpdateForPinMemo = false;
        Diary diary = listDiary.get(position);
        diary.setPinned(false);
        diaryRepository.updateDiary(mUser.getToken(), diary);
    }



    public void getDiaryById(String id) {
        diaryRepository.getDiaryById(mUser.getToken(), id);
    }

    public void getSharedEmails() {
        authRepository.getAllSharedEmails(mUser.getToken());
    }

    private void subscribeAuthObservable() {
        authObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_SHARED_EMAILS)) {
                    ResponseRepo<ArrayList<String>> response = new ResponseRepo<>();
                    listSharedEmail = (ArrayList<String>) responseRepo.getData();
                    sharedEmailPublishSubject.onNext(listSharedEmail);
                }
            }
        };
        authRepository.observableAuthRepo().observeForever(authObservable);
    }


    private void subscribeTagAction() {
        tagsObservable = response -> {
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
        };
        tagsRepository.observableTagLiveData().observeForever(tagsObservable);
    }

    private void subscribeDiary() {
        diaryObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_DIARIES_KEY)) {
                    ResponseRepo<Pair<Utils.State, ArrayList<Diary>>> response = new ResponseRepo<>();
                    Pair<Utils.State, ResultDiaries> pair = (Pair<Utils.State, ResultDiaries>) responseRepo.getData();
                    Pagination pagination = pair.second.getPagination();
                    totalFilterMemo = pagination.getTotalItems();
                    if (getTotalMemoInStart) {
                        totalMemo = pagination.getTotalItems();
                        getTotalMemoInStart = false;
                    }
                    response.setData(new Pair<>(pair.first, (ArrayList<Diary>) pair.second.getDiaries()));
                    response.setKey(Constant.GET_DIARIES_KEY);
                    viewModelLiveData.setValue(response);
                } else if (key.equals(Constant.DELETE_DIARY_KEY)) {
                    deleteDiaryLiveData.setValue((Utils.State) responseRepo.getData());
                } else if (key.equals(Constant.CREATE_DIARY_KEY)) {
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
                } else if (key.equals(Constant.GET_DIARY_BY_ID_KEY)) {
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
                } else if (key.equals(Constant.SHARE_DIARY)) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    ResponseRepo<Utils.State> response = new ResponseRepo<>();
                    response.setData(state);
                    response.setKey(Constant.SHARE_DIARY);
                    viewModelLiveData.setValue(response);
                } else if (key.equals(Constant.UPDATE_DIARY_KEY)) {
                    Pair<Utils.State, JsonObject> pair = (Pair<Utils.State, JsonObject>) responseRepo.getData();
                    ResponseRepo<Utils.State> response = new ResponseRepo<>();
                    if (isUpdateForPublic != null) {
                        if (isUpdateForPublic) {
                            response.setKey("public_diary");
                        } else {
                            response.setKey("private_diary");
                        }
                    }

                    if (isUpdateForPinMemo != null) {
                        if (isUpdateForPinMemo) {
                            response.setKey("pin_diary");
                        } else {
                            response.setKey("unpin_diary");
                        }
                    }
                    response.setData(pair.first);
                    viewModelLiveData.setValue(response);
                } else if (key.equals(Constant.GET_DIARIES_SHARED_WITH_ME)) {
                    ResponseRepo<Pair<Utils.State, ArrayList<Diary>>> response = new ResponseRepo<>();
                    Pair<Utils.State, ResultDiaries> pair = (Pair<Utils.State, ResultDiaries>) responseRepo.getData();
                    Pagination pagination = pair.second.getPagination();
                    totalFilterMemo = pagination.getTotalItems();
                    if (getTotalMemoInStart) {
                        totalMemo = pagination.getTotalItems();
                        getTotalMemoInStart = false;
                    }
                    response.setData(new Pair<>(pair.first, (ArrayList<Diary>) pair.second.getDiaries()));
                    response.setKey(Constant.GET_DIARIES_SHARED_WITH_ME);
                    viewModelLiveData.setValue(response);
                }
            }
        };
        diaryRepository.observableDiaryRepo().observeForever(diaryObservable);
    }


    public int getTotalMemo() {
        return totalMemo;
    }

    public int getTotalFilterMemo() {
        return totalFilterMemo;
    }

    public void setTotalMemo(int totalMemo) {
        this.totalMemo = totalMemo;
    }

    public void setTotalFilterMemo(int totalFilterMemo) {
        this.totalFilterMemo = totalFilterMemo;
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
        authRepository.observableAuthRepo().removeObserver(authObservable);
    }


}
