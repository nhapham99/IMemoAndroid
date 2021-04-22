package com.lnb.imemo.Utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.lnb.imemo.Presentation.UploadActivity.UploadActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    public static String GOOGLE_CLIENT_ID = "335058615265-gcce2lv24jgadcjv20oblhlav3s0caik.apps.googleusercontent.com";
    public static String baseUrls = "http://128.199.169.112:3000/";
    public static String baseUploadUrls = "https://file.imemo.vn/";
    public static String storeUrl = "https://memospace.fra1.cdn.digitaloceanspaces.com/";
    public enum State {
        SUCCESS,
        NO_INTERNET,
        FAILURE
    }
    public enum RegisterState {
        SUCCESS,
        ALREADY_HAVE,
        NO_INTERNET,
        FAILURE
    }
    public enum HTTP_ERROR {
        HTTP_409("HTTP 409 Conflict"),
        HTTP_404("HTTP_404"),
        HTTP_NO_INTERNET("Failed to connect to");

        private final String value;
        HTTP_ERROR(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
