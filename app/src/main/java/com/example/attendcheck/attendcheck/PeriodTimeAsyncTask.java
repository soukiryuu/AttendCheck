package com.example.attendcheck.attendcheck;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by watanabehiroaki on 2016/01/07.
 */
public class PeriodTimeAsyncTask extends AsyncTask<String, Integer, ArrayList<PeriodTime_Subject>> implements DialogInterface.OnCancelListener {

    private AsyncTaskCallback asyncTaskCallback = null;
    private Context context;
    private Activity activity;
    final String TAG = "SubjectAsyncTask";
    public static ProgressDialog dialog;
    private NCMBUser LoginUser;


    //AsyncTaskCallbackのインターフェースの登録
    public interface AsyncTaskCallback{
        void PostExecute(ArrayList<PeriodTime_Subject> result);
    }

    public PeriodTimeAsyncTask(Activity activity, Context context, AsyncTaskCallback asyncTaskCallback) {
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
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(this);
        dialog.setMax(100);
        dialog.setProgress(0);
        dialog.show();
    }

    @Override
    protected ArrayList<PeriodTime_Subject> doInBackground(String... params) {
        Log.d(TAG, "doInBackground - " + params[0]);

        LoginUser = NCMBUser.getCurrentUser();
        Log.d("SubjectAsyncTask", NCMBUser.getCurrentUser().toString());

        ArrayList<PeriodTime_Subject> list = new ArrayList<PeriodTime_Subject>();



//      同期処理
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Subject");
//        query.whereContainedInArray("subject_time", Arrays.asList(1,2,3));
        query.whereEqualTo("subject_flg", true);
        try {
//            List<String> list1 = obj.getList();
            List<NCMBObject> sbj_name = query.find();
            for (NCMBObject s: sbj_name) {
                Subject subject = new Subject();
                subject.setSubjectName(s.getString("subject_name"));

//                List list1 = new ArrayList();
//                list1.add(s.getList("subject_time"));
//                String[] strings = (String[]) list1.toArray(new String[0]);
//                Log.d(TAG,strings[0]);

                list.add(subject);
            }


        } catch (NCMBException e) {
            ShowLogInfo("エラーコード:" + e.getCode().toString());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
