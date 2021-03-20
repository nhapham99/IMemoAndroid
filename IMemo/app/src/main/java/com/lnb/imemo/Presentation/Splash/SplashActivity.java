package com.lnb.imemo.Presentation.Splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.lnb.imemo.Presentation.Introduce.IntroduceActivity;
import com.lnb.imemo.Presentation.NavigationActivity.NavigationActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.AESCrypt;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SplashActivity";
    private final SplashViewModel viewModel = new SplashViewModel();
    private ProgressBar progressBar;
    private TextView autoLoginAgainButton;
    private TextView goToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);
        autoLoginAgainButton = findViewById(R.id.autoLoginAgain_button);
        autoLoginAgainButton.setOnClickListener(this);
        goToLoginButton = findViewById(R.id.goToLogin_button);
        goToLoginButton.setOnClickListener(this);

        subscribeViewModelObserver();
        if (restorePrefData()) {
            if (restoreToken() != null && !restoreToken().equals("")) {
                viewModel.getPersonProfile(restoreToken());
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), IntroduceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void subscribeViewModelObserver() {
        viewModel.getViewModelObservable().observe(this, state -> {
            switch (state) {
                case SUCCESS:
                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case FAILURE:
                    progressBar.setVisibility(View.GONE);
                    autoLoginAgainButton.setVisibility(View.VISIBLE);
                    goToLoginButton.setVisibility(View.VISIBLE);
                    viewModel.getPersonProfile(restoreToken());
                    Toast.makeText(this, "Đăng nhập lỗi vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    break;
                case NO_INTERNET:
                    Toast.makeText(this, "Vui lòng kiểm tra kết nối internet của bạn", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private Boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroduceOpened", false);
    }

    private String restoreToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        try {
            return AESCrypt.decrypt(pref.getString("token", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onCleared();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.autoLoginAgain_button:
                progressBar.setVisibility(View.VISIBLE);
                autoLoginAgainButton.setVisibility(View.GONE);
                goToLoginButton.setVisibility(View.GONE);
                break;
            case R.id.goToLogin_button:
                Intent intent = new Intent(getApplicationContext(), IntroduceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }
}