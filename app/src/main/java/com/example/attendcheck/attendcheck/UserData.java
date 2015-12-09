package com.example.attendcheck.attendcheck;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by watanabehiroaki on 2015/12/01.
 */
public class UserData {

    private String Subject_Name;
    private ArrayList<String> arrayList;

    public String getSubject_Name() {
        return Subject_Name;
    }

    public void setSubject_Name(String subject_Name) {
        this.Subject_Name = subject_Name;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        for (int i=0; i<arrayList.size(); i++) {
            ShowLogInfo("arratlist =" + arrayList.get(i));
        }
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
