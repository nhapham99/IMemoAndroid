package com.lnb.imemo.Presentation.Home.TabFragment.ImageAndVideoAdapter;

import android.content.Context;

import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import io.reactivex.subjects.PublishSubject;


public class ImageAndVideoViewPagerAdapter extends RecyclerView.Adapter<ImageAndVideoViewPagerAdapter.ImageAndVideoViewHolder> {
    private final ArrayList<Resource> listImage;
    private Context mContext;
    private PublishSubject<Pair<String, Object>> imageAndVideoViewPagerObservable;
    private static final String TAG = "ImageAndVideoViewPagerA";


    public ImageAndVideoViewPagerAdapter(ArrayList<Resource> listImage) {
        this.listImage = listImage;
        if (imageAndVideoViewPagerObservable == null) {
            imageAndVideoViewPagerObservable = PublishSubject.create();
        }
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
                String urlImage = listImage.get(position).getUrl();
                if (!urlImage.contains("https")) {
                    urlImage = Utils.storeUrl + urlImage;
                }
                Glide.with(mContext).load(urlImage).into(holder.imageView);
                holder.imageView.setOnClickListener(view -> {
                    imageAndVideoViewPagerObservable.onNext(new Pair<>("image_clicked", position));
                    Log.d(TAG, "onBindViewHolder: click image");
                });
            } else {
                String urlVideo = listImage.get(position).getUrl();
                if (!urlVideo.contains("https")) {
                    urlVideo = Utils.storeUrl + urlVideo;
                }
                holder.imageView.setVisibility(View.GONE);
                SimpleExoPlayer player = new SimpleExoPlayer.Builder(mContext).build();
                holder.videoPlayer.setPlayer(player);
                holder.videoPlayer.setOnTouchListener(new View.OnTouchListener() {
                    final GestureDetector detector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            imageAndVideoViewPagerObservable.onNext(new Pair<>("image_clicked", position));
                            return super.onDoubleTap(e);
                        }
                    });
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        detector.onTouchEvent(event);
                        return false;
                    }
                });
                MediaItem mediaItem = MediaItem.fromUri(urlVideo);
                player.prepare();
                player.setMediaItem(mediaItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublishSubject<Pair<String, Object>> getImageAndVideoViewPagerObservable() {
        return imageAndVideoViewPagerObservable;
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    static class ImageAndVideoViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        PlayerView videoPlayer;
        public ImageAndVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memo_single_image);
            videoPlayer = itemView.findViewById(R.id.video_view);
        }
    }
}
