package com.lnb.imemo.Presentation.Home.TabFragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ImageFragment extends Fragment {

    private ArrayList<Resource> listImageAndVideo;
    private RelativeLayout image1, image2, image3, image4, image5;
    private ArrayList<RelativeLayout> listImageView = new ArrayList<>();
    private LinearLayout linearLayout2;
    private Context mContext;
    public PublishSubject<Boolean> imageFragmentPublish = PublishSubject.create();

    public ImageFragment(ArrayList<Resource> listImageAndVideo, Context mContext) {
        this.listImageAndVideo = listImageAndVideo;
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFragmentPublish.onNext(true);
            }
        });
        image1 = view.findViewById(R.id.image1);
        listImageView.add(image1);
        image2 = view.findViewById(R.id.image2);
        listImageView.add(image2);
        image3 = view.findViewById(R.id.image3);
        listImageView.add(image3);
        image4 = view.findViewById(R.id.image4);
        listImageView.add(image4);
        image5 = view.findViewById(R.id.image5);
        listImageView.add(image5);
        linearLayout2 = view.findViewById(R.id.linear2);

        Single.fromCallable(() -> listPositionBySize(listImageAndVideo.size()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listPosition -> {
                    int tempPos = 0;
                    if (listPosition.size() == 1 || listPosition.size() == 2) {
                        linearLayout2.setVisibility(View.GONE);
                    }
                    for (int position : listPosition) {
                        RelativeLayout containView =  listImageView.get(position);
                        containView.setVisibility(View.VISIBLE);
                        View homeImageLayout = LayoutInflater.from(mContext).inflate(R.layout.home_image_layout, null);
                        RoundedImageView imageView = homeImageLayout.findViewById(R.id.imageView);

                        String url = listImageAndVideo.get(tempPos).getUrl();
                        if (!url.contains("https")) {
                            url = Utils.storeUrl + url;
                        }
                        Glide.with(mContext).load(url).into(imageView);

                        if (listImageAndVideo.get(tempPos).getType().contains(Constant.videoType)) {
                            View blackLayer = homeImageLayout.findViewById(R.id.blackLayer);
                            ImageView playVideoIcon = homeImageLayout.findViewById(R.id.ic_playVideo);
                            blackLayer.setVisibility(View.VISIBLE);
                            playVideoIcon.setVisibility(View.VISIBLE);
                        }

                        if (tempPos == listPosition.size() - 1 && listPosition.size() < listImageAndVideo.size()) {
                            TextView moreImageTextView = homeImageLayout.findViewById(R.id.more_image_textView);
                            moreImageTextView.setVisibility(View.VISIBLE);
                            moreImageTextView.setText("+" + (listImageAndVideo.size() - listPosition.size() + 1));
                        }
                        containView.addView(homeImageLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        tempPos++;
                    }
                });
    }

    private ArrayList<Integer> listPositionBySize(int size) {
        ArrayList<Integer> positions = new ArrayList<>();
        if (size == 1) {
            positions.add(0);
        } else if (size == 2) {
            positions.add(0);
            positions.add(1);
        } else if (size == 3) {
            positions.add(0);
            positions.add(2);
            positions.add(3);
        } else if (size == 4) {
            positions.add(0);
            positions.add(2);
            positions.add(3);
            positions.add(4);
        } else {
            positions.add(0);
            positions.add(1);
            positions.add(2);
            positions.add(3);
            positions.add(4);
        }
        return positions;
    }
}