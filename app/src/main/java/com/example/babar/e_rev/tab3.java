package com.example.babar.e_rev;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class tab3 extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    TextView tv;
    ArrayList<String> subject_area;
    ArrayList<Integer> perc;
    RecyclerView pps_recycle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        swipeRefreshLayout = view.findViewById(R.id.pps_swiperefresh);
        base = userDetails.getBase();
        tv = view.findViewById(R.id.tv_no_pps);
        pps_recycle = view.findViewById(R.id.pps_recycle);
        userDetails = new UserDetails();
        subject_area = new ArrayList<>();
        perc = new ArrayList<>();
        pps_recycle = view.findViewById(R.id.pps_recycle);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new PPS().execute();
            }
        });
        new PPS().execute();
        return view;
    }

    class PPS extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/perc_per_sub");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                cv.put("department", userDetails.getDepartment());
                cv.put("identifier", userDetails.getIdentifier());
                cv.put("firstname", userDetails.getFirstname());
                cv.put("midname", userDetails.getMidname());
                cv.put("lastname", userDetails.getLastname());
                cv.put("id", userDetails.getStudent_id());
                cv.put("offering_id", userDetails.getOffering_id());

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
            } catch (Exception e) {     //error logs
                Log.d("tab3_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("tab3_JSON", strJSON);
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        try {
            subject_area.clear();
            perc.clear();
            if (strJSON == "") { //if empty json
                pps_recycle.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                int i = 0;
                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    subject_area.add(i, jsonObject.getString("name"));
                    String score = jsonObject.getString("score");
                    String total = jsonObject.getString("total");
                    float percentage = (Float.parseFloat(score)/Float.parseFloat(total)) * 100;
                    perc.add(i, (int) percentage);
                    i++;
                }
                tv.setVisibility(View.GONE);
                pps_recycle.setVisibility(View.VISIBLE);

                custom_row_tab3 adapter = new custom_row_tab3(subject_area, perc);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                pps_recycle.setLayoutManager(mLayoutManager);
                pps_recycle.setItemAnimator(new DefaultItemAnimator());
                pps_recycle.setAdapter(adapter);
            }

        } catch (Exception e) {
            Log.i("tab3_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
        }
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
}
