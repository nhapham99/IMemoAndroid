package com.lnb.imemo.Presentation.NavigationActivity;

import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Model.ResultNotification;
import com.lnb.imemo.Data.Repository.Notification.NotificationRepository;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.List;

public class NavigationViewModel extends ViewModel {
    private final NotificationRepository notificationRepository;
    private final User mUser;
    private int page = 1;
    private final MediatorLiveData<ResponseRepo> viewModelLiveDate = new MediatorLiveData<>();
    private Observer<ResponseRepo> notificationObserver;

    public NavigationViewModel() {
        notificationRepository = new NotificationRepository();
        mUser = User.getUser();
        getAllNotification();
        subscribeNotificationObserver();
    }

    public void getAllNotification() {
        notificationRepository.getAllNotificationForCount(mUser.getToken(), page);
    }

    private void subscribeNotificationObserver() {
        notificationObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_ALL_NOTIFICATION_FOR_COUNT)) {
                    ResponseRepo<Pair<Utils.State, Integer>> response = new ResponseRepo<>();
                    ResultNotification resultNotification = (ResultNotification) responseRepo.getData();
                    response.setData(new Pair<>(Utils.State.SUCCESS, resultNotification.getTotalHNotSeen()));
                    response.setKey(Constant.GET_ALL_NOTIFICATION_FOR_COUNT);
                    viewModelLiveDate.setValue(response);
                }
            }
        };
        notificationRepository.getNotificationLiveData().observeForever(notificationObserver);
    }

    public MediatorLiveData<ResponseRepo> getViewModelLiveDate() {
        return viewModelLiveDate;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        notificationRepository.getNotificationLiveData().removeObserver(notificationObserver);
    }
}
