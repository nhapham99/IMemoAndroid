package com.lnb.imemo.Model;

public class Link{
    private String id;
    private String url;
    private Object image;
    private Object title;
    private Object description;
    private String createdAt;
    private String updatedAt;

    public Link() {
    }

    public Link(String id, String url, Object image, Object title, Object description, String createdAt, String updatedAt) {
        this.id = id;
        this.url = url;
        this.image = image;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Object getTitle() {
        return title;
    }

    public void setTitle(Object title) {
        this.title = title;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
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
        return "Link{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                ", title=" + title +
                ", description=" + description +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
