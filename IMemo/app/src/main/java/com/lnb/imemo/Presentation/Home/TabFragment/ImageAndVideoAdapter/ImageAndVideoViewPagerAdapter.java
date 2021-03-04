package com.lnb.imemo.Presentation.Home.TabFragment.ImageAndVideoAdapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import io.reactivex.disposables.CompositeDisposable;


public class ImageAndVideoViewPagerAdapter extends RecyclerView.Adapter<ImageAndVideoViewPagerAdapter.ImageAndVideoViewHolder> {
    private ArrayList<Resource> listImage;
    private Context mContext;
    private static final String TAG = "ImageAndVideoViewPagerA";
    private CompositeDisposable disposable = new CompositeDisposable();
    public ImageAndVideoViewPagerAdapter(ArrayList<Resource> listImage) {
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public ImageAndVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image_in_viewpager, parent, false);
        mContext = parent.getContext();
        return new ImageAndVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAndVideoViewHolder holder, int position) {
        try {
            if (listImage.get(position).getType().contains(Constant.imageType)) {
                holder.videoPlayer.setVisibility(View.GONE);
                holder.blackLayout.setVisibility(View.GONE);
                holder.playIcon.setVisibility(View.GONE);
                String urlImage = listImage.get(position).getUrl();
                if (!urlImage.contains("https")) {
                    urlImage = Utils.storeUrl + urlImage;
                }
                Glide.with(mContext).load(urlImage).into(holder.imageView);
            } else {
                String urlVideo = listImage.get(position).getUrl();
                if (!urlVideo.contains("https")) {
                    urlVideo = Utils.storeUrl + urlVideo;
                }
                Glide.with(mContext)
                        .load(urlVideo)
                        .into(holder.imageView);
                SimpleExoPlayer player = new SimpleExoPlayer.Builder(mContext).build();
                holder.videoPlayer.setPlayer(player);
                MediaItem mediaItem = MediaItem.fromUri(urlVideo);
                player.prepare();
                player.setMediaItem(mediaItem);
                holder.playIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.playIcon.setVisibility(View.GONE);
                        holder.blackLayout.setVisibility(View.GONE);
                        holder.imageView.setVisibility(View.GONE);
                        player.play();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return listImage.size();
    }

    class ImageAndVideoViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        PlayerView videoPlayer;
        ImageView playIcon;
        View blackLayout;
        public ImageAndVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memo_single_image);
            videoPlayer = itemView.findViewById(R.id.video_view);
            playIcon = itemView.findViewById(R.id.playVideo);
            blackLayout = itemView.findViewById(R.id.blackLayout);
        }
    }
}
