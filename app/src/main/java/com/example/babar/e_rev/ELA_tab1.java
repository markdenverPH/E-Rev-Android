package com.example.babar.e_rev;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ELA_tab1 extends BaseExpandableListAdapter {

    Context context;
    ArrayList<String> header;
    ArrayList<ArrayList<String>> content;
    ArrayList<ArrayList<ArrayList<String>>> content2;
    Dialog dialog;
    Activity activity;

    public ELA_tab1(Context context, ArrayList<String> header, ArrayList<ArrayList<String>> content, Activity activity,
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
        final LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.ll_tab1);
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_header);

        String name = header.get(groupPosition);
        String topic_name = content2.get(groupPosition).get(0).get(4);
        textView.setText(name + " ("+ topic_name+ ")");
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
        System.out.println(remarks_perc);
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
            builder.setView(v);



            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return builder.create();
        }
    }

}
