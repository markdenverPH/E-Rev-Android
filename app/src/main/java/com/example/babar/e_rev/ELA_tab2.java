package com.example.babar.e_rev;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ELA_tab2 extends BaseExpandableListAdapter {

    Context context;
    ArrayList<String> header;
    ArrayList<ArrayList<String>> content;
    ArrayList<ArrayList<ArrayList<String>>> content2;
    Dialog dialog;
    Activity activity;

    public ELA_tab2(Context context, ArrayList<String> header, ArrayList<ArrayList<String>> content, Activity activity,
                    ArrayList<ArrayList<ArrayList<String>>> content2){
        this.header = header;
        this.content = content;
        this.context = context;
        this.activity = activity;
        this.content2 = content2;

        dialog = new Dialog(context);
    }

    @Override
    public int getGroupCount() {
        return header.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return content.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return header.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return content.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.custom_row_tab1_header, null);
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_header);

        String name = header.get(groupPosition);
        String topic_name = content2.get(groupPosition).get(0).get(4);
        String text = name + " ("+ topic_name+ ")";

        textView.setText(text);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.custom_row_tab1_content, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        TextView details = (TextView) convertView.findViewById(R.id.tv_details);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.tab1_content);
        String take = content.get(groupPosition).get(childPosition);
        String time = content2.get(groupPosition).get(childPosition).get(2);
        int score = Integer.parseInt(content2.get(groupPosition).get(childPosition).get(0));
        int total = Integer.parseInt(content2.get(groupPosition).get(childPosition).get(1));
        double remarks_perc = Double.valueOf(score)/Double.valueOf(total);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_remarks);

        if(remarks_perc >= 0.70)
            imageView.setImageResource(R.mipmap.remarks_checked);
        else
            imageView.setImageResource(R.mipmap.remarks_cancel);

        textView.setText(take);
        details.setText(time + "  —  "+ score +"/"+total);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new PickQuantity();
                Bundle bundle = new Bundle();
                String det_score = content2.get(groupPosition).get(childPosition).get(0);
                String det_total = content2.get(groupPosition).get(childPosition).get(1);
                double perc = Double.valueOf(det_score)/Double.valueOf(det_total);
                String remarks;
                if(perc >= 0.7){
                    remarks = "Passed";
                } else {
                    remarks = "Failed";
                }
                perc *= 100;
                bundle.putString("title", header.get(groupPosition));
                bundle.putString("take", content.get(groupPosition).get(childPosition));
                bundle.putString("score", det_score);
                bundle.putString("total", det_total);
                bundle.putString("perc", String.valueOf(perc)+"%");
                bundle.putString("time", content2.get(groupPosition).get(childPosition).get(1));
                bundle.putString("remarks", remarks);
                dialog.setArguments(bundle);
                dialog.show(activity.getFragmentManager(), "login");
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class PickQuantity extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.custom_dialog_rga_detail,null);
            Button close = (Button) v.findViewById(R.id.rga_detail_close);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            TextView title = (TextView) v.findViewById(R.id.rga_detail_title);
            TextView take = (TextView) v.findViewById(R.id.dialog_detail_take);
            TextView score = (TextView) v.findViewById(R.id.dialog_detail_score);
            TextView total = (TextView) v.findViewById(R.id.dialog_detail_total);
            TextView perc = (TextView) v.findViewById(R.id.dialog_detail_perc);
            TextView time = (TextView) v.findViewById(R.id.dialog_detail_time);
            TextView remarks = (TextView) v.findViewById(R.id.dialog_detail_remarks);
            String s_title, s_take, s_score, s_total, s_perc, s_time, s_remarks;

            s_title = getArguments().getString("title","No data");
            s_take = getArguments().getString("take","No data");
            s_score = getArguments().getString("score","No data");
            s_total = getArguments().getString("total","No data");
            s_perc = getArguments().getString("perc","No data");
            s_time = getArguments().getString("time","No data");
            s_remarks = getArguments().getString("remarks","No data");

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            title.setText(s_title);
            take.setText("Take: "+s_take);
            score.setText("Score: "+s_score);
            total.setText("Total: "+s_total);
            perc.setText("Percentage: "+s_perc);
            time.setText("Time: "+s_time);
            remarks.setText("Remarks: "+s_remarks);

            builder.setView(v);
            return builder.create();
        }
    }

}
