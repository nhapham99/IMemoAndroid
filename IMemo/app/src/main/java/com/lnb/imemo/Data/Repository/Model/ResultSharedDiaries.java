package com.lnb.imemo.Data.Repository.Model;

import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Pagination;
import com.lnb.imemo.Model.PersonProfile;

import java.util.List;

public class ResultSharedDiaries {
    public List<Diary<PersonProfile>> diaries;
    public Pagination pagination;

    public ResultSharedDiaries(List<Diary<PersonProfile>> diaries, Pagination pagination) {
        this.diaries = diaries;
        this.pagination = pagination;
    }

    public ResultSharedDiaries() {
    }

    public List<Diary<PersonProfile>> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary<PersonProfile>> diaries) {
        this.diaries = diaries;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "ResultDiaries{" +
                "diaries=" + diaries +
                ", pagination=" + pagination +
                '}';
    }
}