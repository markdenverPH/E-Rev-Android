package com.example.babar.e_rev;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    View rootView;
    UserDetails userDetails;
    ImageView prof_image;
    TextView fullname, role, department, id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        userDetails = new UserDetails();
        prof_image = rootView.findViewById(R.id.prof_image);
        fullname = rootView.findViewById(R.id.prof_fullname);
        role = rootView.findViewById(R.id.prof_role);
        department = rootView.findViewById(R.id.prof_department);
        id = rootView.findViewById(R.id.prof_idnum);

        fullname.setText(userDetails.getFull_name().toUpperCase());
        role.setText(userDetails.getIdentifier());
        department.setText(userDetails.getDepartment());
        if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
            id.setText(String.valueOf(userDetails.getStudent_num()));
        } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
            id.setText(String.valueOf(userDetails.getFic_id()));
        }


        Picasso.with(rootView.getContext())
                .load(userDetails.getBase() + userDetails.getImage_path())
                .into(prof_image);
        return rootView;
    }
}
