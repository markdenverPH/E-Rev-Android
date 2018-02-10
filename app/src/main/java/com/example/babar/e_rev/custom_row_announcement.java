package com.example.babar.e_rev;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by babar on 2/11/2018.
 */

public class custom_row_announcement extends BaseAdapter {
    ArrayList<String> announcement_title;
    ArrayList<String> announcement_content;
    ArrayList<String> announcement_created_at;
    ArrayList<String> announcement_end_datetime;
    ArrayList<String> announcement_start_datetime;
    ArrayList<String> announcement_announcer;
    TextView title, content, announcer;
    private static LayoutInflater inflater = null;

    public custom_row_announcement(@NonNull Context context, ArrayList<String> announcement_title, ArrayList<String> announcement_content,
                                   ArrayList<String> announcement_created_at, ArrayList<String> announcement_end_datetime,
                                   ArrayList<String> announcement_start_datetime, ArrayList<String> announcement_announcer) {
        this.announcement_title = announcement_title;
        this.announcement_content = announcement_content;
        this.announcement_created_at = announcement_created_at;
        this.announcement_end_datetime = announcement_end_datetime;
        this.announcement_start_datetime = announcement_start_datetime;
        this.announcement_announcer = announcement_announcer;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_announcement, null);
        title = (TextView) rowView.findViewById(R.id.title);
        content = (TextView) rowView.findViewById(R.id.subtitle);
        announcer = (TextView) rowView.findViewById(R.id.announcer);

        return rowView;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
