package com.lnb.imemo.Presentation.UploadActivity.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.UrlHandler;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class UploadRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "UploadRecyclerViewAdapt";
    private final ArrayList<Object> listUploadResource = new ArrayList<>();
    private static final int TYPE_FILE = 1;
    private static final int TYPE_IMAGE_AND_VIDEO = 2;
    private static final int TYPE_LINK = 3;
    private Context mContext;
    private final PublishSubject<Integer> uploadRecyclerViewObservable = PublishSubject.create();

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
            view = LayoutInflater.from(mContext).inflate(R.layout.upload_link_item_layout, parent, false);
            return new LinkRecyclerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (listUploadResource.get(position) instanceof Resource) {
            Resource resource = (Resource) listUploadResource.get(position);
            if (resource.getType().contains(Constant.videoType)) {
                ImageAndVideoViewHolder imageAndVideoViewHolder = (ImageAndVideoViewHolder) holder;
                Log.d(TAG, "onBindViewHolder: " + resource.getUploading());
                if (resource.getUploading() == null) {
                    imageAndVideoViewHolder.progressIndicator.setVisibility(View.GONE);
                    String url = UrlHandler.convertUrl(resource.getUrl());
                    Glide.with(mContext).load(url).into(imageAndVideoViewHolder.imageResource);
                    imageAndVideoViewHolder.deleteResource.setOnClickListener(v -> {
                        listUploadResource.remove(position);
                        notifyItemRemoved(position);
                        uploadRecyclerViewObservable.onNext(position);
                    });
                } else {
                    imageAndVideoViewHolder.deleteResource.setVisibility(View.GONE);
                    imageAndVideoViewHolder.imageResource.setImageResource(R.drawable.ic_uploading);
                }
                imageAndVideoViewHolder.resourceName.setText(resource.getName());
            } else if (resource.getType().contains(Constant.imageType)) {
                ImageAndVideoViewHolder imageAndVideoViewHolder = (ImageAndVideoViewHolder) holder;
                imageAndVideoViewHolder.playVideo.setVisibility(View.GONE);
                imageAndVideoViewHolder.blackLayout.setVisibility(View.GONE);
                imageAndVideoViewHolder.resourceName.setText(resource.getName());
                Log.d(TAG, "onBindViewHolder: " + resource.getUploading());
                if (resource.getUploading() == null) {
                    imageAndVideoViewHolder.progressIndicator.setVisibility(View.GONE);
                    String url = UrlHandler.convertUrl(resource.getUrl());
                    Glide.with(mContext).load(url).into(imageAndVideoViewHolder.imageResource);
                    imageAndVideoViewHolder.deleteResource.setOnClickListener(v -> {
                        listUploadResource.remove(position);
                        notifyItemRemoved(position);
                        uploadRecyclerViewObservable.onNext(position);
                    });
                } else {
                    imageAndVideoViewHolder.deleteResource.setVisibility(View.GONE);
                    imageAndVideoViewHolder.imageResource.setImageResource(R.drawable.ic_uploading);
                }
            } else if (resource.getType().equals(Constant.audioType)) {
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                fileViewHolder.imageResource.setImageResource(R.drawable.ic_file_audio);
                fileViewHolder.resourceName.setText(resource.getName());
                fileViewHolder.deleteResource.setOnClickListener(v -> {
                    listUploadResource.remove(position);
                    notifyItemRemoved(position);
                    uploadRecyclerViewObservable.onNext(position);
                });
            } else {
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                fileViewHolder.resourceName.setText(resource.getName());
                if (resource.getUploading() == null) {
                    fileViewHolder.imageResource.setImageResource(R.drawable.ic_file);
                    fileViewHolder.deleteResource.setOnClickListener(v -> {
                        listUploadResource.remove(position);
                        notifyItemRemoved(position);
                        uploadRecyclerViewObservable.onNext(position);
                    });
                    fileViewHolder.progressIndicator.setVisibility(View.GONE);
                } else {
                    fileViewHolder.imageResource.setImageResource(R.drawable.ic_uploading);
                }
            }
        } else if (listUploadResource.get(position) instanceof Link) {
            Link link = (Link) listUploadResource.get(position);
            LinkRecyclerViewHolder linkRecyclerViewHolder = (LinkRecyclerViewHolder) holder;
            Glide.with(mContext).load(link.getImage()).into(linkRecyclerViewHolder.linkImage);
            if (link.getTitle() != null) {
                linkRecyclerViewHolder.linkTitle.setText(link.getTitle().toString());
            } else {
                linkRecyclerViewHolder.linkTitle.setText("");
            }
            if (link.getDescription() != null) {
                linkRecyclerViewHolder.linkDescription.setText(link.getDescription().toString());
            } else {
                linkRecyclerViewHolder.linkDescription.setText("");
            }
            if (link.getUrl() != null) {
                linkRecyclerViewHolder.linkUrl.setText(link.getUrl());
            } else {
                linkRecyclerViewHolder.linkUrl.setText("");
            }

            linkRecyclerViewHolder.deleteLink.setOnClickListener(v -> {
                listUploadResource.remove(position);
                notifyItemRemoved(position);
                uploadRecyclerViewObservable.onNext(position);
            });
        }
    }

    public PublishSubject<Integer> getUploadRecyclerViewObservable() {
        return uploadRecyclerViewObservable;
    }

    @Override
    public int getItemViewType(int position) {
        if (listUploadResource.get(position) instanceof Resource) {
            Resource resource = (Resource) listUploadResource.get(position);
            if (resource.getType().contains(Constant.videoType) || resource.getType().contains(Constant.imageType)) {
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

    public void setData(ArrayList<Object> list) {
        listUploadResource.clear();
        listUploadResource.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(Object o) {
        listUploadResource.add(o);
        notifyItemInserted(listUploadResource.size() - 1);
        notifyDataSetChanged();
    }

    public void removeItemAt(int position) {
        Log.d(TAG, "removeItemAt: " + position);
        Object o = listUploadResource.get(position);
        listUploadResource.remove(o);
        notifyItemRemoved(position);
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageResource;
        private final TextView resourceName;
        private final ImageView deleteResource;
        private final LinearProgressIndicator progressIndicator;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageResource = itemView.findViewById(R.id.create_memo_resource_imageView);
            resourceName = itemView.findViewById(R.id.create_memo_resource_name);
            deleteResource = itemView.findViewById(R.id.create_memo_remove_resource);
            progressIndicator = itemView.findViewById(R.id.upload_progressBar);
        }
    }

    static class ImageAndVideoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageResource;
        private final TextView resourceName;
        private final ImageView deleteResource;
        private final View blackLayout;
        private final ImageView playVideo;
        private final LinearProgressIndicator progressIndicator;
        public ImageAndVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageResource = itemView.findViewById(R.id.create_memo_resource_imageView);
            resourceName = itemView.findViewById(R.id.create_memo_resource_name);
            deleteResource = itemView.findViewById(R.id.create_memo_remove_resource);
            blackLayout = itemView.findViewById(R.id.blackLayout);
            playVideo = itemView.findViewById(R.id.playVideo);
            progressIndicator = itemView.findViewById(R.id.upload_progressBar);
        }
    }

    static class LinkRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView linkImage;
        TextView linkTitle;
        TextView linkDescription;
        TextView linkUrl;
        ImageView deleteLink;
        public LinkRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            linkImage = itemView.findViewById(R.id.link_imageView);
            linkTitle = itemView.findViewById(R.id.link_title);
            linkDescription = itemView.findViewById(R.id.link_description);
            linkUrl = itemView.findViewById(R.id.link_url);
            deleteLink = itemView.findViewById(R.id.delete_link);
        }
    }
}
