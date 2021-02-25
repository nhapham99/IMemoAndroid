package com.lnb.imemo.Model;

public class Notification<T> {
    private Boolean seen;
    private String id;
    private T user;
    private String data;
    private String createdAt;
    private String updatedAt;

    public Notification() {
    }

    public Notification(Boolean seen, String id, T user, String data, String createdAt, String updatedAt) {
        this.seen = seen;
        this.id = id;
        this.user = user;
        this.data = data;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "seen=" + seen +
                ", id='" + id + '\'' +
                ", user=" + user +
                ", data='" + data + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
