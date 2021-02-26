package com.lnb.imemo.Presentation.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Model.User;
import com.lnb.imemo.Presentation.Home.RecyclerView.FilterRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.HomeRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.SimpleSectionedRecyclerViewAdapter;
import com.lnb.imemo.Presentation.MemoPreview.MemoPreviewActivity;
import com.lnb.imemo.Presentation.PickTag.PickTagsActivity;
import com.lnb.imemo.Presentation.PreviewImage.PreviewImageActivity;
import com.lnb.imemo.Presentation.PreviewImage.PreviewImageAdapter;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.Presentation.UploadActivity.UploadActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener {
    private static final String TAG = "HomeFragment";
    private static HomeFragment mHomeFragment;
    private Boolean isStart = false;
    {
        Log.d(TAG, "instance initializer: " + isStart);
    }
    // ui
    private RecyclerView homeRecyclerView;
    private SwipeRefreshLayout homeSwipeRefreshLayout;
    private DrawerLayout drawerLayout;

    // slide bar
    private TextView filterMemoTotal, filerMemoByFilter, filterMemoToday;
    private LinearLayout filterMemoResetHighLight, filterMemoResetTime, filterMemoResetTag;
    private RecyclerView filterMemoHighLightRecyclerView, filterMemoTimeRecyclerView, filterMemoTagRecyclerView;
    private Button filterButton;
    private NestedScrollView homeNestedScrollView;
    private ProgressBar loadMoreProgressBar;
    private TextView noMoreMemoText;

    private CircleImageView userAvatar;
    private ImageView homeFilter;
    private FilterRecyclerViewAdapter highLightFilterAdapter, timeFilterAdapter, tagFilterAdapter;
    private EditText searchText;
    private ImageView searchIcon;


    // var
    private HomeRecyclerViewAdapter adapter;
    private HomeViewModel viewModel;
    private int pageNumber = 1;
    private int currentChoosedDiaryPosition = -1;
    private int GET_FILE_CODE = 1;
    private int GET_TAGS = 2;
    private int GET_PREVIEW_LINK = 3;
    private final int UPLOAD_MEMO_CODE = 4;
    private final PublishSubject<Pair<String, String>> filterTimeObservable = PublishSubject.create();
    private final PublishSubject<Pair<String, String>> filterTagObservable = PublishSubject.create();
    private String searchKey;
    private Boolean isLoadMore = false;
    private Boolean isLoading = false;
    private int currentChooseDiaryToShare = -1;

    private ShimmerFrameLayout shimerContainer;



    private HomeFragment(Boolean isStart) {
        this.isStart = isStart;
        viewModel = HomeViewModel.getHomeViewModel(isStart);
    }

    public static HomeFragment getHomeFragment(Boolean isStart) {
        if (mHomeFragment == null || isStart) {
            mHomeFragment = new HomeFragment(isStart);
        }
        return mHomeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }

    @SuppressLint("CheckResult")
    private void init(View view) {
        // init ui
        homeRecyclerView = view.findViewById(R.id.home_recyclerView);
        homeSwipeRefreshLayout = view.findViewById(R.id.home_refresh_layout);

        userAvatar = view.findViewById(R.id.home_user_avatar);
        homeFilter = view.findViewById(R.id.home_filter);
        homeFilter.setOnClickListener(this);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerListener(this);
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
        homeNestedScrollView = view.findViewById(R.id.nested_scroll);
        loadMoreProgressBar = view.findViewById(R.id.load_more_progressBar);
        noMoreMemoText = view.findViewById(R.id.no_more_memo);
        loadMoreProgressBar.setVisibility(View.GONE);
        noMoreMemoText.setVisibility(View.GONE);
        shimerContainer = view.findViewById(R.id.shimmer_container);


        searchText = view.findViewById(R.id.search_bar);
        searchIcon = view.findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(this);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchKey = searchText.getText().toString();
                    filterDiary();
                    searchText.setText("");
                }
                return false;
            }
        });

        if (isStart) {
            Log.d(TAG, "init: " + isStart);
            shimerContainer.startShimmer();
            isStart = false;
        } else {
            Log.d(TAG, "init: istart false");
            shimerContainer.stopShimmer();
            shimerContainer.setVisibility(View.GONE);
        }




        Log.d(TAG, "init: " + viewModel.listDiary.size());
        if (viewModel.listDiary.size() == 0) {
            getAllMemo(null, null, null, null, null);
            subscribeDeleteMemoObservable();
            subscribeViewModelObservable();
            subscribeFilterObservable();
        }

        setupFilterView();
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
                    Log.d(TAG, "onChanged: " + adapterAction.second);
                    currentChoosedDiaryPosition = adapterAction.second;
                    viewModel.deleteDiary(adapterAction.second);
                    viewModel.setTotalMemo(viewModel.getTotalMemo() - 1);
                    viewModel.setTotalFilterMemo(viewModel.getTotalFilterMemo() - 1);
                } else if (key.equals(Constant.CREATE_DIARY_KEY)) {
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    startActivityForResult(intent, UPLOAD_MEMO_CODE);
                } else if (key.equals(Constant.GET_FILE_CODE)) {
                    startUploadFile();
                } else if (key.equals(Constant.GET_TAGS_CODE)) {
                    startPickTags();
                } else if (key.equals(Constant.GET_LINKS_CODE)) {
                    startAddLink();
                } else if (key.equals("share_action")) {
                    currentChooseDiaryToShare = adapterAction.second;
                    viewModel.getSharedEmails();
                } else if (key.equals(Constant.UPDATE_DIARY_KEY)) {
                    currentChoosedDiaryPosition = adapterAction.second;
                    Log.d(TAG, "onChanged: " + currentChoosedDiaryPosition);
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    intent.putExtra("diary_edit", viewModel.listDiary.get(adapterAction.second));
                    startActivityForResult(intent, UPLOAD_MEMO_CODE);
                }
            }
        });

        adapter.getFilterObservable().subscribe(pair -> {
            String key = pair.first;
            switch (key) {
                case "remove_filter_search":
                    searchKey = null;
                    filterDiary();
                    break;
                case "remove_filter_time":
                    filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                    viewModel.filterTimeName = "";
                    filterDiary();
                    break;
                case "remove_filter_tag":
                    Log.d(TAG, "onChanged: " + pair.second);
                    viewModel.filterTagName.remove(pair.second);
                    Log.d(TAG, "onChanged: " + viewModel.filterTagName);
                    filterDiary();
                    break;
                case "reset_all":
                    viewModel.filterTagName.clear();
                    filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                    viewModel.filterTimeName = "";
                    searchKey = null;
                    filterDiary();
                    break;
            }
        });

        adapter.openPreviewImageTrigger.subscribe(new io.reactivex.Observer<Pair<String, ArrayList<Resource>>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, ArrayList<Resource>> pair) {
                String key = pair.first;
                if (key.equals("open_image_preview")) {
                    Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
                    intent.putExtra("data", pair.second);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        homeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.listDiary = new ArrayList<>();
                if (loadMoreProgressBar.getVisibility() == View.VISIBLE) {
                    loadMoreProgressBar.setVisibility(View.GONE);
                }
                if (noMoreMemoText.getVisibility() == View.VISIBLE) {
                    noMoreMemoText.setVisibility(View.GONE);
                }
                filterDiary();
            }
        });



        // load user avatar
        Glide.with(this).load(viewModel.personProfile.getPicture()).into(userAvatar);
        if (viewModel.listTags.size() == 0) {
            viewModel.getAllTags();
        }

        homeNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if (viewModel.listDiary.size() < viewModel.getTotalMemo()) {
                    noMoreMemoText.setVisibility(View.INVISIBLE);
                    loadMoreProgressBar.setVisibility(View.VISIBLE);
                    loadMoreProgressBar.bringToFront();
                    isLoadMore = true;
                    if (!isLoading) {
                        filterDiary();
                    }
                    isLoading = true;
                } else {
                    Log.d(TAG, "init: no more");
                    isLoadMore = false;
                    noMoreMemoText.setVisibility(View.VISIBLE);
                    noMoreMemoText.bringToFront();
                    if (viewModel.listDiary.size() == 0) {
                        noMoreMemoText.setText("Không có memo nào!");
                    } else {
                        noMoreMemoText.setText("Không còn memo nào!");
                    }
                }
            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isStart = false;
    }

    @SuppressLint("CheckResult")
    private void subscribeFilterObservable() {
        filterTimeObservable.subscribe((Pair<String, String> pair) -> {
            Log.d(TAG, "accept: " + pair.toString());
            if (pair.first.equals("add_filter")) {
                String currentFilter = viewModel.filterTimeName;
                viewModel.filterTimeName = pair.second;
                Log.d(TAG, "accept: current " + currentFilter + "new: " + pair.second);
                if (!currentFilter.equals("")) {
                    filterTimeObservable.onNext(new Pair<>("update_filter", currentFilter));
                }
            } else if (pair.first.equals("remove_filter")) {
                viewModel.filterTimeName = "";
            }
        });


        filterTagObservable.subscribe(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(Pair<String, String> pair) throws Exception {
                if (pair.first == "add_filter") {
                    viewModel.filterTagName.add(pair.second);
                } else if (pair.first == "remove_filter") {
                    viewModel.filterTagName.remove(pair.second);
                }
            }
        });
    }


    private void setupFilterView() {
        filterButton.setOnClickListener(this);

        ArrayList<String> listHighLightFilterItem = new ArrayList<>();
        listHighLightFilterItem.add("Sắp tới");
        highLightFilterAdapter = new FilterRecyclerViewAdapter(listHighLightFilterItem, filterTimeObservable);
        filterMemoHighLightRecyclerView.setAdapter(highLightFilterAdapter);
        filterMemoHighLightRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        ArrayList<String> listTimeFilterItem = new ArrayList<>();
        listTimeFilterItem.add("Hôm nay");
        listTimeFilterItem.add("Hôm qua");
        listTimeFilterItem.add("1 tuần trước");
        listTimeFilterItem.add("1 tháng trước");
        listTimeFilterItem.add("1 năm trước");
        timeFilterAdapter = new FilterRecyclerViewAdapter(listTimeFilterItem, filterTimeObservable);
        filterMemoTimeRecyclerView.setAdapter(timeFilterAdapter);
        filterMemoTimeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        tagFilterAdapter = new FilterRecyclerViewAdapter(viewModel.listFilterTags, filterTagObservable);
        tagFilterAdapter.setListChoosed(viewModel.filterTagName);
        filterMemoTagRecyclerView.setAdapter(tagFilterAdapter);
        filterMemoTagRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        filterMemoResetHighLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listHighLightFilterItem.contains(viewModel.filterTimeName)) {
                    filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                    viewModel.filterTimeName = "";
                }
            }
        });

        filterMemoResetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listTimeFilterItem.contains(viewModel.filterTimeName)) {
                    filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                    viewModel.filterTimeName = "";
                }
            }
        });

        filterMemoResetTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.filterTagName.clear();
                tagFilterAdapter.reset();
            }
        });
    }

    private void showShareDialog(ArrayList<String> listSharedEmails, int position) {
        Diary diary = viewModel.listDiary.get(position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.share_memo_layout, null);
        dialogBuilder.setView(view);


        TextView previewMemo = view.findViewById(R.id.preview_memo);
        AutoCompleteTextView email_input = view.findViewById(R.id.input_email);
        String[] listEmailArray = new String[listSharedEmails.size()];
        for (int i = 0; i < listSharedEmails.size(); i++) {
            listEmailArray[i] = listSharedEmails.get(i).toString();
        }
        ArrayAdapter<String> adapterListSharedEmail = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listEmailArray);
        email_input.setAdapter(adapterListSharedEmail);
        email_input.setThreshold(2);


        Button shareButton = view.findViewById(R.id.share_memo);
        ImageButton dialogEscape = view.findViewById(R.id.share_memo_escape);
        SwitchMaterial switchShare = view.findViewById(R.id.switch_share);
        ProgressBar shareProgressBar = view.findViewById(R.id.share_progressBar);
        shareProgressBar.setVisibility(View.INVISIBLE);




        if (diary.getStatus().equals("private")) {
            switchShare.setChecked(false);
            previewMemo.setVisibility(View.GONE);
            email_input.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
        } else if (diary.getStatus().equals("public")) {
            switchShare.setChecked(true);
        }

        switchShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewModel.publicDiary(diary);
                } else {
                    viewModel.privateDiary(diary);
                }
                shareProgressBar.setVisibility(View.VISIBLE);
            }
        });


        previewMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
                intent.putExtra("preview_diary", viewModel.listDiary.get(position));
                intent.putExtra("preview_author_name", viewModel.personProfile.getName());
                intent.putExtra("from", "home");
                startActivity(intent);
            }
        });




        AlertDialog alertDialog = dialogBuilder.create();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.shareDiary(diary.getId(), email_input.getText().toString());
                alertDialog.dismiss();
            }
        });

        dialogEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        viewModel.getViewModelLiveData().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals("public_diary")) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            shareProgressBar.setVisibility(View.INVISIBLE);
                            previewMemo.setVisibility(View.VISIBLE);
                            email_input.setVisibility(View.VISIBLE);
                            shareButton.setVisibility(View.VISIBLE);
                            break;
                        case FAILURE:
                            shareProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Chia sẻ memo không thành công!", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            shareProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Vui lòng kiểm tra lại kết nối của bạn!", Toast.LENGTH_SHORT).show();
                            break;
                    }

                } else if (key.equals("private_diary")) {
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            shareProgressBar.setVisibility(View.INVISIBLE);
                            previewMemo.setVisibility(View.GONE);
                            email_input.setVisibility(View.GONE);
                            shareButton.setVisibility(View.GONE);
                            break;
                        case FAILURE:
                            shareProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Khóa memo không thành công!", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            shareProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Vui lòng kiểm tra lại kết nối của bạn!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
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
                    viewModel.setTotalMemo(viewModel.getTotalMemo() + 1);
                    viewModel.setTotalFilterMemo(viewModel.getTotalFilterMemo() + 1);
                }
                if (data.getParcelableExtra("update_memo") != null) {
                    Log.d(TAG, "onActivityResult: " + currentChoosedDiaryPosition);
                    Diary diary = data.getParcelableExtra("update_memo");
                    adapter.updateMemoAt(currentChoosedDiaryPosition, diary);
                    viewModel.listDiary.set(currentChoosedDiaryPosition, diary);
                }
            }
        }
    }

    private void getAllMemo(String query, ArrayList<String> tagIds, String fromDate, String toDate, String lastId) {
        viewModel.getDiaries(query,
                tagIds,
                pageNumber,
                Constant.pageSize,
                fromDate,
                toDate,
                lastId
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

    private void subscribeViewModelObservable() {
        viewModel.getViewModelLiveData().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key.equals(Constant.CREATE_DIARY_KEY)) {
                    Pair<Utils.State, Diary> pair = (Pair<Utils.State, Diary>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            adapter.removeAtPosition(0);
                            adapter.addMemo(pair.second);
                            viewModel.listDiary.add(0, pair.second);
                            break;
                        case NO_INTERNET:
                            Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key.equals(Constant.GET_DIARY_BY_ID_KEY)) {

                } else if (key.equals(Constant.GET_ALL_TAGS_KEY)) {
                    Pair<Utils.State, ArrayList<Tags>> pair = (Pair<Utils.State, ArrayList<Tags>>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            viewModel.listTags = pair.second;
                            ArrayList<String> listTags = new ArrayList<>();
                            for (Tags tags : pair.second) {
                                listTags.add(tags.getName());
                                viewModel.mIdTagByNameHashMap.put(tags.getName(), tags.getId());
                                viewModel.mTagByNameHashMap.put(tags.getName(), tags);
                            }
                            tagFilterAdapter.setData(listTags);
                            break;
                    }
                } else if (key.equals(Constant.SHARE_DIARY)) {
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
                } else if (key.equals(Constant.GET_DIARIES_KEY)) {
                    shimerContainer.stopShimmer();
                    shimerContainer.setVisibility(View.GONE);
                    Pair<Utils.State, ArrayList<Diary>> pair = (Pair<Utils.State, ArrayList<Diary>>) responseRepo.getData();
                    switch (pair.first) {
                        case SUCCESS:
                            Log.d(TAG, "onChanged: success ");
                            if (homeSwipeRefreshLayout.isRefreshing()) {
                                homeSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (loadMoreProgressBar.getVisibility() == View.VISIBLE) {
                                loadMoreProgressBar.setVisibility(View.GONE);
                            }
                            if (isLoadMore) {
                                Log.d(TAG, "onChanged: insert");
                                viewModel.listDiary.addAll(pair.second);
                                adapter.insertListMemo(pair.second);
                                isLoadMore = false;
                                isLoading = false;
                            } else {
                                Log.d(TAG, "onChanged: update");
                                viewModel.listDiary = pair.second;
                                adapter.updateListMemo(viewModel.listDiary);
                            }
                            if (viewModel.listDiary.size() == 0) {
                                noMoreMemoText.setVisibility(View.VISIBLE);
                                noMoreMemoText.setText("Không có memo nào!");
                            }
                            filerMemoByFilter.setText(String.valueOf(viewModel.getTotalFilterMemo()));
                            filterMemoTotal.setText(String.valueOf(viewModel.getTotalMemo()));
                            break;
                        case FAILURE:
                            if (loadMoreProgressBar.getVisibility() == View.VISIBLE) {
                                loadMoreProgressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getContext(), "Lỗi. Xin vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            if (loadMoreProgressBar.getVisibility() == View.VISIBLE) {
                                loadMoreProgressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key.equals(Constant.GET_SHARED_EMAILS)) {
                    ArrayList<String> listEmail = (ArrayList<String>) responseRepo.getData();
                    if (listEmail == null) {
                        listEmail = new ArrayList<>();
                    }
                    Log.d(TAG, "onChanged: " + listEmail);
                    showShareDialog(listEmail, currentChooseDiaryToShare);
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
                filterDiary();
                break;
            case R.id.search_icon:
                searchKey = searchText.getText().toString();
                filterDiary();
                break;
        }
    }

    private void filterDiary() {
        ArrayList<String> tagIds = new ArrayList<>();
        ArrayList<Tags> listTags = new ArrayList<>();
        for (String tagName : viewModel.filterTagName) {
            tagIds.add(viewModel.mIdTagByNameHashMap.get(tagName));
            listTags.add(viewModel.mTagByNameHashMap.get(tagName));
        }
        String fromDate;
        String toDate;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        Calendar currentTime = Calendar.getInstance();
        String timeKey;
        Log.d(TAG, "filterDiary: " + viewModel.filterTimeName);
        switch (viewModel.filterTimeName) {
            case "Hôm nay":
                String fromDateStr = simpleDateFormat.format(currentTime.getTime());
                fromDate = fromDateStr.split("T")[0] + "T00:00:00." + fromDateStr.split("\\.")[1];
                toDate = simpleDateFormat.format(currentTime.getTime());
                timeKey = toDate.split("T")[0];
                break;
            case "Hôm qua":
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DATE, -1);
                String yesterdayDateStr = simpleDateFormat.format(yesterday.getTime());
                fromDate = yesterdayDateStr.split("T")[0] + "T00:00:00." + yesterdayDateStr.split("\\.")[1];
                toDate = yesterdayDateStr.split("T")[0] + "T23:59:59." + yesterdayDateStr.split("\\.")[1];
                timeKey = toDate.split("T")[0];
                break;
            case "1 tuần trước":
                Calendar weekAgo = Calendar.getInstance();
                weekAgo.add(Calendar.DATE, -7);
                fromDate = simpleDateFormat.format(weekAgo.getTime());
                toDate = simpleDateFormat.format(currentTime.getTime());
                timeKey = fromDate.split("T")[0] + " - " + toDate.split("T")[0];
                break;
            case "1 tháng trước":
                Calendar monthAgo = Calendar.getInstance();
                monthAgo.add(Calendar.MONTH, -1);
                fromDate = simpleDateFormat.format(monthAgo.getTime());
                toDate = simpleDateFormat.format(currentTime.getTime());
                timeKey = fromDate.split("T")[0] + " - " + toDate.split("T")[0];
                break;
            case "1 năm trước":
                Calendar yearAgo = Calendar.getInstance();
                yearAgo.add(Calendar.YEAR, -1);
                fromDate = simpleDateFormat.format(yearAgo.getTime());
                toDate = simpleDateFormat.format(currentTime.getTime());
                timeKey = fromDate.split("T")[0] + " - " + toDate.split("T")[0];
                break;
            default:
                fromDate = null;
                toDate = null;
                timeKey = null;
        }

        Log.d(TAG, "filterDiary: " + searchKey);
        Log.d(TAG, "filterDiary: " + timeKey);
        Log.d(TAG, "filterDiary: " + listTags);
        Log.d(TAG, "filterDiary: " + isLoadMore);
        Log.d(TAG, "filterDiary: " + fromDate);
        Log.d(TAG, "filterDiary: " + toDate);
        String lastId = null;
        if (viewModel.listDiary.size() > 0 && isLoadMore) {
            lastId = viewModel.listDiary.get(viewModel.listDiary.size() - 1).getId();
        }
        Log.d(TAG, "filterDiary: lastId: " + lastId);
        if (searchKey != null || timeKey != null || listTags.size() != 0 || isLoadMore) {
            if (tagIds.size() != 0) {
                Log.d(TAG, "filterDiary: tagSize != 0");
                getAllMemo(searchKey, tagIds, fromDate, toDate, lastId);
            } else {
                Log.d(TAG, "filterDiary: tag size = 0");
                getAllMemo(searchKey, null, fromDate, toDate, lastId);
            }
            if (!isLoadMore) {
                adapter.insertFilter(searchKey, timeKey, listTags);
            }
        } else if (searchKey == null && timeKey == null && listTags.size() == 0 && !isLoadMore) {
            Log.d(TAG, "filterDiary: remove all");
            adapter.removeFilter();
            getAllMemo(null, null, null, null, lastId);
        }
        if (!isLoadMore) {
            homeSwipeRefreshLayout.setRefreshing(true);
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
            filerMemoByFilter.setText(String.valueOf(viewModel.getTotalFilterMemo()));
            filterMemoTotal.setText(String.valueOf(viewModel.getTotalMemo()));
            viewModel.getAllTags();
        }
    }
}