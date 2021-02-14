package com.lnb.imemo.Presentation.Home.TabFragment.ImageAndVideoAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ImageAndVideoViewPagerAdapter extends RecyclerView.Adapter<ImageAndVideoViewPagerAdapter.ImageAndVideoViewHolder> {
    private ArrayList<Resource> listImage;
    private Context mContext;
    private static final String TAG = "ImageAndVideoViewPagerA";

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
            String urlImage = listImage.get(position).getUrl();
            Glide.with(mContext).load(Utils.storeUrl + urlImage).into(holder.imageView);
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

        public ImageAndVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memo_single_image);
        }
    }

}
