package com.example.babar.e_rev;

import java.util.ArrayList;

/**
 * Created by babar on 2/3/2018.
 */

public class UserDetails {
    int student_id, offering_id;
    String full_name, username, password, email, department, image_path, identifier;
    ArrayList<String> announcement_title;
    ArrayList<String> announcement_content;
    ArrayList<String> announcement_created_at;
    ArrayList<String> announcement_end_datetime;
    ArrayList<String> announcement_start_datetime;
    ArrayList<String> announcement_announcer;

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
