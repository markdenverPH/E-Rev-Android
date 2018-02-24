package com.example.babar.e_rev;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class FeedbackView extends AppCompatActivity {

    public UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    String base;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    Spinner interval, sort;
    Integer interval_hold, high, low;
    String sort_hold;
    TextView fbv_message;
    BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_view);

        userDetails = new UserDetails();
        getSupportActionBar().setSubtitle("Lecturer: " + userDetails.feedback_full_name.get(userDetails.getAd_item()));
        interval = (Spinner) findViewById(R.id.fbv_interval);
        sort = (Spinner) findViewById(R.id.fbv_sort);
        interval.setSelection(userDetails.fbv_interval);
        sort.setSelection(userDetails.fbv_sort);
        fbv_message = (TextView) findViewById(R.id.fbv_message);
        lv = (ListView) findViewById(R.id.fbv_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fbv_swipe);

        TextView textView = new TextView(this);
        textView.setText("Load more");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(15);
        textView.setPadding(0, 16, 0, 16);
        lv.addFooterView(textView);

        /***INTERVAL***/
        interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //ignores same picked item
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                userDetails.fbv_interval = position;
                interval_hold = (Integer) interval.getSelectedItem();
//                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                // execute asynctask
                high = (Integer) interval.getSelectedItem();
                low = 0;
                new feedback_view().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing to do here
            }
        });

        /***SORT***/
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //ignores same picked item
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                userDetails.fbv_sort = position;
                switch (position) {
                    case 0:
                        sort_hold = "ASC";
                        break;
                    case 1:
                        sort_hold = "DESC";
                        break;
                }
//                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                // execute asynctask

                new feedback_view().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing to do here
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                high = +(Integer) interval.getSelectedItem();
                low = +(Integer) interval.getSelectedItem();
                new feedback_view().execute();
            }
        });

        BaseAdapter mAdapter;
        mAdapter = new custom_row_announcement(getApplicationContext(), userDetails.announcement_title, userDetails.announcement_content,
                userDetails.announcement_created_at, userDetails.announcement_announcer);
        lv.setAdapter(mAdapter);

    }

    class feedback_view extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "mobile/feedback_fetch/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                cv.put("identifier", userDetails.getIdentifier());
                cv.put("lect_id", userDetails.getAd_item());
                cv.put("lower_limit", low);
                cv.put("higher_limit", high);
                cv.put("enrollment_id", userDetails.getEnrollment());
                cv.put("sort", sort_hold);
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
            parseJSON_lect(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON_lect(String strJSON) {
        Log.d("fb_strJSON", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            int i = 0;

            if (low == 0) {
                userDetails.fbv_content.clear();
                userDetails.fbv_date.clear();
            }

            if (jsonObject.has("message")) {
                lv.setVisibility(View.GONE);
                fbv_message.setVisibility(View.VISIBLE);
                jsonArray = jsonObject.getJSONArray("message");
                userDetails.feedback_content = jsonObject.getString("message");
                fbv_message.setText(userDetails.getFeedback_content());
            } else if (jsonObject.has("result")) {
                lv.setVisibility(View.VISIBLE);
                fbv_message.setVisibility(View.GONE);
                jsonArray = jsonObject.getJSONArray("result");

                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.feedback_full_name.add(jsonObject.getString("full_name"));
                    userDetails.feedback_image_path.add(jsonObject.getString("image_path"));
                    i++;
                }
            }

// LAST - setting up the web. adapter is ready. need to design custom_row
            mAdapter = new custom_row_fbv(getApplicationContext(), userDetails.fbv_content, userDetails.fbv_date);
            lv.setAdapter(mAdapter);

            if (low != 0) {
                lv.invalidateViews();
            }
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
