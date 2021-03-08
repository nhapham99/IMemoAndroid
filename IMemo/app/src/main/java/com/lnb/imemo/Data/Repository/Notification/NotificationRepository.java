package com.lnb.imemo.Data.Repository.Notification;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Data.Repository.Model.ResultNotification;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    private Retrofit retrofit;
    private NotificationAPI notificationAPI;
    private MediatorLiveData<ResponseRepo> notificationLiveData;

    public NotificationRepository() {
        retrofit = APIClient.getInstance();
        notificationAPI = retrofit.create(NotificationAPI.class);
        notificationLiveData = new MediatorLiveData<>();
    }

    public void getAllNotification(String token, int page) {
        LiveData<ResultNotification> source = LiveDataReactiveStreams.fromPublisher(
                notificationAPI.getAllNotification(token, page, Constant.pageSize)
                        .onErrorReturn(new Function<Throwable, ResultNotification>() {
                            @Override
                            public ResultNotification apply(@NonNull Throwable throwable) throws Exception {
                                ResultNotification root = new ResultNotification();
                                return root;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        notificationLiveData.addSource(source, new Observer<ResultNotification>() {
            @Override
            public void onChanged(ResultNotification resultNotification) {
                ResponseRepo<ResultNotification> responseRepo = new ResponseRepo<>();
                responseRepo.setKey(Constant.GET_ALL_NOTIFICATION);
                responseRepo.setData(resultNotification);
                notificationLiveData.setValue(responseRepo);
                notificationLiveData.removeSource(source);
            }
        });
    }

    public void getAllNotificationForCount(String token, int page) {
        LiveData<ResultNotification> source = LiveDataReactiveStreams.fromPublisher(
                notificationAPI.getAllNotification(token, page, Constant.pageSize)
                        .onErrorReturn(new Function<Throwable, ResultNotification>() {
                            @Override
                            public ResultNotification apply(@NonNull Throwable throwable) throws Exception {
                                ResultNotification root = new ResultNotification();
                                return root;
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );

        notificationLiveData.addSource(source, new Observer<ResultNotification>() {
            @Override
            public void onChanged(ResultNotification resultNotification) {
                ResponseRepo<ResultNotification> responseRepo = new ResponseRepo<>();
                responseRepo.setKey(Constant.GET_ALL_NOTIFICATION_FOR_COUNT);
                responseRepo.setData(resultNotification);
                notificationLiveData.setValue(responseRepo);
                notificationLiveData.removeSource(source);
            }
        });
    }

    public void markReadNotification(String token, String notificationId) {
        LiveData<JsonObject> source = LiveDataReactiveStreams
                .fromPublisher(notificationAPI.markReadNotification(token, notificationId)
                        .onErrorReturn(new Function<Throwable, JsonObject>() {
                            @Override
                            public JsonObject apply(@NonNull Throwable throwable) throws Exception {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("ok", false);
                                return jsonObject;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                );

        notificationLiveData.addSource(source, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                ResponseRepo<Utils.State> responseRepo = new ResponseRepo<>();
                Log.d(TAG, "onChanged: " + jsonObject.toString());
                if (jsonObject.get("ok").getAsBoolean()) {
                    responseRepo.setData(Utils.State.SUCCESS);
                } else {
                    responseRepo.setData(Utils.State.FAILURE);
                }
                responseRepo.setKey(Constant.MARK_READ_NOTIFICATION);
                notificationLiveData.setValue(responseRepo);
                notificationLiveData.removeSource(source);
            }
        });
    }

    public MediatorLiveData<ResponseRepo> getNotificationLiveData() {
        return notificationLiveData;
    }


}
