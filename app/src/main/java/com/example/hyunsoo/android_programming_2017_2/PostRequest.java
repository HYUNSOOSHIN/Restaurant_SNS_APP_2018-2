package com.example.hyunsoo.android_programming_2017_2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

//로그인, 회원가입에 사용
public class PostRequest extends AsyncTask<JSONObject, Void, String> {
    Activity activity;
    URL url;

    public PostRequest(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(JSONObject... postDataParams) {

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            String str = getPostDataString(postDataParams[0]);
            Log.e("params", "Post String = " + str);
            writer.write(str);

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("Server Error : " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        /*try {
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString("success");
            String type = jsonObject.getString("type");
            if(type.equals("login")) {
                if(success.equals("true")) {
                    String token = jsonObject.getString("token");
                    SharedPreferences test = activity.getSharedPreferences("token", activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = test.edit();
                    editor.putString("token", token);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();

                    Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity,"아이디/비밀번호를 다시 확인하세요",Toast.LENGTH_LONG).show();
                }
            } else if(type.equals("signup")){
                if(success.equals("true")) {
                    Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    Toast.makeText(activity,"회원가입 완료",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity,"아이디가 중복됩니다",Toast.LENGTH_LONG).show();
                }
            }

        } catch (JSONException e){
            e.printStackTrace();
        }*/
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
