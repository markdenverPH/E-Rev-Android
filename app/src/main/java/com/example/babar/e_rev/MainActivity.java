package com.example.babar.e_rev;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;

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
        userDetails = new UserDetails();
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
                SharedPreferences sp = getSharedPreferences("IDValue", 0);
                SharedPreferences.Editor spedit = sp.edit();
                spedit.putBoolean("logged_in", false);
                spedit.apply();

                try {
                    String filename = "userdetails";
                    FileOutputStream outputStream;
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write("".getBytes());
                    outputStream.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
            current_menu = id;

            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
