package com.lnb.imemo.Model;

import java.util.ArrayList;
import java.util.List;

public class Diary{
    private String id;
    private String title;
    private String content;
    private Object emotion;
    private String status;
    private String time;
    private String createdAt;
    private String updatedAt;
    private List<Resource> resources;
    private ArrayList<Tags> tags;
    private List<Link> links;
    private List<String> tagIds;

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
                 List<Resource> resources,
                 ArrayList<Tags> tags, List<Link> links) {
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

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
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