package com.lnb.imemo.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Diary<T> implements Parcelable {
    private String id;
    private String title;
    private String content;
    private Object emotion;
    private String status;
    private String time;
    private String createdAt;
    private String updatedAt;
    private ArrayList<Resource> resources = new ArrayList<>();
    private ArrayList<Tags> tags = new ArrayList<>();
    private List<Link> links = new ArrayList<>();
    private List<String> tagIds = new ArrayList<>();
    private Boolean isUploading;
    private Boolean pinned;
    private T user;
    private String action;

    public Diary() {
    }

    public Diary(String id,
                 String title,
                 String content,
                 Object emotion,
                 String status,
                 String time,
                 String createdAt,
                 String updatedAt,
                 ArrayList<Resource> resources,
                 ArrayList<Tags> tags,
                 List<Link> links,
                 List<String> tagIds,
                 Boolean isUploading,
                 Boolean pinned,
                 T user,
                 String action) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.emotion = emotion;
        this.status = status;
        this.time = time;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resources = resources;
        this.tags = tags;
        this.links = links;
        this.tagIds = tagIds;
        this.isUploading = isUploading;
        this.pinned = pinned;
        this.user = user;
        this.action = action;
    }

    protected Diary(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        status = in.readString();
        time = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        resources = in.createTypedArrayList(Resource.CREATOR);
        tags = in.createTypedArrayList(Tags.CREATOR);
        links = in.createTypedArrayList(Link.CREATOR);
        tagIds = in.createStringArrayList();
        byte tmpIsUploading = in.readByte();
        isUploading = tmpIsUploading == 0 ? null : tmpIsUploading == 1;
        byte tmpPinned = in.readByte();
        pinned = tmpPinned == 0 ? null : tmpPinned == 1;
        action = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(status);
        dest.writeString(time);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeTypedList(resources);
        dest.writeTypedList(tags);
        dest.writeTypedList(links);
        dest.writeStringList(tagIds);
        dest.writeByte((byte) (isUploading == null ? 0 : isUploading ? 1 : 2));
        dest.writeByte((byte) (pinned == null ? 0 : pinned ? 1 : 2));
        dest.writeString(action);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void createListLinks() {
        links = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getEmotion() {
        return emotion;
    }

    public void setEmotion(Object emotion) {
        this.emotion = emotion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public void setResources(ArrayList<Resource> resources) {
        this.resources = resources;
    }

    public ArrayList<Tags> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tags> tags) {
        this.tags = tags;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(ArrayList<String> tagIds) {
        this.tagIds = tagIds;
    }

    public Boolean getUploading() {
        return isUploading;
    }

    public void setUploading(Boolean uploading) {
        isUploading = uploading;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }

    public void setDiary(Diary diary) {
        setId(diary.getId());
        setTagIds((ArrayList<String>) diary.getTagIds());
        setStatus(diary.getStatus());
        setTags(diary.getTags());
        setUploading(diary.getUploading());
        setTime(diary.getTime());
        setContent(diary.getContent());
        setTitle(diary.getTitle());
        setCreatedAt(diary.getCreatedAt());
        setLinks(diary.getLinks());
        setResources(diary.getResources());
        setUpdatedAt(diary.getUpdatedAt());
        setUser((T) diary.getUser());
    }

    @Override
    public String toString() {
        return "Diary{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", emotion=" + emotion +
                ", status='" + status + '\'' +
                ", time='" + time + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", resources=" + resources +
                ", tag=" + tags +
                ", links=" + links +
                ", tagId='" + tagIds + '\'' +
                '}';
    }
}