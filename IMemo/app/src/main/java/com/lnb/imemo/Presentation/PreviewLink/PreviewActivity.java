package com.lnb.imemo.Presentation.PreviewLink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Presentation.PreviewLink.RecyclerViewAdapter.LinkRecyclerViewAdapter;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.functions.Consumer;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText linkUrl;
    private RecyclerView linkRecyclerView;
    private ImageButton backLinkPreView;
    private Button getLinkButton;

    private LinkRecyclerViewAdapter adapter;
    private PreviewLinkViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_preview);
        init();
    }

    private void init() {
        linkUrl = findViewById(R.id.link_url);
        linkRecyclerView = findViewById(R.id.add_link_recyclerView);
        backLinkPreView = findViewById(R.id.add_link_back);
        backLinkPreView.setOnClickListener(this);
        getLinkButton = findViewById(R.id.add_link_button);
        getLinkButton.setOnClickListener(this);

        viewModel = new PreviewLinkViewModel();
        subscribePreiewLinkObservable();

        linkUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.getPreViewLink(linkUrl.getText().toString());
                }
                return false;
            }
        });

    }

    private void subscribePreiewLinkObservable() {
        viewModel.observablePreviewLink().observe(this, new Observer<Pair<Utils.State, Link>>() {
            @Override
            public void onChanged(Pair<Utils.State, Link> response) {
                switch (response.first) {
                    case SUCCESS:
                        adapter = new LinkRecyclerViewAdapter(response.second);
                        linkRecyclerView.setAdapter(adapter);
                        linkRecyclerView.setLayoutManager(new LinearLayoutManager(PreviewActivity.this, LinearLayoutManager.VERTICAL, false));
                        getLinkButton.setEnabled(true);
                        break;
                    case FAILURE:
                        Toast.makeText(PreviewActivity.this, "Lỗi lấy link", Toast.LENGTH_SHORT).show();
                        getLinkButton.setEnabled(false);
                        break;
                    case NO_INTERNET:
                        Toast.makeText(PreviewActivity.this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                        getLinkButton.setEnabled(false);
                        break;
                }
            }
        });
    }

    private void sendLinkResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("previewLink", adapter.getLink());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_link_back:
                finish();
                break;
            case R.id.add_link_button:
                sendLinkResult();
                break;
        }
    }
}