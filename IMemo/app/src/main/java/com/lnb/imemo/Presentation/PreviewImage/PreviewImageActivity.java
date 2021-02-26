package com.lnb.imemo.Presentation.PreviewImage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Presentation.Introduce.IntroduceViewPagerAdapter;
import com.lnb.imemo.Presentation.Login.LoginActivity;
import com.lnb.imemo.R;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class PreviewImageActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private CircleIndicator3 circleIndicator;
    private PreviewImageAdapter adapter;
    private ArrayList<Resource> listResource;
    private ImageView escapeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        hideSystemUI();
        listResource = getIntent().getParcelableArrayListExtra("data");
        initView();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    private void initView() {
        viewPager = findViewById(R.id.preview_image_viewPager);
        circleIndicator = findViewById(R.id.circle_indicator);
        adapter = new PreviewImageAdapter(listResource);
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);

        escapeBtn = findViewById(R.id.ic_escape);
        escapeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}