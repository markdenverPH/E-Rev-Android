package com.example.babar.e_rev;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class custom_row_tab3 extends RecyclerView.Adapter<custom_row_tab3.ViewHolder>{

    private ArrayList<String> subject_name;
    private ArrayList<Integer> percentage;

    public custom_row_tab3(ArrayList<String> subject_name, ArrayList<Integer> percentage){
        this.subject_name = subject_name;
        this.percentage = percentage;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView sub, percentage;
        public ViewHolder(View v) {
            super(v);
            sub = v.findViewById(R.id.pps_subject);
            percentage = v.findViewById(R.id.pps_percent);
        }
    }

    // Create new views (invoked by the layout manager)

    @Override
    public custom_row_tab3.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row_tab3, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sub.setText(subject_name.get(position));
        holder.percentage.setText(String.valueOf(percentage.get(position)) + "%");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return subject_name.size();
    }
}
