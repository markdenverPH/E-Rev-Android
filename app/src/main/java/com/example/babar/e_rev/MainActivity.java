package com.example.babar.e_rev;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public HomeFragment homeFragment = new HomeFragment();
    public GradeAssessmentFragment gradeAssessmentFragment = new GradeAssessmentFragment();
    public CourseModulesFragment course_modules_Fragment = new CourseModulesFragment();
    public ScheduleFragment scheduleFragment = new ScheduleFragment();
    public FeedbackFragment feedbackFragment = new FeedbackFragment();
    public AttendanceFragment attendanceFragment = new AttendanceFragment();
    NavigationView navigationView;
    TextView nav_full_name, nav_user_role;
    int current_menu = R.id.nav_home;
    UserDetails userDetails;
    ImageView nav_profile;
    String base;
    JSONArray jsonArray;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Home");

        SharedPreferences sp = getSharedPreferences("NotifyID", 0);
        if (sp.getInt("notify_id", 0) == 0) {   //checks if first run; if so, then update to 1
            SharedPreferences.Editor spedit = sp.edit();
            spedit.putInt("notify_id", 1);
            spedit.apply();
            Log.d("NotifyID", String.valueOf(sp.getInt("notify_id", 0)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        userDetails = new UserDetails();
        base = userDetails.getBase();
        new update_token().execute();   //update token to mysql
        //determines the menu items to be shown depending on identifier
        if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
            navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_schedule).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_course_modules).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_feedback).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_grade).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_attendance).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
            navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_schedule).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_course_modules).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_feedback).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_grade).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_attendance).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        }
        View v = navigationView.getHeaderView(0);
        nav_full_name = (TextView) v.findViewById(R.id.nav_full_name);
        nav_user_role = (TextView) v.findViewById(R.id.nav_user_role);
        nav_profile = (ImageView) v.findViewById(R.id.nav_profile);
        //capitalize
        String firstname = userDetails.getFirstname().substring(0, 1).toUpperCase() + userDetails.getFirstname().substring(1) + " ";
        String lastname = userDetails.getLastname().substring(0, 1).toUpperCase() + userDetails.getLastname().substring(1);
        String midname = userDetails.getMidname().substring(0, 1).toUpperCase() + ". ";
        nav_full_name.setText(firstname + midname + lastname);
        if(userDetails.getIdentifier().equalsIgnoreCase("student")){
            nav_user_role.setText(userDetails.getStudent_num() + " — " + userDetails.getIdentifier() + " — " + userDetails.getDepartment());
        } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")){
            nav_user_role.setText(userDetails.getFic_id() + " — " + userDetails.getIdentifier() + " — " + userDetails.getDepartment());
        }

        Picasso.with(v.getContext())
                .load(userDetails.getBase() + userDetails.getImage_path())
                .into(nav_profile);

        //default nav
        if (userDetails.getFrag_hold() == null) {
//            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                    .replace(R.id.fragment_hold, homeFragment);
            fragmentTransaction.commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        //end default nav
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            navigationView.setCheckedItem(R.id.nav_home);
            this.setTitle("Home");
            current_menu = R.id.nav_home;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (current_menu != id) {
            fragmentTransaction = fragmentManager.beginTransaction();
            getFragmentManager().popBackStack();
            if (id == R.id.nav_home) {
                userDetails.setFrag_hold("Home");
//            fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
//                    .replace(R.id.fragment_hold, homeFragment);
                this.setTitle("Home");
            } else if (id == R.id.nav_grade) {
                userDetails.setFrag_hold("Grade Assessment");
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, gradeAssessmentFragment);
                this.setTitle("Grade Assessment");
                fragmentTransaction.addToBackStack(null);
            } else if (id == R.id.nav_course_modules) {
                userDetails.setFrag_hold("Course Modules");
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, course_modules_Fragment);
                this.setTitle("Course Modules");
                fragmentTransaction.addToBackStack(null);
            } else if (id == R.id.nav_schedule) {
                userDetails.setFrag_hold("Schedule");
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, scheduleFragment);
                this.setTitle("Schedule");
                fragmentTransaction.addToBackStack(null);
            } else if (id == R.id.nav_feedback) {
                userDetails.setFrag_hold("Feedback");
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, feedbackFragment);
                this.setTitle("Feedback");
                fragmentTransaction.addToBackStack(null);
            } else if (id == R.id.nav_attendance) {
                userDetails.setFrag_hold("Attendance");
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, attendanceFragment);
                this.setTitle("Attendance");
                fragmentTransaction.addToBackStack(null);
            } else if (id == R.id.nav_logout) {
                new delete_token().execute();
            }
            current_menu = id;

            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        //tells the system the user is not logged in
        SharedPreferences sp = getSharedPreferences("IDValue", 0);
        SharedPreferences.Editor spedit = sp.edit();
        spedit.putBoolean("logged_in", false);
        spedit.apply();
        //reset to zero notify_id
        sp = getSharedPreferences("NotifyID", 0);
        spedit = sp.edit();
        spedit.putInt("notify_id", 0);
        spedit.apply();

        try {
            String filename = "userdetails";
            FileOutputStream outputStream;
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    class update_token extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/update_token");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                Log.d("FCM_token", FirebaseInstanceId.getInstance().getToken()); //must delete
                cv.put("token", FirebaseInstanceId.getInstance().getToken());
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
            } catch (ConnectException e){
                Snackbar.make(findViewById(R.id.coor_layout), "Cannot connect to the server, please check your internet connection", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {     //error logs
                Log.d("update_token", e.toString());
                Snackbar.make(findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            parseJSON(strJSON);
        }
    }

    public void parseJSON(String strJSON) {
        Log.d("update_token", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            jsonArray = jsonObject.getJSONArray("result");
            jsonObject = jsonArray.getJSONObject(0);
            String msg = jsonObject.getString("msg");
            if(msg.equalsIgnoreCase("not_enrolled")){
                logout();
            }
        } catch (Exception e) {
            Log.i("update_token", String.valueOf(e));
            Snackbar.make(findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
        }
    }

    class delete_token extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/delete_token");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
                    cv.put("id", userDetails.getStudent_id());
                } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
                    cv.put("id", userDetails.getFic_id());
                }
                cv.put("identifier", userDetails.getIdentifier());
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
                return "no_connection";
            } catch (Exception e) {     //error logs
                Log.d("delete_token1", e.toString());
                Snackbar.make(findViewById(R.id.coor_layout), "An error occured, please try again.", Snackbar.LENGTH_LONG).show();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            Log.d("delete_token2", strJSON);
            parseJSON2(strJSON);
        }
    }
    public void parseJSON2(String strJSON){
        View cl = findViewById(R.id.coor_layout);
        if(strJSON.equalsIgnoreCase("no_connection")){
            //please connect to internet (snackbar)
            Snackbar.make(cl, "Internet is required before logging out. Please try again.", Snackbar.LENGTH_LONG).show();
        } else {
            try {
                Log.d("delete_token", "test1");
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("result");
                jsonObject = jsonArray.getJSONObject(0);
                String msg = jsonObject.getString("msg");
                if(msg.equalsIgnoreCase("true")){
                    logout();
                } else {
                    //please connect to internet (snackbar)
                    Snackbar.make(cl, "An error occured to the server. Please try again.", Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i("delete_token3", String.valueOf(e)); // MAY PROBLEM DITO
            }
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
