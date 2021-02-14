package com.lnb.imemo.Data.Repository.PreviewLink;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Root;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PreviewLinkRepository {
    private static final String TAG = "PreviewLinkRepository";
    private PreviewLinkAPI previewLinkAPI;
    private Retrofit retrofit;
    private MediatorLiveData<ResponseRepo> previewLinkLiveData;

    public PreviewLinkRepository() {
        retrofit = APIClient.getInstance();
        previewLinkAPI = retrofit.create(PreviewLinkAPI.class);
        previewLinkLiveData = new MediatorLiveData<>();
    }

    public void getPreViewLink(String url) {
        LiveData<Root<Link>> source = LiveDataReactiveStreams.fromPublisher(
                previewLinkAPI.getPreviewLink(url)
                .onErrorReturn(new Function<Throwable, Root<Link>>() {
                    @Override
                    public Root<Link> apply(@NonNull Throwable throwable) throws Exception {
                        String message = throwable.getMessage();
                        Root<Link> previewLinkResponseRoot = new Root<>();
                        if (message.contains(Utils.HTTP_ERROR.HTTP_409.getValue())) {
                            previewLinkResponseRoot.setStatusCode(409);
                        } else if (message.contains(Utils.HTTP_ERROR.HTTP_NO_INTERNET.getValue())) {
                            previewLinkResponseRoot.setStatusCode(-1);
                        } else {
                            previewLinkResponseRoot.setStatusCode(-2);
                        }
                        return previewLinkResponseRoot;
                    }
                })
                .subscribeOn(Schedulers.io())
        );

        previewLinkLiveData.addSource(source, new Observer<Root<Link>>() {
            @Override
            public void onChanged(Root<Link> previewLinkResponseRoot) {
                ResponseRepo<Pair<Utils.State, Link>> response = new ResponseRepo<>();
                if (previewLinkResponseRoot.getStatusCode() == 0) {
                    response.setData(new Pair<>(Utils.State.SUCCESS, previewLinkResponseRoot.getResult()));
                } else if (previewLinkResponseRoot.getStatusCode() == -1) {
                    response.setData(new Pair<>(Utils.State.NO_INTERNET, null));
                } else {
                    response.setData(new Pair<>(Utils.State.FAILURE, null));
                }
                response.setKey(Constant.GET_PREVIEW_LINK_KEY);
                previewLinkLiveData.setValue(response);
                previewLinkLiveData.removeSource(source);
            }
        });
    }

    public MediatorLiveData<ResponseRepo> observablePreviewLink() {
        return previewLinkLiveData;
    }

}
