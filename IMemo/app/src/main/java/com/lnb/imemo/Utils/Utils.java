package com.lnb.imemo.Utils;

public class Utils {
    public static String GOOGLE_CLIENT_ID = "335058615265-gcce2lv24jgadcjv20oblhlav3s0caik.apps.googleusercontent.com";
    public static String baseUrls = "http://134.122.11.145:3000/";
    public static String baseUploadUrls = "http://134.122.11.145:8081/";
    public static String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaWQiOjgsImlhdCI6MTYxMjQ2Mjg0NSwiZXhwIjoxNjEzMDY3NjQ1fQ.xsEj9e6fbg-Xl_jdnEAZa70egzUJJrx_Hr3unC5wZSY";
    public static String storeUrl = "https://memospace.fra1.digitaloceanspaces.com/";
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
        HTTP_NO_INTERNET("Failed to connect to /134.122.11.145:3000");

        private final String value;
        HTTP_ERROR(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
