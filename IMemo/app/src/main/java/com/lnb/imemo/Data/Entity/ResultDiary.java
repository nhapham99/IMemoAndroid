package com.lnb.imemo.Data.Entity;

import com.lnb.imemo.Model.Diary;

public class ResultDiary {
    private Diary diary;

    public ResultDiary() {
    }

    public ResultDiary(Diary diary) {
        this.diary = diary;
    }

    public Diary getDiary() {
        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }
}
