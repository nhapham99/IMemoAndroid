package com.lnb.imemo.Data.Repository.PreviewLink;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.lnb.imemo.Data.APIClient;
import com.lnb.imemo.Data.Entity.PreviewLinkResponse;
import com.lnb.imemo.Data.Entity.ResponseRepo;
import com.lnb.imemo.Data.Entity.ResultDiaries;
import com.lnb.imemo.Data.Entity.Root;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
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
        LiveData<Root<PreviewLinkResponse>> source = LiveDataReactiveStreams.fromPublisher(
                previewLinkAPI.getPreviewLink(url)
                .onErrorReturn(new Function<Throwable, Root<PreviewLinkResponse>>() {
                    @Override
                    public Root<PreviewLinkResponse> apply(@NonNull Throwable throwable) throws Exception {
                        Log.d(TAG, "apply: " + throwable.getMessage());
                        String message = throwable.getMessage();
                        Root<PreviewLinkResponse> previewLinkResponseRoot = new Root<>();
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

        previewLinkLiveData.addSource(source, new Observer<Root<PreviewLinkResponse>>() {
            @Override
            public void onChanged(Root<PreviewLinkResponse> previewLinkResponseRoot) {
                ResponseRepo<Pair<Utils.State, PreviewLinkResponse>> response = new ResponseRepo<>();
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
