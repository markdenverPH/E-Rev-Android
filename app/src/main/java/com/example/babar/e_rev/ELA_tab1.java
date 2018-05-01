package com.example.babar.e_rev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ELA_tab1 extends BaseExpandableListAdapter {

    Context context;
    ArrayList<String> header;
    ArrayList<ArrayList<String>> content;

    public ELA_tab1(Context context, ArrayList<String> header, ArrayList<ArrayList<String>> content){
        this.header = header;
        this.content = content;
        this.context = context;

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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.custom_row_tab1_content, null);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
        textView.setText(content.get(groupPosition).get(childPosition));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LAST!!! - pop dialog and show details
                Toast.makeText(context, content.get(groupPosition).get(childPosition), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
