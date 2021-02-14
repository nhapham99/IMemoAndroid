package com.lnb.imemo.Presentation.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Presentation.Home.RecyclerView.HomeRecyclerViewAdapter;
import com.lnb.imemo.Presentation.UploadActivity.UploadActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static HomeFragment mHomeFragment;
    // ui
    private RecyclerView homeRecyclerView;
    private SwipeRefreshLayout homeSwipeRefreshLayout;
    private TextView homeNewMemo;
    private CircleImageView userAvatar;
    private ImageView homeFilter;
    private LinearLayout memoUploadFile, memoUploadTag, memoUpLoadLink;
    private MaterialSearchBar searchBar;

    // var
    private HomeRecyclerViewAdapter adapter;
    private HomeViewModel viewModel;
    private int pageNumber = 1;
    private int currentChoosedDiaryPosition = -1;

    private HomeFragment() {

    }

    public static HomeFragment getHomeFragment() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        return mHomeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        // init ui
        homeRecyclerView = view.findViewById(R.id.home_recyclerView);
        homeSwipeRefreshLayout = view.findViewById(R.id.home_refresh_layout);
        homeNewMemo = view.findViewById(R.id.home_create_new_memo);
        homeNewMemo.setOnClickListener(this);
        userAvatar = view.findViewById(R.id.home_user_avatar);
        homeFilter = view.findViewById(R.id.home_filter);
        homeFilter.setOnClickListener(this);
        memoUploadFile = view.findViewById(R.id.upload_memo_file);
        memoUploadFile.setOnClickListener(this);
        memoUploadTag = view.findViewById(R.id.upload_memo_tag);
        memoUploadTag.setOnClickListener(this);
        memoUpLoadLink = view.findViewById(R.id.upload_memo_link);
        memoUpLoadLink.setOnClickListener(this);
        searchBar = view.findViewById(R.id.searchBar);

        // init var
        viewModel = new HomeViewModel();
        adapter = new HomeRecyclerViewAdapter((ArrayList<Diary>) viewModel.listDiary);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        homeRecyclerView.setAdapter(adapter);

        adapter.observableAction().observe(this, new Observer<Pair<String, Integer>>() {
            @Override
            public void onChanged(Pair<String, Integer> adapterAction) {
                String key = adapterAction.first;
                if (key.equals(Constant.DELETE_DIARY_KEY)) {
                    currentChoosedDiaryPosition = adapterAction.second;
                    viewModel.deleteDiary(adapterAction.second);
                }
            }
        });

        homeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllMemo();
            }
        });

        // load user avatar
        Glide.with(this).load(viewModel.mUser.getAvatarUrl()).into(userAvatar);


        subscribeMemoObservable();
        subscribeDeleteMemoObservable();
        getAllMemo();
    }

    private void getAllMemo() {
        viewModel.getDiaries(null,
                null,
                pageNumber,
                Constant.pageSize,
                null,
                null,
                null
        );
    }

    private void subscribeDeleteMemoObservable() {
        viewModel.observableDeleteDiary().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                switch (state) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged: removed " + currentChoosedDiaryPosition);
                        viewModel.listDiary.remove(currentChoosedDiaryPosition);
                        adapter.removeAtPosition(currentChoosedDiaryPosition);
                        break;
                    case FAILURE:
                        Toast.makeText(getContext(), "Lỗi xóa memo. Xin vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        break;
                    case NO_INTERNET:
                        Toast.makeText(getContext(), "Xin kiểm tra lại kết nối của bạn", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void subscribeMemoObservable() {
        viewModel.observableGetAllDiary().observe(this, new Observer<Utils.State>() {
            @Override
            public void onChanged(Utils.State state) {
                switch (state) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged: success ");
                        if (homeSwipeRefreshLayout.isRefreshing()) {
                            homeSwipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.updateListMemo((ArrayList<Diary>) viewModel.listDiary);
                        break;
                    case FAILURE:
                        Toast.makeText(getContext(), "Lỗi. Xin vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        break;
                    case NO_INTERNET:
                        Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_create_new_memo:
                startActivity(new Intent(getActivity(), UploadActivity.class));
                break;
        }
    }
}