package com.example.babar.e_rev;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class AttendanceDetail extends AppCompatActivity {
    int lec_id;
    UserDetails userDetails;
    String base;
    JSONArray jsonArray;
    JSONObject jsonObject;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    TextView tv_message;

    ArrayList<String> lecturer_attendance_id = new ArrayList<>();
    ArrayList<String> lecturer_attendance_date = new ArrayList<>();
    ArrayList<String> lecturer_attendance_in = new ArrayList<>();
    ArrayList<String> lecturer_attendance_out = new ArrayList<>();
    ArrayList<String> sched_start = new ArrayList<>();
    ArrayList<String> sched_end = new ArrayList<>();
    ArrayList<String> remarks_s = new ArrayList<>();
    ArrayList<String> remarks_e = new ArrayList<>();
    ArrayList<String> hours_rendered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);

        userDetails = new UserDetails();
        base = userDetails.getBase();
        lec_id = userDetails.getAd_item();
        swipeRefreshLayout = findViewById(R.id.attend_detail_swiperefresh);
        lv = findViewById(R.id.lv_attend_detail);
        tv_message = findViewById(R.id.tv_no_attend_detail);
        String full_name = getIntent().getStringExtra("lect_name");

        new fetch_lect_details().execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new fetch_lect_details().execute();
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder fbv_dialog = new AlertDialog.Builder(AttendanceDetail.this);
                View v = getLayoutInflater().inflate(R.layout.custom_dialog_attend_detail, null);

                TextView ad_id = v.findViewById(R.id.ad_id);
                TextView sched_s = v.findViewById(R.id.ad_sched_start);
                TextView sched_e = v.findViewById(R.id.ad_sched_end);
                TextView time_in = v.findViewById(R.id.ad_time_in);
                TextView time_out = v.findViewById(R.id.ad_time_out);
                TextView rendered_hours = v.findViewById(R.id.ad_rendered_hours);
                TextView remarks_s = v.findViewById(R.id.ad_remarks_s);
                TextView remarks_e = v.findViewById(R.id.ad_remarks_e);
                Button close = (Button) v.findViewById(R.id.attend_detail_dialog_close);

                //LASTTTT - design and set values

                fbv_dialog.setView(v);
                final AlertDialog dialog = fbv_dialog.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        getSupportActionBar().setSubtitle(full_name.toUpperCase());
    }

    class fetch_lect_details extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/fetch_lecturer_attendance/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("id", lec_id);
                cv.put("department", userDetails.getDepartment());
                bw.write(createPostString(cv));
                bw.flush();
                bw.close();
                os.close();
//                int rc = con.getResponseCode();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder sb = new StringBuilder();
                String str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                br.close();
                is.close();
                con.disconnect();
                return sb.toString();
            } catch (ConnectException e) {     //error logs
                Snackbar.make(findViewById(R.id.attend_detail_base), "Cannot connect to the server, please check your internet connection", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {     //error logs
                Log.d("attendance_detail_bg", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("attendance_detail_post", strJSON);
            parseJSON(strJSON);
        }
    }

    public void parseJSON(String strJSON) {
        Log.d("attendance_detail_parse", strJSON);
        lecturer_attendance_id.clear();
        lecturer_attendance_date.clear();
        lecturer_attendance_in.clear();
        lecturer_attendance_out.clear();
        sched_start.clear();
        sched_end.clear();
        remarks_s.clear();
        remarks_e.clear();
        hours_rendered.clear();
        try {
            jsonObject = new JSONObject(strJSON);
            jsonArray = jsonObject.getJSONArray("result");
            if (strJSON == "" || jsonArray.length() == 0) { //if empty json
                lv.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                lv.setAdapter(null);
            } else {
                int i = 0;

                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    lecturer_attendance_id.add(i, jsonObject.getString("lecturer_attendance_id"));
                    lecturer_attendance_date.add(i, jsonObject.getString("lecturer_attendance_date"));
                    lecturer_attendance_in.add(i, jsonObject.getString("lecturer_attendance_in"));
                    lecturer_attendance_out.add(i, jsonObject.getString("lecturer_attendance_out"));
                    sched_start.add(i, jsonObject.getString("sched_start"));
                    sched_end.add(i, jsonObject.getString("sched_end"));
                    remarks_s.add(i, jsonObject.getString("remarks_s"));
                    remarks_e.add(i, jsonObject.getString("remarks_e"));
                    hours_rendered.add(i, jsonObject.getString("hours_rendered"));
                    i++;
                }
                tv_message.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                BaseAdapter mAdapter;
                mAdapter = new custom_row_attendance_detail(getApplicationContext(), lecturer_attendance_id, lecturer_attendance_date,
                        remarks_s, remarks_e, hours_rendered);
                lv.setAdapter(mAdapter);
            }
        } catch (Exception e) {
            Log.i("attendance_detail_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public String createPostString(ContentValues cv) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean flag = true;
        Set set = cv.valueSet();

        for (Map.Entry<String, Object> v : cv.valueSet()) {
            if (flag) {
                flag = false;
            } else {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(v.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(v.getValue().toString(), "UTF-8"));
        }
        return sb.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:         //when user pressed back, possible to execute func here
//                Toast.makeText(getApplicationContext(), "TEST BACK", Toast.LENGTH_LONG).show();
//                NavUtils.navigateUpFromSameTask(this);    //default
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
