package com.lnb.imemo.Presentation.Notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class NotificationFragment extends Fragment {
    private static NotificationFragment mNotificationFragment;
    private NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    private final NotificationViewModel viewModel;
    private PublishSubject<Pair<String, Notification>> notificationObservable;
    private int currentReadNotificationPosition = -1;
    private static final String TAG = "NotificationFragment";
    private Boolean isStart = false;
    private ProgressBar progressBar;
    private Boolean isLoadingMore = false;
    private PublishSubject<Pair<String, Integer>> markReadObserver;
    private final CompositeDisposable disposable = new CompositeDisposable();


    private NotificationFragment(Boolean isStart,
                                 PublishSubject<Pair<String, Notification>> notificationObservable,
                                 PublishSubject<Pair<String, Object>> centerObserver) {
        this.isStart = isStart;
        if (this.notificationObservable == null) {
            this.notificationObservable = notificationObservable;
        }
        viewModel = NotificationViewModel.getNotificationViewModel(isStart);
        viewModel.getAllNotification();
    }

    public static NotificationFragment getNotificationFragment(Boolean isStart,
                                                               PublishSubject<Pair<String, Notification>> notificationObservable,
                                                               PublishSubject<Pair<String, Object>> centerObservable) {
        if (mNotificationFragment == null || isStart) {
            mNotificationFragment = new NotificationFragment(isStart, notificationObservable, centerObservable);
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
        RecyclerView notificationRecyclerView = view.findViewById(R.id.notification_recyclerView);
        NestedScrollView nestedScrollView = view.findViewById(R.id.nested_scroll);
        progressBar = view.findViewById(R.id.progressBar);
        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter((ArrayList<Notification>) viewModel.listNotification);
        notificationRecyclerView.setAdapter(notificationRecyclerViewAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (viewModel.listNotification.size() < viewModel.totalMemo) {
                        progressBar.setVisibility(View.VISIBLE);
                        viewModel.getAllNotification();
                        isLoadingMore = true;
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }
        });

        subscribeNotificationObservable();
        subscribeMarkReadNotification();
        subscribeShowNotification();
        subscribeViewModelObserver();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.isStart = false;
    }

    private void subscribeShowNotification() {
        notificationRecyclerViewAdapter.getRecyclerViewObserver().subscribe(new io.reactivex.Observer<Pair<Diary, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<Diary, String> diaryStringPair) {
                Log.d(TAG, "onNext: show notification");
                Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
                intent.putExtra("preview_diary", diaryStringPair.first);
                intent.putExtra("preview_author_name", diaryStringPair.second);
                intent.putExtra("from", "notification");
                startActivity(intent);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }


    private void subscribeMarkReadNotification() {
        if ( markReadObserver == null) {
            markReadObserver = notificationRecyclerViewAdapter.getMarkReadObserver();
        }
        markReadObserver.subscribe(new io.reactivex.Observer<Pair<String, Integer>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, Integer> pair) {
                Log.d(TAG, "onNext: subscribe mark read" );
                viewModel.markReadNotification(pair.first);
                currentReadNotificationPosition = pair.second;
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void subscribeNotificationObservable() {
        notificationObservable.subscribe(new io.reactivex.Observer<Pair<String, Notification>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, Notification> pair) {
                Log.d(TAG, "onNext: push notification");
                String key = pair.first;
                if (key.equals("push_notification")) {
                    Notification<String> notification = pair.second;
                    viewModel.listNotification.add(0, notification);
                    notificationRecyclerViewAdapter.addNotification(pair.second);
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void subscribeViewModelObserver() {
        viewModel.getViewModelLiveDate().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                Log.d(TAG, "onChanged: " + responseRepo.toString());
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_ALL_NOTIFICATION)) {
                    Log.d(TAG, "onChanged: get all notification");
                    Pair<Utils.State, List<Notification>> pair = (Pair<Utils.State, List<Notification>>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            if (isLoadingMore) {
                                viewModel.listNotification.addAll(pair.second);
                                isLoadingMore = false;
                            } else {
                                viewModel.listNotification = pair.second;
                            }
                            notificationRecyclerViewAdapter.setListNotifications((ArrayList<Notification>) viewModel.listNotification);
                            break;
                        case FAILURE:
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getContext(), "Lỗi. Vui lòng thử lai", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getContext(), "Vui lòng kiểm tra lại kết nối internet của bạn", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key.equals(Constant.MARK_READ_NOTIFICATION)) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    Log.d(TAG, "onChanged: " + state);
                    switch (state) {
                        case SUCCESS:
                            notificationObservable.onNext(new Pair<>("mark_read", new Notification()));
                            notificationRecyclerViewAdapter.updateItem(currentReadNotificationPosition);
                            break;
                        case FAILURE:
                            break;
                    }

                }
            }
        });
    }
}