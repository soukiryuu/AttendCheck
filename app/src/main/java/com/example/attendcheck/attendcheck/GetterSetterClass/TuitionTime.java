package com.example.attendcheck.attendcheck.GetterSetterClass;

import java.util.Calendar;

/**
 * Created by watanabehiroaki on 2016/01/15.
 */
public class TuitionTime {

    boolean flg = false;

    TuitionTime(){
        attendFlg();
    }

    public boolean attendFlg (){
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return flg;
    }

}
