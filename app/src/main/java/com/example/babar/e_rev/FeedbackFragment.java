package com.example.babar.e_rev;


import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {
    public UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    String base;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    TextView tv_message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        userDetails = new UserDetails();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fb_swipe);
        lv = (ListView) view.findViewById(R.id.fb_lv);
        tv_message = (TextView) view.findViewById(R.id.feedback_message);
        base = userDetails.getBase();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new feedback().execute();
            }
        });

        new feedback().execute();
        return view;
    }

    class feedback extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(base + "mobile/feedback/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                cv.put("offering_id", userDetails.getOffering_id());
                cv.put("identifier", userDetails.getIdentifier());
                cv.put("student_id", userDetails.getStudent_id());
                cv.put("department", userDetails.getDepartment());
                String[] separated = userDetails.getFull_name().split(" ");
                int size = separated.length;
                cv.put("firstname", separated[0]);
                cv.put("midname", separated[size - 2]);
                cv.put("lastname", separated[size - 1]);
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
                Log.d("feedbackerror", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {     //LAST - receiving of data on to be placed on listview
        Log.d("fb_strJSON", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            int i = 0;
            jsonArray = jsonObject.getJSONArray("result");
            jsonObject = jsonArray.getJSONObject(i);

            if (jsonObject.has("message")) {
                jsonArray = jsonObject.getJSONArray("message");
                jsonObject = jsonArray.getJSONObject(i);
                userDetails.feedback_content = jsonObject.getString("message_content");
            } else {
                jsonArray = jsonObject.getJSONArray("result");
                userDetails.announcement_title.clear();
                userDetails.announcement_content.clear();
                userDetails.announcement_created_at.clear();
                userDetails.announcement_end_datetime.clear();
                userDetails.announcement_start_datetime.clear();
                userDetails.announcement_announcer.clear();
                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.feedback_done.add(jsonObject.getInt("announcement_content"));
                    userDetails.feedback_offering_id.add(jsonObject.getInt("announcement_created_at"));
                    userDetails.feedback_lect.add(jsonObject.getString("announcement_created_at"));
                    i++;
                }
            }

//            BaseAdapter mAdapter;
//            mAdapter = new custom_row_announcement(getActivity(), userDetails.announcement_title, userDetails.announcement_content,
//                    userDetails.announcement_created_at, userDetails.announcement_announcer);
//            lv.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.i("feedback_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
