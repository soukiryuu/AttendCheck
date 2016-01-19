package com.example.attendcheck.attendcheck.OtherClass;

import android.util.Log;

/**
 * Created by watanabehiroaki on 2016/01/07.
 */
public class PeriodTime_Subject {
    long id;
    String subjectName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        Log.d("PeriodTime_Subject", "subjectName =" + subjectName.toString());
    }
}
