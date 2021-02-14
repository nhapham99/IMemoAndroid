package com.lnb.imemo.Presentation.PickTag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.lnb.imemo.Data.Repository.Tags.TagsRepository;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.PickTag.RecyclerViewAdapter.PickTagsRecyclerViewAdapter;
import com.lnb.imemo.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PickTagsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PickTagsActivity";
    private RecyclerView pickTagsRecyclerView;
    private ImageButton backButton;
    private Button chooseTagsButton;

    private PickTagsRecyclerViewAdapter adapter;
    private PickTagsViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_tags);
        init();
    }

    private void init() {
        pickTagsRecyclerView = findViewById(R.id.pick_tags_recyclerView);
        chooseTagsButton = findViewById(R.id.choose_tags_button);
        chooseTagsButton.setOnClickListener(this);
        backButton = findViewById(R.id.choose_tags_back);
        backButton.setOnClickListener(this);

        viewModel = new PickTagsViewModel();
        viewModel.getAllTags();
        subscribeGetAllTagObservable();
    }

    private void observableTagsList() {
        adapter.observableListTags().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer tagsSize) throws Exception {
                if (tagsSize > 0) {
                    chooseTagsButton.setEnabled(true);
                } else {
                    chooseTagsButton.setEnabled(false);
                }
            }
        });
    }

    private void subscribeGetAllTagObservable() {
        viewModel.observableAllTags().observe(this, new Observer<ArrayList<Tags>>() {
            @Override
            public void onChanged(ArrayList<Tags> tags) {
                adapter = new PickTagsRecyclerViewAdapter(tags);
                pickTagsRecyclerView.setAdapter(adapter);
                pickTagsRecyclerView.setLayoutManager(new GridLayoutManager(PickTagsActivity.this,2));
                observableTagsList();
            }
        });
    }

    private void backWithData() {
        Intent returnIntent = new Intent();
        Log.d(TAG, "backWithData: " + adapter.getListTagsId().toString());
        returnIntent.putExtra("arrayTagIds", adapter.getListTagsId());
        returnIntent.putExtra("arrayTag", adapter.getCheckedTags());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_tags_back:
                finish();
                break;
            case R.id.choose_tags_button:
                backWithData();
                break;
        }
    }
}