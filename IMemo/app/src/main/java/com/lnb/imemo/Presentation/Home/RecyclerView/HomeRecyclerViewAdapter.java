package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.Home.HomeViewModel;
import com.lnb.imemo.Presentation.Home.TabFragment.AudioFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.ImageFragment;
import com.lnb.imemo.Presentation.Home.TabFragment.LinkFragment;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.DateHelper;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeRecyclerViewHolder> {
    private static final String TAG = "HomeRecyclerViewAdapter";
    private ArrayList<Diary> listMemo = new ArrayList<>();
    private HomeViewModel viewModel;
    private Context mContext;

    private MediatorLiveData<Pair<String, Integer>> actionObservable = new MediatorLiveData<>();

    public HomeRecyclerViewAdapter(ArrayList<Diary> listMemo) {
        this.listMemo = listMemo;
    }

    @NonNull
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_view_item, parent, false);
        mContext = parent.getContext();
        return new HomeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewHolder holder, int position) {
        Diary diary = listMemo.get(position);
        // setup views

        holder.title.setText(diary.getTitle());
        holder.time.setText(DateHelper.dateConverter(diary.getCreatedAt()));

        holder.popDownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.popDownMenu);
                popupMenu.inflate(R.menu.pop_down_menu);
                popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop_down_edit:
                                break;
                            case R.id.pop_down_pin:
                                break;
                            case R.id.pop_down_delete:
                                actionObservable.setValue(new Pair<>(Constant.DELETE_DIARY_KEY, position));
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        // if diary tags is null remove view
        if (diary.getTags() == null) {
            holder.tags.setVisibility(View.GONE);
        } else {
            ArrayList<Tags> listTags = diary.getTags();
            Log.d(TAG, "onBindViewHolder: " + diary.getTags().size());
            holder.tags.setAdapter(new TagRecyclerViewAdapter(listTags));
            holder.tags.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }

        if (diary.getContent() == null) {
            holder.content.setVisibility(View.GONE);
        }

        holder.content.setText(diary.getContent());

        ArrayList<Resource> listResource = (ArrayList<Resource>) diary.getResources();
        ArrayList<Resource> listImageAndVideo = new ArrayList<>();
        ArrayList<Resource> listAudio = new ArrayList<>();
        ArrayList<Link> listLinks = (ArrayList<Link>) diary.getLinks();
        for (Resource resource : listResource) {
            if (resource.getType().equals(Constant.imageType)) {
                listImageAndVideo.add(resource);
            } else if (resource.getType().equals(Constant.videoType)) {
                listImageAndVideo.add(resource);
            } else if (resource.getType().equals(Constant.audioType)) {
                listAudio.add(resource);
            }
        }

        ArrayList<String> listTabs = new ArrayList<>();
        ArrayList<Fragment> listFragments = new ArrayList<>();

        if (listImageAndVideo.size() != 0) {
            listTabs.add(mContext.getResources().getText(R.string.image_and_video).toString());
            listFragments.add(new ImageFragment(listImageAndVideo));
        }

        if (listLinks.size() != 0) {
            listTabs.add(mContext.getResources().getText(R.string.link).toString());
            listFragments.add(new LinkFragment(listLinks));
        }

        if (listAudio.size() != 0) {
            listTabs.add(mContext.getResources().getText(R.string.audio).toString());
            Log.d(TAG, "onBindViewHolder: " + listAudio.size());
            listFragments.add(new AudioFragment(listAudio));
        }

        if (listTabs.size() == 0) {
            holder.tabLayout.setVisibility(View.GONE);
        }

        MemoTabAdapter adapter = new MemoTabAdapter(((FragmentActivity)mContext));
        adapter.setData(listTabs, listFragments);
        holder.viewPager.setAdapter(adapter);

        new TabLayoutMediator(holder.tabLayout, holder.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(listTabs.get(position));
            }
        }).attach();

    }

    public MediatorLiveData<Pair<String, Integer>> observableAction() {
        return actionObservable;
    }


    @Override
    public int getItemCount() {
        return listMemo.size();
    }

    public void updateListMemo(ArrayList<Diary> listMemo) {
        this.listMemo.clear();
        this.listMemo.addAll(listMemo);
        this.notifyDataSetChanged();
    }

    public void removeAtPosition(int position) {
        this.listMemo.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listMemo.size());
    }


    class HomeRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        RecyclerView tags;
        TextView content;
        TabLayout tabLayout;
        ViewPager2 viewPager;
        ImageView popDownMenu;
        public HomeRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            time = itemView.findViewById(R.id.memo_time);
            tags = itemView.findViewById(R.id.memo_item_tag_list);
            content = itemView.findViewById(R.id.memo_item_content);
            tabLayout = itemView.findViewById(R.id.memo_item_tabLayout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = itemView.findViewById(R.id.view_pager);
//            viewPager.setUserInputEnabled(false);
            popDownMenu = itemView.findViewById(R.id.pop_down_menu);
        }
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
