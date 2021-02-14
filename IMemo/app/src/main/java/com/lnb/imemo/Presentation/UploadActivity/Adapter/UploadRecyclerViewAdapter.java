package com.lnb.imemo.Presentation.UploadActivity.Adapter;

import android.content.Context;
import android.icu.text.MessagePattern;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;

import java.util.ArrayList;

public class UploadRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final ArrayList<Object> listUploadResource = new ArrayList<>();
    private static int TYPE_FILE = 1;
    private static int TYPE_IMAGE_AND_VIDEO = 2;
    private static int TYPE_LINK = 3;
    private Context mContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();
        if (viewType == TYPE_IMAGE_AND_VIDEO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.create_memo_image_and_video_layout, parent, false);
            return new ImageAndVideoViewHolder(view);
        } else if (viewType == TYPE_FILE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.create_memo_resource_layout, parent, false);
            return new FileViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.link_item_layout, parent, false);
            return new LinkRecyclerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (listUploadResource.get(position) instanceof Resource) {
            Resource resource = (Resource) listUploadResource.get(position);
            if (resource.getType().equals(Constant.videoType)) {
                ImageAndVideoViewHolder imageAndVideoViewHolder = (ImageAndVideoViewHolder) holder;
                Glide.with(mContext).load(resource.getUrl()).into(imageAndVideoViewHolder.imageResource);
                imageAndVideoViewHolder.resourceName.setText(resource.getName());
            } else if (resource.getType().equals(Constant.imageType)) {
                ImageAndVideoViewHolder imageAndVideoViewHolder = (ImageAndVideoViewHolder) holder;
                Glide.with(mContext).load(resource.getUrl()).into(imageAndVideoViewHolder.imageResource);
                imageAndVideoViewHolder.resourceName.setText(resource.getName());
                imageAndVideoViewHolder.playVideo.setVisibility(View.GONE);
                imageAndVideoViewHolder.blackLayout.setVisibility(View.GONE);
            } else if (resource.getType().equals(Constant.audioType)) {
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                fileViewHolder.imageResource.setImageResource(R.drawable.ic_file_audio);
                fileViewHolder.resourceName.setText(resource.getName());
            } else {
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                fileViewHolder.imageResource.setImageResource(R.drawable.ic_file);
                fileViewHolder.resourceName.setText(resource.getName());
            }
        } else if (listUploadResource.get(position) instanceof Link) {
            Link link = (Link) listUploadResource.get(position);
            LinkRecyclerViewHolder linkRecyclerViewHolder = (LinkRecyclerViewHolder) holder;
            Glide.with(mContext).load(link.getImage()).into(linkRecyclerViewHolder.linkImage);
            linkRecyclerViewHolder.linkTitle.setText(link.getTitle().toString());
            linkRecyclerViewHolder.linkDescription.setText(link.getDescription().toString());
            linkRecyclerViewHolder.linkUrl.setText(link.getUrl());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (listUploadResource.get(position) instanceof Resource) {
            Resource resource = (Resource) listUploadResource.get(position);
            if (resource.getType().equals(Constant.videoType) || resource.getType().equals(Constant.imageType)) {
                return TYPE_IMAGE_AND_VIDEO;
            } else {
                return TYPE_FILE;
            }
        } else if (listUploadResource.get(position) instanceof Link) {
            return TYPE_LINK;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return listUploadResource.size();
    }

    public void addItem(Object o) {
        listUploadResource.add(o);
        notifyItemInserted(listUploadResource.size() - 1);
        notifyDataSetChanged();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageResource;
        private TextView resourceName;
        private ImageView deleteResource;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageResource = itemView.findViewById(R.id.create_memo_resource_imageView);
            resourceName = itemView.findViewById(R.id.create_memo_resource_name);
            deleteResource = itemView.findViewById(R.id.create_memo_remove_resource);
        }
    }

    class ImageAndVideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageResource;
        private TextView resourceName;
        private ImageView deleteResource;
        private View blackLayout;
        private ImageView playVideo;
        public ImageAndVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageResource = itemView.findViewById(R.id.create_memo_resource_imageView);
            resourceName = itemView.findViewById(R.id.create_memo_resource_name);
            deleteResource = itemView.findViewById(R.id.create_memo_remove_resource);
            blackLayout = itemView.findViewById(R.id.blackLayout);
            playVideo = itemView.findViewById(R.id.playVideo);
        }
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
