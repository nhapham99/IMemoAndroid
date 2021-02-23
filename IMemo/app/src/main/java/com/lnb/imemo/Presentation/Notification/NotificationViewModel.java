package com.lnb.imemo.Presentation.Notification;

import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Notification.NotificationRepository;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private static NotificationViewModel mInstance;
    private NotificationRepository notificationRepository;
    public int page = 1;
    private User mUser;
    private MediatorLiveData<ResponseRepo> viewModelLiveDate = new MediatorLiveData<>();
    private Observer<ResponseRepo> notificationObserver;
    private NotificationViewModel() {
        notificationRepository = new NotificationRepository();
        mUser = User.getUser();
        subscribeNotificationObserver();
    }

    public static NotificationViewModel getNotificationViewModel(Boolean isStart) {
        if (mInstance == null || isStart) {
            mInstance = new NotificationViewModel();
        }
        return mInstance;
    }

    public void getAllNotification() {
        notificationRepository.getAllNotification(mUser.getToken(), page);
    }

    private void subscribeNotificationObserver() {
        notificationObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.GET_ALL_NOTIFICATION) {
                    List<Notification> listNotification = (List<Notification>) responseRepo.getData();
                    ResponseRepo<Pair<Utils.State, List<Notification>>> response = new ResponseRepo<>();
                    response.setData(new Pair<>(Utils.State.SUCCESS, listNotification));
                    response.setKey(Constant.GET_ALL_NOTIFICATION);
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
