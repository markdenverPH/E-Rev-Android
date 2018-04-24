package com.example.babar.e_rev;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class custom_row_cm_detail extends BaseAdapter {
    ArrayList<Integer> id;
    ArrayList<String> name;
    ArrayList<String> path;
    TextView cm_name, cm_size;
    ImageView cm_image;
    UserDetails userDetails;
    int[] image_array;
    private static LayoutInflater inflater = null;
    public custom_row_cm_detail(@NonNull Context context, ArrayList<String> name, ArrayList<String> path, ArrayList<Integer> id) {
        this.name = name;
        this.path = path;
        this.id = id;
        userDetails = new UserDetails();
        image_array = new int[] { R.drawable.ic_pdf, R.drawable.ic_doc};

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_cm_detail, null);
        cm_name = (TextView) rowView.findViewById(R.id.cmd_name);
        cm_size = (TextView) rowView.findViewById(R.id.cmd_size);
        cm_image = (ImageView) rowView.findViewById(R.id.cmd_file_type);

        cm_name.setText(name.get(position));
        String string = path.get(position);
        int index = string.lastIndexOf(".") + 1;
        String check = string.substring(index);
        if(check.equalsIgnoreCase("pdf")){
            cm_image.setImageResource(image_array[0]);
        } else {
            cm_image.setImageResource(image_array[1]);
        }
        cm_size.setText(path.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return id.size();
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
