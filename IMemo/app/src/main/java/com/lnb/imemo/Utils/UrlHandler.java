package com.lnb.imemo.Utils;

public class UrlHandler {
    public static String convertUrl(String url) {
        if (url.contains(Utils.storeUrl)) {
            return url;
        } else {
            String[] urlArr = url.split("/");
            return Utils.storeUrl + urlArr[urlArr.length - 1];
        }
    }
}
