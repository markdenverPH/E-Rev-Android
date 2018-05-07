package com.example.babar.e_rev;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class tab3 extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    String base;
    UserDetails userDetails;
    JSONArray jsonArray;
    JSONObject jsonObject;
    TextView tv;
    ArrayList<String> subject_area;
    ArrayList<String> score;
    ArrayList<String> total;
    BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pps_swiperefresh);
        base = userDetails.getBase();
        tv = (TextView) view.findViewById(R.id.tv_no_pps);
        userDetails = new UserDetails();
        barChart = (BarChart) view.findViewById(R.id.barchart);
        subject_area = new ArrayList<>();
        score = new ArrayList<>();
        total = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new PPS().execute();
            }
        });
        new PPS().execute();
        return view;
    }

    class PPS extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/perc_per_sub");
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
                cv.put("id", userDetails.getStudent_id());
                cv.put("offering_id", userDetails.getOffering_id());

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
                Log.d("tab3_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("tab3_JSON", strJSON);
            parseJSON(strJSON);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(String strJSON) {
        try {

            if (strJSON == "") { //if empty json
                barChart.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
            } else {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                int i = 0;
                while (jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    subject_area.add(i, jsonObject.getString("name"));
                    score.add(i, jsonObject.getString("score"));
                    total.add(i, jsonObject.getString("total"));
                    i++;
                }
                tv.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                setup_piechart();
            }

        } catch (Exception e) {
            Log.i("tab3_error", String.valueOf(e.getStackTrace()[0].getLineNumber() + e.toString()));
        }
    }

    private void setup_piechart(){
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
//        xAxis.setPosition(XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTf);
//        xAxis.setDrawGridLines(false);
//        xAxis.setSpaceBetweenLabels(2);

        YAxis rightaxis = barChart.getAxisRight();
//        leftAxis.setTypeface(mTf);
//        leftAxis.setLabelCount(8);
//        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
            rightaxis.setEnabled(false);
        Legend l = barChart.getLegend();
//        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
//        l.setXEntrySpace(4f);

        barChart.animateXY(1000, 2500);

        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < subject_area.size(); i++){
            float fl = Float.parseFloat(score.get(i))/Float.parseFloat(total.get(i)) * 100;
            barEntries.add(new BarEntry(i, fl));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Percentage");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
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
