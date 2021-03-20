package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.FilterRecyclerViewHolder> {
    private static final String TAG = "FilterRecyclerViewAdapt";
    private ArrayList<String> listFilter;
    private Context mContext;
    private HashMap<String, Boolean> itemChooseHashMap = new HashMap<>();
    private final PublishSubject<Pair<String, String>> filterObservable;
    private ArrayList<String> listChoosed = new ArrayList<>();

    public FilterRecyclerViewAdapter(ArrayList<String> listFilter, PublishSubject<Pair<String, String>> filterObservable) {
        this.listFilter = listFilter;
        for (String string : listFilter) {
            itemChooseHashMap.put(string, false);
        }
        this.filterObservable = filterObservable;
        subscribeFilterObservable();
    }

    @NonNull
    @Override
    public FilterRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_grey_filter_item, parent, false);
        return new FilterRecyclerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull FilterRecyclerViewHolder holder, int position) {
        holder.filterName.setText(listFilter.get(position));
        if (listChoosed.size() > 0) {
            if (listChoosed.contains(listFilter.get(position))) {
                holder.filterName.setBackground(mContext.getDrawable(R.drawable.blue_stroke_layout2));
                itemChooseHashMap.put(listFilter.get(position), true);
            } else {
                holder.filterName.setBackground(mContext.getDrawable(R.drawable.black_stroke_layout));
            }
        } else {
            holder.filterName.setBackground(mContext.getDrawable(R.drawable.black_stroke_layout));
        }

        holder.itemView.setOnClickListener(v -> {
            if (itemChooseHashMap.get(listFilter.get(position))) {
                holder.filterName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.black_stroke_layout));
                itemChooseHashMap.replace(listFilter.get(position), false);
                filterObservable.onNext(new Pair<>("remove_filter", listFilter.get(position)));
            } else {
                holder.filterName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.blue_stroke_layout2));
                itemChooseHashMap.replace(listFilter.get(position), true);
                filterObservable.onNext(new Pair<>("add_filter", listFilter.get(position)));
            }
        });
    }

    private void subscribeFilterObservable() {
        filterObservable.subscribe(new Observer<Pair<String, String>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, String> pair) {
                if (pair.first.equals("update_filter")) {
                    if (itemChooseHashMap.containsKey(pair.second)) {
                        itemChooseHashMap.replace(pair.second, false);
                        int index = listFilter.indexOf(pair.second);
                        notifyItemChanged(index);
                    }
                    listChoosed.remove(pair.second);
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

    public void setData(ArrayList<String> listFilter) {
        this.listFilter = new ArrayList<>();
        this.listFilter.addAll(listFilter);
        itemChooseHashMap.clear();
        itemChooseHashMap = new HashMap<>();
        for (String string : listFilter) {
            itemChooseHashMap.put(string, false);
        }
        notifyDataSetChanged();
    }

    public void setListChoosed(ArrayList<String> listChoosed) {
        this.listChoosed = listChoosed;
        notifyDataSetChanged();
    }

    public void reset() {
        itemChooseHashMap.clear();
        for (String string : listFilter) {
            itemChooseHashMap.put(string, false);
            int index = listFilter.indexOf(string);
            notifyItemChanged(index);
        }
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    static class FilterRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView filterName;
        public FilterRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
        }
    }
}
