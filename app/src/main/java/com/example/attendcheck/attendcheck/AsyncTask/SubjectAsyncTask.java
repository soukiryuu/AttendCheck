package com.example.attendcheck.attendcheck.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.attendcheck.attendcheck.OtherClass.Subject;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by watanabehiroaki on 2015/12/01.
 */
public class SubjectAsyncTask extends AsyncTask<String, Integer, ArrayList<Subject>> implements DialogInterface.OnCancelListener {

    private AsyncTaskCallback asyncTaskCallback = null;
    private NCMBUser LoginUser;
    final String TAG = "SubjectAsyncTask";
    public static ProgressDialog dialog;
    private Context context;
    private Activity activity;
    private NCMBObject obj,obj2;
    public Calendar calendar;
    public int dWeek;
    public String dWeekStr,subject_idStr,object_idStr,subjectName;
    public int attendRate;
    public Subject subject;

    //AsyncTaskCallbackのインターフェースの登録
    public interface AsyncTaskCallback{
        void PostExecute(ArrayList<Subject> result);
    }

    public SubjectAsyncTask(Activity activity, Context context, AsyncTaskCallback asyncTaskCallback) {
        this.activity = activity;
        this.context = context;
        this.asyncTaskCallback = asyncTaskCallback;
    }

//    UI処理に関するメソッド
    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        dialog = new ProgressDialog(activity);
        dialog.setTitle("処理中");
        dialog.setMessage("データを取得中です。");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(this);
        dialog.setMax(100);
        dialog.setProgress(0);
        dialog.show();
    }

