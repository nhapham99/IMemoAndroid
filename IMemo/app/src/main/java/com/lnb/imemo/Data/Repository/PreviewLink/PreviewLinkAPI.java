package com.lnb.imemo.Data.Repository.PreviewLink;

import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Data.Repository.Model.Root;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PreviewLinkAPI {

    @GET("preview-link")
    Flowable<Root<Link>> getPreviewLink(@Query("url") String url);
}
