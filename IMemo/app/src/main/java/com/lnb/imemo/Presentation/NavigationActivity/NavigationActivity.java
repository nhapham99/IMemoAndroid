package com.lnb.imemo.Presentation.NavigationActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Presentation.Home.HomeFragment;
import com.lnb.imemo.Presentation.Mail.MailFragment;
import com.lnb.imemo.Presentation.Notification.NotificationFragment;
import com.lnb.imemo.Presentation.Person.PersonFragment;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.net.URISyntaxException;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;


public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = "NavigationActivity";
    private PublishSubject<Pair<String, Object>> centerObservable;
    {
        if (centerObservable == null) {
            centerObservable = PublishSubject.create();
        }
    }


    private BottomNavigationView bottomNavigationView;
    private Fragment homeFragment = HomeFragment.getHomeFragment(true, centerObservable);
    private Fragment mailFragment = MailFragment.getMailFragment();
    private PersonFragment personFragment = PersonFragment.getPersonFragment(centerObservable);
    private Gson gsonBuilder = new GsonBuilder().create();
    private NavigationViewModel viewModel = new NavigationViewModel();
    private int totalNotSeen = 0;
    private Fragment currentFragment;
    private Boolean setupStart = true;
    private PublishSubject<Pair<String, Notification>> notificationObservable;
    {
        if (notificationObservable == null) {
            notificationObservable = PublishSubject.create();
        }
    }
    private CompositeDisposable disposable;
    {
        if (disposable == null) {
            disposable = new CompositeDisposable();
        }
    }
    private Fragment notificationFragment = NotificationFragment.getNotificationFragment(true, notificationObservable, centerObservable);



    public static Socket mSocket;
    {
        try {
            mSocket = IO.socket(Utils.baseUrls);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_navigation);
        subscribeViewModelObservable();
        init();
        Log.d(TAG, "onCreate: " + User.getUser().getToken());
        subscribeNotificationObservable();
        mSocket.connect();
        mSocket.emit("user-join", PersonProfile.getInstance().getId());
        mSocket.on("push-noti", onNewNotification);
    }

    private void subscribeViewModelObservable() {
        viewModel.getViewModelLiveDate().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.GET_ALL_NOTIFICATION_FOR_COUNT)) {
                    Pair<Utils.State, Integer> pair = (Pair<Utils.State, Integer>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            totalNotSeen = pair.second;
                            if (totalNotSeen != 0) {
                                BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
                                badgeDrawable.setVisible(true);
                                badgeDrawable.setBackgroundColor(Color.parseColor("#E22A49"));
                                badgeDrawable.setMaxCharacterCount(2);
                                badgeDrawable.setNumber(totalNotSeen);
                            }
                            break;
                        case FAILURE:
                            break;
                        case NO_INTERNET:
                            break;
                    }
                }
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
                String key = pair.first;
                if (key.equals("mark_read")) {
                    Log.d(TAG, "onNext: mark read");
                    BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
                    if (totalNotSeen > 0) {
                        totalNotSeen--;
                    }
                    if (totalNotSeen == 0) {
                        badgeDrawable.setVisible(false);
                    } else {
                        badgeDrawable.setNumber(totalNotSeen);
                    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!(currentFragment instanceof HomeFragment)) {
            bottomNavigationView.setSelectedItemId(R.id.home);
            currentFragment = homeFragment;
        } else {
            finish();
        }
    }

    private void init() {
        loadFragment(homeFragment);
        currentFragment = homeFragment;
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        if (!(currentFragment instanceof HomeFragment)) {
                            loadFragment(homeFragment);
                        } else {
                            centerObservable.onNext(new Pair<>("on_top_home_recyclerView", true));
                        }
                        return true;
//                    case R.id.mail:
//                        loadFragment(mailFragment);
//                        return true;
                    case R.id.notification:
                        if (!(currentFragment instanceof NotificationFragment)) {
                            loadFragment(notificationFragment);
                        }
                        return true;
                    case R.id.person:
                        if (!(currentFragment instanceof PersonFragment)) {
                            loadFragment(personFragment);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private final Emitter.Listener onNewNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String objectStr = args[0].toString();
                    Notification<String> notification = gsonBuilder.fromJson(objectStr, Notification.class);
                    if (currentFragment == notificationFragment) {
                        Log.d(TAG, "run: " + notification.toString());
                        notificationObservable.onNext(new Pair<>("push_notification", notification));
                    }
                    BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setBackgroundColor(Color.parseColor("#E22A49"));
                    badgeDrawable.setMaxCharacterCount(2);
                    totalNotSeen++;
                    badgeDrawable.setNumber(totalNotSeen);
                }
            });
        }
    };

    private int positionOfFragment(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            return 1;
        } else if (fragment instanceof NotificationFragment) {
            return 2;
        } else if (fragment instanceof PersonFragment) {
            return 3;
        }
        return 0;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.d(TAG, "loadFragment: " + positionOfFragment(currentFragment));
        if (positionOfFragment(currentFragment) < positionOfFragment(fragment)) {
            Log.d(TAG, "loadFragment: right");
            transaction.setCustomAnimations(R.anim.tab_right_in, R.anim.tab_right_out);
        } else {
            Log.d(TAG, "loadFragment: left");
            transaction.setCustomAnimations(R.anim.tab_left_in, R.anim.tab_left_out);
        }
        currentFragment = fragment;
        transaction.replace(R.id.fragment_container, fragment);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            for (int i = 0; i < fm.getBackStackEntryCount() - 1; i++) {
                fm.popBackStack();
            }
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }
}