package com.example.babar.e_rev;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class FeedbackSend extends AppCompatActivity {
    TextView fbs_name, fbs_section_topic, fbs_expertise;
    EditText fbs_content;
    Button fbs_cancel, fbs_submit;
    JSONArray jsonArray;
    JSONObject jsonObject;
    UserDetails userDetails;
    ImageView fbs_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_send);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fbs_name = (TextView) findViewById(R.id.fbs_name);
        fbs_section_topic = (TextView) findViewById(R.id.fbs_section_topic);
        fbs_expertise = (TextView) findViewById(R.id.fbs_expertise);
        fbs_content = (EditText) findViewById(R.id.fbs_content);
        fbs_cancel = (Button) findViewById(R.id.fbs_cancel);
        fbs_submit = (Button) findViewById(R.id.fbs_submit);
        fbs_image = (ImageView) findViewById(R.id.fbs_image);
        userDetails = new UserDetails();

        new feedback_fetch().execute();

        fbs_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fbs_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new feedback_send().execute();
            }
        });
    }

    class feedback_send extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(FeedbackSend.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(userDetails.getBase() + "mobile/feedback_submit/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                cv.put("lect_id", userDetails.getAd_item() + 1);
                cv.put("stud_id", userDetails.getStudent_id());
                cv.put("department", userDetails.getDepartment());
                cv.put("enrollment_id", userDetails.getEnrollment());
                cv.put("offering_id", userDetails.getOffering_id());
                cv.put("content", fbs_content.getText().toString());
                Log.d("fbs_sent", userDetails.getAd_item() + " " + String.valueOf(userDetails.getStudent_id()) + " " +
                        userDetails.getDepartment() + " " + userDetails.getEnrollment() + " " + userDetails.getOffering_id());
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
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d("fbs_send_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
                finish();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            dialog.dismiss();
            parseJSON_send(strJSON);
        }
    }

    public void parseJSON_send(String strJSON) {
        Log.d("fbs_send_strJSON", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            if (jsonObject.has("message")) {
                jsonArray = jsonObject.getJSONArray("message");
                jsonObject = jsonArray.getJSONObject(0);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

            } else {
                jsonArray = jsonObject.getJSONArray("result");
                jsonObject = jsonArray.getJSONObject(0);
                Toast.makeText(getApplicationContext(), jsonObject.getString("result"), Toast.LENGTH_LONG).show();
            }
            finish();
        } catch (Exception e) {
            Log.i("fbs_send_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + ": " + e.toString()));
        }
    }

    /****FETCHING***/
    class feedback_fetch extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(FeedbackSend.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(userDetails.getBase() + "mobile/feedback_fetch/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();

                cv.put("lect_id", userDetails.getAd_item() + 1);
                cv.put("stud_id", userDetails.getStudent_id());
                cv.put("identifier", userDetails.getIdentifier());
                cv.put("department", userDetails.getDepartment());
                cv.put("enrollment_id", userDetails.getEnrollment());
                cv.put("offering_id", userDetails.getOffering_id());
                Log.d("fbs_fetch", userDetails.getAd_item() + " " + String.valueOf(userDetails.getStudent_id()) + " " +
                        userDetails.getDepartment() + " " + userDetails.getEnrollment() + " " + userDetails.getOffering_id());
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
                Log.d("fbs_fetch_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            parseJSON_fetch(strJSON);
            dialog.dismiss();
        }
    }

    public void parseJSON_fetch(String strJSON) {
        Log.d("fbs_fetch_strJSON", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            if (jsonObject.has("message")) {
                jsonArray = jsonObject.getJSONArray("message");
                jsonObject = jsonArray.getJSONObject(0);
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                finish();
            } else {
                jsonArray = jsonObject.getJSONArray("result");
                jsonObject = jsonArray.getJSONObject(0);
                fbs_expertise.setText(jsonObject.getString("lecturer_expertise"));
                fbs_name.setText(userDetails.getFull_name());
                Picasso.with(getApplicationContext())
                        .load(userDetails.getBase() + userDetails.feedback_image_path.get(userDetails.getAd_item()))
                        .into(fbs_image);
                fbs_section_topic.setText(userDetails.feedback_offering_name.get(userDetails.getAd_item()) + ": " + userDetails.feedback_subject_name.get(userDetails.getAd_item()));
            }
        } catch (Exception e) {
            Log.i("fbs_fetch_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + ": " + e.toString()));
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
