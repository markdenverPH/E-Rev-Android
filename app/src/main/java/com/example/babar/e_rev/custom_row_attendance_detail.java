package com.example.babar.e_rev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class custom_row_attendance_detail extends BaseAdapter {
    ArrayList<String> lecturer_attendance_id;
    ArrayList<String> lecturer_attendance_date;
    ArrayList<String> remarks_s;
    ArrayList<String> remarks_e;
    ArrayList<String> hours_rendered;
    LayoutInflater inflater;

    TextView ad_hours;
    TextView ad_rem_s;
    TextView ad_rem_e;
    TextView ad_date;
    public custom_row_attendance_detail(Context context, ArrayList<String> lecturer_attendance_id, ArrayList<String> lecturer_attendance_date,
                                        ArrayList<String> remarks_s, ArrayList<String> remarks_e, ArrayList<String> hours_rendered){
        this.lecturer_attendance_id = lecturer_attendance_id;
        this.lecturer_attendance_date = lecturer_attendance_date;
        this.remarks_s = remarks_s;
        this.remarks_e = remarks_e;
        this.hours_rendered = hours_rendered;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_attendance_detail, null);

        ad_hours = rowView.findViewById(R.id.ad_hours);
        ad_rem_s = rowView.findViewById(R.id.ad_remarks_s);
        ad_rem_e = rowView.findViewById(R.id.ad_remarks_e);
        ad_date = rowView.findViewById(R.id.ad_date);

        ad_hours.setText(hours_rendered.get(position) + " hrs.");
        ad_rem_s.setText("Remarks In: "+remarks_s.get(position));
        ad_rem_e.setText("Remarks Out: "+remarks_e.get(position));
        ad_date.setText(lecturer_attendance_date.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return lecturer_attendance_id.size();
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
