package com.lnb.imemo.Presentation.PreviewLink;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lnb.imemo.Data.Repository.PreviewLink.PreviewLinkRepository;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Utils.Utils;

public class PreviewLinkViewModel extends ViewModel {
    private static final String TAG = "PreviewLinkViewModel";
    private PreviewLinkRepository previewLinkRepository;
    private MediatorLiveData<Pair<Utils.State, Link>> previewLinkLiveData;
    private Observer<ResponseRepo> previewLinkObservable;

    public PreviewLinkViewModel() {
        previewLinkRepository = new PreviewLinkRepository();
        previewLinkLiveData = new MediatorLiveData<>();
        subscribePreviewLink();
    }

    public void getPreViewLink(String url) {
        previewLinkRepository.getPreViewLink(url);
    }

    private void subscribePreviewLink() {
        previewLinkObservable = new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                Pair<Utils.State, Link> response = (Pair<Utils.State, Link>) responseRepo.getData();
                previewLinkLiveData.setValue(response);
            }
        };
        previewLinkRepository.observablePreviewLink().observeForever(previewLinkObservable);
    }

    public MediatorLiveData<Pair<Utils.State, Link>> observablePreviewLink() {
        return previewLinkLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        previewLinkRepository.observablePreviewLink().removeObserver(previewLinkObservable);
    }
}
