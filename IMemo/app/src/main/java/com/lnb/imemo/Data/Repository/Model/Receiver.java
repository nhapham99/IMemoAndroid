package com.lnb.imemo.Data.Repository.Model;

public class Receiver {
    private String id;
    private String email;
    private String name;
    private String picture;
    private Object username;

    public Receiver(String id, String email, String name, String picture, Object username) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.username = username;
    }

    public Receiver() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Object getUsername() {
        return username;
    }

    public void setUsername(Object username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", username=" + username +
                '}';
    }
}
