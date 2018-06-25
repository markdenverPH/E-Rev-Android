package com.example.babar.e_rev;


import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ListView lv_sched_fic = null;
    TextView no_sched_fic = null;
    View rootView;
    ProgressDialog dialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userDetails = new UserDetails();
        base = userDetails.getBase();

        if(userDetails.getIdentifier().equalsIgnoreCase("student")){
            rootView = inflater.inflate(R.layout.fragment_schedule_student, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_schedule_fic, container, false);
            swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh_sched_fic);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new schedule().execute();
                }
            });
        }

        new schedule().execute();

        return rootView;
    }

    class schedule extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(userDetails.getIdentifier().equalsIgnoreCase("student")){
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Loading...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            } else {
                swipeRefreshLayout.setRefreshing(true);
            }
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
                Log.d("sched_error1", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
                Snackbar.make(getActivity().findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("sched_strJSON", strJSON);
            parseJSON(strJSON);
            if(userDetails.getIdentifier().equalsIgnoreCase("student")){
                dialog.dismiss();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }

        }
    }

    public void parseJSON(String strJSON) {
        TextView venue = null;
        TextView day = null;
        TextView time = null;
        TextView section = null;

        TextView tv_no_data = null;
        LinearLayout tv_with_data = null;

        ArrayList<String> fic_section = null;
        ArrayList<String> fic_schedule = null;
        ArrayList<String> fic_venue = null;
        try {

            if(userDetails.getIdentifier().equalsIgnoreCase("student")){
                venue = rootView.findViewById(R.id.sched_venue);
                day = rootView.findViewById(R.id.sched_day);
                time = rootView.findViewById(R.id.sched_time);
                section = rootView.findViewById(R.id.sched_section);

                tv_no_data = rootView.findViewById(R.id.sched_no_data);
                tv_with_data = rootView.findViewById(R.id.sched_with_data);
            } else {
                fic_section = new ArrayList<>();
                fic_schedule = new ArrayList<>();
                fic_venue = new ArrayList<>();

                lv_sched_fic = rootView.findViewById(R.id.lv_sched_fic);
                no_sched_fic = rootView.findViewById(R.id.tv_no_sched_fic);
                swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh_sched_fic);
            }

            jsonObject = new JSONObject(strJSON);
            jsonArray = jsonObject.getJSONArray("result");
            if (strJSON == "" || jsonArray.length() == 0) { //if empty json
                if(userDetails.getIdentifier().equalsIgnoreCase("student")){
                    tv_no_data.setVisibility(View.VISIBLE);
                    tv_with_data.setVisibility(View.GONE);
                } else {
                    lv_sched_fic.setVisibility(View.GONE);
                    no_sched_fic.setVisibility(View.VISIBLE);
                    lv_sched_fic.setAdapter(null);
                }
            } else {
                int i = 0;

                if(userDetails.getIdentifier().equalsIgnoreCase("student")){
                    tv_no_data.setVisibility(View.GONE);
                    tv_with_data.setVisibility(View.VISIBLE);

                    jsonObject = jsonArray.getJSONObject(i);
                    venue.setText(jsonObject.getString("schedule_venue"));
                    day.setText(jsonObject.getString("day"));
                    time.setText(jsonObject.getString("schedule"));
                    section.setText(jsonObject.getString("offering_name"));
                } else {
                    while (jsonArray.length() > i) {
                        jsonObject = jsonArray.getJSONObject(i);
                        fic_section.add(i, jsonObject.getString("offering_name"));
                        fic_schedule.add(i, jsonObject.getString("schedule"));
                        fic_venue.add(i, jsonObject.getString("schedule_venue"));
                        i++;
                    }
                    no_sched_fic.setVisibility(View.GONE);
                    lv_sched_fic.setVisibility(View.VISIBLE);
                    BaseAdapter mAdapter;
                    mAdapter = new custom_row_schedule(getActivity(), fic_section, fic_schedule,fic_venue);
                    lv_sched_fic.setAdapter(mAdapter);
                }

            }
        } catch (Exception e) {
            Log.i("sched_error2", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
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
