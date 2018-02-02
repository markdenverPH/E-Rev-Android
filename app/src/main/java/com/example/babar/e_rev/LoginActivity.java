package com.example.babar.e_rev;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    public static EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);

    }

    public void login(View v){
        if(username.getText().equals("") || password.getText().equals("")){         //empty
            Toast.makeText(getApplicationContext(), "Empty field" , Toast.LENGTH_LONG).show();
        } else {
            new fetch_login().execute();
        }

//        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(myIntent);
    }

    class fetch_login extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Loading..");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            Toast.makeText(getApplicationContext(), "DUMAAN" , Toast.LENGTH_SHORT).show();
            try{
                URL url = new URL("http://localhost/Engineering/mobile/login");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("username", username.getFreezesText());
                cv.put("pass", password.getFreezesText());
                bw.write(createPostString(cv));
                bw.flush();
                bw.close();
                os.close();
                String rc = String.valueOf(con.getResponseCode());
                con.disconnect();
                return rc;
            }
            catch (Exception e){
                System.err.println(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strJSON) {
            dialog.dismiss();
            TextView test = (TextView) findViewById(R.id.tv_test);
            JSONArray jsonArray;
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("user_details");

                jsonObject = jsonArray.getJSONObject(0);
                String firstname = jsonObject.getString("firstname");
                test.setText(firstname);
            } catch (Exception e){
                Log.i("Check",String.valueOf(e));
            }
        }
    }
    public String createPostString(ContentValues cv) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean flag = true;
        Set set = cv.valueSet();

        for(Map.Entry<String, Object> v: cv.valueSet()){
            if(flag){
                flag = false;
            } else {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(v.getKey(),"UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(v.getValue().toString(),"UTF-8"));
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}
