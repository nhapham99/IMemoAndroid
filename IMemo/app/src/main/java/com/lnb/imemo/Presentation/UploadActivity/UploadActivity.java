package com.lnb.imemo.Presentation.UploadActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.util.IOUtils;
import com.lnb.imemo.Data.Entity.ResponseRepo;
import com.lnb.imemo.Data.Repository.UploadFile.UploadFileRepository;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.FileUtils;
import com.lnb.imemo.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UploadActivity";
    // ui
    private Button uploadButton;

    // var
    private int GET_FILE_CODE = 1;
    private UploadViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);
        init();
    }

    private void init() {
        // init ui
        uploadButton = findViewById(R.id.upload_file_button);
        uploadButton.setOnClickListener(this);

        // init var
        viewModel = new UploadViewModel(this);
        subscribeViewModelObservable();
    }

    private void subscribeViewModelObservable() {
       viewModel.getViewModelObservable().observe(this, new Observer<ResponseRepo>() {
           @Override
           public void onChanged(ResponseRepo responseRepo) {
               String key = responseRepo.getKey();
               if (key == Constant.UPLOAD_FILE_KEY) {
                   Utils.State state = (Utils.State) responseRepo.getData();
                   Log.d(TAG, "onChanged: " + state.name());
               }
           }
       });
    }

    private void startUploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, GET_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FILE_CODE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                viewModel.uploadFile(data.getData());
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_file_button:
                startUploadFile();
                break;
        }
    }


}