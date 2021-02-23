package com.lnb.imemo.Presentation.NavigationActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lnb.imemo.Presentation.Home.HomeFragment;
import com.lnb.imemo.Presentation.Mail.MailFragment;
import com.lnb.imemo.Presentation.Notification.NotificationFragment;
import com.lnb.imemo.Presentation.Person.PersonFragment;
import com.lnb.imemo.R;

public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = "NavigationActivity";
    // ui
    private BottomNavigationView bottomNavigationView;
    private Fragment homeFragment = HomeFragment.getHomeFragment(true);
    private Fragment mailFragment = MailFragment.getMailFragment();
    private Fragment notificationFragment = NotificationFragment.getNotificationFragment(true);
    private PersonFragment personFragment = PersonFragment.getPersonFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        init();
    }

    private void init() {
        loadFragment(homeFragment);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragment(homeFragment);
                        return true;
//                    case R.id.mail:
//                        loadFragment(mailFragment);
//                        return true;
                    case R.id.notification:
                        loadFragment(notificationFragment);
                        return true;
                    case R.id.person:
                        loadFragment(personFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}