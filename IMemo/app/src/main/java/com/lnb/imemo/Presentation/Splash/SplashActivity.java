package com.lnb.imemo.Presentation.Splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.lnb.imemo.Presentation.Introduce.IntroduceActivity;
import com.lnb.imemo.Presentation.Login.LoginActivity;
import com.lnb.imemo.Presentation.NavigationActivity.NavigationActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.AESCrypt;

public class SplashActivity extends AppCompatActivity {

    private final SplashViewModel viewModel = new SplashViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        subscribeViewModelObserver();
        setContentView(R.layout.activity_splash);

        if (restorePrefData()) {
            if (restoreGoogleToken() != null && !restoreGoogleToken().equals("")) {
                viewModel.getTokenFromGoogleToken(restoreGoogleToken());
            } else if (restoreEmail()!= null && restorePassword() != null && !restoreEmail().equals("") && !restorePassword().equals("")) {
                viewModel.signIn(restoreEmail(), restorePassword());
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), IntroduceActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
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
                    Toast.makeText(this, "Đăng nhập lỗi vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    break;
                case NO_INTERNET:
                    Toast.makeText(this, "Vui lòng kiểm tra kết nối internet của bạn", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private String restoreGoogleToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        try {
            return AESCrypt.decrypt(pref.getString("googleToken", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroduceOpened", false);
    }

    private String restoreEmail() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        try {
            return AESCrypt.decrypt(pref.getString("email", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String restorePassword() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        try {
            return AESCrypt.decrypt(pref.getString("password", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}