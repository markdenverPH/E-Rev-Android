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
    ArrayList<String> announcement_title = new ArrayList<>();
    ArrayList<String> announcement_content = new ArrayList<>();
    ArrayList<String> announcement_created_at = new ArrayList<>();
    TextView title, content, date;
    private static LayoutInflater inflater = null;

    public custom_row_announcement(@NonNull Context context, ArrayList<String> announcement_title, ArrayList<String> announcement_content,
                                   ArrayList<String> announcement_created_at) {
        this.announcement_title = announcement_title;
        this.announcement_content = announcement_content;
        this.announcement_created_at = announcement_created_at;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_announcement, null);
        title = (TextView) rowView.findViewById(R.id.title);
        content = (TextView) rowView.findViewById(R.id.subtitle);
        date = (TextView) rowView.findViewById(R.id.date);

        title.setText(announcement_title.get(position));
        content.setText(announcement_content.get(position));
        date.setText(announcement_created_at.get(position));
        return rowView;
    }

    @Override
    public int getCount() {
        return announcement_title.size();
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
