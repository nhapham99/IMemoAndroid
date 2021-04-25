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
    private final PublishSubject<Pair<String, Object>> centerObservable = PublishSubject.create();
    private BottomNavigationView bottomNavigationView;
    private final Fragment homeFragment = new HomeFragment(centerObservable);
    private final Fragment mailFragment = MailFragment.getMailFragment();
    private final PersonFragment personFragment = PersonFragment.getPersonFragment(centerObservable);
    private final Gson gsonBuilder = new GsonBuilder().create();
    private final NavigationViewModel viewModel = new NavigationViewModel();
    private int totalNotSeen = 0;
    private Fragment currentFragment;
    private PersonProfile personProfile;
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
    private final Fragment notificationFragment = new NotificationFragment(notificationObservable, centerObservable);



    public static Socket mSocket;
    {
        try {
            if (mSocket == null) {
                mSocket = IO.socket(Utils.baseUrls);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    {
        Log.d(TAG, "instance initializer: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_navigation);
        subscribeViewModelObservable();
        personProfile = PersonProfile.getInstance();
        init();
        Log.d(TAG, "onCreate: " + User.getUser().getToken());
        subscribeNotificationObservable();
        mSocket.connect();
        mSocket.emit("user-join", PersonProfile.getInstance().getId());
        mSocket.on("push-noti", onNewNotification);

    }

    private void subscribeViewModelObservable() {
        viewModel.getViewModelLiveDate().observe(this, responseRepo -> {
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
            for (int i = fragmentManager.getBackStackEntryCount(); i >= 1; i--) {
                fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            loadFragment(homeFragment);
            currentFragment = homeFragment;
        } else {
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void init() {
        fragmentManager.beginTransaction().add(R.id.fragment_container, personFragment,  "3").hide(personFragment).commit();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().add(R.id.fragment_container, notificationFragment,  "2").hide(notificationFragment).commit();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment, "1").addToBackStack("root").commit();
        currentFragment = homeFragment;

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    if (!(currentFragment instanceof HomeFragment)) {
                       loadFragment(homeFragment);
                    } else {
                        centerObservable.onNext(new Pair<>("on_top_home_recyclerView", true));
                    }
                    return true;
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
        });
    }

    private void loadFragment(Fragment fragment) {
        Log.d(TAG, "loadFragment: " + fragment.getTag());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Log.d(TAG, "loadFragment: " + positionOfFragment(fragment));
        Log.d(TAG, "loadFragment: " + positionOfFragment(currentFragment));
        if (positionOfFragment(currentFragment) < positionOfFragment(fragment)) {
            Log.d(TAG, "loadFragment: right");
            transaction.setCustomAnimations(R.anim.tab_right_in, R.anim.tab_right_out);
        } else {
            Log.d(TAG, "loadFragment: left");
            transaction.setCustomAnimations(R.anim.tab_left_in, R.anim.tab_left_out);
        }

        transaction.addToBackStack(String.valueOf(fragmentManager.getBackStackEntryCount()));

        if (currentFragment instanceof HomeFragment) {
            centerObservable.onNext(new Pair<>("clear_home_fragment", null));
        } else if (currentFragment instanceof NotificationFragment) {
             centerObservable.onNext(new Pair<>("clear_notification_fragment", null));
         }
        transaction.hide(currentFragment).show(fragment).commit();
        Log.d(TAG, "loadFragment: " + fragmentManager.getBackStackEntryCount());
        currentFragment = fragment;
    }

    private final Emitter.Listener onNewNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                String objectStr = args[0].toString();
                Notification<String> notification = gsonBuilder.fromJson(objectStr, Notification.class);
                if (notification.getUser().equals(personProfile.getId())) {
                    Log.d(TAG, "call: " + notification);
                    notificationObservable.onNext(new Pair<>("push_notification", notification));
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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        disposable.clear();
        super.onDestroy();
    }
}