//    非同期処理をするメソッド
    @Override
    protected ArrayList<Subject> doInBackground(String... params) {
        Log.d(TAG, "doInBackground - " + params[0]);

        //現在の曜日を取得
        calendar = Calendar.getInstance();
        dWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        DayWeek(dWeek);
        Log.d(TAG, dWeekStr);

        LoginUser = NCMBUser.getCurrentUser();
        Log.d("SubjectAsyncTask", NCMBUser.getCurrentUser().toString());

        ArrayList<Subject> list = new ArrayList<Subject>();

//        NCMBQuery<NCMBObject> testquery = new NCMBQuery<>("PeriodOfTime");
//        testquery.whereEqualTo("day_week", "水");
//        testquery.whereContainedInArray("subject_time", Arrays.asList(1));
//        testquery.
//        testquery.findInBackground(new );


//      同期処理
        //午前授業の取得
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("PeriodOfTime");
//        query.whereContainedInArray("subject_time", Arrays.asList(1,2,3));
        query.whereContainedInArray("subject_time", Arrays.asList(1));
        query.whereEqualTo("day_week", dWeekStr);
        try {
//            List<String> list1 = obj.getList();
            List<NCMBObject> sbj_name = query.find();
            for (NCMBObject s: sbj_name) {
                subject = new Subject();
                subject.setSubjectName(s.getString("subject_name"));
                subject.setSubjectId(s.getString("subject_id"));
                subject.setClassRoom(s.getString("classroom_name"));
                subjectName = s.getString("subject_name");
                subject_idStr = s.getString("subject_id");
//                object_idStr = s.getObjectId();
                getAttendRate(subject_idStr);

                list.add(subject);

                //年間の授業の数分、生徒の出欠テーブルにフィールドを作成する
//                obj = new NCMBObject("Pre_Absence");
//                obj.setObjectId(LoginUser.getObjectId());
//                obj.put("student_pointer", NCMBUser.getCurrentUser());
//                obj.put("subject_id", s.getString("subject_id"));

//                obj2 = new NCMBObject("Subject");
//                obj2.setObjectId("subject_id");
//                obj.put("subject_pointer", obj2);
//                obj.put("presence", 0);
//                obj.put("absence", 0);
//                obj.saveInBackground(null);

            }


        } catch (NCMBException e) {
            ShowLogInfo("エラーコード:" + e.getCode().toString());
            e.printStackTrace();
        }

        //      同期処理
        //午後授業の取得
        NCMBQuery<NCMBObject> query2 = new NCMBQuery<>("PeriodOfTime");
//        query.whereContainedInArray("subject_time", Arrays.asList(1,2,3));
        query2.whereContainedInArray("subject_time", Arrays.asList(4));
        query2.whereEqualTo("day_week", dWeekStr);
        try {
//            List<String> list1 = obj.getList();
            List<NCMBObject> sbj_name = query2.find();
            for (NCMBObject s: sbj_name) {
                subject = new Subject();
                subject.setSubjectName(s.getString("subject_name"));
                subject.setSubjectId(s.getString("subject_id"));
                subject.setClassRoom(s.getString("classroom_name"));
                subjectName = s.getString("subject_name");
                subject_idStr = s.getString("subject_id");
//                object_idStr = s.getObjectId();
                getAttendRate(subject_idStr);

                list.add(subject);

            }


        } catch (NCMBException e) {
            ShowLogInfo("エラーコード:" + e.getCode().toString());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d(TAG, "onProgressUpdate - " + values[0]);
        dialog.setProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "onCancelled");
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(ArrayList<Subject> result) {
//        AsyncTaskCallbackに取得してきたデータを入れてあげる
//        resultに取得したデータが格納される
        Log.d(TAG, "onPostExecute - " + result);
        super.onPostExecute(result);
        asyncTaskCallback.PostExecute(result);
        dialog.dismiss();
    }

//    非同期処理中にCancelが要求された時のメソッド
    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "Dialog onCancel... calling cancel(true)");
        this.cancel(true);
    }

    public String DayWeek (int dWeek) {
        switch (dWeek){
            case 1:
                dWeekStr = "日";
                break;
            case 2:
                dWeekStr = "月";
                break;
            case 3:
                dWeekStr = "火";
                break;
            case 4:
                dWeekStr = "水";
                break;
            case 5:
                dWeekStr = "木";
                break;
            case 6:
                dWeekStr = "金";
                break;
            case 7:
                dWeekStr = "土";
                break;
        }
        return dWeekStr;
    }

    public int getAttendRate(String subjectId) {
        Log.d(TAG, "getAttendRate");
        NCMBQuery<NCMBObject> query3 = new NCMBQuery<>("Pre_Absence");
        query3.whereEqualTo("student_id", LoginUser.getObjectId());
        query3.whereEqualTo("subject_id", subjectId);
        query3.whereEqualTo("subject_name", subjectName);
//        query3.whereGreaterThanOrEqualTo("attend_rate", 0);
        NCMBQuery<NCMBObject> query4 = new NCMBQuery<>("Subject");
        query4.whereEqualTo("subject_name", subjectName);
        try {
            Log.d(TAG,"getAttendRateのtry");
            List<NCMBObject> attendrate = query3.find();
            List<NCMBObject> sbjName = query4.find();
            if (attendrate.size() == 0) {
                Log.d(TAG, "getAttendRateのif文:Pre_Absenceにデータなし");
                obj = new NCMBObject("Pre_Absence");
//                obj.setObjectId(LoginUser.getObjectId());
                obj.put("student_pointer", NCMBUser.getCurrentUser());
                obj.put("student_id", LoginUser.getObjectId());
                obj.put("subject_name", subjectName);
                obj.put("subject_id", subject_idStr);
                obj2 = new NCMBObject("Subject");
                obj2 = sbjName.get(0);
                obj2.setObjectId(obj2.getObjectId());
                obj.put("subject_pointer", obj2);
                obj.put("absence", 0);
                obj.put("presence", 0);
                obj.put("attend_rate", 0);
                obj.put("check_flg",false);
                obj.saveInBackground(new DoneCallback() {
                    @Override
                    public void done(NCMBException e) {
                        if (e != null){
                            Log.d(TAG,"保存失敗");
                            e.printStackTrace();
                        }else {
                            Log.d(TAG,"保存成功");
                        }
                    }
                });
            }else {
                Log.d(TAG, "getAttendRateのif文:Pre_Absenceにデータあり");
                for (NCMBObject s: attendrate) {
//                    subject = new Subject();
                    subject.setAttendRate(s.getInt("attend_rate"));
                }
            }
        }catch (NCMBException e) {
            ShowLogInfo("エラーコード:" + e.getCode().toString());
            e.printStackTrace();
        }
        return attendRate;
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
