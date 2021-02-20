package com.lnb.imemo.Presentation.Notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Presentation.MemoPreview.MemoPreviewActivity;
import com.lnb.imemo.Presentation.Notification.RecyclerViewAdapter.NotificationRecyclerViewAdapter;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class NotificationFragment extends Fragment {
    private static NotificationFragment mNotificationFragment;
    private RecyclerView notificationRecyclerView;
    private NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    private NotificationViewModel viewModel;


    private NotificationFragment() { }

    public static NotificationFragment getNotificationFragment() {
        if (mNotificationFragment == null) {
            mNotificationFragment = new NotificationFragment();
        }
        return mNotificationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        notificationRecyclerView = view.findViewById(R.id.notification_recyclerView);

        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter();
        notificationRecyclerView.setAdapter(notificationRecyclerViewAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        notificationRecyclerViewAdapter.getRecyclerViewObserver().subscribe(new Consumer<Pair<Diary, String>>() {
            @Override
            public void accept(Pair<Diary, String> diaryStringPair) throws Exception {
                Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
                intent.putExtra("preview_diary", diaryStringPair.first);
                intent.putExtra("preview_author_name", diaryStringPair.second);
                intent.putExtra("from", "notification");
                startActivity(intent);
            }
        });
        viewModel = NotificationViewModel.getNotificationViewModel();
        if (notificationRecyclerViewAdapter.getItemCount() == 0) {
            viewModel.getAllNotification();
        }
        subscribeViewModelObserver();
    }

    private void subscribeViewModelObserver() {
        viewModel.getViewModelLiveDate().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.GET_ALL_NOTIFICATION) {
                    Pair<Utils.State, List<Notification>> pair = (Pair<Utils.State, List<Notification>>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            notificationRecyclerViewAdapter.setListNotifications((ArrayList<Notification>) pair.second);
                            break;
                        case FAILURE:
                            Toast.makeText(getContext(), "Lỗi. Vui lòng thử lai", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(getContext(), "Vui lòng kiểm tra lại kết nối internet của bạn", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }


}