package com.lnb.imemo.Presentation.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.Home.RecyclerView.FilterRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.HomeRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.SimpleSectionedRecyclerViewAdapter;
import com.lnb.imemo.Presentation.MemoPreview.MemoPreviewActivity;
import com.lnb.imemo.Presentation.PickTag.PickTagsActivity;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.Presentation.UploadActivity.UploadActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener {
    private static final String TAG = "HomeFragment";
    private static HomeFragment mHomeFragment;
    // ui
    private RecyclerView homeRecyclerView;
    private SwipeRefreshLayout homeSwipeRefreshLayout;
    private DrawerLayout drawerLayout;

    // slide bar
    private TextView filterMemoTotal, filerMemoByFilter, filterMemoToday;
    private LinearLayout filterMemoResetHighLight, filterMemoResetTime, filterMemoResetTag;
    private RecyclerView filterMemoHighLightRecyclerView, filterMemoTimeRecyclerView, filterMemoTagRecyclerView;
    private Button filterButton;

    private CircleImageView userAvatar;
    private ImageView homeFilter;
    private MaterialSearchBar searchBar;
    private FilterRecyclerViewAdapter highLightFilterAdapter, timeFilterAdapter, tagFilterAdapter;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        drawerLayout = view.findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerListener(this);
        searchBar = view.findViewById(R.id.searchBar);
        filterMemoTotal = view.findViewById(R.id.filter_memo_total);
        filerMemoByFilter = view.findViewById(R.id.filter_memo_by_filter);
        filterMemoToday = view.findViewById(R.id.filter_memo_today);
        filterMemoResetHighLight = view.findViewById(R.id.reset_filter_highlight);
        filterMemoResetTime = view.findViewById(R.id.reset_filter_time);
        filterMemoResetTag = view.findViewById(R.id.reset_filter_tag);
        filterMemoHighLightRecyclerView = view.findViewById(R.id.filter_highLight_recyclerView);
        filterMemoTimeRecyclerView = view.findViewById(R.id.filter_time_recyclerView);
        filterMemoTagRecyclerView = view.findViewById(R.id.filter_tag_recyclerview);
        filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        viewModel = new HomeViewModel();
        getAllMemo();
        ArrayList<String> listHighLightFilterItem = new ArrayList<>();
        listHighLightFilterItem.add("Sắp tới");
        highLightFilterAdapter = new FilterRecyclerViewAdapter(listHighLightFilterItem, viewModel.filterTimeName);
        filterMemoHighLightRecyclerView.setAdapter(highLightFilterAdapter);
        filterMemoHighLightRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        ArrayList<String> listTimeFilterItem = new ArrayList<>();
        listTimeFilterItem.add("Hôm nay");
        listTimeFilterItem.add("Hôm qua");
        listTimeFilterItem.add("1 tuần trước");
        listTimeFilterItem.add("1 tháng trước");
        listTimeFilterItem.add("1 năm trước");
        timeFilterAdapter = new FilterRecyclerViewAdapter(listTimeFilterItem, viewModel.filterTimeName);
        filterMemoTimeRecyclerView.setAdapter(timeFilterAdapter);
        filterMemoTimeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        tagFilterAdapter = new FilterRecyclerViewAdapter(viewModel.listFilterTags);
        filterMemoTagRecyclerView.setAdapter(tagFilterAdapter);
        filterMemoTagRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // init var

        adapter = new HomeRecyclerViewAdapter((ArrayList<Diary>) viewModel.listDiary);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        homeRecyclerView.setAdapter(adapter);
        List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.sections, R.id.section_text, adapter);
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
                } else if (key == "share_action") {
                    showShareDialog(adapterAction.second);
                } else if (key == Constant.UPDATE_DIARY_KEY) {
                    currentChoosedDiaryPosition = adapterAction.second;
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    intent.putExtra("diary_edit", viewModel.listDiary.get(adapterAction.second - 1));
                    startActivityForResult(intent, UPLOAD_MEMO_CODE);

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
        viewModel.getAllTags();
        subscribeMemoObservable();
        subscribeDeleteMemoObservable();
        subscribeViewModelObservable();

    }

    private void showShareDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.share_memo_layout, null);
        dialogBuilder.setView(view);

        TextView previewMemo = view.findViewById(R.id.preview_memo);
        TextInputLayout email_input = view.findViewById(R.id.input_email);
        Button shareButton = view.findViewById(R.id.share_memo);
        ImageButton dialogEscape = view.findViewById(R.id.share_memo_escape);

        previewMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
                intent.putExtra("preview_diary", viewModel.listDiary.get(position - 1));
                intent.putExtra("preview_author_name", viewModel.personProfile.getName());
                intent.putExtra("from", "home");
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Diary diary = viewModel.listDiary.get(position);
                if (diary.getStatus().equals("public")) {
                    viewModel.shareDiary(diary.getId(), email_input.getEditText().getText().toString());
                } else {
                    diary.setStatus("public");
                    viewModel.publicDiary(diary, email_input.getEditText().getText().toString());
                }
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();

        dialogEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
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
                Log.d(TAG, "onActivityResult: failure");
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
                if (data.getParcelableExtra("create_memo") != null) {
                    Diary diary = data.getParcelableExtra("create_memo");
                    diary.setUploading(true);
                    viewModel.createDiary(diary);
                    adapter.addMemo(diary);
                }
                if (data.getParcelableExtra("update_memo") != null) {
                    Diary diary = data.getParcelableExtra("update_memo");
                    adapter.updateMemoAt(currentChoosedDiaryPosition - 1, diary);
                }
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

                } else if (key == Constant.GET_ALL_TAGS_KEY) {
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            viewModel.listTags = pair.second;
                            ArrayList<String> listTags = new ArrayList<>();
                            for (Tags tags: pair.second) {
                                listTags.add(tags.getName());
                            }
                            tagFilterAdapter.setData(listTags);

                            break;
                    }
                } else if (key == Constant.SHARE_DIARY) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            Toast.makeText(getContext(), "Chia sẻ thành công", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            Toast.makeText(getContext(), "Chia sẻ lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_filter:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.filter_button:
                break;
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_SETTLING) {
            Log.d(TAG, "onDrawerOpened: ");
            filerMemoByFilter.setText(String.valueOf(adapter.getItemCount() - 1));
            viewModel.getAllTags();
        }
    }
}