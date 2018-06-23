package com.example.babar.e_rev;


import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

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

        userDetails = new UserDetails();
        base = userDetails.getBase();

        if(userDetails.getIdentifier().equalsIgnoreCase("student")){
            rootView = inflater.inflate(R.layout.fragment_schedule_student, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_schedule_fic, container, false);
        }

        new schedule().execute();

        return rootView;
    }

    class schedule extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/fetch_schedule");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                cv.put("department", userDetails.getDepartment());
                cv.put("identifier", userDetails.getIdentifier());
                if(userDetails.getIdentifier().equalsIgnoreCase("student")){
                    cv.put("id", userDetails.getStudent_id());
                    cv.put("offering_id", userDetails.getOffering_id());
                } else {
                    cv.put("id", userDetails.getFic_id());
                }
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
                Snackbar.make(getActivity().findViewById(R.id.coor_layout), "Cannot connect to the server, please check your internet connection", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {     //error logs
                Log.d("announcementerror", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
                Snackbar.make(getActivity().findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(getActivity().findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
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
