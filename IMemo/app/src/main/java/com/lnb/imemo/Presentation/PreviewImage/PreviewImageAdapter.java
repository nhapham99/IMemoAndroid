package com.lnb.imemo.Presentation.PreviewImage;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import java.util.ArrayList;

public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.PreviewImageViewHolder> {

    private final ArrayList<Resource> listResource;
    private Context mContext;
    private SimpleExoPlayer player;
    private final PreviewImageActivity activity;

    public PreviewImageAdapter(ArrayList<Resource> listResource, PreviewImageActivity activity) {
        this.listResource = listResource;
        this.activity = activity;
    }

    public void destroyMedia() {
        if (player != null && player.isPlaying()) {
            player.release();
        }
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
            holder.imageView.setOnLongClickListener(v -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.preview_image_bottom_dialog, null);
                TextView saveImageToDevice = view.findViewById(R.id.save_to_device);
                saveImageToDevice.setOnClickListener(v1 -> {
                    startDownloadFile(position);
                    bottomSheetDialog.dismiss();
                });
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                return false;
            });
        } else if (resource.getType().contains(Constant.videoType)){
            String videoUrl = resource.getUrl();
            if (!videoUrl.contains("https")) {
                videoUrl = Utils.storeUrl + videoUrl;
            }

            player = new SimpleExoPlayer.Builder(mContext).build();
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            holder.playerView.setPlayer(player);

            holder.playerView.setOnLongClickListener(v -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.preview_image_bottom_dialog, null);
                TextView saveImageToDevice = view.findViewById(R.id.save_to_device);
                saveImageToDevice.setOnClickListener(v1 -> {
                    startDownloadFile(position);
                    bottomSheetDialog.dismiss();
                });
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                return false;
            });
        }
    }

    private void startDownloadFile(int position) {
        String url = listResource.get(position).getUrl();
        if (!url.contains("https")) {
            url = Utils.storeUrl + url;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(listResource.get(position).getName());
        request.setDescription("Downloading " + listResource.get(position).getName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + listResource.get(position).getName());
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        assert downloadManager != null;
        downloadManager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return listResource.size();
    }

    static class PreviewImageViewHolder extends RecyclerView.ViewHolder {
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
