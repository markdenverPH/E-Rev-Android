package com.example.babar.e_rev;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
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

public class tab1 extends Fragment {    //REMEDIAL COURSEWARE GRADE
    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ExpandableListView elv;
    TextView tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.rcg_swiperefresh);
        base = userDetails.getBase();
        elv = (ExpandableListView) view.findViewById(R.id.lv_rcg_cw_name);
        tv = (TextView) view.findViewById(R.id.tv_no_rcg);
        userDetails = new UserDetails();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RCG().execute();
            }
        });
        new RCG().execute();
        return view;
    }

    class RCG extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/rem_grade_assess");
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
                Log.d("tab1_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("tab1_JSON", strJSON);
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        try {
            userDetails.tab1_header.clear();
            userDetails.tab1_content.clear();
            userDetails.tab1_content2.clear();
            if (strJSON == "") { //if empty json
                elv.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("topic_name");
                int i = 0;
                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.tab1_header.add(i, jsonObject.getString("topic"));
                    i++;
                }
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("values");
                i = 0;

                JSONArray innerjsonArray;
                JSONObject innerjsonObject;
                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    innerjsonArray = jsonObject.getJSONArray("inner_val");
                    int i2 = 0;

                    ArrayList<String> temp = new ArrayList<>();
                    ArrayList<ArrayList<String>> temp2 = new ArrayList<>();

                    while(innerjsonArray.length() > i2){
                        innerjsonObject = innerjsonArray.getJSONObject(i2);
                        temp.add(i2, innerjsonObject.getString("take"));

                        ArrayList<String> innertemp = new ArrayList<>();
                        innertemp.add(0, innerjsonObject.getString("remedial_grade_assessment_score"));
                        innertemp.add(1, innerjsonObject.getString("remedial_grade_assessment_total"));
                        innertemp.add(2, innerjsonObject.getString("remedial_grade_assessment_time"));
                        innertemp.add(3, innerjsonObject.getString("courseware_name"));
                        innertemp.add(4, innerjsonObject.getString("topic_name"));
                        temp2.add(i2, innertemp);

                        i2++;
                    }
                    userDetails.tab1_content.add(i, temp);
                    userDetails.tab1_content2.add(i, temp2);
                    i++;
                }
                tv.setVisibility(View.GONE);
                elv.setVisibility(View.VISIBLE);
                BaseExpandableListAdapter mAdapter;
                mAdapter = new ELA_tab1(getActivity(), userDetails.tab1_header, userDetails.tab1_content, getActivity(), userDetails.tab1_content2);
                elv.setAdapter(mAdapter);
            }

        } catch (Exception e) {
            Log.i("tab1_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
