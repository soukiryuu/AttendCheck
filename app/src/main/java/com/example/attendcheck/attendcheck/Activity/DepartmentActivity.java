package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.List;

/**
 * Created by watanabehiroaki on 2016/02/01.
 */
public class DepartmentActivity extends Activity {

    public EditText sbj_name, sbj_id, sbj_num;
    public String sbjName;
    public int sbjId, sbjNum;
    public ImageView reg_btn, p_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        sbj_name = (EditText)findViewById(R.id.sbj_name);
        sbj_id = (EditText)findViewById(R.id.sbj_id);
        sbj_num = (EditText)findViewById(R.id.sbj_num);

        reg_btn = (ImageView)findViewById(R.id.sbj_reg_Btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if ((sbj_name.getText().toString().equals("")) &&
                       (sbj_id.getText().toString().equals("")) &&
                       (sbj_num.getText().toString().equals(""))) {
                   AlertDialog.Builder dialog = new AlertDialog.Builder(DepartmentActivity.this);
                   dialog.setMessage("全ての項目に入力してください。")
                           .setTitle("登録エラーです")
                           .setPositiveButton("OK",null);
                   dialog.create().show();
               }else {
                   sbjName = sbj_name.getText().toString();
                   sbjId = Integer.parseInt(sbj_id.getText().toString());
                   sbjNum = Integer.parseInt(sbj_num.getText().toString());

                   NCMBQuery<NCMBObject> query = new NCMBQuery<NCMBObject>("Subject");
                   query.whereEqualTo("subject_id", sbjId);
                   try {
                       List<NCMBObject> list = query.find();
                       if (list.size() == 0) {
                           NCMBObject obj = new NCMBObject("Subject");
                           obj.put("subject_name", sbjName);
                           obj.put("subject_id", sbjId);
                           obj.put("num_schooldays", sbjNum);
                           obj.put("subject_flg", true);
                           try {
                               obj.save();
                           } catch (NCMBException error) {
                               error.printStackTrace();
                           }
                           AlertDialog.Builder dialog = new AlertDialog.Builder(DepartmentActivity.this);
                           dialog.setMessage("科目の登録が完了しました。右上の時間割ボタンから戻ると時間割登録画面に反映されます。")
                                   .setTitle("登録完了")
                                   .setPositiveButton("OK", null);
                           dialog.create().show();
                       }else {
                           AlertDialog.Builder dialog = new AlertDialog.Builder(DepartmentActivity.this);
                           dialog.setMessage("他の科目IDと重複しています。")
                                   .setTitle("登録エラーです")
                                   .setPositiveButton("OK",null);
                           dialog.create().show();
                       }
                   }catch (NCMBException e){
                       e.printStackTrace();
                   }
               }
            }
        });

        p_btn = (ImageView)findViewById(R.id.periodtime_btn);
        p_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteIntent = new Intent();
                setResult(RESULT_OK, deleteIntent);
                finish();
            }
        });
    }

    public void onDestroy() {
        Log.d("DepartmentActivity", "onDestroy");
        finish();
        super.onDestroy();
    }
}
