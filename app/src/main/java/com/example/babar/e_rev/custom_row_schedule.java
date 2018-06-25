package com.example.babar.e_rev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class custom_row_schedule extends BaseAdapter {

    ArrayList<String> fic_section;
    ArrayList<String> fic_schedule;
    ArrayList<String> fic_venue;

    private static LayoutInflater inflater = null;
    public custom_row_schedule(Context context, ArrayList<String> fic_section, ArrayList<String> fic_schedule, ArrayList<String> fic_venue){
        this.fic_section = fic_section;
        this.fic_schedule = fic_schedule;
        this.fic_venue = fic_venue;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_schedule, null);
        TextView tv_sec = rowView.findViewById(R.id.sched_section);
        TextView tv_sched = rowView.findViewById(R.id.sched_schedule);
        TextView tv_ven = rowView.findViewById(R.id.sched_venue);

        tv_sec.setText(fic_section.get(position));
        tv_sched.setText(fic_schedule.get(position));
        tv_ven.setText(fic_venue.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return fic_section.size();
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
