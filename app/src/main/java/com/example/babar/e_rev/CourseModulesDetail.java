package com.example.babar.e_rev;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CourseModulesDetail extends AppCompatActivity {
    int item_pos;
    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    public UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ListView lv;
    TextView no_cm_detail;
DownloadManager downloadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_modules_detail);
        userDetails = new UserDetails();
        item_pos = userDetails.getAd_item();
        getSupportActionBar().setSubtitle(userDetails.course_module_topics.get(item_pos));
        lv = (ListView) findViewById(R.id.lv_cm_detail);
        no_cm_detail = (TextView) findViewById(R.id.tv_no_cm_detail);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.cm_detail_swiperefresh);
        userDetails = new UserDetails();
        new cm_detail().execute();
        base = userDetails.getBase();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new cm_detail().execute();
            }
        });
//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
////            @Override
////            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
////                                           int pos, long id) {
////                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
////                Uri uri = Uri.parse(userDetails.getBase()+"assets/modules/"+userDetails.getCourse_module_topics_path().get(pos));
////                DownloadManager.Request request = new DownloadManager.Request(uri);
////                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
////                Long ref = downloadManager.enqueue(request);
////
////                return true;
////            }
////        });
        registerForContextMenu(lv);
    }

    class cm_detail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/course_modules_detail");
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
                cv.put("topic_id", userDetails.course_module_topics_id.get(item_pos));
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
            } catch (ConnectException e){
                Snackbar.make(findViewById(R.id.cm_detail_base), "Cannot connect to the server, please check your internet connection", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {     //error logs
                Log.d("cm_detail_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
                Snackbar.make(findViewById(R.id.cm_detail_base), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("cm_detail_JSON", strJSON);
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        Log.d("cm_detail_parseJSON", strJSON);
        try {
            userDetails.course_module_topics.clear();
            userDetails.course_module_topics_path.clear();
            userDetails.course_module_topics_id.clear();
            if (strJSON == "") { //if empty json
                lv.setVisibility(View.GONE);
                no_cm_detail.setVisibility(View.VISIBLE);
                lv.setAdapter(null);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                int i = 0;
                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    userDetails.course_module_topics.add(i, jsonObject.getString("course_modules_name"));
                    userDetails.course_module_topics_path.add(i, jsonObject.getString("course_modules_path"));
                    userDetails.course_module_topics_id.add(i, jsonObject.getInt("course_modules_id"));
                    i++;
                }
                no_cm_detail.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                BaseAdapter mAdapter;
                mAdapter = new custom_row_cm_detail(getApplicationContext(), userDetails.course_module_topics,
                        userDetails.course_module_topics_path, userDetails.course_module_topics_id);
                lv.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.i("cm_detail_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            Snackbar.make(findViewById(R.id.cm_detail_base), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.download:
                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(userDetails.getBase()+"assets/modules/"+userDetails.getCourse_module_topics_path().get(info.position));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                Long ref = downloadManager.enqueue(request);
                return true;
            default:
                return super.onContextItemSelected(item);
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
