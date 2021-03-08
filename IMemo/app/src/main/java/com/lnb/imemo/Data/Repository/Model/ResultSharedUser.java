package com.lnb.imemo.Data.Repository.Model;

import java.util.ArrayList;
import java.util.List;

public class ResultSharedUser {

    private List<SharedUser> users;

    public ResultSharedUser(ArrayList<SharedUser> users) {
        this.users = users;
    }

    public ResultSharedUser() {
    }

    public List<SharedUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<SharedUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ResultSharedUser{" +
                "users=" + users +
                '}';
    }
}
