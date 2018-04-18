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


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseModulesFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    public UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ListView lv;
    View rootView;
    TextView no_course_modules;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_course_modules, container, false);
        lv = (ListView) rootView.findViewById(R.id.lv_course_modules);
        no_course_modules = (TextView) rootView.findViewById(R.id.tv_no_course_modules);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.cm_swiperefresh);
        userDetails = new UserDetails();
        new course_module_topic().execute();
        base = userDetails.getBase();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new course_module_topic().execute();
            }
        });
        return rootView;
    }

    class course_module_topic extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/course_modules");
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
            } catch (Exception e) {     //error logs
                Log.d("course_module_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("course_module_JSON", strJSON);
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        try {
            userDetails.course_module_topics.clear();

            if (strJSON == "") { //if empty json
                lv.setVisibility(View.GONE);
                no_course_modules.setVisibility(View.VISIBLE);
                lv.setAdapter(null);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                int i = 0;

                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    //LAST!!!!!!!!
                    userDetails.course_module_topics.add(i, jsonObject.toString());
                    i++;
                }
                no_course_modules.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                BaseAdapter mAdapter;
                mAdapter = new custom_row_course_modules(getActivity(), userDetails.course_module_topics);
                lv.setAdapter(mAdapter);
            }

        } catch (Exception e) {
            Log.i("course_module_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
