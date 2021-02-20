package com.lnb.imemo.Data.Repository.Notification;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Data.Repository.Diary.DiaryAPI;
import com.lnb.imemo.Data.Repository.Model.ResultNotification;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Root;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.List;

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
        Log.d(TAG, "getAllNotification: ");
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
                Log.d(TAG, "onChanged: " + resultNotification.toString());
                List<Notification> listNotification = resultNotification.getNotifications();
                ResponseRepo<List<Notification>> responseRepo = new ResponseRepo<>();
                responseRepo.setKey(Constant.GET_ALL_NOTIFICATION);
                responseRepo.setData(listNotification);
                notificationLiveData.setValue(responseRepo);
                notificationLiveData.removeSource(source);
            }
        });
    }

    public MediatorLiveData<ResponseRepo> getNotificationLiveData() {
        return notificationLiveData;
    }
}
