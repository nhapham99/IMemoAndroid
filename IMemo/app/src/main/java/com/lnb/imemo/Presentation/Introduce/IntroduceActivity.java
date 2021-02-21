package com.lnb.imemo.Presentation.Introduce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Presentation.Home.TabFragment.ImageAndVideoAdapter.ImageAndVideoViewPagerAdapter;
import com.lnb.imemo.Presentation.Login.LoginActivity;
import com.lnb.imemo.Presentation.NavigationActivity.NavigationActivity;
import com.lnb.imemo.R;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import me.relex.circleindicator.CircleIndicator3;

public class IntroduceActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private IntroduceViewPagerAdapter adapter;
    private CircleIndicator3 circleIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introduce);
        if (restorePrefData()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
    }

    private Boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroduceOpened = pref.getBoolean("isIntroduceOpened", false);
        return isIntroduceOpened;
    }

    private void initView() {
        viewPager2 = findViewById(R.id.introduce_viewPager);
        circleIndicator = findViewById(R.id.circle_indicator);
        adapter = new IntroduceViewPagerAdapter();
        viewPager2.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager2);

        adapter.publishSubject.subscribe(aBoolean -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            savePrefData();
        });
    }

    private void savePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroduceOpened", true);
        editor.commit();
    }
}