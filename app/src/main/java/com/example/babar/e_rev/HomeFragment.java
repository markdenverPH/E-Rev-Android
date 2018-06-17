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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.net.ConnectException;
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
    public UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ListView lv;
    TextView no_announcements;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        base = userDetails.getBase();
        lv = (ListView) rootView.findViewById(R.id.lv_announce);
        no_announcements = (TextView) rootView.findViewById(R.id.tv_no_announcements);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        new announcement().execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new announcement().execute();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AnnouncementDetail.class);
                userDetails.setAd_item(position);
                getActivity().startActivity(intent);
            }
        });

        userDetails = new UserDetails();

        return rootView;
    }


    class announcement extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/announcement");
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
            } catch(ConnectException e){
                //replace with snackbar
                Log.d("announcementerror", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            } catch (Exception e) {     //error logs
                Log.d("announcementerror", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("strJSON", strJSON);
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        try {
            userDetails.announcement_title.clear();
            userDetails.announcement_content.clear();
            userDetails.announcement_created_at.clear();
            userDetails.announcement_end_datetime.clear();
            userDetails.announcement_start_datetime.clear();
            userDetails.announcement_announcer.clear();

            if (strJSON == "") { //if empty json
                lv.setVisibility(View.GONE);
                no_announcements.setVisibility(View.VISIBLE);
                lv.setAdapter(null);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                int i = 0;

                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.announcement_title.add(i, jsonObject.getString("announcement_title"));
                    userDetails.announcement_content.add(jsonObject.getString("announcement_content"));
                    userDetails.announcement_created_at.add(jsonObject.getString("announcement_created_at"));
                    userDetails.announcement_end_datetime.add(jsonObject.getString("announcement_end_datetime"));
                    userDetails.announcement_start_datetime.add(jsonObject.getString("announcement_start_datetime"));
                    userDetails.announcement_announcer.add(jsonObject.getString("announcement_announcer"));
                    i++;
                }
                no_announcements.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                BaseAdapter mAdapter;
                mAdapter = new custom_row_announcement(getActivity(), userDetails.announcement_title, userDetails.announcement_content,
                        userDetails.announcement_created_at, userDetails.announcement_announcer);
                lv.setAdapter(mAdapter);
            }

        } catch (Exception e) {
            Log.i("announcement_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
