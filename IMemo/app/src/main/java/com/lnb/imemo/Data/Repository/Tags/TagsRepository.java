package com.lnb.imemo.Data.Repository.Tags;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Data.Repository.Model.ResultTags;
import com.lnb.imemo.Data.Repository.Model.Root;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TagsRepository {
    private static final String TAG = "TagsRepository";
    private Retrofit retrofit;
    private TagAPI tagAPI;
    private MediatorLiveData<ResponseRepo> tagRepoLiveData;
    private final Gson gsonBuilder;

    public TagsRepository() {
        retrofit = APIClient.getInstance();
        tagAPI = retrofit.create(TagAPI.class);
        tagRepoLiveData = new MediatorLiveData<>();
        gsonBuilder = new GsonBuilder().create();
    }

    public void getAllTagAction(String token) {
        LiveData<Root<ResultTags>> source = LiveDataReactiveStreams.fromPublisher(
                tagAPI.getAllTag(token)
                        .onErrorReturn(new Function<Throwable, Root<ResultTags>>() {
                            @Override
                            public Root<ResultTags> apply(@NonNull Throwable throwable) throws Exception {
                                String message = throwable.getMessage();
                                Root<ResultTags> root = new Root<>();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    root.setStatusCode(409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    root.setStatusCode(-1);
                                } else {
                                    root.setStatusCode(-2);
                                }
                                return root;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        tagRepoLiveData.addSource(source, new Observer<Root<ResultTags>>() {
            @Override
            public void onChanged(Root<ResultTags> listRoot) {
                int statusCode = listRoot.getStatusCode();
                ResponseRepo<Pair<Utils.State, ArrayList<Tags>>> response = new ResponseRepo<>();
                if (statusCode == 0) {
                    ResultTags resultTags = (ResultTags) listRoot.getResult();
                    ArrayList<Tags> listTags = (ArrayList<Tags>) resultTags.getTags();
                    response.setData(new Pair<>(Utils.State.SUCCESS, listTags));
                } else if (statusCode == -1) {
                    response.setData(new Pair<>(Utils.State.NO_INTERNET, new ArrayList<>()));
                } else {
                    response.setData(new Pair<>(Utils.State.FAILURE, new ArrayList<>()));
                }
                response.setKey(Constant.GET_ALL_TAGS_KEY);
                tagRepoLiveData.setValue(response);
                tagRepoLiveData.removeSource(source);
            }
        });

    }

    public void getTagById(String token, String id) {
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                tagAPI.getTagById(token, id)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        tagRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Pair<Utils.State, Tags>> response = new ResponseRepo<>();
            if (statusCode == 0) {
                JsonObject TagJsonObject = jsonObject.getAsJsonObject(Constant.RESULT).getAsJsonObject(Constant.TAG);
                Tags mTag = gsonBuilder.fromJson(TagJsonObject.toString(), Tags.class);
                response.setData(new Pair<>(Utils.State.SUCCESS, mTag));
            } else if (statusCode == -1) {
                response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
            } else {
                response.setData(new Pair<>(Utils.State.FAILURE, null));
            }
            response.setKey(Constant.GET_TAGS_BY_ID_KEY);
            tagRepoLiveData.setValue(response);
            tagRepoLiveData.removeSource(source);
        });
    }

    public void updateTags(String token, Tags tag) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.NAME, tag.getName());
        body.addProperty(Constant.COLOR_HEX, tag.getColor());
        body.addProperty(Constant.IS_DEFAULT, tag.getIsDefault());
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                tagAPI.updateTag(token, tag.getId(), body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        tagRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            if (statusCode == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (statusCode == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.UPDATE_TAGS_KEY);
            tagRepoLiveData.setValue(response);
        });
    }

    public void deleteTags(String token, String id) {
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                tagAPI.deleteTag(token, id)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        tagRepoLiveData.addSource(source, jsonObject -> {
            int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
            ResponseRepo<Utils.State> response = new ResponseRepo<>();
            if (statusCode == 0) {
                response.setData(Utils.State.SUCCESS);
            } else if (statusCode == -1) {
                response.setData(Utils.State.NO_INTERNET);
            } else {
                response.setData(Utils.State.FAILURE);
            }
            response.setKey(Constant.DELETE_TAG_KEY);
            tagRepoLiveData.setValue(response);
        });
    }

    public void createTags(String token, Tags tag) {
        JsonObject body = new JsonObject();
        body.addProperty(Constant.NAME, tag.getName());
        body.addProperty(Constant.COLOR_HEX, tag.getColor());
        body.addProperty(Constant.IS_DEFAULT, tag.getIsDefault());
        LiveData<JsonObject> source = LiveDataReactiveStreams.fromPublisher(
                tagAPI.createTag(token, body)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                String message = throwable.getMessage();
                                JsonObject jsonObject = new JsonObject();
                                if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, 409);
                                } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -1);
                                } else {
                                    jsonObject.addProperty(Constant.STATUS_CODE, -2);
                                }
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        tagRepoLiveData.addSource(source, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                int statusCode = jsonObject.get(Constant.STATUS_CODE).getAsInt();
                ResponseRepo<Pair<Utils.State, String>> response = new ResponseRepo<>();
                if (statusCode == 0) {
                    String tagId = jsonObject.getAsJsonObject(Constant.RESULT)
                            .get(Constant.ID)
                            .toString();
                    response.setData(new Pair<>(Utils.State.SUCCESS, tagId));
                } else if (statusCode == -1) {
                    response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                } else {
                    response.setData(new Pair<>(Utils.State.FAILURE, null));
                }
                response.setKey(Constant.CREATE_TAG_KEY);
                tagRepoLiveData.setValue(response);
                tagRepoLiveData.removeSource(source);
            }
        });

    }

    public MediatorLiveData<ResponseRepo> observableTagLiveData() {
        return tagRepoLiveData;
    }
}
