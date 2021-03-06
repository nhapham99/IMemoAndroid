package com.lnb.imemo.Presentation.Home.TabFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Presentation.Home.TabFragment.ImageAndVideoAdapter.ImageAndVideoViewPagerAdapter;
import com.lnb.imemo.Presentation.PreviewImage.PreviewImageActivity;
import com.lnb.imemo.R;
import java.util.ArrayList;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import me.relex.circleindicator.CircleIndicator3;

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    private final ArrayList<Resource> listImageAndVideo;
    public PublishSubject<Boolean> imageFragmentPublish;
    private CompositeDisposable disposable;
    private Context context;

    public ImageFragment(ArrayList<Resource> listImageAndVideo, Context context) {
        this.listImageAndVideo = listImageAndVideo;
        this.context = context;
        if (imageFragmentPublish == null) {
            imageFragmentPublish = PublishSubject.create();
        }
        if (disposable == null) {
            disposable = new CompositeDisposable();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        ViewPager2 imageAndVideoViewPager = view.findViewById(R.id.memo_image_viewPager);
        ImageAndVideoViewPagerAdapter adapter = new ImageAndVideoViewPagerAdapter(listImageAndVideo);
        imageAndVideoViewPager.setAdapter(adapter);

        imageAndVideoViewPager.setClipToPadding(false);
        imageAndVideoViewPager.setClipChildren(false);
        imageAndVideoViewPager.setOffscreenPageLimit(3);
        imageAndVideoViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(20));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        imageAndVideoViewPager.setPageTransformer(compositePageTransformer);
        adapter.getImageAndVideoViewPagerObservable().subscribe(new Observer<Pair<String, Object>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Pair<String, Object> pair) {
                String key = pair.first;
                if (key.equals("image_clicked")) {
                    Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
                    ArrayList<Resource> listResource = new ArrayList<>();
                    listResource.add(listImageAndVideo.get((Integer) pair.second));
                    intent.putExtra("data", listResource);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }
}