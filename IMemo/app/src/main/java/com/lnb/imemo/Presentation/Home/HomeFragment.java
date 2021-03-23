package com.lnb.imemo.Presentation.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
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
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lnb.imemo.Data.Repository.Model.SharedUser;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.Home.RecyclerView.DetailSharedUserRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.FilterRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.HomeRecyclerViewAdapter;
import com.lnb.imemo.Presentation.Home.RecyclerView.SimpleSectionedRecyclerViewAdapter;
import com.lnb.imemo.Presentation.MemoPreview.MemoPreviewActivity;
import com.lnb.imemo.Presentation.PickTag.PickTagsActivity;
import com.lnb.imemo.Presentation.PreviewImage.PreviewImageActivity;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.Presentation.UploadActivity.UploadActivity;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener {
    private static final String TAG = "HomeFragment";
    private SwipeRefreshLayout homeSwipeRefreshLayout;
    private DrawerLayout drawerLayout;

    // slide bar
    private TextView filterMemoTotal, filerMemoByFilter;
    private LinearLayout filterMemoResetHighLight, filterMemoResetTime, filterMemoResetTag;
    private RecyclerView filterMemoHighLightRecyclerView, filterMemoTimeRecyclerView, filterMemoTagRecyclerView;
    private Button filterButton;
    private ProgressBar loadMoreProgressBar;
    private TextView noMoreMemoText;
    private FilterRecyclerViewAdapter tagFilterAdapter;
    private EditText searchText;
    private TextView allMyMemo, allMemoSharedWithMe;
    private LinearLayout filterArea;
    private NestedScrollView homeNestedScrollView;


    // var
    private HomeRecyclerViewAdapter adapter;
    private final HomeViewModel viewModel;
    private int currentChoosedDiaryPosition = -1;
    private final int GET_FILE_CODE = 1;
    private final int GET_TAGS = 2;
    private final int GET_PREVIEW_LINK = 3;
    private final int UPLOAD_MEMO_CODE = 4;
    private final PublishSubject<Pair<String, Object>> centerObserver;
    private final PublishSubject<Pair<String, String>>  filterTimeObservable = PublishSubject.create();
    private final PublishSubject<Pair<String, String>> filterTagObservable = PublishSubject.create();
    private final PublishSubject<Pair<String, String>> filterHighLightObservable = PublishSubject.create();

    private String searchKey;
    private Boolean isLoadMore = false;
    private Boolean isLoading = false;
    String highLightKey = null;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private ShimmerFrameLayout shimerContainer;
    private int currentPinDiary = -1;
    private String homeViewMode = "allMyMemo"; // allMyMemo or allMemoSharedWithMe


    // start init
    public HomeFragment(PublishSubject<Pair<String, Object>> centerObservable) {
        viewModel = new HomeViewModel();
        this.centerObserver = centerObservable;
    }

    // end init

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }

    // start init
    @SuppressLint("CheckResult")
    private void init(View view) {
        subscribeCenterObservable();

        RecyclerView homeRecyclerView = view.findViewById(R.id.home_recyclerView);
        homeSwipeRefreshLayout = view.findViewById(R.id.home_refresh_layout);
        CircleImageView userAvatar = view.findViewById(R.id.home_user_avatar);
        ImageView homeFilter = view.findViewById(R.id.home_filter);
        homeFilter.setOnClickListener(this);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerListener(this);
        filterMemoTotal = view.findViewById(R.id.filter_memo_total);
        filerMemoByFilter = view.findViewById(R.id.filter_memo_by_filter);
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
        ImageView searchIcon = view.findViewById(R.id.search_icon);
        allMyMemo = view.findViewById(R.id.all_my_memo_textView);
        allMemoSharedWithMe = view.findViewById(R.id.all_memo_shared_with_me);
        filterArea = view.findViewById(R.id.filter_area);
        AppBarLayout appBarLayout = view.findViewById(R.id.appBarLayout);
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        homeViewModeListener();

        searchIcon.setOnClickListener(this);
        searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!searchText.getText().toString().equals("")) {
                    searchKey = searchText.getText().toString();
                    filterDiary();
                    searchText.setText("");
                    searchText.setCursorVisible(false);
                }
            }
            return false;
        });



        shimerContainer.startShimmer();

        Log.d(TAG, "init: " + viewModel.listDiary.size());
        if (viewModel.listDiary.size() == 0) {
            getAllMemo(null, null, null, null, null, null);
            subscribeDeleteMemoObservable();
            subscribeViewModelObservable();
            subscribeFilterObservable();
        }

        setupFilterView();
        // init var
        adapter = new HomeRecyclerViewAdapter((ArrayList<Diary>) viewModel.listDiary, false);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        homeRecyclerView.setAdapter(adapter);

        homeRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Log.d(TAG, "onChildViewDetachedFromWindow: " + view);
            }
        });

        subscribeHomeRecyclerViewObservable();


        homeSwipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.listDiary = new ArrayList<>();
            if (loadMoreProgressBar.getVisibility() == View.VISIBLE) {
                loadMoreProgressBar.setVisibility(View.GONE);
            }
            if (noMoreMemoText.getVisibility() == View.VISIBLE) {
                noMoreMemoText.setVisibility(View.GONE);
            }
            if (homeViewMode.equals("allMyMemo")) {
                filterDiary();
            } else {
                getAllMemoSharedWithMe(null, null, null, null, null, null);
            }
        });


        // load user avatar
        Glide.with(this).load(viewModel.personProfile.getPicture()).into(userAvatar);
        if (viewModel.listTags.size() == 0) {
            viewModel.getAllTags();
        }

        homeNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if (viewModel.listDiary.size() < viewModel.getTotalFilterMemo()) {
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

    private void homeViewModeListener() {
        allMyMemo.setOnClickListener(view -> {
            if (homeViewMode.equals("allMemoSharedWithMe")) {
                homeNestedScrollView.scrollTo(0,0);
                homeViewMode = "allMyMemo";
                allMemoSharedWithMe.setTextColor(Color.parseColor("#999999"));
                allMyMemo.setTextColor(Color.parseColor("#333333"));
                viewModel.setGetTotalMemoInStart(true);
                getAllMemo(null, null, null, null, null, null);
                homeSwipeRefreshLayout.setRefreshing(true);
                filterArea.setVisibility(View.VISIBLE);
            }
        });

        allMemoSharedWithMe.setOnClickListener(view -> {
            if (homeViewMode.equals("allMyMemo")) {
                homeNestedScrollView.scrollTo(0,0);
                homeViewMode = "allMemoSharedWithMe";
                allMemoSharedWithMe.setTextColor(Color.parseColor("#333333"));
                allMyMemo.setTextColor(Color.parseColor("#999999"));
                viewModel.setGetTotalMemoInStart(true);
                if (viewModel.filterTagName.size() != 0 || viewModel.filterTimeName.length() != 0 || viewModel.filterHighLight.length() != 0) {
                    viewModel.filterTagName.clear();
                    filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                    viewModel.filterTimeName = "";
                    filterHighLightObservable.onNext(new Pair<>("update_filter", viewModel.filterHighLight));
                    viewModel.filterHighLight = "";
                    searchKey = null;
                    adapter.removeFilter();
                }
                filterArea.setVisibility(View.GONE);
                getAllMemoSharedWithMe(null, null, null, null, null, null);
                homeSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void subscribeHomeRecyclerViewObservable() {
        adapter.observableAction().observe(this, adapterAction -> {
            String key = adapterAction.first;
            if (key.equals(Constant.DELETE_DIARY_KEY)) {
                Log.d(TAG, "onChanged: " + adapterAction.second);
                showDeleteAlert(adapterAction.second);

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
                showShareDialog(adapterAction.second);
            } else if (key.equals(Constant.UPDATE_DIARY_KEY)) {
                currentChoosedDiaryPosition = adapterAction.second;
                Log.d(TAG, "onChanged: " + currentChoosedDiaryPosition);
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.putExtra("diary_edit", viewModel.listDiary.get(adapterAction.second));
                startActivityForResult(intent, UPLOAD_MEMO_CODE);
            } else if (key.equals("to_preview_memo")) {
                Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
                intent.putExtra("preview_diary", viewModel.listDiary.get(adapterAction.second));
                intent.putExtra("preview_author_name", viewModel.personProfile.getName());
                intent.putExtra("from", "home");
                startActivity(intent);
            } else if (key.equals("pin_diary")) {
                viewModel.pinDiary(adapterAction.second);
            } else if (key.equals("unpin_diary")) {
                viewModel.unpinDiary(adapterAction.second);
                currentPinDiary = adapterAction.second;
            }
        });

        adapter.getFilterObservable().subscribe(new io.reactivex.Observer<Pair<String, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, String> pair) {
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
                        filterTagObservable.onNext(new Pair<>("update_filter", pair.second));
                        viewModel.filterTagName.remove(pair.second);
                        Log.d(TAG, "onChanged: " + viewModel.filterTagName);
                        filterDiary();
                        break;
                    case "remove_filter_high_light":
                        Log.d(TAG, "init: remove filter high light");
                        filterHighLightObservable.onNext(new Pair<>("update_filter", viewModel.filterHighLight));
                        viewModel.filterHighLight = "";
                        highLightKey = null;
                        filterDiary();
                        break;
                    case "reset_all":
                        viewModel.filterTagName.clear();
                        filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                        viewModel.filterTimeName = "";
                        filterHighLightObservable.onNext(new Pair<>("update_filter", viewModel.filterHighLight));
                        viewModel.filterHighLight = "";
                        searchKey = null;
                        viewModel.filterTagName = new ArrayList<>();
                        tagFilterAdapter.reset();
                        filterDiary();
                        break;
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

        adapter.openPreviewImageTrigger.subscribe(new io.reactivex.Observer<Pair<String, ArrayList<Resource>>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
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
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void showDeleteAlert(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.delete_memo_alert, null);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        TextView deleteBtn = view.findViewById(R.id.delete_memo);
        TextView cancelDeleteBtn = view.findViewById(R.id.cancel_delete);
        deleteBtn.setOnClickListener(v -> {
            currentChoosedDiaryPosition = position;
            viewModel.deleteDiary(position);
            viewModel.setTotalMemo(viewModel.getTotalMemo() - 1);
            viewModel.setTotalFilterMemo(viewModel.getTotalFilterMemo() - 1);
            dialog.dismiss();
        });
        cancelDeleteBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void subscribeCenterObservable() {
        centerObserver.subscribe(new io.reactivex.Observer<Pair<String, Object>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, Object> pair) {
                String key = pair.first;
                switch (key) {
                    case "refresh_tag":
                        Log.d(TAG, "onNext: refresh tag");
                        viewModel.getAllTags();
                        break;
                    case "on_top_home_recyclerView":
                        Log.d(TAG, "onNext: on_top_home_recyclerView");
                        homeNestedScrollView.smoothScrollTo(0, 0);
                        break;
                    case "clear_home_fragment":
                        adapter.clearMedia();
                        break;
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }


    @SuppressLint("CheckResult")
    private void subscribeFilterObservable() {

        filterTimeObservable.subscribe(new io.reactivex.Observer<Pair<String, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, String> pair) {
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
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });

        filterTagObservable.subscribe(new io.reactivex.Observer<Pair<String, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, String> pair) {
                if (pair.first.equals("add_filter")) {
                    viewModel.filterTagName.add(pair.second);
                } else if (pair.first.equals("remove_filter")) {
                    viewModel.filterTagName.remove(pair.second);
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });

        filterHighLightObservable.subscribe(new io.reactivex.Observer<Pair<String, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, String> pair) {
                String key = pair.first;
                if (key.equals("add_filter")) {
                    viewModel.filterHighLight = pair.second;
                } else if (key.equals("remove_filter")) {
                    viewModel.filterHighLight = "";
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }


    private void setupFilterView() {
        filterButton.setOnClickListener(this);

        ArrayList<String> listHighLightFilterItem = new ArrayList<>();
        listHighLightFilterItem.add("Đã ghim");
        FilterRecyclerViewAdapter highLightFilterAdapter = new FilterRecyclerViewAdapter(listHighLightFilterItem, filterHighLightObservable);
        filterMemoHighLightRecyclerView.setAdapter(highLightFilterAdapter);
        filterMemoHighLightRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        filterMemoResetHighLight.setOnClickListener(v -> {
            if (listHighLightFilterItem.contains(viewModel.filterHighLight)) {
                Log.d(TAG, "setupFilterView: reset filter high light");
                filterHighLightObservable.onNext(new Pair<>("update_filter", viewModel.filterHighLight));
                viewModel.filterTimeName = "";
            }
        });


        ArrayList<String> listTimeFilterItem = new ArrayList<>();
        listTimeFilterItem.add("Hôm nay");
        listTimeFilterItem.add("Hôm qua");
        listTimeFilterItem.add("1 tuần trước");
        listTimeFilterItem.add("1 tháng trước");
        listTimeFilterItem.add("1 năm trước");
        FilterRecyclerViewAdapter timeFilterAdapter = new FilterRecyclerViewAdapter(listTimeFilterItem, filterTimeObservable);
        filterMemoTimeRecyclerView.setAdapter(timeFilterAdapter);
        filterMemoTimeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));


        tagFilterAdapter = new FilterRecyclerViewAdapter(viewModel.listFilterTags, filterTagObservable);
        tagFilterAdapter.setListChoosed(viewModel.filterTagName);
        filterMemoTagRecyclerView.setAdapter(tagFilterAdapter);
        filterMemoTagRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        filterMemoResetTime.setOnClickListener(v -> {
            if (listTimeFilterItem.contains(viewModel.filterTimeName)) {
                filterTimeObservable.onNext(new Pair<>("update_filter", viewModel.filterTimeName));
                viewModel.filterTimeName = "";
            }
        });

        filterMemoResetTag.setOnClickListener(v -> {
            viewModel.filterTagName.clear();
            tagFilterAdapter.reset();
        });
    }

    private void showShareDialog(int position) {
        viewModel.getSharedEmails();
        viewModel.getSharedUser(position);
        final ArrayList[] listSharedEmails = new ArrayList[]{new ArrayList<>()};

        Diary diary = viewModel.listDiary.get(position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.share_memo_layout, null);
        dialogBuilder.setView(view);


        TextView previewMemo = view.findViewById(R.id.preview_memo);
        AutoCompleteTextView email_input = view.findViewById(R.id.input_email);
        LinearLayout sharedUserAvatarArea = view.findViewById(R.id.sharedUserAvatarArea);
        ArrayList<CircleImageView> listSharedUserAvatar = new ArrayList<>();
        CircleImageView userAvatar1 = view.findViewById(R.id.sharedUserAvatar1);
        CircleImageView userAvatar2 = view.findViewById(R.id.sharedUserAvatar2);
        CircleImageView userAvatar3 = view.findViewById(R.id.sharedUserAvatar3);
        CircleImageView userAvatar4 = view.findViewById(R.id.sharedUserAvatarMore);
        listSharedUserAvatar.add(userAvatar1);
        listSharedUserAvatar.add(userAvatar2);
        listSharedUserAvatar.add(userAvatar3);
        listSharedUserAvatar.add(userAvatar4);
        View sharedUserAvatarBlackLayout = view.findViewById(R.id.sharedUserAvatarBlackLayout);
        TextView sharedUserAvatarMoreTextView = view.findViewById(R.id.sharedUserAvatarMoreTextView);

        viewModel.sharedEmailPublishSubject.subscribe(new io.reactivex.Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull ArrayList<String> strings) {
                listSharedEmails[0] = strings;
                String[] listEmailArray = new String[listSharedEmails[0].size()];
                for (int i = 0; i < listSharedEmails[0].size(); i++) {
                    listEmailArray[i] = listSharedEmails[0].get(i).toString();
                }
                ArrayAdapter<String> adapterListSharedEmail = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listEmailArray);
                email_input.setAdapter(adapterListSharedEmail);
                email_input.setThreshold(2);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });

        viewModel.sharedUserPublishSubject.subscribe(new io.reactivex.Observer<List<SharedUser>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNext(@io.reactivex.annotations.NonNull List<SharedUser> listSharedUsers) {
                Log.d(TAG, "onNext: " + listSharedUsers.toString());
                if (listSharedUsers.size() > 0) {
                    int count = 0;
                    sharedUserAvatarArea.setVisibility(View.VISIBLE);
                    for (SharedUser sharedUser : listSharedUsers) {
                        if (sharedUser.getReceiver() != null) {
                            listSharedUserAvatar.get(count).setVisibility(View.VISIBLE);
                            Log.d(TAG, "onNext: " + listSharedUsers.get(count).getReceiver().getPicture());
                            Glide.with(getActivity())
                                    .load(listSharedUsers.get(count).getReceiver().getPicture())
                                    .into(listSharedUserAvatar.get(count));
                            count++;
                        }
                    }
                   if (count >= 5) {
                       sharedUserAvatarBlackLayout.setVisibility(View.VISIBLE);
                       sharedUserAvatarMoreTextView.setVisibility(View.VISIBLE);
                       sharedUserAvatarMoreTextView.setText("+" + (listSharedUsers.size() - 3));
                   }
                   sharedUserAvatarArea.setOnClickListener(v -> showDetailSharedUsers(listSharedUsers));
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });

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

        switchShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.publicDiary(diary);
            } else {
                viewModel.privateDiary(diary);
            }
            shareProgressBar.setVisibility(View.VISIBLE);
        });


        previewMemo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MemoPreviewActivity.class);
            intent.putExtra("preview_diary", viewModel.listDiary.get(position));
            intent.putExtra("preview_author_name", viewModel.personProfile.getName());
            intent.putExtra("from", "home");
            startActivity(intent);
        });


        AlertDialog alertDialog = dialogBuilder.create();

        shareButton.setOnClickListener(v -> {
            viewModel.shareDiary(diary.getId(), email_input.getText().toString());
            alertDialog.dismiss();
        });

        dialogEscape.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();

        viewModel.getViewModelLiveData().observe(this, responseRepo -> {
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
        });
    }

    private void showDetailSharedUsers(List<SharedUser> listSharedUsers) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.detail_shared_user_layout, null);
        dialogBuilder.setView(view);
        ImageButton escapeButton = view.findViewById(R.id.detail_shared_user_escape);
        RecyclerView detailUserSharedRecyclerView = view.findViewById(R.id.detailSharedUserRecyclerView);
        DetailSharedUserRecyclerViewAdapter adapter = new DetailSharedUserRecyclerViewAdapter(listSharedUsers);
        detailUserSharedRecyclerView.setAdapter(adapter);
        detailUserSharedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        escapeButton.setOnClickListener(v -> {
            dialog.dismiss();
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
            if (resultCode == RESULT_OK && Objects.requireNonNull(data).getData() != null) {
                intent.putExtra(Constant.GET_FILE_CODE, data.getData().toString());
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
            startActivityForResult(intent, UPLOAD_MEMO_CODE);
        } else if (requestCode == GET_TAGS) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                intent.putExtra(Constant.GET_TAGS_CODE, data.getParcelableArrayListExtra("arrayTag"));
                intent.putExtra("arrayTagIds", data.getStringArrayListExtra("arrayTagIds"));
                Log.d(TAG, "onActivityResult: " + data.getStringArrayListExtra("arrayTagIds"));
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
            startActivityForResult(intent, UPLOAD_MEMO_CODE);
        } else if (requestCode == GET_PREVIEW_LINK) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                intent.putExtra(Constant.GET_LINKS_CODE, (Link) data.getParcelableExtra("previewLink"));
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
            startActivityForResult(intent, UPLOAD_MEMO_CODE);
        } else if (requestCode == UPLOAD_MEMO_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                if (data.getParcelableExtra("create_memo") != null) {
                    Diary diary = data.getParcelableExtra("create_memo");
                    assert diary != null;
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

    private void getAllMemo(String query, ArrayList<String> tagIds, String fromDate, String toDate, String lastId, Boolean pinned) {
        int pageNumber = 1;
        viewModel.getDiaries(query,
                tagIds,
                pageNumber,
                Constant.pageSize,
                fromDate,
                toDate,
                lastId,
                pinned
        );
    }

    private void getAllMemoSharedWithMe(String query, ArrayList<String> tagIds, String fromDate, String toDate, String lastId, Boolean pinned) {
        int pageNumber = 1;
        viewModel.getDiariesSharedWithMe(query,
                tagIds,
                pageNumber,
                Constant.pageSize,
                fromDate,
                toDate,
                lastId,
                pinned
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
        viewModel.getViewModelLiveData().observe(this, responseRepo -> {
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
                if (pair.first == Utils.State.SUCCESS) {
                    viewModel.listTags = pair.second;
                    Log.d(TAG, "subscribeViewModelObservable: get all tags key " + pair.second);
                    ArrayList<String> listTags = new ArrayList<>();
                    for (Tags tags : pair.second) {
                        if (tags.getIsDefault()) {
                            listTags.add(tags.getName());
                            viewModel.mIdTagByNameHashMap.put(tags.getName(), tags.getId());
                            viewModel.mTagByNameHashMap.put(tags.getName(), tags);
                        }
                    }
                    tagFilterAdapter.setData(listTags);
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
                            adapter.setSharedMode(false);
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
            } else if (key.equals(Constant.GET_DIARIES_SHARED_WITH_ME)) {
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
                            adapter.setSharedMode(true);
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
            } else if (key.equals("pin_diary")) {
                Log.d(TAG, "subscribeViewModelObservable: pin diary");
                Utils.State state = (Utils.State) responseRepo.getData();
                switch (state) {
                    case SUCCESS:
                        Toast.makeText(getActivity(), "Ghim memo thành công", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILURE:
                        Toast.makeText(getActivity(), "Ghim memo không thành công", Toast.LENGTH_SHORT).show();
                        break;
                    case NO_INTERNET:
                        Toast.makeText(getContext(), "Vui lòng kiểm tra lại kết nối của bạn!", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else if (key.equals("unpin_diary")) {
                Log.d(TAG, "subscribeViewModelObservable: unpin diary");
                Utils.State state = (Utils.State) responseRepo.getData();
                switch (state) {
                    case SUCCESS:
                        if (highLightKey != null && highLightKey.equals("Đã ghim")) {
                            adapter.removeAtPosition(currentPinDiary);
                        }
                        Toast.makeText(getActivity(), "Gỡ ghim memo thành công", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILURE:
                        Toast.makeText(getActivity(), "Gỡ ghim memo không thành công", Toast.LENGTH_SHORT).show();
                        break;
                    case NO_INTERNET:
                        Toast.makeText(getContext(), "Vui lòng kiểm tra lại kết nối của bạn!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
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
                if (!searchText.getText().toString().equals("")) {
                    searchKey = searchText.getText().toString();
                    filterDiary();
                    searchText.setText("");
                    searchText.setCursorVisible(false);
                }
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void filterDiary() {
        Boolean isPined = null;
        if (viewModel.filterHighLight.equals("Đã ghim")) {
            isPined = true;
            highLightKey = "Đã ghim";
        }
        Log.d(TAG, "filterDiary: " + isPined);
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
        if (searchKey != null || timeKey != null || listTags.size() != 0 || isLoadMore || isPined != null) {
            if (tagIds.size() != 0) {
                Log.d(TAG, "filterDiary: tagSize != 0");
                getAllMemo(searchKey, tagIds, fromDate, toDate, lastId, isPined);
            } else {
                Log.d(TAG, "filterDiary: tag size = 0");
                getAllMemo(searchKey, null, fromDate, toDate, lastId, isPined);
            }
            if (!isLoadMore) {
                adapter.insertFilter(searchKey, timeKey, listTags, highLightKey);
            }
        } else {
            listTags.size();
            Log.d(TAG, "filterDiary: remove all");
            adapter.removeFilter();
            getAllMemo(null, null, null, null, lastId, isPined);
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.destroyMedia();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.clearMedia();
    }
}