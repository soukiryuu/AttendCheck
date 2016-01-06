package com.example.attendcheck.attendcheck;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
//        View view = convertView;
        convertView = layoutInflater.inflate(R.layout.subjlist, parent, false);

        ((TextView)convertView.findViewById(R.id.subjname)).setText(subjlist.get(position).getSubjectName());

        Button button = (Button) convertView.findViewById(R.id.attendbtn);
        button.setTag(position);

        final ListView listView = (ListView) parent;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SubjectAdapter", "ボタンが押された");
                AdapterView.OnItemClickListener listener = listView.getOnItemClickListener();
                long id = getItemId(position);
                listener.onItemClick((AdapterView<?>) parent, v, position, id);
            }
        });

        return convertView;
    }
}
