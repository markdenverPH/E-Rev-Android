package com.example.babar.e_rev;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static EditText username, password;
    JSONArray jsonArray;
    JSONObject jsonObject;
    UserDetails userDetails;
    String base, user_hold, pass_hold;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userDetails = new UserDetails();

        sp = getSharedPreferences("IDValue", 0);
        if (sp.getBoolean("logged_in", false)) {
            getUserDetails();
        } else {
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.pass);

            base = userDetails.getBase();

            //FOR TEMPORARY USE | FASTER TESTING
//        user_hold = "riza";
//        pass_hold = "riza";
//        new fetch_login().execute();}
        }
    }

    public void login(View v) {
        user_hold = username.getText().toString();
        pass_hold = password.getText().toString();
        if (user_hold.equals("") || pass_hold.equals("")) {
            Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_LONG).show();
        } else {
            new fetch_login().execute();
        }
    }

    class fetch_login extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(base + "Mobile/login");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("username", user_hold);
                cv.put("password", pass_hold);
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
                Log.d("check", e.toString());
                dialog.dismiss();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String strJSON) {
            if (strJSON.isEmpty()) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Invalid Account", Toast.LENGTH_SHORT).show();
            } else if (!strJSON.isEmpty()) {
                parseJSON(strJSON);
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                Toast.makeText(getApplicationContext(), userDetails.toString(), Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();
            }
        }
    }

    public void parseJSON(String strJSON) {
        Log.d("userdetails", strJSON);
        try {
            jsonObject = new JSONObject(strJSON);
            jsonArray = jsonObject.getJSONArray("result");
            jsonObject = jsonArray.getJSONObject(0);

            userDetails.setIdentifier(jsonObject.getString("identifier"));
            userDetails.setEmail(jsonObject.getString("email"));
            userDetails.setFull_name(jsonObject.getString("full_name"));
            userDetails.setImage_path(jsonObject.getString("image_path"));
            userDetails.setFirstname(jsonObject.getString("firstname"));
            userDetails.setMidname(jsonObject.getString("midname"));
            userDetails.setLastname(jsonObject.getString("lastname"));
            if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
                userDetails.setStudent_id(jsonObject.getInt("student_id"));
                userDetails.setDepartment(jsonObject.getString("student_department"));
                userDetails.setOffering_id(jsonObject.getInt("offering_id"));
                userDetails.setStudent_num(jsonObject.getInt("student_num"));
                userDetails.setEnrollment(jsonObject.getString("enrollment_id"));
            } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
                userDetails.setFic_id(jsonObject.getInt("fic_id"));
                userDetails.setFic_status(jsonObject.getInt("fic_status"));
                userDetails.setDepartment(jsonObject.getString("fic_department"));
            }
            saveUserDetails();
        } catch (Exception e) {
            Log.i("loginerror", String.valueOf(e));
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

    public void saveUserDetails() {
        String fileContents;
        /* "%^&" is the hatian hahaha*/
        fileContents = userDetails.getIdentifier() +
                "%^&" + userDetails.getEmail() +
                "%^&" + userDetails.getFull_name() +
                "%^&" + userDetails.getImage_path() +
                "%^&" + userDetails.getFirstname() +
                "%^&" + userDetails.getMidname() +
                "%^&" + userDetails.getLastname() +
                "%^&" + userDetails.getDepartment() +
                "%^&" + userDetails.getEnrollment();
        if (userDetails.getIdentifier().equalsIgnoreCase("Student")) {
            fileContents = fileContents + "%^&" + userDetails.getStudent_id() +
                    "%^&" + userDetails.getOffering_id() +
                    "%^&" + userDetails.getStudent_num();
        } else if (userDetails.getIdentifier().equalsIgnoreCase("Faculty in Charge")) {
            fileContents = fileContents + "%^&" + userDetails.getFic_id() +
                    "%^&" + userDetails.getFic_status();
        }

        String filename = "userdetails";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();

            SharedPreferences sp = getSharedPreferences("IDValue", 0);
            SharedPreferences.Editor spedit = sp.edit();
            spedit.putBoolean("logged_in", true);
            spedit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserDetails() {
        String filename = "userdetails";
        try {
            FileInputStream fin = openFileInput(filename);
            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            if(temp.isEmpty()){
                SharedPreferences sp = getSharedPreferences("IDValue", 0);
                SharedPreferences.Editor spedit = sp.edit();
                spedit.putBoolean("logged_in", false);
                spedit.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                String[] tem = temp.split(Pattern.quote("%^&"));
                userDetails.setIdentifier(tem[0]);
                userDetails.setEmail(tem[1]);
                userDetails.setFull_name(tem[2]);
                userDetails.setImage_path(tem[3]);
                userDetails.setFirstname(tem[4]);
                userDetails.setMidname(tem[5]);
                userDetails.setLastname(tem[6]);
                userDetails.setDepartment(tem[7]);
                userDetails.setEnrollment(tem[8]);
                if (tem[0].equalsIgnoreCase("student")) {
                    userDetails.setStudent_id(Integer.valueOf(tem[9]));
                    userDetails.setOffering_id(Integer.valueOf(tem[10]));
                    userDetails.setStudent_num(Integer.valueOf(tem[11]));
                } else if (tem[0].equalsIgnoreCase("faculty in charge")) {
                    userDetails.setFic_id(Integer.valueOf(tem[9]));
                    userDetails.setFic_status(Integer.valueOf(tem[10]));
                }
                fin.close();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
