package com.lnb.imemo.Model;

import androidx.annotation.NonNull;

import com.lnb.imemo.Utils.Utils;

public class ResponseRepo<T> {
    private T data;
    private String key;

    public ResponseRepo() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(@NonNull T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseRepo{" +
                "data=" + data +
                ", key='" + key + '\'' +
                '}';
    }
}
