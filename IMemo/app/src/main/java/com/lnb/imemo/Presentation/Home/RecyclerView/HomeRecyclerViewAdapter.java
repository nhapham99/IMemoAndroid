package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MediatorLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.Home.TabFragment.AudioFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.FileFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.ImageFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.LinkFragment;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.DateHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerViewAdapter";
    private ArrayList<Diary> listMemo;
    private Context mContext;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private static final int TYPE_HEADER = 0;
    private final int TYPE_NORMAL = 1;
    private final int TYPE_UPLOADING = 2;
    private final int TYPE_NO_TAGS = 3;
    private final int TYPE_NO_TAB = 4;
    private final int TYPE_NO_TAGS_AND_TAB = 5;
    private final int TYPE_FILTER = 6;
    private static final int TYPE_SHARED_NO_TAGS_AND_TAB = 7;
    private static final int TYPE_SHARED_NO_TAGS = 8;
    private static final int TYPE_SHARED_NO_TAB = 9;
    private static final int TYPE_SHARED_NORMAL = 10;
    private Boolean isSharedMode;

    private Boolean isFilter = false;
    private final FilterItemRecyclerViewAdapter filterItemRecyclerViewAdapter;
    private ArrayList<Tags> listFilterTags;
    private String searchKey;
    private String timeKey;
    private final MediatorLiveData<Pair<String, Integer>> actionObservable = new MediatorLiveData<>();
    private final PublishSubject<Pair<String, String>> filterObservable = PublishSubject.create();
    public final PublishSubject<Pair<String, ArrayList<Resource>>> openPreviewImageTrigger = PublishSubject.create();
    private final HashMap<Integer, Set<Fragment>> listFragmentNeedClearMedia = new HashMap<>();

    public HomeRecyclerViewAdapter(ArrayList<Diary> listMemo, Boolean isSharedMode) {
        filterItemRecyclerViewAdapter = new FilterItemRecyclerViewAdapter();
        this.listMemo = listMemo;
        this.isSharedMode = isSharedMode;
    }


    public void setSharedMode(Boolean sharedMode) {
        isSharedMode = sharedMode;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_view_item, parent, false);
            return new HomeRecyclerViewHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_FILTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_filter_layout, parent, false);
            return new HomeFilterViewHolder(view);
        } else if (viewType == TYPE_UPLOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploading_memo_layout, parent, false);
            return new HomeRecyclerViewHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_NO_TAGS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_no_tag, parent, false);
            return new HomeRecyclerViewNoTagsHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_NO_TAB) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_view_no_tab_layout, parent, false);
            return new HomeRecyclerViewNoTabHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_NO_TAGS_AND_TAB) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_view_no_tag_and_tab, parent, false);
            return new HomeRecyclerViewNoTabAndTagHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_view_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_SHARED_NO_TAB) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_with_me_no_tab_item, parent, false);
            return new SharedWithMeNoTabViewHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_SHARED_NO_TAGS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_with_me_no_tag_item, parent, false);
            return new SharedWithMeNoTagsHolder(view, actionObservable);
        } else if (viewType == TYPE_SHARED_NO_TAGS_AND_TAB) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_with_me_no_tag_and_tab_item, parent, false);
            return new SharedWithMeNoTabAndTagViewHolder(view, actionObservable, isSharedMode);
        } else if (viewType == TYPE_SHARED_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_with_me_item, parent, false);
            return new SharedWithMeViewHolder(view, actionObservable, isSharedMode);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int size = 0;
        if (!isSharedMode) {
            if (isFilter) {
                size = size + 2;
            } else {
                size = size + 1;
            }

            if (position == 0 && !isSharedMode) {
                initViewForHeader((HeaderViewHolder) holder);
            } else if (position == 1 && isFilter) {
                initViewForHomeFilter((HomeFilterViewHolder) holder);
            } else if (listMemo.get(position - size).getUploading() != null) {
                initViewForHomeItem((HomeRecyclerViewHolder) holder, position - size);
            } else if (listMemo.get(position - size).getTags().size() == 0 && listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                initViewForHomeNoTabAndTagItem((HomeRecyclerViewNoTabAndTagHolder) holder, position - size);
            } else if (listMemo.get(position - size).getTags().size() == 0) {
                initViewForHomeNoTagItem((HomeRecyclerViewNoTagsHolder) holder, position - size);
            } else if (listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                initViewForHomeNoTabItem((HomeRecyclerViewNoTabHolder) holder, position - size);
            } else {
                initViewForHomeItem((HomeRecyclerViewHolder) holder, position - size);
            }
        } else {
            if (listMemo.get(position - size).getTags().size() == 0 && listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                initViewForSharedNoTabAndTagItem((SharedWithMeNoTabAndTagViewHolder) holder, position - size);
            } else if (listMemo.get(position - size).getTags().size() == 0) {
                initViewForSharedNoTagItem((SharedWithMeNoTagsHolder) holder, position - size);
            } else if (listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                initViewForSharedNoTabItem((SharedWithMeNoTabViewHolder) holder, position - size);
            } else {
                initViewForSharedItem((SharedWithMeViewHolder) holder, position - size);
            }
        }
    }


    public void clearMedia() {
        Set<Integer> listKey = listFragmentNeedClearMedia.keySet();
        for (Integer key : listKey) {
            Set<Fragment> listFragment = listFragmentNeedClearMedia.get(key);
            if (listFragment != null) {
                for (Fragment fragment : listFragment) {
                    if (fragment instanceof ImageFragment) {
                        ((ImageFragment) fragment).clearMedia();
                    } else if (fragment instanceof AudioFragment) {
                        ((AudioFragment) fragment).clearMedia();
                    }
                }
            }
        }
    }

    public void destroyMedia() {
        Set<Integer> listKey = listFragmentNeedClearMedia.keySet();
        for (Integer key : listKey) {
            Set<Fragment> listFragment = listFragmentNeedClearMedia.get(key);
            if (listFragment != null) {
                for (Fragment fragment : listFragment) {
                    if (fragment instanceof ImageFragment) {
                        ((ImageFragment) fragment).destroyMedia();
                    } else if (fragment instanceof AudioFragment) {
                        ((AudioFragment) fragment).destroyMedia();
                    }
                }
            }
        }
    }

    private void initViewForSharedNoTabItem(SharedWithMeNoTabViewHolder holder, int position) {
        Diary diary = listMemo.get(position);
        //start setup diary title
        holder.title.setText(diary.getTitle());
        // end setup diary title

        // start setup diary is uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end setup diary is uploading


        // start setup resource view
        try {
            ArrayList<Tags> listTags = diary.getTags();
            holder.tags.setAdapter(new TagRecyclerViewAdapter(listTags));
            holder.tags.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }

        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        String sourceString = "Chia sẻ bởi " + "<b>" + ((PersonProfile) diary.getUser()).getName() + "</b> ";
        holder.sharedByTextView.setText(Html.fromHtml(sourceString));

        holder.sharedByTextView.setVisibility(View.VISIBLE);

        if (diary.getAction() == null || diary.getAction().equals("view")) {
            holder.popDownMenu.setVisibility(View.INVISIBLE);
        } else if (diary.getAction().equals("edit")) {
            // start setup pop down menu
            holder.popDownMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
                popupMenu.inflate(R.menu.pop_down_menu_shared_memo);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.pop_down_edit:
                            actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                            break;
                        case R.id.pop_down_pin:
                            break;
                        case R.id.pop_down_delete:
                            actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
            // end setup pop down menu
        }
    }

    private void initViewForSharedNoTagItem(SharedWithMeNoTagsHolder holder, int position) {
        Set<Fragment> listFragmentsToClear = new HashSet<>();
        Diary diary = listMemo.get(position);
        // setup views

        // start set diary title
        holder.title.setText(diary.getTitle());
        // end set diary title

        // start handle is diary uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end handle is diary uploading


        // start set content
        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        // end set content

        // start set resource tab view
        ArrayList<Resource> listResource = (ArrayList<Resource>) diary.getResources();
        ArrayList<Resource> listImageAndVideo = new ArrayList<>();
        ArrayList<Resource> listAudio = new ArrayList<>();
        ArrayList<Link> listLinks = (ArrayList<Link>) diary.getLinks();
        ArrayList<Resource> listFile = new ArrayList<>();

        try {
            for (Resource resource : listResource) {
                if (resource.getType().contains(Constant.imageType)) {
                    listImageAndVideo.add(resource);
                } else if (resource.getType().contains(Constant.videoType)) {
                    listImageAndVideo.add(resource);
                } else if (resource.getType().contains(Constant.audioType)) {
                    listAudio.add(resource);
                } else {
                    listFile.add(resource);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }

        ArrayList<String> listTabs = new ArrayList<>();
        ArrayList<Fragment> listFragments = new ArrayList<>();

        try {
            if (listImageAndVideo.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.image_and_video).toString());
                ImageFragment imageFragment = new ImageFragment(listImageAndVideo, mContext);
                listFragmentsToClear.add(imageFragment);
                imageFragment.imageFragmentPublish.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                        openPreviewImageTrigger.onNext(new Pair<>("open_image_preview", listImageAndVideo));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
                listFragments.add(imageFragment);
            }

            if (listLinks.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.link).toString());
                listFragments.add(new LinkFragment(listLinks));
            }

            if (listAudio.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.audio).toString());
                AudioFragment audioFragment = new AudioFragment(listAudio);
                listFragmentsToClear.add(audioFragment);
                listFragments.add(audioFragment);
            }

            if (listFile.size() != 0) {
                listTabs.add("File");
                listFragments.add(new FileFragment(listFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MemoTabAdapter adapter = new MemoTabAdapter(((FragmentActivity) mContext));
            adapter.setData(listTabs, listFragments);
            holder.viewPager.setAdapter(adapter);
            new TabLayoutMediator(holder.tabLayout, holder.viewPager, (tab, position1) -> tab.setText(listTabs.get(position1))).attach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listFragmentNeedClearMedia.put(position, listFragmentsToClear);
        // end setup resource tab view

        String sourceString = "Chia sẻ bởi " + "<b>" + ((PersonProfile) diary.getUser()).getName() + "</b> ";
        holder.sharedByTextView.setText(Html.fromHtml(sourceString));

        if (diary.getAction() == null || diary.getAction().equals("view")) {
            holder.popDownMenu.setVisibility(View.INVISIBLE);
        } else if (diary.getAction().equals("edit")) {
            // start setup pop down menu
            holder.popDownMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
                popupMenu.inflate(R.menu.pop_down_menu_shared_memo);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.pop_down_edit:
                            actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                            break;
                        case R.id.pop_down_pin:
                            break;
                        case R.id.pop_down_delete:
                            actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
            // end setup pop down menu
        }

    }

    @SuppressLint("NonConstantResourceId")
    private void initViewForSharedNoTabAndTagItem(SharedWithMeNoTabAndTagViewHolder holder, int position) {
        Diary diary = listMemo.get(position);

        // start setup diary title
        holder.title.setText(diary.getTitle());
        // end setup diary title

        // start setup diary is uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end setup diary is uploading

        // start setup diary content
        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        // end setup diary content

        String sourceString = "Chia sẻ bởi " + "<b>" + ((PersonProfile) diary.getUser()).getName() + "</b> ";
        holder.sharedByTextView.setText(Html.fromHtml(sourceString));
        Log.d(TAG, "initViewForSharedNoTabAndTagItem: " + diary.getAction());
        if (diary.getAction() == null || diary.getAction().equals("view")) {
            holder.popDownMenu.setVisibility(View.INVISIBLE);
        }
        if (diary.getAction() != null && diary.getAction().equals("edit")) {
            holder.popDownMenu.setVisibility(View.VISIBLE);
            // start setup pop down menu
            holder.popDownMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
                popupMenu.inflate(R.menu.pop_down_menu_shared_memo);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.pop_down_edit:
                            actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                            break;
                        case R.id.pop_down_pin:
                            break;
                        case R.id.pop_down_delete:
                            actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
            // end setup pop down menu
        }
    }

    private void initViewForSharedItem(SharedWithMeViewHolder holder, int position) {
        Set<Fragment> listFragmentsToClear = new HashSet<>();
        Diary diary = listMemo.get(position);

        // start setup diary title
        holder.title.setText(diary.getTitle());
        // end setup diary title

        // start setup is diary uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end setup is diary uploading


        // start setup tags
        if (diary.getTags().size() == 0) {
            holder.tags.setVisibility(View.GONE);
        } else {
            ArrayList<Tags> listTags = diary.getTags();
            holder.tags.setAdapter(new TagRecyclerViewAdapter(listTags));
            holder.tags.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }
        // end setup tags

        // start setup content
        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        // end setup content

        // start setup resource tab view
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
                listTabs.add(mContext.getResources().getText(R.string.image_and_video).toString());
                ImageFragment imageFragment = new ImageFragment(listImageAndVideo, mContext);
                listFragmentsToClear.add(imageFragment);
                imageFragment.imageFragmentPublish.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                        openPreviewImageTrigger.onNext(new Pair<>("open_image_preview", listImageAndVideo));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
                listFragments.add(imageFragment);
            }

            if (listLinks.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.link).toString());
                listFragments.add(new LinkFragment(listLinks));
            }

            if (listAudio.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.audio).toString());
                AudioFragment audioFragment = new AudioFragment(listAudio);
                listFragmentsToClear.add(audioFragment);
                listFragments.add(new AudioFragment(listAudio));
            }
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }


        if (listTabs.size() == 0) {
            holder.tabLayout.setVisibility(View.GONE);
        }

        MemoTabAdapter adapter = new MemoTabAdapter(((FragmentActivity) mContext));
        adapter.setData(listTabs, listFragments);
        holder.viewPager.setAdapter(adapter);
        new TabLayoutMediator(holder.tabLayout, holder.viewPager, (tab, position1) -> tab.setText(listTabs.get(position1))).attach();
        listFragmentNeedClearMedia.put(position, listFragmentsToClear);
        // end setup resource tab view

        String sourceString = "Chia sẻ bởi " + "<b>" + ((PersonProfile) diary.getUser()).getName() + "</b> ";
        holder.sharedByTextView.setText(Html.fromHtml(sourceString));

        if (diary.getAction() == null || diary.getAction().equals("view")) {
            holder.popDownMenu.setVisibility(View.INVISIBLE);
        } else if (diary.getAction().equals("edit")) {
            // start setup pop down menu
            holder.popDownMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
                popupMenu.inflate(R.menu.pop_down_menu_shared_memo);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.pop_down_edit:
                            actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                            break;
                        case R.id.pop_down_pin:
                            break;
                        case R.id.pop_down_delete:
                            actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
            // end setup pop down menu
        }
    }

    private void initViewForHomeFilter(HomeFilterViewHolder holder) {
        if (searchKey != null) {
            filterItemRecyclerViewAdapter.setSearchKey(searchKey);
        }

        if (timeKey != null) {
            filterItemRecyclerViewAdapter.setTime(timeKey);
        }

        if (listFilterTags != null) {
            filterItemRecyclerViewAdapter.setListTags(listFilterTags);
        }

        holder.filterRecyclerView.setAdapter(filterItemRecyclerViewAdapter);
        holder.filterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.resetFilter.setOnClickListener(v -> {
            listFilterTags = null;
            searchKey = null;
            timeKey = null;
            filterItemRecyclerViewAdapter.notifyDataSetChanged();
            isFilter = false;
            notifyItemRemoved(1);
            filterObservable.onNext(new Pair<>("reset_all", null));
        });

        filterItemRecyclerViewAdapter.getFilterObservable().subscribe(new Observer<Pair<String, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, String> pair) {
                filterObservable.onNext(pair);
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

    private void initViewForHeader(HeaderViewHolder holder) {
        // star setup click create diary
        holder.homeNewMemo.setOnClickListener(v -> actionObservable.setValue(new Pair<>(Constant.CREATE_DIARY_KEY, null)));
        // end setup click create diary

        // start setup click pick file
        holder.memoUploadFile.setOnClickListener(v -> actionObservable.setValue(new Pair<>(Constant.GET_FILE_CODE, null)));
        // end setup click pick file

        // start setup pick tags
        holder.memoUploadTag.setOnClickListener(v -> actionObservable.setValue(new Pair<>(Constant.GET_TAGS_CODE, null)));
        // end setup pick tags

        // start setup pick link
        holder.memoUpLoadLink.setOnClickListener(v -> actionObservable.setValue(new Pair<>(Constant.GET_LINKS_CODE, null)));
        // end setup pick link

        holder.speechToTextButton.setOnClickListener(v -> actionObservable.setValue(new Pair<>(Constant.SPEECH_TO_TEXT_CODE, null)));
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void initViewForHomeNoTagItem(HomeRecyclerViewNoTagsHolder holder, int position) {
        Set<Fragment> listFragmentsToClear = new HashSet<>();
        Diary diary = listMemo.get(position);
        // setup views

        // start set diary title
        holder.title.setText(diary.getTitle());
        // end set diary title

        // start handle is diary uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end handle is diary uploading

        // start handle pop down menu
        holder.popDownMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
            if (diary.getPinned() != null && diary.getPinned()) {
                popupMenu.inflate(R.menu.pop_down_menu_unpin);
            } else {
                popupMenu.inflate(R.menu.pop_down_menu_pin);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.pop_down_edit:
                        actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                        break;
                    case R.id.pop_down_pin:
                        if (diary.getPinned()) {
                            actionObservable.setValue(new Pair<>("unpin_diary", position));
                        } else {
                            actionObservable.setValue(new Pair<>("pin_diary", position));
                        }
                        break;
                    case R.id.pop_down_delete:
                        actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        // end handle pop down menu

        // start set content
        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        // end set content

        // start set resource tab view
        ArrayList<Resource> listResource = (ArrayList<Resource>) diary.getResources();
        ArrayList<Resource> listImageAndVideo = new ArrayList<>();
        ArrayList<Resource> listAudio = new ArrayList<>();
        ArrayList<Link> listLinks = (ArrayList<Link>) diary.getLinks();
        ArrayList<Resource> listFile = new ArrayList<>();

        try {
            for (Resource resource : listResource) {
                if (resource.getType().contains(Constant.imageType)) {
                    listImageAndVideo.add(resource);
                } else if (resource.getType().contains(Constant.videoType)) {
                    listImageAndVideo.add(resource);
                } else if (resource.getType().contains(Constant.audioType)) {
                    listAudio.add(resource);
                } else {
                    listFile.add(resource);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }

        ArrayList<String> listTabs = new ArrayList<>();
        ArrayList<Fragment> listFragments = new ArrayList<>();

        try {
            if (listImageAndVideo.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.image_and_video).toString());
                ImageFragment imageFragment = new ImageFragment(listImageAndVideo, mContext);
                listFragmentsToClear.add(imageFragment);
                imageFragment.imageFragmentPublish.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                        openPreviewImageTrigger.onNext(new Pair<>("open_image_preview", listImageAndVideo));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
                listFragments.add(imageFragment);
            }

            if (listLinks.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.link).toString());
                listFragments.add(new LinkFragment(listLinks));
            }

            if (listAudio.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.audio).toString());
                AudioFragment audioFragment = new AudioFragment(listAudio);
                listFragmentsToClear.add(audioFragment);
                listFragments.add(audioFragment);
            }

            if (listFile.size() != 0) {
                listTabs.add("File");
                listFragments.add(new FileFragment(listFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MemoTabAdapter adapter = new MemoTabAdapter(((FragmentActivity) mContext));
            adapter.setData(listTabs, listFragments);
            holder.viewPager.setAdapter(adapter);
            new TabLayoutMediator(holder.tabLayout, holder.viewPager, (tab, position1) -> tab.setText(listTabs.get(position1))).attach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listFragmentNeedClearMedia.put(position, listFragmentsToClear);
        // end setup resource tab view

        // start setup share memo
        holder.shareMemo.setOnClickListener(v -> actionObservable.setValue(new Pair<>("share_action", position)));
        // end setup share memo
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void initViewForHomeNoTabItem(HomeRecyclerViewNoTabHolder holder, int position) {
        Diary diary = listMemo.get(position);
        //start setup diary title
        holder.title.setText(diary.getTitle());
        // end setup diary title

        // start setup diary is uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end setup diary is uploading

        // start setup pop down menu
        holder.popDownMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
            popupMenu.inflate(R.menu.pop_down_menu_pin);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.pop_down_edit:
                        actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                        break;
                    case R.id.pop_down_pin:
                        break;
                    case R.id.pop_down_delete:
                        actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        // end setup pop down menu


        // start setup resource view
        try {
            ArrayList<Tags> listTags = diary.getTags();
            holder.tags.setAdapter(new TagRecyclerViewAdapter(listTags));
            holder.tags.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }

        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);

        holder.shareMemo.setOnClickListener(v -> actionObservable.setValue(new Pair<>("share_action", position)));
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void initViewForHomeItem(HomeRecyclerViewHolder holder, int position) {
        Set<Fragment> listFragmentsToClear = new HashSet<>();
        Diary diary = listMemo.get(position);

        // start setup diary title
        holder.title.setText(diary.getTitle());
        // end setup diary title

        // start setup is diary uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end setup is diary uploading


        // start setup pop down menu
        holder.popDownMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
            popupMenu.inflate(R.menu.pop_down_menu_pin);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.pop_down_edit:
                        actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                        break;
                    case R.id.pop_down_pin:
                        break;
                    case R.id.pop_down_delete:
                        actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        // end setup pop down menu

        // start setup tags
        if (diary.getTags().size() == 0) {
            holder.tags.setVisibility(View.GONE);
        } else {
            ArrayList<Tags> listTags = diary.getTags();
            holder.tags.setAdapter(new TagRecyclerViewAdapter(listTags));
            holder.tags.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }
        // end setup tags

        // start setup content
        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        // end setup content

        // start setup resource tab view
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
                listTabs.add(mContext.getResources().getText(R.string.image_and_video).toString());
                ImageFragment imageFragment = new ImageFragment(listImageAndVideo, mContext);
                listFragmentsToClear.add(imageFragment);
                imageFragment.imageFragmentPublish.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                        openPreviewImageTrigger.onNext(new Pair<>("open_image_preview", listImageAndVideo));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
                listFragments.add(imageFragment);
            }

            if (listLinks.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.link).toString());
                listFragments.add(new LinkFragment(listLinks));
            }

            if (listAudio.size() != 0) {
                listTabs.add(mContext.getResources().getText(R.string.audio).toString());
                AudioFragment audioFragment = new AudioFragment(listAudio);
                listFragmentsToClear.add(audioFragment);
                listFragments.add(new AudioFragment(listAudio));
            }
        } catch (Exception e) {
            Log.d(TAG, "initViewForHomeItem: " + e.getMessage());
        }


        if (listTabs.size() == 0) {
            holder.tabLayout.setVisibility(View.GONE);
        }

        MemoTabAdapter adapter = new MemoTabAdapter(((FragmentActivity) mContext));
        adapter.setData(listTabs, listFragments);
        holder.viewPager.setAdapter(adapter);
        new TabLayoutMediator(holder.tabLayout, holder.viewPager, (tab, position1) -> tab.setText(listTabs.get(position1))).attach();
        listFragmentNeedClearMedia.put(position, listFragmentsToClear);
        // end setup resource tab view

        // start setup share memo
        holder.shareMemo.setOnClickListener(v -> actionObservable.setValue(new Pair<>("share_action", position)));
        // end setup share memo
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void initViewForHomeNoTabAndTagItem(HomeRecyclerViewNoTabAndTagHolder holder, int position) {
        Diary diary = listMemo.get(position);

        // start setup diary title
        holder.title.setText(diary.getTitle());
        // end setup diary title

        // start setup diary is uploading
        if (diary.getUploading() == null) {
            holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));
        }
        // end setup diary is uploading

        // start setup pop down menu
        holder.popDownMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
            popupMenu.inflate(R.menu.pop_down_menu_pin);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.pop_down_edit:
                        actionObservable.setValue(new Pair<>(Constant.UPDATE_DIARY_KEY, position));
                        break;
                    case R.id.pop_down_pin:
                        break;
                    case R.id.pop_down_delete:
                        actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        // end setup pop down menu

        // start setup diary content
        holder.setText(diary.getContent());
        holder.ExpandCollapse(holder.content, holder.itemView, position);
        // end setup diary content

        // start setup share memo
        holder.shareMemo.setOnClickListener(v -> actionObservable.setValue(new Pair<>("share_action", position)));
        // end setup share memo
    }


    public PublishSubject<Pair<String, String>> getFilterObservable() {
        return filterObservable;
    }

    public MediatorLiveData<Pair<String, Integer>> observableAction() {
        return actionObservable;
    }


    @Override
    public int getItemCount() {
        int size = listMemo.size();
        if (!isSharedMode) {
            if (isFilter) {
                size = size + 2;
            } else {
                size = size + 1;
            }
        }
        return size;
    }

    public void updateListMemo(ArrayList<Diary> listDiary) {
        this.listMemo = new ArrayList<>();
        this.listMemo.addAll(listDiary);
        notifyDataSetChanged();
    }

    public void insertListMemo(ArrayList<Diary> listDiary) {
        this.listMemo.addAll(listDiary);
        notifyDataSetChanged();
    }

    public void addMemo(Diary diary) {
        listMemo.add(0, diary);
        notifyDataSetChanged();
    }

    public void removeAtPosition(int position) {
        int size = 0;
        if (!isSharedMode) {
            if (isFilter) {
                size = size + 2;
            } else {
                size = size + 1;
            }
        }
        this.listMemo.remove(position);
        notifyItemRemoved(position + size);
        notifyItemRangeChanged(position + size, listMemo.size());
    }

    public void updateMemoAt(int position, Diary diary) {
        listMemo.set(position, diary);
        notifyItemRangeChanged(position, 2);
    }

    public void insertFilter(String searchKey, String timeKey, ArrayList<Tags> listFilterTags, String highLightKey) {
        this.searchKey = searchKey;
        this.timeKey = timeKey;
        this.listFilterTags = listFilterTags;
        filterItemRecyclerViewAdapter.setTime(timeKey);
        filterItemRecyclerViewAdapter.setSearchKey(searchKey);
        filterItemRecyclerViewAdapter.setListTags(listFilterTags);
        filterItemRecyclerViewAdapter.setHighLightKey(highLightKey);
        if (!isFilter) {
            notifyItemInserted(1);
            isFilter = true;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (!isSharedMode) {
            int size = 0;
            if (isFilter) {
                size = size + 2;
            } else {
                size = size + 1;
            }

            if (position == 0 && !isSharedMode) {
                return TYPE_HEADER;
            } else if (position == 1 && isFilter) {
                return TYPE_FILTER;
            } else if (listMemo.get(position - size).getUploading() != null) {
                return TYPE_UPLOADING;
            } else if (listMemo.get(position - size).getTags().size() == 0 && listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                return TYPE_NO_TAGS_AND_TAB;
            } else if (listMemo.get(position - size).getTags().size() == 0) {
                return TYPE_NO_TAGS;
            } else if (listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                return TYPE_NO_TAB;
            } else {
                return TYPE_NORMAL;
            }
        } else {
            int size = 0;
            if (listMemo.get(position - size).getTags().size() == 0 && listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                return TYPE_SHARED_NO_TAGS_AND_TAB;
            } else if (listMemo.get(position - size).getTags().size() == 0) {
                return TYPE_SHARED_NO_TAGS;
            } else if (listMemo.get(position - size).getLinks().size() == 0 && listMemo.get(position - size).getResources().size() == 0) {
                return TYPE_SHARED_NO_TAB;
            } else {
                return TYPE_SHARED_NORMAL;
            }
        }
    }

    public void removeFilter() {
        notifyItemRemoved(1);
        isFilter = false;
    }


    static class HomeRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        RecyclerView tags;
        TextView content;
        TabLayout tabLayout;
        ViewPager2 viewPager;
        ImageView popDownMenu;
        LinearLayout shareMemo;
        Boolean isExpanded = true;
        TextView seeMore;
        MediatorLiveData<Pair<String, Integer>> adapterAction;

        public HomeRecyclerViewHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tags = itemView.findViewById(R.id.memo_item_tag_list);
            tabLayout = itemView.findViewById(R.id.memo_item_tabLayout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = itemView.findViewById(R.id.view_pager);
            viewPager.setUserInputEnabled(false);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
            shareMemo = itemView.findViewById(R.id.memo_share);
            seeMore = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMore.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMore.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMore.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded && textView.getVisibility() == View.VISIBLE) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class HomeRecyclerViewNoTabAndTagHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView content;
        ImageView popDownMenu;
        LinearLayout shareMemo;
        Boolean isExpanded = true;
        TextView seeMore;
        MediatorLiveData<Pair<String, Integer>> adapterAction;

        public HomeRecyclerViewNoTabAndTagHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
            shareMemo = itemView.findViewById(R.id.memo_share);
            seeMore = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMore.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMore.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMore.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded && textView.getVisibility() == View.VISIBLE) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class HomeRecyclerViewNoTabHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        RecyclerView tags;
        TextView content;
        ImageView popDownMenu;
        LinearLayout shareMemo;
        Boolean isExpanded = true;
        TextView seeMore;
        MediatorLiveData<Pair<String, Integer>> adapterAction;

        public HomeRecyclerViewNoTabHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tags = itemView.findViewById(R.id.memo_item_tag_list);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
            shareMemo = itemView.findViewById(R.id.memo_share);
            seeMore = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMore.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMore.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMore.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class HomeRecyclerViewNoTagsHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView content;
        TabLayout tabLayout;
        ViewPager2 viewPager;
        ImageView popDownMenu;
        LinearLayout shareMemo;
        TextView seeMoreTextView;
        Boolean isExpanded = true;
        MediatorLiveData<Pair<String, Integer>> adapterAction;


        public HomeRecyclerViewNoTagsHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tabLayout = itemView.findViewById(R.id.memo_item_tabLayout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = itemView.findViewById(R.id.view_pager);
            viewPager.setUserInputEnabled(false);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
            shareMemo = itemView.findViewById(R.id.memo_share);
            seeMoreTextView = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMoreTextView.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMoreTextView.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMoreTextView.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class SharedWithMeViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        RecyclerView tags;
        TextView content;
        TabLayout tabLayout;
        ViewPager2 viewPager;
        Boolean isExpanded = true;
        TextView seeMore;
        MediatorLiveData<Pair<String, Integer>> adapterAction;
        TextView sharedByTextView;
        ImageView popDownMenu;

        public SharedWithMeViewHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tags = itemView.findViewById(R.id.memo_item_tag_list);
            tabLayout = itemView.findViewById(R.id.memo_item_tabLayout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = itemView.findViewById(R.id.view_pager);
            viewPager.setUserInputEnabled(false);
            seeMore = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
            sharedByTextView = itemView.findViewById(R.id.shared_by_textView);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMore.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMore.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMore.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded && textView.getVisibility() == View.VISIBLE) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class SharedWithMeNoTabAndTagViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView content;
        Boolean isExpanded = true;
        TextView seeMore;
        MediatorLiveData<Pair<String, Integer>> adapterAction;
        TextView sharedByTextView;
        ImageView popDownMenu;

        public SharedWithMeNoTabAndTagViewHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            seeMore = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
            sharedByTextView = itemView.findViewById(R.id.shared_by_textView);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMore.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMore.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMore.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded && textView.getVisibility() == View.VISIBLE) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class SharedWithMeNoTabViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        RecyclerView tags;
        TextView content;
        Boolean isExpanded = true;
        TextView seeMore;
        MediatorLiveData<Pair<String, Integer>> adapterAction;
        TextView sharedByTextView;
        ImageView popDownMenu;

        public SharedWithMeNoTabViewHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction, Boolean isSharedMode) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tags = itemView.findViewById(R.id.memo_item_tag_list);
            seeMore = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
            sharedByTextView = itemView.findViewById(R.id.shared_by_textView);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMore.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMore.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMore.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class SharedWithMeNoTagsHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView content;
        TabLayout tabLayout;
        ViewPager2 viewPager;
        TextView seeMoreTextView;
        Boolean isExpanded = true;
        MediatorLiveData<Pair<String, Integer>> adapterAction;
        TextView sharedByTextView;
        ImageView popDownMenu;

        public SharedWithMeNoTagsHolder(@NonNull View itemView, MediatorLiveData<Pair<String, Integer>> adapterAction) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tabLayout = itemView.findViewById(R.id.memo_item_tabLayout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = itemView.findViewById(R.id.view_pager);
            viewPager.setUserInputEnabled(false);
            seeMoreTextView = itemView.findViewById(R.id.see_more_textView);
            content = itemView.findViewById(R.id.memo_item_content);
            this.adapterAction = adapterAction;
            sharedByTextView = itemView.findViewById(R.id.shared_by_textView);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
        }

        private void Ellipsize(boolean activate, TextView textView) {
            if (activate) {
                textView.setMaxLines(3);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                seeMoreTextView.setVisibility(View.VISIBLE);
            } else {
                textView.setSingleLine(false);
                textView.setEllipsize(null);
                seeMoreTextView.setVisibility(View.GONE);
            }
        }

        void setText(String text) {
            content.setSoundEffectsEnabled(false);
            if (text == null) {
                content.setVisibility(View.GONE);
                seeMoreTextView.setVisibility(View.GONE);
            } else {
                content.setText(text);
                Ellipsize(text.length() > 150 || text.split("\r\n|\r|\n").length > 3, content);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view, int position) {
            textView.setOnClickListener(v -> {
                if (isExpanded) {
                    String text = textView.getText().toString();
                    int numberOfLine = text.split("\r\n|\r|\n").length;
                    if (textView.getText().toString().length() > 10000 || numberOfLine > 30) {
                        adapterAction.setValue(new Pair<>("to_preview_memo", position));
                    } else {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                        isExpanded = !isExpanded;
                    }
                } else {
                    TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                    Ellipsize(true, textView);
                    isExpanded = !isExpanded;
                }
            });
        }
    }

    static class HomeFilterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout resetFilter;
        RecyclerView filterRecyclerView;

        public HomeFilterViewHolder(@NonNull View itemView) {
            super(itemView);
            resetFilter = itemView.findViewById(R.id.reset_filter);
            filterRecyclerView = itemView.findViewById(R.id.filter_item_recyclerview);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout memoUploadFile;
        private final LinearLayout memoUploadTag;
        private final LinearLayout memoUpLoadLink;
        private final TextView homeNewMemo;
        private RelativeLayout speechToTextButton;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            homeNewMemo = itemView.findViewById(R.id.home_create_new_memo);
            memoUploadFile = itemView.findViewById(R.id.upload_memo_file);
            memoUploadTag = itemView.findViewById(R.id.upload_memo_tag);
            memoUpLoadLink = itemView.findViewById(R.id.upload_memo_link);
            speechToTextButton = itemView.findViewById(R.id.speech_to_text_button);
        }
    }

    static class MemoTabAdapter extends FragmentStateAdapter {

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
