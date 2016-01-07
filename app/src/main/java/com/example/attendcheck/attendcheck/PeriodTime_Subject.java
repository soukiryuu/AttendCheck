package com.example.attendcheck.attendcheck;

import android.util.Log;

/**
 * Created by watanabehiroaki on 2016/01/07.
 */
public class PeriodTime_Subject {
    String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        Log.d("PeriodTime_Subject", "subjectName =" + subjectName.toString());
    }
}
