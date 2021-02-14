package com.lnb.imemo.Presentation.Home.TabFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Presentation.Home.TabFragment.ImageAndVideoAdapter.ImageAndVideoViewPagerAdapter;
import com.lnb.imemo.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class ImageFragment extends Fragment {

    private ViewPager2 imageAndVideoViewPager;
    private ArrayList<Resource> listImageAndVideo;
    private ImageAndVideoViewPagerAdapter adapter;
    private CircleIndicator3 circleIndicator;

    public ImageFragment(ArrayList<Resource> listImageAndVideo) {
        this.listImageAndVideo = listImageAndVideo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        imageAndVideoViewPager = view.findViewById(R.id.memo_image_viewPager);
        circleIndicator = view.findViewById(R.id.circle_indicator);
        adapter = new ImageAndVideoViewPagerAdapter(listImageAndVideo);
        imageAndVideoViewPager.setAdapter(adapter);
        circleIndicator.setViewPager(imageAndVideoViewPager);
        if (listImageAndVideo.size() == 1) {
            circleIndicator.setVisibility(View.GONE);
        }
    }
}