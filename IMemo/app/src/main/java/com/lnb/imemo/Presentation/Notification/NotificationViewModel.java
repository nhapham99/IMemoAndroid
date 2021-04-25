package com.lnb.imemo.Presentation.Notification;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.Diary.DiaryRepository;
import com.lnb.imemo.Data.Repository.Model.ResultNotification;
import com.lnb.imemo.Data.Repository.Notification.NotificationRepository;
import com.lnb.imemo.Model.Diary;
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
    private final NotificationRepository notificationRepository;
    private final DiaryRepository diaryRepository;
    {
        diaryRepository = new DiaryRepository();
    }
    public int totalMemo = 0;
    private final User mUser;
    private final MediatorLiveData<ResponseRepo> viewModelLiveDate;
    {
        viewModelLiveDate = new MediatorLiveData<>();
    }
    private Observer<ResponseRepo> notificationObserver;
    private Observer<ResponseRepo> diaryObserver;
    public List<Notification> listNotification = new ArrayList<>();

    public NotificationViewModel() {
        notificationRepository = new NotificationRepository();
        mUser = User.getUser();
        subscribeNotificationObserver();
        subscribeDiaryRepoObservable();
    }


    public void getAllNotification(int page) {
        Log.d(TAG, "getAllNotification: ");
        if (listNotification.size() < totalMemo || page == 1) {
            notificationRepository.getAllNotification(mUser.getToken(), page);
        }

    }

    private void subscribeNotificationObserver() {
        notificationObserver = responseRepo -> {
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
        };
        notificationRepository.getNotificationLiveData().observeForever(notificationObserver);
    }

    private void subscribeDiaryRepoObservable() {
        diaryObserver = responseRepo -> {
            String key = responseRepo.getKey();
            if (key.equals(Constant.GET_DIARY_SHARED_BY_ID_KEY)) {
                ResponseRepo<Utils.State> checkDiaryNotification = new ResponseRepo<>();
                checkDiaryNotification.setKey("check_diary_in_notification");
                Pair<Utils.State, Diary<PersonProfile>> response = (Pair<Utils.State, Diary<PersonProfile>>) responseRepo.getData();
                switch (response.first) {
                    case SUCCESS:
                        checkDiaryNotification.setData(Utils.State.SUCCESS);
                        break;
                    case NO_INTERNET:
                        checkDiaryNotification.setData(Utils.State.NO_INTERNET);
                        break;
                    case FAILURE:
                        checkDiaryNotification.setData(Utils.State.FAILURE);
                        break;
                }
                viewModelLiveDate.setValue(checkDiaryNotification);
            }
        };

        diaryRepository.observableDiaryRepo().observeForever(diaryObserver);
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
        Log.d(TAG, "onCleared: ");
        notificationRepository.getNotificationLiveData().removeObserver(notificationObserver);
        diaryRepository.observableDiaryRepo().removeObserver(diaryObserver);
    }

    public void getDiarySharedById(String id) {
        diaryRepository.getDiarySharedById(mUser.getToken(), id);
    }
}
