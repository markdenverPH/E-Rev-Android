package com.example.babar.e_rev;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
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
import java.net.ConnectException;
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
    Boolean initial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_view);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDetails = new UserDetails();
        base = userDetails.getBase();
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
        initial = null;

        /***INTERVAL***/
        interval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //ignores same picked item
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                userDetails.fbv_interval = position;    //saved the index
                interval_hold = Integer.valueOf(interval.getSelectedItem().toString()); //value
//                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                // execute asynctask
                high = interval_hold;
                low = 0;
                Log.d("fbv_interval", "interval: high=" + high + " low=" + low);
                if (initial == null) {
                    initial = false;
                }
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
                Log.d("fbv_sort", "sort");
                userDetails.fbv_sort = position;
                switch (position) {
                    case 0:
                        sort_hold = "ASC";
                        break;
                    case 1:
                        sort_hold = "DESC";
                        break;
                }
                if (initial) {
                    high = Integer.valueOf(interval.getSelectedItem().toString());
                    low = 0;
//                Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                    // execute asynctask

                    new feedback_view().execute();
                }
                initial = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing to do here
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                low = low + high;
                Log.d("fbv_loadmore", "loadmore: high=" + high + " low=" + low);
                new feedback_view().execute();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("fbv_swipe", "swipe");
                low = 0;
                high = Integer.valueOf(interval.getSelectedItem().toString());
                userDetails.fbv_interval = interval.getSelectedItemPosition();
                userDetails.fbv_sort = sort.getSelectedItemPosition();
                switch (userDetails.fbv_sort) {
                    case 0:
                        sort_hold = "ASC";
                        break;
                    case 1:
                        sort_hold = "DESC";
                        break;
                }

                new feedback_view().execute();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder fbv_dialog = new AlertDialog.Builder(FeedbackView.this);
                View v = getLayoutInflater().inflate(R.layout.custom_dialog, null);

                TextView content = (TextView) v.findViewById(R.id.fbv_dialog_content);
                TextView fbv_dialog_date = (TextView) v.findViewById(R.id.fbv_dialog_date);
                Button close = (Button) v.findViewById(R.id.fbv_dialog_close);

                content.setText("— " + userDetails.fbv_content.get(position));
                String hold = userDetails.fbv_date.get(position);
                hold.replaceAll("\\n", " ");
                fbv_dialog_date.setText("— " + hold);

                fbv_dialog.setView(v);
                final AlertDialog dialog = fbv_dialog.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

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

                Log.d("fbv_sentPOST", userDetails.getIdentifier() + " " + (userDetails.getAd_item() + 1) + " " + low + " " + high + " " + sort_hold + " " + userDetails.getDepartment());
                cv.put("identifier", userDetails.getIdentifier());
                cv.put("lect_id", userDetails.getAd_item() + 1);
                cv.put("lower_limit", low);
                cv.put("higher_limit", high);
                cv.put("sort", sort_hold);
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
            } catch (ConnectException e){
                Snackbar.make(findViewById(R.id.fb_view_base), "Cannot connect to the server, please check your internet connection", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {     //error logs
                Log.d("fbverror", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
                Snackbar.make(findViewById(R.id.cm_detail_base), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
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
        Log.d("fbv_strJSON", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            int i = 0;

            if (low == 0) {
                userDetails.fbv_content.clear();
                userDetails.fbv_date.clear();
            }

            if (jsonObject.has("message")) {
                if (low == 0) {
                    lv.setVisibility(View.GONE);
                    fbv_message.setVisibility(View.VISIBLE);
                    jsonArray = jsonObject.getJSONArray("message");
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.feedback_content = jsonObject.getString("message");
                    fbv_message.setText(userDetails.getFeedback_content());
                }
            } else if (jsonObject.has("result")) {
                lv.setVisibility(View.VISIBLE);
                fbv_message.setVisibility(View.GONE);
                jsonArray = jsonObject.getJSONArray("result");

                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.fbv_date.add(jsonObject.getString("lecturer_feedback_timedate"));
                    userDetails.fbv_content.add(jsonObject.getString("lecturer_feedback_comment"));
                    i++;
                }
                mAdapter = new custom_row_fbv(getApplicationContext(), userDetails.fbv_content, userDetails.fbv_date);
                lv.setAdapter(mAdapter);

                if (low != 0) {
                    lv.invalidateViews();
                }
            }
            int lv_count = lv.getAdapter().getCount();
            if ((lv_count - 1) != (low + high)) {
                low = lv_count - 2;
                Log.d("fbv_lv_count", "low: " + low);
            }
            lv.smoothScrollToPosition(low);
        } catch (Exception e) {
            Log.i("fbv_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            Snackbar.make(findViewById(R.id.cm_detail_base), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:         //when user pressed back, possible to execute func here
//                Toast.makeText(getApplicationContext(), "TEST BACK", Toast.LENGTH_LONG).show();
//                NavUtils.navigateUpFromSameTask(this);    //default
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
