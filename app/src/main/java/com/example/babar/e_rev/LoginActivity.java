package com.example.babar.e_rev;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    public static EditText username, password;
    ArrayList user_data;
    JSONArray jsonArray;
    JSONObject jsonObject;
    UserDetails userDetails;
    String base, user_hold, pass_hold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);
        userDetails = new UserDetails();

//        WifiManager wifi = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (wifi.isWifiEnabled()) {
//            Toast.makeText(getApplicationContext(), "Wifi is ENABLED", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "Wifi is DISABLED", Toast.LENGTH_SHORT).show();
//        }
//        base = intToIp(Integer.valueOf(getWifiApIpAddress(this)));

        base = userDetails.getBase();

        //FOR TEMPORARY USE | FASTER TESTING
//        user_hold = "riza";
//        pass_hold = "riza";
//        new fetch_login().execute();
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
                URL url = new URL(base + "mobile/login");
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

//            Toast.makeText(getApplicationContext(), jsonObject.getString("identifier"), Toast.LENGTH_LONG).show();
            userDetails.setIdentifier(jsonObject.getString("identifier"));
            if (userDetails.getIdentifier().equalsIgnoreCase("student")) {
                userDetails.setStudent_id(jsonObject.getInt("student_id"));
                userDetails.setEmail(jsonObject.getString("email"));
                userDetails.setDepartment(jsonObject.getString("student_department"));
                userDetails.setFull_name(jsonObject.getString("full_name"));
                userDetails.setOffering_id(jsonObject.getInt("offering_id"));
                userDetails.setImage_path(jsonObject.getString("image_path"));
                userDetails.setEnrollment(jsonObject.getString("enrollment_id"));
                userDetails.setFirstname(jsonObject.getString("firstname"));
                userDetails.setMidname(jsonObject.getString("midname"));
                userDetails.setLastname(jsonObject.getString("lastname"));
            } else if (userDetails.getIdentifier().equalsIgnoreCase("faculty in charge")) {
                userDetails.setFic_id(jsonObject.getInt("fic_id"));
                userDetails.setFull_name(jsonObject.getString("full_name"));
                userDetails.setEmail(jsonObject.getString("email"));
                userDetails.setImage_path(jsonObject.getString("image_path"));
                userDetails.setDepartment(jsonObject.getString("fic_department"));
                userDetails.setFic_status(jsonObject.getInt("fic_status"));
                userDetails.setEnrollment(jsonObject.getString("enrollment_id"));
                userDetails.setFirstname(jsonObject.getString("firstname"));
                userDetails.setMidname(jsonObject.getString("midname"));
                userDetails.setLastname(jsonObject.getString("lastname"));
            }
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

    public String getWifiApIpAddress(Context context) {
        WifiManager wifii = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String d = "";
        d = String.valueOf(wifii.getDhcpInfo().gateway);
        return d;
    }

    public String intToIp(int addr) {
        return ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }
}
