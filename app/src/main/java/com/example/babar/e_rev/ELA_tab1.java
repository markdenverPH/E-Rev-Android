package com.example.babar.e_rev;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ELA_tab1 extends BaseExpandableListAdapter {

    Context context;
    ArrayList<String> header;
    ArrayList<ArrayList<String>> content;
    Dialog dialog;
    Activity activity;

    public ELA_tab1(Context context, ArrayList<String> header, ArrayList<ArrayList<String>> content, Activity activity){
        this.header = header;
        this.content = content;
        this.context = context;
        this.activity = activity;

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
        TextView textView = (TextView) convertView.findViewById(R.id.tv_header);
        textView.setText(header.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.custom_row_tab1_content, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText(content.get(groupPosition).get(childPosition));
        textView.setOnClickListener(new View.OnClickListener() {
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
            //LAST!! - put the details here
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
