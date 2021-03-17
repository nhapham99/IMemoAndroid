package com.lnb.imemo.Presentation.Introduce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lnb.imemo.Presentation.Login.LoginActivity;
import com.lnb.imemo.R;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator3;

public class IntroduceActivity extends AppCompatActivity {
    private final CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        ViewPager2 viewPager2 = findViewById(R.id.introduce_viewPager);
        CircleIndicator3 circleIndicator = findViewById(R.id.circle_indicator);
        IntroduceViewPagerAdapter adapter = new IntroduceViewPagerAdapter();
        viewPager2.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager2);

        adapter.publishSubject.subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                savePrefData();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {}
        });
    }

    private void savePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroduceOpened", true);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}