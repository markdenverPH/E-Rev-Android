package com.example.babar.e_rev;

import java.util.ArrayList;

/**
 * Created by babar on 2/3/2018.
 */

public class UserDetails {
    public static int student_id, offering_id, ad_item, fic_id, fic_status, fbv_interval = 1, fbv_sort;
    public static String full_name, username, password, email, department, image_path, identifier, feedback_content, enrollment, firstname, midname, lastname;
    public static ArrayList<String> announcement_title = new ArrayList<>();
    public static ArrayList<String> announcement_content = new ArrayList<>();
    public static ArrayList<String> announcement_created_at = new ArrayList<>();
    public static ArrayList<String> announcement_end_datetime = new ArrayList<>();
    public static ArrayList<String> announcement_start_datetime = new ArrayList<>();
    public static ArrayList<String> announcement_announcer = new ArrayList<>();
    public static ArrayList<String> feedback_full_name = new ArrayList<>();
    public static ArrayList<String> feedback_offering_name = new ArrayList<>();
    public static ArrayList<String> feedback_subject_name = new ArrayList<>();
    public static ArrayList<Integer> feedback_done = new ArrayList<>();
    public static ArrayList<String> feedback_image_path = new ArrayList<>();
    public static ArrayList<Integer> feedback_lect_id = new ArrayList<>();
    public static ArrayList<String> fbv_content = new ArrayList<>();
    public static ArrayList<String> fbv_date = new ArrayList<>();
    private String frag_hold;
    static String base = "http://192.168.1.7/Engineering/";

    @Override
    public String toString() {
        return "UserDetails{" +
                "student_id=" + student_id +
                ", offering_id=" + offering_id +
                ", full_name='" + full_name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", image_path='" + image_path + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    public static int getFbv_interval() {
        return fbv_interval;
    }

    public static void setFbv_interval(int fbv_interval) {
        UserDetails.fbv_interval = fbv_interval;
    }

    public static int getFbv_sort() {
        return fbv_sort;
    }

    public static void setFbv_sort(int fbv_sort) {
        UserDetails.fbv_sort = fbv_sort;
    }

    public static String getFirstname() {
        return firstname;
    }

    public static void setFirstname(String firstname) {
        UserDetails.firstname = firstname;
    }

    public static String getMidname() {
        return midname;
    }

    public static void setMidname(String midname) {
        UserDetails.midname = midname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        UserDetails.lastname = lastname;
    }

    public static String getEnrollment() {
        return enrollment;
    }

    public static void setEnrollment(String enrollment) {
        UserDetails.enrollment = enrollment;
    }

    public String getFrag_hold() {
        return frag_hold;
    }

    public void setFrag_hold(String frag_hold) {
        this.frag_hold = frag_hold;
    }

    public static String getFeedback_content() {
        return feedback_content;
    }

    public static void setFeedback_content(String feedback_content) {
        UserDetails.feedback_content = feedback_content;
    }

    public static int getFic_status() {
        return fic_status;
    }

    public static void setFic_status(int fic_status) {
        UserDetails.fic_status = fic_status;
    }

    public static int getFic_id() {
        return fic_id;
    }

    public static void setFic_id(int fic_id) {
        UserDetails.fic_id = fic_id;
    }

    public static int getAd_item() {
        return ad_item;
    }

    public static void setAd_item(int ad_item) {
        UserDetails.ad_item = ad_item;
    }

    public static String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public static String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getOffering_id() {
        return offering_id;
    }

    public void setOffering_id(int offering_id) {
        this.offering_id = offering_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
