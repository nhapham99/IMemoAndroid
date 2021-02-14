package com.lnb.imemo.Presentation.PreviewLink.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.R;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class LinkRecyclerViewAdapter extends RecyclerView.Adapter<LinkRecyclerViewAdapter.LinkRecyclerViewHolder>{
    private Link link;
    private Context mContext;

    public LinkRecyclerViewAdapter(Link link) {
        this.link = link;
    }

    @NonNull
    @Override
    public LinkRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item_layout, parent, false);
        mContext = parent.getContext();
        return new LinkRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkRecyclerViewHolder holder, int position) {
        if (link.getImage() != null && (link.getImage().toString().contains("https") || link.getImage().toString().contains("http"))) {
            Glide.with(mContext).load(link.getImage().toString()).into(holder.linkImage);
        }
        holder.linkTitle.setText(link.getTitle().toString());
        holder.linkDescription.setText(link.getDescription().toString());
        holder.linkUrl.setText(link.getUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public Link getLink() {
        return link;
    }

    class LinkRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView linkImage;
        TextView linkTitle;
        TextView linkDescription;
        TextView linkUrl;
        public LinkRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            linkImage = itemView.findViewById(R.id.link_imageView);
            linkTitle = itemView.findViewById(R.id.link_title);
            linkDescription = itemView.findViewById(R.id.link_description);
            linkUrl = itemView.findViewById(R.id.link_url);
        }
    }
}
