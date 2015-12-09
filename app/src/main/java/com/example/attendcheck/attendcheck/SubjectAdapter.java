package com.example.attendcheck.attendcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by watanabehiroaki on 2015/11/26.
 */
public class SubjectAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Subject> subjlist;

    public SubjectAdapter(Context context) {
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
        convertView = layoutInflater.inflate(R.layout.subjlist, parent, false);

        ((TextView)convertView.findViewById(R.id.subjname)).setText(subjlist.get(position).getSubjectName());

        return convertView;
    }
}
