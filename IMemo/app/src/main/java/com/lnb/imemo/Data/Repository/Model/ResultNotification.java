package com.lnb.imemo.Data.Repository.Model;

import com.lnb.imemo.Model.Notification;
import com.lnb.imemo.Model.Pagination;
import com.lnb.imemo.Model.PersonProfile;

import java.util.List;

public class ResultNotification {
    private List<Notification<PersonProfile>> notifications;
    private int totalHNotSeen;
    private Pagination pagination;

    public ResultNotification() {
    }

    public ResultNotification(List<Notification<PersonProfile>> notifications, int totalHNotSeen, Pagination pagination) {
        this.notifications = notifications;
        this.totalHNotSeen = totalHNotSeen;
        this.pagination = pagination;
    }

    public List<Notification<PersonProfile>> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification<PersonProfile>> notifications) {
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
