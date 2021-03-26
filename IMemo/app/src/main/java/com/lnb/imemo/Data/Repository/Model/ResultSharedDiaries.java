package com.lnb.imemo.Data.Repository.Model;

import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Pagination;

import java.util.List;

public class ResultSharedDiaries {
    public List<ResultShareDiary> diaries;
    public Pagination pagination;

    public ResultSharedDiaries(List<ResultShareDiary> diaries, Pagination pagination) {
        this.diaries = diaries;
        this.pagination = pagination;
    }

    public ResultSharedDiaries() {
    }

    public List<ResultShareDiary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<ResultShareDiary> diaries) {
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