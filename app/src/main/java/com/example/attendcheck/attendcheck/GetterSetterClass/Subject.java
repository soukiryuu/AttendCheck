package com.example.attendcheck.attendcheck.GetterSetterClass;

import android.nfc.Tag;
import android.util.Log;

import java.lang.annotation.Target;

/**
 * Created by watanabehiroaki on 2015/11/26.
 */
public class Subject {
    long id;
    String subjectName,subjectId;
    int attendRate;

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
        Log.d("Subject", "subjectName =" + subjectName.toString());
    }

    public int getAttendRate() {
        return attendRate;
    }

    public void setAttendRate(int attendRate) {
        this.attendRate = attendRate;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
        Log.d("Subject", "subject_id =" + subjectId.toString());
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
