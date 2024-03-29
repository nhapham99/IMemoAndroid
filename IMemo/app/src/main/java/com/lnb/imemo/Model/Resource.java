package com.lnb.imemo.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable {
    private String id;
    private String url;
    private String name;
    private String type;
    private String createdAt;
    private String updatedAt;
    private Boolean uploading;

    public Resource() {
    }

    public Resource(String id, String url, String name, String type, String createdAt, String updatedAt, Boolean uploading) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.uploading = uploading;
    }


    protected Resource(Parcel in) {
        id = in.readString();
        url = in.readString();
        name = in.readString();
        type = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        byte tmpUploading = in.readByte();
        uploading = tmpUploading == 0 ? null : tmpUploading == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeByte((byte) (uploading == null ? 0 : uploading ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getUploading() {
        return uploading;
    }

    public void setUploading(Boolean uploading) {
        this.uploading = uploading;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}