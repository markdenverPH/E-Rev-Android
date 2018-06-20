package com.example.babar.e_rev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class custom_row_attendance extends BaseAdapter{
    ArrayList<Integer> lect_id;
    ArrayList<String> firstname;
    ArrayList<String> midname;
    ArrayList<String> lastname;
    ArrayList<String> image_path;

    TextView attend_name;
    ImageView attend_profile;
    UserDetails userDetails = new UserDetails();
    private static LayoutInflater inflater = null;
    public custom_row_attendance(Context context, ArrayList<Integer> lect_id, ArrayList<String> firstname, ArrayList<String> midname,
                                 ArrayList<String> lastname, ArrayList<String> image_path){
        this.lect_id = lect_id;
        this.firstname = firstname;
        this.midname = midname;
        this.lastname = lastname;
        this.image_path = image_path;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        rowView = inflater.inflate(R.layout.custom_row_attendance, null);
        attend_name = rowView.findViewById(R.id.attend_name);
        attend_profile = rowView.findViewById(R.id.attend_profile);

        attend_name.setText(firstname.get(position) + " " + midname.get(position) + " " + lastname.get(position) + " " );
        attend_name.setAllCaps(true);
        Picasso.with(rowView.getContext())
                .load(userDetails.getBase() + image_path.get(position))
                .into(attend_profile);
        return rowView;
    }
}
