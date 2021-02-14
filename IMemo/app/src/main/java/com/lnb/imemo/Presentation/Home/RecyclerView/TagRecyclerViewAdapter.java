package com.lnb.imemo.Presentation.Home.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.R;

import java.util.ArrayList;

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.TagRecyclerViewHolder> {
    ArrayList<Tags> listTags;

    public TagRecyclerViewAdapter(ArrayList<Tags> listTags) {
        this.listTags = listTags;
    }

    @NonNull
    @Override
    public TagRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_tag_layout, parent, false);
        return new TagRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagRecyclerViewHolder holder, int position) {
        holder.tagName.setText(listTags.get(position).getName());
        holder.itemView.getBackground().setColorFilter(Color.parseColor(listTags.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    class TagRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        public TagRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.memo_tag_name);
        }
    }
}
