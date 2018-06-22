package com.example.babar.e_rev;


import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    protected UserDetails userDetails;
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
                new feedback_lect().execute();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
                    if (userDetails.feedback_done.get(position) == 1) {         //means feedback is done
                        Toast.makeText(getActivity(), "Feedback already submitted", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), FeedbackSend.class);
                        userDetails.setAd_item(position);
                        startActivity(intent);
                    }
                } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
                    Intent intent = new Intent(getActivity(), FeedbackView.class);
                    userDetails.setAd_item(position);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        Log.d("feedback_onStart", "feedback_onStart");
        new feedback_lect().execute();
        super.onStart();
    }

    class feedback_lect extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/feedback/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
                    cv.put("offering_id", userDetails.getOffering_id());
                    cv.put("id", userDetails.getStudent_id());
                } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
                    cv.put("id", userDetails.getFic_id());
                }
                cv.put("identifier", userDetails.getIdentifier());
                cv.put("department", userDetails.getDepartment());
                cv.put("firstname", userDetails.getFirstname());
                cv.put("midname", userDetails.getMidname());
                cv.put("lastname", userDetails.getLastname());
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
                Log.d("feedback_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            parseJSON_lect(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON_lect(String strJSON) {
        Log.d("feedback_strJSON", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            int i = 0;

            userDetails.feedback_done.clear();
            userDetails.feedback_offering_name.clear();
            userDetails.feedback_full_name.clear();
            userDetails.feedback_image_path.clear();
            userDetails.feedback_lect_id.clear();
            userDetails.feedback_subject_name.clear();

            if (jsonObject.has("message")) {
                lv.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                jsonArray = jsonObject.getJSONArray("message");
                jsonObject = jsonArray.getJSONObject(i);
                userDetails.feedback_content = jsonObject.getString("message");
                tv_message.setText(userDetails.getFeedback_content());
            } else if (jsonObject.has("result")) {
                lv.setVisibility(View.VISIBLE);
                tv_message.setVisibility(View.GONE);
                jsonArray = jsonObject.getJSONArray("result");
                if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
                    while (jsonArray.length() > i) {
                        jsonObject = jsonArray.getJSONObject(i);
                        userDetails.feedback_done.add(jsonObject.getInt("feedback_done"));
                        userDetails.feedback_offering_name.add(jsonObject.getString("offering_name"));
                        userDetails.feedback_full_name.add(jsonObject.getString("full_name"));
                        userDetails.feedback_image_path.add(jsonObject.getString("image_path"));
                        userDetails.feedback_lect_id.add(jsonObject.getInt("lecturer_id"));
                        userDetails.feedback_subject_name.add(jsonObject.getString("subject_name"));
                        i++;
                    }
                } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
                    while (jsonArray.length() > i) {
                        jsonObject = jsonArray.getJSONObject(i);
                        userDetails.feedback_full_name.add(jsonObject.getString("full_name"));
                        userDetails.feedback_image_path.add(jsonObject.getString("image_path"));
                        userDetails.feedback_lect_id.add(jsonObject.getInt("lecturer_id"));
                        userDetails.feedback_subject_name.add(jsonObject.getString("lecturer_expertise"));
                        i++;
                    }
                }
            }

            BaseAdapter mAdapter;
            mAdapter = new custom_row_feedback(getActivity(), userDetails.feedback_subject_name, userDetails.feedback_image_path,
                    userDetails.feedback_offering_name, userDetails.feedback_full_name, userDetails.feedback_lect_id,
                    userDetails.feedback_done);
            lv.setAdapter(mAdapter);
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
