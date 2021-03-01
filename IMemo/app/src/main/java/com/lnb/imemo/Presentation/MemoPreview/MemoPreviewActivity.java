package com.lnb.imemo.Presentation.MemoPreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.Home.RecyclerView.HomeRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.TagRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.TabFragment.AudioFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.ImageFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.LinkFragment;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.DateHelper;

import java.util.ArrayList;

public class MemoPreviewActivity extends AppCompatActivity {
    private static final String TAG = "MemoPreviewActivity";
    private TextView title;
    private TextView time;
    private RecyclerView tags;
    private TextView content;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView popDownMenu;
    private TextView memoAuthor;
    private Button backButton;
    private Diary diary;
    private String authorName;
    private LinearLayout share;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_memo_preview);
        init();
    }

    private void init() {
        title = findViewById(R.id.memo_title);
        time = findViewById(R.id.memo_time);
        tags = findViewById(R.id.memo_item_tag_list);
        content = findViewById(R.id.memo_item_content);
        tabLayout = findViewById(R.id.memo_item_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setUserInputEnabled(false);
        popDownMenu = findViewById(R.id.pop_down_menu);
        memoAuthor = findViewById(R.id.memo_author);
        backButton = findViewById(R.id.back_button);
        share = findViewById(R.id.memo_share);
        share.setVisibility(View.GONE);

        diary = getIntent().getParcelableExtra("preview_diary");
        authorName = getIntent().getStringExtra("preview_author_name");
        String from = getIntent().getStringExtra("from");
        if (from.equals("notification")) {
            backButton.setText("Quay lại thông báo");
        }
        setupDiary();
        memoAuthor.setText("Bản ghi của " + authorName);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupDiary() {
        // setup views

        title.setText(diary.getTitle());
        // uploading == null => uploaded
        if (diary.getUploading() == null) {
            time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        popDownMenu.setVisibility(View.GONE);

        // if diary tags is null remove view
        if (diary.getTags().size() == 0) {
            tags.setVisibility(View.GONE);
        } else {
            ArrayList<Tags> listTags = diary.getTags();
            tags.setAdapter(new TagRecyclerViewAdapter(listTags));
            tags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        if (diary.getContent() == null) {
            content.setVisibility(View.GONE);
        } else {
            content.setText(diary.getContent());
        }

        ArrayList<Resource> listResource = (ArrayList<Resource>) diary.getResources();
        ArrayList<Resource> listImageAndVideo = new ArrayList<>();
        ArrayList<Resource> listAudio = new ArrayList<>();
        ArrayList<Link> listLinks = (ArrayList<Link>) diary.getLinks();
        try {
            for (Resource resource : listResource) {
                if (resource.getType().contains(Constant.imageType)) {
                    listImageAndVideo.add(resource);
                } else if (resource.getType().contains(Constant.videoType)) {
                    listImageAndVideo.add(resource);
                } else if (resource.getType().contains(Constant.audioType)) {
                    listAudio.add(resource);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }

        ArrayList<String> listTabs = new ArrayList<>();
        ArrayList<Fragment> listFragments = new ArrayList<>();

        try {
            if (listImageAndVideo.size() != 0) {
                listTabs.add(getResources().getText(R.string.image_and_video).toString());
                listFragments.add(new ImageFragment(listImageAndVideo, this));
            }

            if (listLinks.size() != 0) {
                listTabs.add(getResources().getText(R.string.link).toString());
                listFragments.add(new LinkFragment(listLinks));
            }

            if (listAudio.size() != 0) {
                listTabs.add(getResources().getText(R.string.audio).toString());
                Log.d(TAG, "onBindViewHolder: " + listAudio.size());
                listFragments.add(new AudioFragment(listAudio));
            }
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }


        if (listTabs.size() == 0) {
            tabLayout.setVisibility(View.GONE);
        }

        MemoTabAdapter adapter = new MemoTabAdapter(this);
        adapter.setData(listTabs, listFragments);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(listTabs.get(position));
            }
        }).attach();

    }

    class MemoTabAdapter extends FragmentStateAdapter {

        ArrayList<String> listTabs = new ArrayList<>();
        ArrayList<Fragment> listFragment = new ArrayList<>();

        public MemoTabAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public void setData(ArrayList<String> listTabs, ArrayList<Fragment> listFragment) {
            this.listTabs = listTabs;
            this.listFragment = listFragment;
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getItemCount() {
            return listFragment.size();
        }

    }
}