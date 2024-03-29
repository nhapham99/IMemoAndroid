package com.lnb.imemo.Data.Repository.Diary;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Data.Repository.Model.ResultSharedDiaries;
import com.lnb.imemo.Data.Repository.Model.ResultSharedUser;
import com.lnb.imemo.Data.Repository.Model.SharedUser;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Data.Repository.Model.Root;
import com.lnb.imemo.Data.Repository.Model.ResultDiaries;
import com.lnb.imemo.Data.Repository.Model.ResultDiary;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DiaryRepository {
    private static final String TAG = "DiaryRepository";
    private final DiaryAPI diaryAPI;
    private final MediatorLiveData<ResponseRepo> diaryRepoLiveData;
    private final Gson gsonBuilder;

    public DiaryRepository() {
        Retrofit retrofit = APIClient.getInstance();
        diaryAPI = retrofit.create(DiaryAPI.class);
        diaryRepoLiveData = new MediatorLiveData<>();
        gsonBuilder = new GsonBuilder().create();
    }

    public void getDiaries(@NonNull String token,
                           @Nullable String query,
                           @Nullable String tag,
                           int page,
                           int pageSize,
                           @Nullable String fromDate,
                           @Nullable String toDate,
                           @Nullable String lastId,
                           @Nullable Boolean pinned) {
        LiveData<Root<ResultDiaries>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.getDiaries(token, query, tag, page, pageSize, fromDate, toDate, lastId, pinned)
                        .onErrorReturn(throwable -> {
                            String message = throwable.getMessage();
                            Root<ResultDiaries> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, roots -> {
            ResponseRepo<Pair<Utils.State, ResultDiaries>> response = new ResponseRepo<>();
            if (roots.getStatusCode() == 0) {
                response.setData(new Pair<>(Utils.State.SUCCESS, roots.getResult()));
            } else if (roots.getStatusCode() == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            response.setKey(Constant.GET_DIARIES_KEY);
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void createDiary(@NonNull String token, @NonNull Diary<String> diary) {
        String jsonString = gsonBuilder.toJson(diary);
        JsonObject body = new JsonParser().parse(jsonString).getAsJsonObject();
        body.remove("isUploading");
        body.remove("tags");

        LiveData<Root<JsonObject>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.createDiary(token, body)
                        .onErrorReturn(throwable -> {
                            String message = throwable.getMessage();
                            Root<JsonObject> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Pair<Utils.State, JsonObject>> responseRepo = new ResponseRepo<>();
            responseRepo.setKey(Constant.CREATE_DIARY_KEY);
            if (root.getStatusCode() == 0) {
                responseRepo.setData(new Pair<>(Utils.State.SUCCESS, root.getResult()));
            } else if (root.getStatusCode() == -1) {
                responseRepo.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                responseRepo.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            diaryRepoLiveData.setValue(responseRepo);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void getDiaryById(@NonNull String token, @NonNull String id) {
        LiveData<Root<ResultDiary>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.getDiaryById(token, id)
                        .onErrorReturn(throwable -> {
                            String message = throwable.getMessage();
                            Root<ResultDiary> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Pair<Utils.State, Diary<PersonProfile>>> response = new ResponseRepo<>();
            Log.d(TAG, "getDiaryById: " + root);
            if (root.getStatusCode() == 0) {
                Diary<PersonProfile> diary = root.getResult().getDiary();
                response.setData(new Pair<>(Utils.State.SUCCESS, diary));
            } else if (root.getStatusCode() == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            response.setKey(Constant.GET_DIARY_BY_ID_KEY);
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void deleteDiary(@NonNull String token, @NonNull String id) {
        LiveData<Root<JsonObject>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.deleteDiary(token, id)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
                            String message = throwable.getMessage();
                            Root<JsonObject> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );
        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            Log.d(TAG, "onChanged: " + root.toString());
            if (root.getStatusCode() == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (root.getStatusCode() == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.DELETE_DIARY_KEY);
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void shareDiary(String token, String diaryId, ArrayList<String> emails) {
        JsonObject body = new JsonObject();
        body.addProperty("id", diaryId);
        JsonArray jsonElements = new JsonArray();
        for (String email : emails) {
            jsonElements.add(email);
        }
        body.add("emails", jsonElements);
        Log.d(TAG, "shareDiary: " + body.toString());

        LiveData<Root<JsonObject>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.shareDiary(token, body)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
                            String message = throwable.getMessage();
                            Root<JsonObject> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            Log.d(TAG, "onChanged: " + root.toString());
            if (root.getStatusCode() == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (root.getStatusCode() == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.SHARE_DIARY);
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void getSharedUser(@NonNull String token, @NonNull String id) {
        LiveData<Root<ResultSharedUser>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.getSharedUser(token, id)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
                            String message = throwable.getMessage();
                            Root<ResultSharedUser> root = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                root.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                root.setStatusCode(-1);
                            } else {
                                root.setStatusCode(-2);
                            }
                            return root;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Pair<Utils.State, List<SharedUser>>> response = new ResponseRepo<>();
            Log.d(TAG, "onChanged: " + root.toString());
            if (root.getStatusCode() == 0) {
                response.setData(new Pair<>(Utils.State.SUCCESS, root.getResult().getUsers()));
            } else if (root.getStatusCode() == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            response.setKey(Constant.GET_SHARED_USERS);
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });

    }

    public void publicDiary(@NonNull String token, @NonNull String id) {
        LiveData<Root<JsonObject>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.publicDiary(token, id)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
                            String message = throwable.getMessage();
                            Root<JsonObject> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );
        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            Log.d(TAG, "onChanged: " + root.toString());
            if (root.getStatusCode() == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (root.getStatusCode() == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.PUBLIC_DIARY);
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void updateDiary(String token, Diary<String> diary) {
        ArrayList<Tags> listTags = diary.getTags();
        ArrayList<String> tagIds = new ArrayList<>();
        for (Tags tag : listTags) {
            tagIds.add(tag.getId());
        }
        diary.setTagIds(tagIds);
        String jsonString = gsonBuilder.toJson(diary);
        JsonObject body = new JsonParser().parse(jsonString).getAsJsonObject();
        body.remove("isUploading");
        body.remove("tags");
        Log.d(TAG, "createDiary: " + body.toString());

        LiveData<Root<JsonObject>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.updateDiary(token, diary.getId(), body)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
                            String message = throwable.getMessage();
                            Root<JsonObject> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, root -> {
            ResponseRepo<Pair<Utils.State, JsonObject>> responseRepo = new ResponseRepo<>();
            responseRepo.setKey(Constant.UPDATE_DIARY_KEY);
            if (root.getStatusCode() == 0) {
                responseRepo.setData(new Pair<>(Utils.State.SUCCESS, root.getResult()));
            } else if (root.getStatusCode() == -1) {
                responseRepo.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                responseRepo.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            diaryRepoLiveData.setValue(responseRepo);
            diaryRepoLiveData.removeSource(source);
        });
    }

    public void getDiariesSharedWithMe(@NonNull String token,
                                       @Nullable String query,
                                       @Nullable String tag,
                                       int page,
                                       int pageSize,
                                       @Nullable String fromDate,
                                       @Nullable String toDate,
                                       @Nullable String lastId,
                                       @Nullable Boolean pinned) {
        LiveData<Root<ResultSharedDiaries>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.getDiariesSharedWithMe(token, query, tag, page, pageSize, fromDate, toDate, lastId, pinned)
                        .onErrorReturn(throwable -> {
                            String message = throwable.getMessage();
                            Root<ResultSharedDiaries> diaryRoot = new Root<>();
                            if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                diaryRoot.setStatusCode(409);
                            } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                diaryRoot.setStatusCode(-1);
                            } else {
                                diaryRoot.setStatusCode(-2);
                            }
                            return diaryRoot;
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, roots -> {
            ResponseRepo<Pair<Utils.State, ResultSharedDiaries>> response = new ResponseRepo<>();
            if (roots.getStatusCode() == 0) {
                response.setData(new Pair<>(Utils.State.SUCCESS, roots.getResult()));
            } else if (roots.getStatusCode() == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            response.setKey(Constant.GET_DIARIES_SHARED_WITH_ME );
            diaryRepoLiveData.setValue(response);
            diaryRepoLiveData.removeSource(source);
        });
    }


    public MediatorLiveData<ResponseRepo> observableDiaryRepo() {
        return diaryRepoLiveData;
    }

}
