package com.lnb.imemo.Data.Repository.Model;

public class Root<T> {
    private T result;
    private int statusCode;
    private Object error;

    public Root() {
    }

    public Root(T result, int statusCode, Object error) {
        this.result = result;
        this.statusCode = statusCode;
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Root{" +
                "result=" + result +
                ", statusCode=" + statusCode +
                ", error=" + error +
                '}';
    }
}