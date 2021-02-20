package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.R;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.FilterRecyclerViewHolder> {
    private static final String TAG = "FilterRecyclerViewAdapt";
    private ArrayList<String> listFilter = new ArrayList<>();
    private Context mContext;
    private PublishSubject<String> filterObserver = PublishSubject.create();
    private ArrayList<String> listChoosed = new ArrayList<>();

    public FilterRecyclerViewAdapter(ArrayList<String> listFilter, ArrayList<String> listChoosed) {
        this.listFilter = listFilter;
        this.listChoosed = listChoosed;
    }

    public FilterRecyclerViewAdapter(ArrayList<String> listChoosed) {
        this.listChoosed = listChoosed;
    }

    @NonNull
    @Override
    public FilterRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_filter_item, parent, false);
        return new FilterRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterRecyclerViewHolder holder, int position) {
        holder.filterName.setText(listFilter.get(position));
        int index = listChoosed.indexOf(listFilter.get(position));
        if (index >= 0) {
            holder.filterName.setBackground(mContext.getDrawable(R.drawable.blue_stroke_layout2));
        }

        holder.filterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterObserver.onNext(listFilter.get(position));
            }
        });
    }

    public void setData(ArrayList<String> listFilter) {
        this.listFilter.clear();
        this.listFilter.addAll(listFilter);
        notifyDataSetChanged();
    }

    public PublishSubject<String> getFilterObserver() {
        return filterObserver;
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    class FilterRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView filterName;

        public FilterRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
        }
    }
}
