package com.lnb.imemo.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tags implements Parcelable {
    private String id;
    private String name;
    private String color;
    private Boolean isisDefault;
    private String createdAt;
    private String updatedAt;

    public Tags() {
    }

    public Tags(String id, String name, String color, Boolean isisDefault, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isisDefault = isisDefault;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected Tags(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readString();
        byte tmpIsisDefault = in.readByte();
        isisDefault = tmpIsisDefault == 0 ? null : tmpIsisDefault == 1;
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeByte((byte) (isisDefault == null ? 0 : isisDefault ? 1 : 2));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tags> CREATOR = new Creator<Tags>() {
        @Override
        public Tags createFromParcel(Parcel in) {
            return new Tags(in);
        }

        @Override
        public Tags[] newArray(int size) {
            return new Tags[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIsisDefault() {
        return isisDefault;
    }

    public void setIsisDefault(Boolean isisDefault) {
        this.isisDefault = isisDefault;
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
        return "Tags{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", isisDefault=" + isisDefault +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
