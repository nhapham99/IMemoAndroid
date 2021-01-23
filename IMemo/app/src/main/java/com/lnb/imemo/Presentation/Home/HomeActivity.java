package com.lnb.imemo.Presentation.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;

import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

    }

    private void init() {
        viewModel = new HomeViewModel(this);
        subscribeGetAllTags();
        subscribeGetTagById();
        subscribeUpdateTags();
        subscribeDeleteTag();
    }

    private void subscribeDeleteTag() {
        viewModel.observableDeleteTags().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                Log.d(TAG, "onChanged: " + state);
            }
        });
    }

    private void subscribeUpdateTags() {
        viewModel.observableUpdateTags().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                Log.d(TAG, "onChanged: " + state);
            }
        });
    }

    private void subscribeGetAllTags() {
        viewModel.observableAllTags().observe(this, new Observer<List<Tags>>() {
            @Override
            public void onChanged(List<Tags> listTags) {
                Log.d(TAG, "onChanged: " + listTags.toString());
            }
        });
    }

    private void subscribeGetTagById() {
        viewModel.observableTag().observe(this, new Observer<Tags>() {
            @Override
            public void onChanged(Tags tags) {
                Log.d(TAG, "onChanged: " + tags.toString());
            }
        });
    }
}