package com.lnb.imemo.Presentation.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Presentation.Home.RecyclerView.HomeRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.SimpleSectionedRecyclerViewAdapter;
import com.lnb.imemo.Presentation.PickTag.PickTagsActivity;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.Presentation.UploadActivity.Adapter.TagRecyclerViewAdapter;
import com.lnb.imemo.Presentation.UploadActivity.UploadActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static HomeFragment mHomeFragment;
    // ui
    private RecyclerView homeRecyclerView;
    private SwipeRefreshLayout homeSwipeRefreshLayout;

    private CircleImageView userAvatar;
    private ImageView homeFilter;
    private MaterialSearchBar searchBar;

    // var
    private HomeRecyclerViewAdapter adapter;
    private HomeViewModel viewModel;
    private int pageNumber = 1;
    private int currentChoosedDiaryPosition = -1;
    private int GET_FILE_CODE = 1;
    private int GET_TAGS = 2;
    private int GET_PREVIEW_LINK = 3;
    private int UPLOAD_MEMO_CODE = 4;

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

        userAvatar = view.findViewById(R.id.home_user_avatar);
        homeFilter = view.findViewById(R.id.home_filter);
        homeFilter.setOnClickListener(this);

        searchBar = view.findViewById(R.id.searchBar);

        // init var
        viewModel = new HomeViewModel();
        adapter = new HomeRecyclerViewAdapter((ArrayList<Diary>) viewModel.listDiary);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        homeRecyclerView.setAdapter(adapter);
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getContext(),R.layout.sections,R.id.section_text,adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        homeRecyclerView.setAdapter(mSectionedAdapter);

        adapter.observableAction().observe(this, new Observer<Pair<String, Integer>>() {
            @Override
            public void onChanged(Pair<String, Integer> adapterAction) {
                String key = adapterAction.first;
                if (key.equals(Constant.DELETE_DIARY_KEY)) {
                    currentChoosedDiaryPosition = adapterAction.second;
                    viewModel.deleteDiary(adapterAction.second);
                } else if (key == Constant.CREATE_DIARY_KEY) {
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    startActivityForResult(intent, UPLOAD_MEMO_CODE);
                } else if (key == Constant.GET_FILE_CODE) {
                    startUploadFile();
                } else if (key == Constant.GET_TAGS_CODE) {
                    startPickTags();
                } else if (key == Constant.GET_LINKS_CODE) {
                    startAddLink();
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
        Glide.with(this).load(viewModel.personProfile.getPicture()).into(userAvatar);

        subscribeMemoObservable();
        subscribeDeleteMemoObservable();
        subscribeViewModelObservable();
        getAllMemo();
    }

    private void startUploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, GET_FILE_CODE);
    }

    private void startPickTags() {
        startActivityForResult(new Intent(getActivity(), PickTagsActivity.class), GET_TAGS);
    }

    private void startAddLink() {
        startActivityForResult(new Intent(getActivity(), PreviewActivity.class), GET_PREVIEW_LINK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(getActivity(), UploadActivity.class);
        if (requestCode == GET_FILE_CODE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                intent.putExtra(Constant.GET_FILE_CODE, data.getData().toString());
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
            startActivityForResult(intent, UPLOAD_MEMO_CODE);
        } else if (requestCode == GET_TAGS) {
            if (resultCode == RESULT_OK) {
                intent.putExtra(Constant.GET_TAGS_CODE, data.getParcelableArrayListExtra("arrayTag"));
                intent.putExtra("arrayTagIds", data.getStringArrayListExtra("arrayTagIds"));
                Log.d(TAG, "onActivityResult: " + data.getStringArrayListExtra("arrayTagIds"));
            } else {
                Log.d(TAG, "onActivityResult: failure" );
            }
            startActivityForResult(intent, UPLOAD_MEMO_CODE);
        } else if (requestCode == GET_PREVIEW_LINK) {
            if (resultCode == RESULT_OK) {
                intent.putExtra(Constant.GET_LINKS_CODE, (Link) data.getParcelableExtra("previewLink"));
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
            startActivityForResult(intent, UPLOAD_MEMO_CODE);
        } else if (requestCode == UPLOAD_MEMO_CODE) {
            if (resultCode == RESULT_OK) {
                Diary diary = data.getParcelableExtra("create_memo");
                diary.setUploading(true);
                viewModel.createDiary(diary);
                adapter.addMemo(diary);
            }
        }
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

    private void subscribeViewModelObservable() {
        viewModel.getViewModelLiveData().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.CREATE_DIARY_KEY) {
                    Pair<Utils.State, Diary> pair = (Pair<Utils.State, Diary>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            adapter.removeAtPosition(0);
                            adapter.addMemo(pair.second);
                            break;
                        case NO_INTERNET:
                            Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key == Constant.GET_DIARY_BY_ID_KEY) {

                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}