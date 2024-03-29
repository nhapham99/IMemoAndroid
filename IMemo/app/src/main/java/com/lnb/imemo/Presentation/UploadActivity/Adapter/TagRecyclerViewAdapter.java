package com.lnb.imemo.Presentation.UploadActivity.Adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
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

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.TagRecyclerViewHolder> {
    private ArrayList<Tags> listTags = new ArrayList<>();
    private PublishSubject<Integer> removeTagObservable = PublishSubject.create();
    public TagRecyclerViewAdapter(ArrayList<Tags> listTags) {
        this.listTags = listTags;
    }
    public TagRecyclerViewAdapter() {}

    @NonNull
    @Override
    public TagRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_tag_upload_layout, parent, false);
        return new TagRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagRecyclerViewHolder holder, int position) {
        holder.tagName.setText(listTags.get(position).getName());
        holder.itemView.getBackground().setColorFilter(Color.parseColor(listTags.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
        holder.removeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listTags.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listTags.size());
                removeTagObservable.onNext(position);
            }
        });
    }

    public ArrayList<Tags> getListTags() {
        return listTags;
    }

    public void setListTags(ArrayList<Tags> listTags) {
        this.listTags.clear();
        this.listTags.addAll(listTags);
        notifyDataSetChanged();
    }

    public PublishSubject<Integer> getRemoveTagObservable() {
        return removeTagObservable;
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    class TagRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        ImageView removeTag;
        public TagRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.memo_tag_name);
            removeTag = itemView.findViewById(R.id.remove_tag);
        }
    }
}
