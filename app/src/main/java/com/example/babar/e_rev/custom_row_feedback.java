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

public class custom_row_feedback extends BaseAdapter {
    ArrayList<Integer> lect_id = new ArrayList<>();
    ArrayList<String> image_path = new ArrayList<>();
    ArrayList<String> offering_name = new ArrayList<>();
    ArrayList<String> subject_name = new ArrayList<>();
    ArrayList<String> full_name = new ArrayList<>();
    ArrayList<Integer> feedback_done = new ArrayList<>();

    TextView tv_offering_name, tv_subject_name, tv_full_name;
    ImageView tv_image_path;
    LinearLayout holder;
    UserDetails userDetails = new UserDetails();
    String ident = userDetails.getIdentifier();
    private static LayoutInflater inflater = null;

    public custom_row_feedback(@NonNull Context context, ArrayList<String> subject_name, ArrayList<String> image_path,
                               ArrayList<String> offering_name, ArrayList<String> full_name, ArrayList<Integer> lect_id,
                               ArrayList<Integer> feedback_done) {
        this.lect_id = lect_id;
        this.image_path = image_path;
        this.offering_name = offering_name;
        this.subject_name = subject_name;
        this.full_name = full_name;
        this.feedback_done = feedback_done;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_feedback, null);
        tv_image_path = (ImageView) rowView.findViewById(R.id.lect_profile);
        tv_subject_name = (TextView) rowView.findViewById(R.id.fb_subj);
        tv_full_name = (TextView) rowView.findViewById(R.id.fb_name);
        holder = (LinearLayout) rowView.findViewById(R.id.ll_holder);
        tv_offering_name = (TextView) rowView.findViewById(R.id.fb_offering);

        if (ident.equalsIgnoreCase("student")) {
            tv_offering_name.setText(offering_name.get(position));
        } else if (ident.equalsIgnoreCase("faculty in charge")) {
            tv_offering_name.setVisibility(View.GONE);
        }
        if (!feedback_done.isEmpty() && feedback_done.get(position) == 1) {
            holder.setBackgroundColor(Color.parseColor("#adad9c"));
        }

        Picasso.with(rowView.getContext())
                .load(userDetails.getBase() + userDetails.feedback_image_path.get(position))
                .into(tv_image_path);

        tv_full_name.setText(full_name.get(position));
        tv_subject_name.setText(" — " + subject_name.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return lect_id.size();
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
