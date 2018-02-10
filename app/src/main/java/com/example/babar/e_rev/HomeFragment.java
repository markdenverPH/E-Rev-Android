package com.example.babar.e_rev;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    /*public HomeFragment() {
        // Required empty public constructor
    }*/
    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        base = "192.168.254.101";

        lv = (ListView) rootView.findViewById(R.id.lv_announce);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new announcement().execute();
            }
        });

        userDetails = new UserDetails();

        return rootView;
    }

    class announcement extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://" + base + "/Engineering/mobile/announcement");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
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
            } catch (Exception e) {     //error logs
                Log.d("check", e.toString());
                dialog.dismiss();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        try {
            jsonObject = new JSONObject(strJSON);
            jsonArray = jsonObject.getJSONArray("result");
            int i = 0;

            while (jsonArray.length() > i) {
                jsonObject = jsonArray.getJSONObject(i);
                userDetails.announcement_title.add(jsonObject.getString("announcement_title"));
                userDetails.announcement_content.add(jsonObject.getString("announcement_content"));
                userDetails.announcement_created_at.add(jsonObject.getString("announcement_created_at"));
                userDetails.announcement_end_datetime.add(jsonObject.getString("announcement_end_datetime"));
                userDetails.announcement_start_datetime.add(jsonObject.getString("announcement_start_datetime"));
                userDetails.announcement_announcer.add(jsonObject.getString("announcement_announcer"));

                i++;
            }

            BaseAdapter mAdapter;
            mAdapter = new custom_row_announcement(getActivity(), userDetails.announcement_title, userDetails.announcement_content,
                    userDetails.announcement_created_at, userDetails.announcement_end_datetime,
                    userDetails.announcement_start_datetime, userDetails.announcement_announcer);
            lv.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.i("check", String.valueOf(e));
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
