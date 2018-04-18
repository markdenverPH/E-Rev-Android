package com.example.babar.e_rev;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class custom_row_course_modules extends BaseAdapter {
    ArrayList<String> course_module_topics = new ArrayList<>();
    TextView topic_name;
    private static LayoutInflater inflater = null;
    public custom_row_course_modules(@NonNull Context context, ArrayList<String> course_module_topics) {
        this.course_module_topics = course_module_topics;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.activity_custom_row_course_modules, null);
        topic_name = (TextView) rowView.findViewById(R.id.cm_topic_name);
        topic_name.setText(course_module_topics.get(position));
        return rowView;
    }

    @Override
    public int getCount() {
        return course_module_topics.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
