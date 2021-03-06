package com.lnb.imemo.Presentation.PreviewImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.PreviewImageViewHolder> {

    private ArrayList<Resource> listResource;
    private Context mContext;

    public PreviewImageAdapter(ArrayList<Resource> listResource) {
        this.listResource = listResource;
    }

    @NonNull
    @Override
    public PreviewImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_detail_image_layout, parent, false);
        mContext = parent.getContext();
        return new PreviewImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewImageViewHolder holder, int position) {
        Resource resource = listResource.get(position);
        if (resource.getType().contains(Constant.imageType)) {
            String imageUrl = resource.getUrl();
            if (!imageUrl.contains("https")) {
                imageUrl = Utils.storeUrl + imageUrl;
            }
            Glide.with(mContext).load(imageUrl).into(holder.imageView);
        } else if (resource.getType().contains(Constant.videoType)){
            String videoUrl = resource.getUrl();
            if (!videoUrl.contains("https")) {
                videoUrl = Utils.storeUrl + videoUrl;
            }
            SimpleExoPlayer player = new SimpleExoPlayer.Builder(mContext).build();
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            holder.playerView.setPlayer(player);
        }
    }

    @Override
    public int getItemCount() {
        return listResource.size();
    }

    class PreviewImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView imageView;
        PlayerView playerView;
        View blackLayout;
        ImageView ic_playVideo;
        public PreviewImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memo_single_image);
            playerView = itemView.findViewById(R.id.video_view);
            blackLayout = itemView.findViewById(R.id.blackLayout);
            ic_playVideo = itemView.findViewById(R.id.playVideo);
            ic_playVideo.setVisibility(View.GONE);
            blackLayout.setVisibility(View.GONE);
        }
    }
}
