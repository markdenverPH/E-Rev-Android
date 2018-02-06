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
import android.widget.EditText;
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
    UserDetails userdetails;
    String hey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);
        userdetails = new UserDetails();
        hey = intToIp(Integer.valueOf(getWifiApIpAddress(this)));
    }

    public void login(View v) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if (user.equals("") || pass.equals("")) {         //empty
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
                URL url = new URL("http://" + hey + "/Engineering/mobile/login");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("username", username.getText().toString());
                cv.put("password", password.getText().toString());
                bw.write(createPostString(cv));
                bw.flush();
                bw.close();
                os.close();
//                int rc = con.getResponseCode();
//                con.disconnect();
//                return rc;

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
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show(); //ERROR LOGS
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strJSON) {
            if (strJSON.isEmpty()) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Invalid Account", Toast.LENGTH_SHORT).show();
            } else {
                parseJSON(strJSON);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public void parseJSON(String strJSON) {
        try {
            user_data = new ArrayList<>();
            jsonObject = new JSONObject(strJSON);
            jsonArray = jsonObject.getJSONArray("items");
            jsonObject = jsonArray.getJSONObject(0);

            userdetails.setStudent_id(jsonObject.getInt("student_id"));
            userdetails.setEmail(jsonObject.getString("email"));
            userdetails.setStudent_department(jsonObject.getString("student_department"));
            userdetails.setFull_name(jsonObject.getString("full_name"));
            userdetails.setOffering_id(jsonObject.getInt("offering_id"));
            userdetails.setImage_path(jsonObject.getString("image_path"));

        } catch (Exception e) {
            Log.i("Check", String.valueOf(e));
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
