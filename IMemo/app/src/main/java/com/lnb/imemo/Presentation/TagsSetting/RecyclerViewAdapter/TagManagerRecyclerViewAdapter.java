package com.lnb.imemo.Presentation.TagsSetting.RecyclerViewAdapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.R;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class TagManagerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TagManagerRecyclerViewA";
    private ArrayList<Tags> listTags;
    private ArrayList<Tags> listDefaultTags = new ArrayList<>();
    private ArrayList<Tags> listNotDefaultTags = new ArrayList<>();
    private PublishSubject<Integer> managerTagOptionListener = PublishSubject.create();
    private int TYPE_DEFAULT = 0;
    private int TYPE_UNDEFAULT = 1;

    public TagManagerRecyclerViewAdapter() {
        listTags = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DEFAULT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_tag_default_item, parent, false);
            return new TagDefaultRecyclerViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_tag_undefault_item, parent, false);
            return new TagUnDefaultRecyclerViewHolder(view);
        }
    }

    public TagManagerRecyclerViewAdapter(ArrayList<Tags> listTags) {
        this.listTags = listTags;
        listDefaultTags.clear();
        listNotDefaultTags.clear();
        for (Tags tags : listTags) {
            if (tags.getIsDefault()) {
                listDefaultTags.add(tags);
            } else {
                listNotDefaultTags.add(tags);
            }
        }
    }

    public ArrayList<Tags> getListDefaultTags() {
        return listDefaultTags;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (listTags.get(position).getIsDefault()) {
            TagDefaultRecyclerViewHolder defaultRecyclerViewHolder = (TagDefaultRecyclerViewHolder) holder;
            defaultRecyclerViewHolder.tagColor.getBackground().setColorFilter(Color.parseColor(listTags.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
            defaultRecyclerViewHolder.tagName.setText(listTags.get(position).getName());
            defaultRecyclerViewHolder.managerTagOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    managerTagOptionListener.onNext(position);
                }
            });
        } else {
            TagUnDefaultRecyclerViewHolder defaultRecyclerViewHolder = (TagUnDefaultRecyclerViewHolder) holder;
            defaultRecyclerViewHolder.tagColor.getBackground().setColorFilter(Color.parseColor(listTags.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
            defaultRecyclerViewHolder.tagName.setText(listTags.get(position).getName());
            defaultRecyclerViewHolder.managerTagOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    managerTagOptionListener.onNext(position);
                }
            });
        }
    }


    public PublishSubject<Integer> getManagerTagOptionListener() {
        return managerTagOptionListener;
    }

    public ArrayList<Tags> getListTags() {
        return listTags;
    }

    @Override
    public int getItemCount() {
        return listTags.size();
    }

    public void updateListTags(ArrayList<Tags> listTags) {
        this.listTags = listTags;
        listDefaultTags.clear();
        listNotDefaultTags.clear();
        for (Tags tags : listTags) {
            if (tags.getIsDefault()) {
                listDefaultTags.add(tags);
            } else {
                listNotDefaultTags.add(tags);
            }
        }
        notifyDataSetChanged();
    }

    public void updateListTags(Tags tag) {
        if (tag.getIsDefault()) {
            listDefaultTags.add(tag);
            listTags.clear();
            listTags.addAll(listDefaultTags);
            listTags.addAll(listNotDefaultTags);
            notifyItemInserted(listDefaultTags.size() - 1);
        } else {
            listTags.add(tag);
            notifyItemInserted(listTags.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (listTags.get(position).getIsDefault()) {
            return TYPE_DEFAULT;
        } else {
            return TYPE_UNDEFAULT;
        }
    }

    class TagDefaultRecyclerViewHolder extends RecyclerView.ViewHolder {
        View tagColor;
        TextView isDefault;
        TextView tagName;
        ImageButton managerTagOption;

        public TagDefaultRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tagColor = itemView.findViewById(R.id.tag_color);
            isDefault = itemView.findViewById(R.id.is_default_tag);
            tagName = itemView.findViewById(R.id.tag_name);
            managerTagOption = itemView.findViewById(R.id.manager_tag_option);
        }
    }

    class TagUnDefaultRecyclerViewHolder extends RecyclerView.ViewHolder {
        View tagColor;
        TextView tagName;
        ImageButton managerTagOption;

        public TagUnDefaultRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tagColor = itemView.findViewById(R.id.tag_color);
            tagName = itemView.findViewById(R.id.tag_name);
            managerTagOption = itemView.findViewById(R.id.manager_tag_option);
        }
    }
}
