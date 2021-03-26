package com.lnb.imemo.Presentation.PreviewImage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.UrlHandler;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;
public class PreviewImageActivity extends AppCompatActivity {
    private static final String TAG = "PreviewImageActivity";
    private ArrayList<Resource> listResource;
    private PreviewImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_preview_image);
        //hideSystemUI();
        listResource = getIntent().getParcelableArrayListExtra("data");
        initView();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    private void initView() {
        ViewPager2 viewPager = findViewById(R.id.preview_image_viewPager);
        adapter = new PreviewImageAdapter(listResource, this);
        viewPager.setAdapter(adapter);
        ImageView escapeBtn = findViewById(R.id.ic_escape);
        escapeBtn.setOnClickListener(v -> finish());
        ImageView moreBtn = findViewById(R.id.ic_more);
        moreBtn.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.preview_image_bottom_dialog, null);
            TextView saveImageToDevice = view.findViewById(R.id.save_to_device);
            saveImageToDevice.setOnClickListener(v1 -> {
                startDownloadFile();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        });
    }

    private void startDownloadFile() {
        String url = UrlHandler.convertUrl(listResource.get(0).getUrl());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(listResource.get(0).getName());
        request.setDescription("Downloading " + listResource.get(0).getName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + listResource.get(0).getName());
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        assert downloadManager != null;
        downloadManager.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.destroyMedia();
    }

}