package com.lnb.imemo.Data.Entity;

public class PreviewLinkResponse {
    private String title;
    private String image;
    private String description;
    private String url;

    public PreviewLinkResponse() {
    }

    public PreviewLinkResponse(String title, String image, String description, String url) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PreviewLinkResponse{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
