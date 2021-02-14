package com.lnb.imemo.Presentation.PickTag.RecyclerViewAdapter;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class PickTagsRecyclerViewAdapter extends RecyclerView.Adapter<PickTagsRecyclerViewAdapter.PickTagsRecyclerViewHolder>{
    private static final String TAG = "PickTagsRecyclerViewAda";
    private ArrayList<Tags> listTags = new ArrayList<>();
    private final ArrayList<String> checkedTagsId = new ArrayList<>();
    private final ArrayList<Tags> checkedTags = new ArrayList<>();
    private final PublishSubject<Integer> listTagsIdListener = PublishSubject.create();

    public PickTagsRecyclerViewAdapter(ArrayList<Tags> listTags) {
        this.listTags = listTags;
    }
    @NonNull
    @Override
    public PickTagsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_pick_tag_layout, parent, false);
        return new PickTagsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickTagsRecyclerViewHolder holder, int position) {
        holder.tagName.setText(listTags.get(position).getName());
        holder.tagName.getBackground().setColorFilter(Color.parseColor(listTags.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tickImage.getVisibility() == View.VISIBLE) {
                    checkedTagsId.remove(listTags.get(position).getId());
                    checkedTags.remove(listTags.get(position));
                    holder.tickImage.setVisibility(View.INVISIBLE);
                    listTagsIdListener.onNext(checkedTagsId.size());
                    Log.d(TAG, "onClick: " + checkedTagsId.size());
                } else {
                    checkedTagsId.add(listTags.get(position).getId());
                    checkedTags.add(listTags.get(position));
                    holder.tickImage.setVisibility(View.VISIBLE);
                    listTagsIdListener.onNext(checkedTagsId.size());
                    Log.d(TAG, "onClick: " + checkedTagsId.size());
                }
            }
        });
    }

    public PublishSubject<Integer> observableListTags() {
        return listTagsIdListener;
    }

    public ArrayList<String> getListTagsId() {
        return checkedTagsId;
    }

    public ArrayList<Tags> getCheckedTags() {
        return checkedTags;
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    class PickTagsRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        ImageView tickImage;
        public PickTagsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.text_tag);
            tickImage = itemView.findViewById(R.id.tick_image);

        }
    }
}
