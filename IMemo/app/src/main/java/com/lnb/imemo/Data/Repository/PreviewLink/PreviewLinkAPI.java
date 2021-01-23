package com.lnb.imemo.Data.Repository.PreviewLink;

import com.lnb.imemo.Data.Entity.PreviewLinkResponse;
import com.lnb.imemo.Data.Entity.Root;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PreviewLinkAPI {

    @GET("preview-link")
    Flowable<Root<PreviewLinkResponse>> getPreviewLink(@Query("url") String url);
}
