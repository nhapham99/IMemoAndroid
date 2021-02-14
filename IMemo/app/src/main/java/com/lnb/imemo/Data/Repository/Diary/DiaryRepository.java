package com.lnb.imemo.Data.Repository.Diary;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Root;
import com.lnb.imemo.Model.ResultDiaries;
import com.lnb.imemo.Model.ResultDiary;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.List;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DiaryRepository {
    private static final String TAG = "DiaryRepository";
    private Retrofit retrofit;
    private DiaryAPI diaryAPI;
    private MediatorLiveData<ResponseRepo> diaryRepoLiveData;
    private Gson gsonBuilder;

    public DiaryRepository() {
        retrofit = APIClient.getInstance();
        diaryAPI = retrofit.create(DiaryAPI.class);
        diaryRepoLiveData = new MediatorLiveData<>();
        gsonBuilder = new GsonBuilder().create();
    }

    public void getDiaries(@NonNull String token,
                           @Nullable String query,
                           @Nullable List<String> tags,
                           @NonNull int page,
                           @NonNull int pageSize,
                           @Nullable String fromDate,
                           @Nullable String toDate,
                           @Nullable String lastId) {
        LiveData<Root<ResultDiaries>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.getDiaries(token, query, tags, page, pageSize, fromDate, toDate, lastId)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
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
            ResponseRepo<Pair<Utils.State, List<Diary>>> response = new ResponseRepo<>();
            if (roots.getStatusCode() == 0) {
                List<Diary> diaries = roots.getResult().getDiaries();
                response.setData(new Pair<>(Utils.State.SUCCESS, diaries));
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

    public void createDiary(@NonNull String token, @NonNull Diary diary) {
        String jsonString = gsonBuilder.toJson(diary);
        JsonObject body = new JsonParser().parse(jsonString).getAsJsonObject();
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.createDiary(token, body)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
                            return new JsonObject();
                        })
                        .subscribeOn(Schedulers.io())
        );

        diaryRepoLiveData.addSource(source, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                Log.d(TAG, "onChanged: " + jsonObject.toString());
            }
        });
        diaryRepoLiveData.removeSource(source);
    }

    public void getDiaryById(@NonNull String token, @NonNull String id) {
        LiveData<Root<ResultDiary>> source = LiveDataReactiveStreams.fromPublisher(
                diaryAPI.getDiaryById(token, id)
                        .onErrorReturn(throwable -> {
                            Log.d(TAG, "apply: " + throwable.getMessage());
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
            ResponseRepo<Pair<Utils.State, Diary>> response = new ResponseRepo<>();
            if (root.getStatusCode() == 0) {
                Diary diary = root.getResult().getDiary();
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
        diaryRepoLiveData.addSource(source, new Observer<Root<JsonObject>>() {
            @Override
            public void onChanged(Root<JsonObject> root) {
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
            }
        });
    }


    public MediatorLiveData<ResponseRepo> observableDiaryRepo() {
        return diaryRepoLiveData;
    }

}
