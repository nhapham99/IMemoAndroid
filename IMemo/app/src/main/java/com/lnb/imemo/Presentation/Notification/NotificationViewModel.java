package com.lnb.imemo.Presentation.Notification;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private static final String TAG = "NotificationViewModel";
    private static NotificationViewModel mInstance;
    private final NotificationRepository notificationRepository;
    public int totalMemo = 0;
    public int page = 1;
    private final User mUser;
    private MediatorLiveData<ResponseRepo> viewModelLiveDate;
    {
        if (viewModelLiveDate == null) {
            viewModelLiveDate = new MediatorLiveData<>();
        }
    }
    private Observer<ResponseRepo> notificationObserver;
    public List<Notification> listNotification = new ArrayList<>();
    private NotificationViewModel() {
        notificationRepository = new NotificationRepository();
        mUser = User.getUser();
        subscribeNotificationObserver();
    }

    public static NotificationViewModel getNotificationViewModel(Boolean isStart) {

        if (mInstance == null || isStart) {
            Log.d(TAG, "getNotificationViewModel: ");

            mInstance = new NotificationViewModel();
        }
        return mInstance;
    }

    public void getAllNotification() {
        Log.d(TAG, "getAllNotification: ");
        if (listNotification.size() < totalMemo || page == 1) {
            notificationRepository.getAllNotification(mUser.getToken(), page);
            page++;
        }

    }

    private void subscribeNotificationObserver() {
        notificationObserver = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                Log.d(TAG, "onChanged: " + responseRepo.toString());
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_ALL_NOTIFICATION)) {
                    Log.d(TAG, "onChanged: get all notification");
                    ResultNotification resultNotification = (ResultNotification) responseRepo.getData();
                    List<Notification<PersonProfile>> listNotification = resultNotification.getNotifications();
                    totalMemo = resultNotification.getPagination().getTotalItems();
                    ResponseRepo<Pair<Utils.State, List<Notification<PersonProfile>>>> response = new ResponseRepo<>();
                    response.setData(new Pair<>(Utils.State.SUCCESS, listNotification));
                    response.setKey(Constant.GET_ALL_NOTIFICATION);
                    viewModelLiveDate.setValue(response);
                } else if (key.equals(Constant.MARK_READ_NOTIFICATION)) {
                    ResponseRepo<Utils.State> response = new ResponseRepo<>();
                    response.setKey(Constant.MARK_READ_NOTIFICATION);
                    response.setData((Utils.State) responseRepo.getData());
                    viewModelLiveDate.setValue(response);
                }
            }
        };
        notificationRepository.getNotificationLiveData().observeForever(notificationObserver);
    }

    public MediatorLiveData<ResponseRepo> getViewModelLiveDate() {
        return viewModelLiveDate;
    }

    public void markReadNotification(String notificationId) {
        notificationRepository.markReadNotification(mUser.getToken(), notificationId);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        notificationRepository.getNotificationLiveData().removeObserver(notificationObserver);
    }


}
