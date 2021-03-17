package com.lnb.imemo.Data.Repository.Model;

import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.PersonProfile;

public class ResultDiary {
    private Diary<PersonProfile> diary;

    public ResultDiary() {
    }

    public ResultDiary(Diary<PersonProfile> diary) {
        this.diary = diary;
    }

    public Diary<PersonProfile> getDiary() {
        return diary;
    }

    public void setDiary(Diary<PersonProfile> diary) {
        this.diary = diary;
    }
}
