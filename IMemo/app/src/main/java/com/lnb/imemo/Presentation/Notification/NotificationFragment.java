package com.lnb.imemo.Presentation.Notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
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

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class NotificationFragment extends Fragment {
    private NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    private NotificationViewModel viewModel;
    private PublishSubject<Pair<String, Notification>> notificationObservable;
    private int currentReadNotificationPosition = -1;
    private static final String TAG = "NotificationFragment";
    private ProgressBar progressBar;
    private Boolean isLoadingMore = false;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private int page;
    private PublishSubject<Pair<String, Object>> centerObserver;
    private Pair<Diary, String> currentDiaryPair = null;


    public NotificationFragment( PublishSubject<Pair<String, Notification>> notificationObservable,
                                 PublishSubject<Pair<String, Object>> centerObserver) {
        if (this.notificationObservable == null) {
            this.notificationObservable = notificationObservable;
        }

        if (this.centerObserver == null) {
            this.centerObserver = centerObserver;
        }
        Log.d(TAG, "NotificationFragment: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        page = 1;
        viewModel = new NotificationViewModel();
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
                        page++;
                        viewModel.getAllNotification(page);
                        isLoadingMore = true;
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }
        });

        viewModel.getAllNotification(page);
        subscribeNotificationObservable();
        subscribeMarkReadNotification();
        subscribeShowNotification();
        subscribeViewModelObserver();
        subscribeCenterObservable();
    }

    private void subscribeShowNotification() {
        Log.d(TAG, "subscribeShowNotification: ");
        notificationRecyclerViewAdapter.getRecyclerViewObserver().subscribe(new io.reactivex.Observer<Pair<Diary, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<Diary, String> diaryStringPair) {
                Log.d(TAG, "onNext: show notification");
                currentDiaryPair = diaryStringPair;
                viewModel.getDiarySharedById(diaryStringPair.first.getId());
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
        Log.d(TAG, "subscribeMarkReadNotification: ");
        notificationRecyclerViewAdapter.getMarkReadObserver().subscribe(new io.reactivex.Observer<Pair<String, Integer>>() {
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
        Log.d(TAG, "subscribeNotificationObservable: ");
        notificationObservable.subscribe(new io.reactivex.Observer<Pair<String, Notification>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, Notification> pair) {
                String key = pair.first;
                if (key.equals("push_notification")) {
                    Log.d(TAG, "onNext: push notification");
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
            public void onComplete() {}
        });
    }

    private void subscribeViewModelObserver() {
        Log.d(TAG, "subscribeViewModelObserver: ");
        viewModel.getViewModelLiveDate().observe(this, responseRepo -> {
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
                        Notification notification = viewModel.listNotification.get(currentReadNotificationPosition);
                        notification.setSeen(true);
                        viewModel.listNotification.set(currentReadNotificationPosition, notification);
                        notificationRecyclerViewAdapter.updateItem(currentReadNotificationPosition);
                        break;
                    case FAILURE:
                        break;
                }

            } else if (key.equals("check_diary_in_notification")) {
                Utils.State state = (Utils.State) responseRepo.getData();
                switch (state) {
                    case SUCCESS:
                        Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
                        intent.putExtra("preview_diary", currentDiaryPair.first);
                        intent.putExtra("preview_author_name", currentDiaryPair.second);
                        intent.putExtra("from", "notification");
                        startActivity(intent);
                        break;
                    case FAILURE:
                        Intent intent1 = new Intent(getActivity(), MemoPreviewActivity.class);
                        intent1.putExtra("preview_author_name", currentDiaryPair.second);
                        intent1.putExtra("from", "notification");
                        startActivity(intent1);

                        break;
                    case NO_INTERNET:
                        Toast.makeText(getActivity(), "Lỗi. Vui lòng kiểm tra kết nối của bạn", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
    }

    private void subscribeCenterObservable() {
        centerObserver.subscribe(new Observer<Pair<String, Object>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Pair<String, Object> pair) {
                String key = pair.first;
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        disposable.clear();
    }

}