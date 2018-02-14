package com.example.babar.e_rev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class AnnouncementDetail extends AppCompatActivity {
    int item_pos;
    TextView announcer, title, date, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        UserDetails userDetails = new UserDetails();
        item_pos = userDetails.getAd_item();
        getSupportActionBar().setSubtitle("From: " + userDetails.announcement_announcer.get(item_pos));
        title = (TextView) findViewById(R.id.ad_title);
        date = (TextView) findViewById(R.id.ad_date);
        content = (TextView) findViewById(R.id.ad_content);

        title.setText(userDetails.announcement_title.get(item_pos));
        date.setText(userDetails.announcement_created_at.get(item_pos));
        content.setText("— " + userDetails.announcement_content.get(item_pos));

    }
}
