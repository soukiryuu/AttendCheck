package com.example.attendcheck.attendcheck.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.attendcheck.attendcheck.GetterSetterClass.PeriodTime_Subject;
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
    final String TAG = "PeriodTimeAsyncTask";
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
                PeriodTime_Subject pSubject = new PeriodTime_Subject();
                pSubject.setSubjectName(s.getString("subject_name"));

//                List list1 = new ArrayList();
//                list1.add(s.getList("subject_time"));
//                String[] strings = (String[]) list1.toArray(new String[0]);
                Log.d(TAG,s.getString("subject_name"));

                list.add(pSubject);
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
    protected void onPostExecute(ArrayList<PeriodTime_Subject> result) {
//        AsyncTaskCallbackに取得してきたデータを入れてあげる
//        resultに取得したデータが格納される
        Log.d(TAG, "onPostExecute - " + result);
        super.onPostExecute(result);
        asyncTaskCallback.PostExecute(result);
        dialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "Dialog onCancel... calling cancel(true)");
        this.cancel(true);
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
