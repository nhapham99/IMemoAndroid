package com.lnb.imemo.Presentation.Introduce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lnb.imemo.R;

import io.reactivex.subjects.PublishSubject;


public class IntroduceViewPagerAdapter extends RecyclerView.Adapter<IntroduceViewPagerAdapter.ImageAndVideoViewHolder> {

    private Context mContext;
    private static final String TAG = "ImageAndVideoViewPagerA";
    public IntroduceViewPagerAdapter() {}
    PublishSubject<Boolean> publishSubject = PublishSubject.create();

    @NonNull
    @Override
    public ImageAndVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_introduce_layout, parent, false);
        mContext = parent.getContext();
        return new ImageAndVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAndVideoViewHolder holder, int position) {
        if (position == 0) {
            holder.imageView.setImageResource(R.drawable.introduce1);
        } else if (position == 1) {
            holder.imageView.setImageResource(R.drawable.introduce2);
            holder.startMemo.setVisibility(View.VISIBLE);
        }
        holder.startMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishSubject.onNext(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class ImageAndVideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button startMemo;
        public ImageAndVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.introduce_single_image);
            startMemo = itemView.findViewById(R.id.start_memo);
            startMemo.setVisibility(View.INVISIBLE);
        }
    }
}
