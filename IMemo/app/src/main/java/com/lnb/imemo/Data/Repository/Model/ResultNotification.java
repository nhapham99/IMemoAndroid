package com.lnb.imemo.Data.Repository.Model;

import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.Pagination;

import java.util.List;

public class ResultNotification {
    private List<Notification> notifications;
    private int totalHNotSeen;
    private Pagination pagination;

    public ResultNotification() {
    }

    public ResultNotification(List<Notification> notifications, int totalHNotSeen, Pagination pagination) {
        this.notifications = notifications;
        this.totalHNotSeen = totalHNotSeen;
        this.pagination = pagination;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public int getTotalHNotSeen() {
        return totalHNotSeen;
    }

    public void setTotalHNotSeen(int totalHNotSeen) {
        this.totalHNotSeen = totalHNotSeen;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "ResultNotification{" +
                "notifications=" + notifications +
                ", totalHNotSeen=" + totalHNotSeen +
                ", pagination=" + pagination +
                '}';
    }
}
