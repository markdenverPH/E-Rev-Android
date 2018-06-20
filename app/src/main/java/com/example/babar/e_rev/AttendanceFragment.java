package com.example.babar.e_rev;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class AttendanceFragment extends Fragment {
    protected UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    String base;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    TextView tv_message;
    ArrayList<Integer> lect_id = new ArrayList<>();
    ArrayList<String> firstname = new ArrayList<>();
    ArrayList<String> midname = new ArrayList<>();
    ArrayList<String> lastname = new ArrayList<>();
    ArrayList<String> image_path = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_attendance_fragment, container, false);
        userDetails = new UserDetails();
        swipeRefreshLayout = view.findViewById(R.id.attend_swiperefresh);
        lv = view.findViewById(R.id.lv_attend);
        tv_message = view.findViewById(R.id.tv_no_attend);
        base = userDetails.getBase();

        new attend_lect().execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new attend_lect().execute();
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AttendanceDetail.class);
                userDetails.setAd_item(lect_id.get(position));
                intent.putExtra("lect_name", firstname.get(position) + " " + midname.get(position) + " " + lastname.get(position));
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    class attend_lect extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/attendance_lecturers/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("id", userDetails.getFic_id());
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
                Snackbar.make(getActivity().findViewById(R.id.coor_layout), "Cannot connect to the server, please check your internet connection", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {     //error logs
                Log.d("attendance_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
        Log.d("strJSON", strJSON);
        try {
            if (strJSON == "") { //if empty json
                lv.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                lv.setAdapter(null);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                int i = 0;

                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    lect_id.add(i, jsonObject.getInt("lecturer_id"));
                    firstname.add(jsonObject.getString("firstname"));
                    midname.add(jsonObject.getString("midname"));
                    lastname.add(jsonObject.getString("lastname"));
                    image_path.add(jsonObject.getString("image_path"));
                    i++;
                }
                tv_message.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                BaseAdapter mAdapter;
                mAdapter = new custom_row_attendance(getActivity(), lect_id, firstname, midname, lastname, image_path);
                lv.setAdapter(mAdapter);
            }
        } catch (Exception e) {
            Log.i("attendance_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
