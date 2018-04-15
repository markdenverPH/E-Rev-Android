package com.example.babar.e_rev;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class custom_row_fbv extends BaseAdapter {
    ArrayList<String> content = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();

    TextView tv_content, tv_date;
    private static LayoutInflater inflater = null;

    public custom_row_fbv() {
    }

    public custom_row_fbv(@NonNull Context context, ArrayList<String> content, ArrayList<String> date) {
        this.content = content;
        this.date = date;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.activity_custom_row_fbv, null);
        tv_content = (TextView) rowView.findViewById(R.id.fbv_content);
        tv_date = (TextView) rowView.findViewById(R.id.fbv_date);

        tv_content.setText("— " + content.get(position));
        tv_date.setText(date.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return content.size();
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
