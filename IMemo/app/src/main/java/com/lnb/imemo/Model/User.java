package com.lnb.imemo.Model;

public class User {
    private static User mInstance;
    private String email;
    private String avatarUrl;
    private String name;
    private String token;

    private User() {
    }

    public static User getUser() {
        if (mInstance == null) {
            mInstance = new User();
        }
        return mInstance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clear() {
        mInstance.token = "";
        mInstance.avatarUrl = "";
        mInstance.email = "";
        mInstance.name = "";
    }
}
