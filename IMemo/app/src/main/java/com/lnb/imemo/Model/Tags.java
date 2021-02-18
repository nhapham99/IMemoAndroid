package com.lnb.imemo.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lnb.imemo.Utils.Constant;

public class Tags implements Parcelable {
    private String id;
    private String name;
    private String color;
    private Boolean isDefault;
    private String createdAt;
    private String updatedAt;

    public Tags() {
        setColor(Constant.DEFAULT_TAG_COLOR);
    }

    public Tags(String id, String name, String color, Boolean isDefault, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected Tags(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readString();
        byte tmpIsisDefault = in.readByte();
        isDefault = tmpIsisDefault == 0 ? null : tmpIsisDefault == 1;
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeByte((byte) (isDefault == null ? 0 : isDefault ? 1 : 2));
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

    public void resetTag() {
        this.setColor(Constant.DEFAULT_TAG_COLOR);
        this.setIsDefault(null);
        this.setName(null);
        this.setCreatedAt(null);
        this.setId(null);
        this.setUpdatedAt(null);
    }

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

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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
                ", isisDefault=" + isDefault +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
