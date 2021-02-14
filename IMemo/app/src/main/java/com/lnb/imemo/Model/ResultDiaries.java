package com.lnb.imemo.Model;

import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Pagination;

import java.util.List;

public class ResultDiaries {
    public List<Diary> diaries;
    public Pagination pagination;

    public ResultDiaries(List<Diary> diaries, Pagination pagination) {
        this.diaries = diaries;
        this.pagination = pagination;
    }

    public ResultDiaries() {
    }

    public List<Diary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary> diaries) {
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