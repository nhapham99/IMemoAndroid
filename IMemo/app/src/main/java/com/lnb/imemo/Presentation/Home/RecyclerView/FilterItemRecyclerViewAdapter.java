package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.R;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class FilterItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String searchKey;
    private String time;
    private ArrayList<Tags> listTags;
    private String highLightKey;
    private final int TYPE_SEARCH = 0;
    private final int TYPE_TIME = 1;
    private final int TYPE_TAG = 2;
    private final int TYPE_HIGH_LIGHT = 3;
    private static final String TAG = "FilterItemRecyclerViewA";
    private PublishSubject<Pair<String, String>> filterObservable = PublishSubject.create();

    public FilterItemRecyclerViewAdapter() {}

    public FilterItemRecyclerViewAdapter(String searchKey, String time, ArrayList<Tags> listTags, String highLightKey) {
        this.searchKey = searchKey;
        this.time = time;
        this.listTags = listTags;
        this.highLightKey = highLightKey;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        if (viewType == TYPE_SEARCH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_search_item_layout, parent, false);
            return new FilterItemSearchViewHolder(view);
        } else if (viewType == TYPE_TIME) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_time_item_layout, parent, false);
            return new FilterItemTimeViewHolder(view);
        } else if (viewType == TYPE_HIGH_LIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_high_light_item_layout, parent, false);
            return new FilterItemHighLightViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_tag_item_layout, parent, false);
            return new FilterItemTagViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int highLightPosition = 1;
        if (searchKey == null) {
            highLightPosition = 0;
        }

        int timeKeyPosition = 2;
        if (searchKey == null && highLightKey == null) {
            timeKeyPosition = 0;
        } else if (highLightPosition == 0) {
            timeKeyPosition = 1;
        }

        if (position == 0 && searchKey != null) {
            initForFilterSearch((FilterItemSearchViewHolder) holder);
        } else if (position == timeKeyPosition && time != null) {
            initForFilterTime((FilterItemTimeViewHolder) holder);
        } else if (position == highLightPosition && highLightKey != null) {
            initForFilterHighLigt((FilterItemHighLightViewHolder) holder);
        } else {
            initForFilterTag((FilterItemTagViewHolder) holder, position);
        }
    }

    private void initForFilterHighLigt(FilterItemHighLightViewHolder holder) {
        holder.highLightName.setText(highLightKey);
        holder.deleteHighLightFilter.setOnClickListener(view -> {
            filterObservable.onNext(new Pair<>("remove_filter_high_light", highLightKey));
        });
    }

    private void initForFilterTag(FilterItemTagViewHolder holder, int position) {
        int removePosition = 0;
        if (time != null) {
            removePosition++;
        }
        if (searchKey != null) {
            removePosition++;
        }

        if (highLightKey != null) {
            removePosition++;
        }

        Tags tags = listTags.get(position - removePosition);
        holder.tagName.setText(tags.getName());
        holder.itemView.getBackground().setColorFilter(Color.parseColor(tags.getColor()), PorterDuff.Mode.SRC_ATOP);
        int finalRemovePosition = removePosition;
        holder.deleteTagFilter.setOnClickListener(v -> {
            Log.d(TAG, "initForFilterTag: " + finalRemovePosition);
            filterObservable.onNext(new Pair<>("remove_filter_tag", tags.getName()));
            notifyDataSetChanged();
        });

    }

    private void initForFilterTime(FilterItemTimeViewHolder holder) {
        holder.timeName.setText(time);
        holder.deleteTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = null;
                int removePosition = 0;
                if (searchKey != null) removePosition++;
                notifyDataSetChanged();
                filterObservable.onNext(new Pair<>("remove_filter_time", null));
            }
        });
    }

    private void initForFilterSearch(FilterItemSearchViewHolder holder) {
        holder.searchName.setText(searchKey);
        holder.deleteSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchKey = null;
                notifyDataSetChanged();
                filterObservable.onNext(new Pair<>("remove_filter_search", null));
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = listTags.size();
        if (searchKey != null) size++;
        if (time != null) size++;
        if (highLightKey != null) size++;
        return size;
    }

    public PublishSubject<Pair<String, String>> getFilterObservable() {
        return filterObservable;
    }

    @Override
    public int getItemViewType(int position) {
        int highLightPosition = 1;
        if (searchKey == null) {
            highLightPosition = 0;
        }

        int timeKeyPosition = 2;
        if (searchKey == null && highLightKey == null) {
            timeKeyPosition = 0;
        } else if (highLightPosition == 0) {
            timeKeyPosition = 1;
        }

        if (position == 0 && searchKey != null) {
            return TYPE_SEARCH;
        } else if (position == timeKeyPosition && time != null) {
            return TYPE_TIME;
        } else if (position == highLightPosition && highLightKey != null) {
            return TYPE_HIGH_LIGHT;
        } else {
            return TYPE_TAG;
        }
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        notifyDataSetChanged();
    }

    public void setTime(String time) {
        this.time = time;
        notifyDataSetChanged();
    }

    public void setListTags(ArrayList<Tags> listTags) {
        this.listTags = listTags;
        notifyDataSetChanged();
    }

    public void setHighLightKey(String highLightKey) {
        this.highLightKey = highLightKey;
        notifyDataSetChanged();
    }

    static class FilterItemTimeViewHolder extends RecyclerView.ViewHolder {
        TextView timeName;
        ImageView deleteTimeFilter;
        public FilterItemTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            timeName = itemView.findViewById(R.id.filter_time_item);
            deleteTimeFilter = itemView.findViewById(R.id.filter_time_item_delete);
        }
    }

    static class FilterItemTagViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        ImageView deleteTagFilter;
        public FilterItemTagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.filter_tag);
            deleteTagFilter = itemView.findViewById(R.id.filter_tag_delete);
        }
    }

    static class FilterItemSearchViewHolder extends RecyclerView.ViewHolder {
        TextView searchName;
        ImageView deleteSearchFilter;
        public FilterItemSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            searchName = itemView.findViewById(R.id.filter_search);
            deleteSearchFilter = itemView.findViewById(R.id.filter_search_delete);
        }
    }

    static class FilterItemHighLightViewHolder extends RecyclerView.ViewHolder {
        TextView highLightName;
        ImageView deleteHighLightFilter;
        public FilterItemHighLightViewHolder(@NonNull View itemView) {
            super(itemView);
            highLightName = itemView.findViewById(R.id.filter_high_light);
            deleteHighLightFilter = itemView.findViewById(R.id.filter_high_light_delete);
        }
    }
}
