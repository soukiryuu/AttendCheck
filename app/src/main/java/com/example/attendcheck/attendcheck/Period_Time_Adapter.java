package com.example.attendcheck.attendcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by watanabehiroaki on 2016/01/07.
 */
public class Period_Time_Adapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Subject> subjlist;

    public Period_Time_Adapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSubjlist(ArrayList<Subject> subjlist) {
        this.subjlist = subjlist;
    }

    @Override
    public int getCount() {
        return subjlist.size();
    }

    @Override
    public Object getItem(int position) {
        return subjlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return subjlist.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.
                    inflate(R.layout.spinner_item, null);
        }

        ((TextView)convertView.findViewById(R.id.subjectName)).setText(subjlist.get(position).getSubjectName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position,
                                View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.
                    inflate(R.layout.spinner_item, null);
        }

        ((TextView)convertView.findViewById(R.id.subjectName)).setText(subjlist.get(position).getSubjectName());

        return convertView;
    }
}
