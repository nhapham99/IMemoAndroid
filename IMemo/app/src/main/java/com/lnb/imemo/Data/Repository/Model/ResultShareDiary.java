package com.lnb.imemo.Data.Repository.Model;

import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.PersonProfile;

public class ResultShareDiary {
    private String _id;
    private String action;
    private PersonProfile sender;
    private String time;
    private String createdAt;
    private String updatedAt;
    private Diary<String> record;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public PersonProfile getSender() {
        return sender;
    }

    public void setSender(PersonProfile sender) {
        this.sender = sender;
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

    public Diary<String> getRecord() {
        return record;
    }

    public void setRecord(Diary<String> record) {
        this.record = record;
    }
}
