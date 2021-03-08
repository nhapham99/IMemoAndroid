package com.lnb.imemo.Data.Repository.Model;

public class SharedUser {

    private String id;
    private String sender;
    private Receiver receiver;
    private String receiverEmail;
    private String time;
    private String record;
    private String createdAt;
    private String updatedAt;

    public SharedUser(String id, String sender, Receiver receiver, String receiverEmail, String time, String record, String createdAt, String updatedAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.receiverEmail = receiverEmail;
        this.time = time;
        this.record = record;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public SharedUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
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
        return "SharedUser{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver=" + receiver +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", time='" + time + '\'' +
                ", record='" + record + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
