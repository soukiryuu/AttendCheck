package com.example.attendcheck.attendcheck.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.attendcheck.attendcheck.R;
import com.example.attendcheck.attendcheck.OtherClass.Subject;

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
        ((TextView)convertView.findViewById(R.id.attendrate)).setText(Integer.toString(subjlist.get(position).getAttendRate()));
        ((TextView)convertView.findViewById(R.id.classroom)).setText(subjlist.get(position).getClassRoom());

//        Button button = (Button) convertView.findViewById(R.id.attendbtn);
        ImageView attendview = (ImageView) convertView.findViewById(R.id.attendbtn);
        String subject_ID = subjlist.get(position).getSubjectId();
//        button.setTag(position);
        attendview.setTag(subject_ID);

        final ListView listView = (ListView) parent;
        attendview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SubjectAdapter", "ボタンが押された");
                Log.d("SubjectAdapter", "View=" + v.getTag());
                AdapterView.OnItemClickListener listener = listView.getOnItemClickListener();
                long id = getItemId(position);
                listener.onItemClick((AdapterView<?>) parent, v, position, id);
            }
        });

        return convertView;
    }
}
