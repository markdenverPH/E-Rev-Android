package com.example.babar.e_rev;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public HomeFragment homeFragment = new HomeFragment();
    public GradeAssessmentFragment gradeAssessmentFragment = new GradeAssessmentFragment();
    public CoursewareFragment coursewareFragment = new CoursewareFragment();
    public ScheduleFragment scheduleFragment = new ScheduleFragment();
    public FeedbackFragment feedbackFragment = new FeedbackFragment();
    NavigationView navigationView;
    TextView nav_full_name, nav_user_role;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    final String key_user = "username";
    final String key_pass = "password";
    int current_menu = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Home");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        nav_full_name = (TextView) v.findViewById(R.id.nav_full_name);
        nav_user_role = (TextView) v.findViewById(R.id.nav_user_role);

        UserDetails userDetails = new UserDetails();
        Intent intent = getIntent();
        userDetails.setStudent_id(intent.getIntExtra("student_id", -1));
        userDetails.setEmail(intent.getStringExtra("email"));
        userDetails.setDepartment(intent.getStringExtra("department"));
        userDetails.setFull_name(intent.getStringExtra("full_name"));
        userDetails.setOffering_id(intent.getIntExtra("offering_id", -1));
        userDetails.setImage_path(intent.getStringExtra("image_path"));
        userDetails.setIdentifier(intent.getStringExtra("identifier"));

        nav_full_name.setText(userDetails.getFull_name());
        String temp = userDetails.getIdentifier().substring(0, 1).toUpperCase() + userDetails.getIdentifier().substring(1);
        nav_user_role.setText(temp);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //default nav
        if (savedInstanceState == null) {

            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                    .replace(R.id.fragment_hold, homeFragment);
            fragmentTransaction.commit();
        }
        navigationView.setCheckedItem(R.id.nav_home);
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
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (current_menu != id) {
            fragmentTransaction = fragmentManager.beginTransaction();
            getFragmentManager().popBackStack();
            if (id == R.id.nav_home) {
//            fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
//                    .replace(R.id.fragment_hold, homeFragment);
                this.setTitle("Home");
            } else if (id == R.id.nav_grade) {
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, gradeAssessmentFragment);
                this.setTitle("Grade Assessment");
            } else if (id == R.id.nav_courseware) {
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, coursewareFragment);
                this.setTitle("Courseware");
            } else if (id == R.id.nav_schedule) {
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, scheduleFragment);
                this.setTitle("Schedule");
            } else if (id == R.id.nav_feedback) {
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_from_left)
                        .replace(R.id.fragment_hold, feedbackFragment);
                this.setTitle("Feedback");
            } else if (id == R.id.nav_logout) {
                sharedpreferences = getPreferences(MODE_PRIVATE);
                editor = sharedpreferences.edit();
                sharedpreferences.edit().remove(key_pass).apply();
                sharedpreferences.edit().remove(key_user).apply();
                finish();
            }
//            Toast.makeText(getApplicationContext(), "id: " + id + " current: " + current_menu, Toast.LENGTH_LONG).show();
            current_menu = id;
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
