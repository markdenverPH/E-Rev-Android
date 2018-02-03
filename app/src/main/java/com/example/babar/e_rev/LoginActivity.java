package com.example.babar.e_rev;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    public TextView result;
    ArrayList user_data;
    JSONArray jsonArray;
    JSONObject jsonObject;
    UserDetails userdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);
        result = (TextView) findViewById(R.id.tv_test);
        userdetails = new UserDetails();
    }

    public void login(View v) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if (user.equals("") || pass.equals("")) {         //empty
            Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_LONG).show();
        } else {
            new fetch_login().execute();
        }

//        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(myIntent);
    }

    ProgressDialog dialog;

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
                URL url = new URL("http://192.168.1.4/Engineering/mobile/login");
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
                int rc = con.getResponseCode();
//                con.disconnect();
//                return rc;

                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder sb = new StringBuilder();
                String str = "";
                while ((str = bufferedReader.readLine()) != null) {
                    sb.append(str);
                }
                return sb.toString();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show(); //ERROR LOGS
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strJSON) {
            dialog.dismiss();
            result.setText(strJSON);
            Toast.makeText(getApplicationContext(), strJSON, Toast.LENGTH_LONG).show();
        }
    }

    public void parseJSON(String strJSON) {
        try {
            user_data = new ArrayList<>();
            jsonObject = new JSONObject(strJSON);

            jsonArray = jsonObject.getJSONArray("items");
            jsonObject = jsonArray.getJSONObject(0);
            userdetails.set = jsonObject.getString("name"); //LAST

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
        System.out.println(sb.toString());
        return sb.toString();
    }
}
