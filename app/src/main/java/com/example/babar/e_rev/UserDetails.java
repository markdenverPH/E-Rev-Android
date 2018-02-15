package com.example.babar.e_rev;

import java.util.ArrayList;

/**
 * Created by babar on 2/3/2018.
 */

public class UserDetails {
    public static int student_id, offering_id, ad_item, fic_id, fic_status;
    public static String full_name, username, password, email, department, image_path, identifier;
    public static ArrayList<String> announcement_title = new ArrayList<>();
    public static ArrayList<String> announcement_content = new ArrayList<>();
    public static ArrayList<String> announcement_created_at = new ArrayList<>();
    public static ArrayList<String> announcement_end_datetime = new ArrayList<>();
    public static ArrayList<String> announcement_start_datetime = new ArrayList<>();
    public static ArrayList<String> announcement_announcer = new ArrayList<>();
    static String base = "172.16.86.42";

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

    public String getIdentifier() {
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
