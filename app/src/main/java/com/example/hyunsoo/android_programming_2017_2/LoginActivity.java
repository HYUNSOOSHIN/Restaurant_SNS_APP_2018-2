package com.example.hyunsoo.android_programming_2017_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends FragmentActivity {
    SharedPreferences test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText edit_ID = (EditText) findViewById(R.id.userID);
        final EditText edit_Password = (EditText) findViewById(R.id.userPassword);
        Button loginbtn = (Button) findViewById(R.id.loginbtn);
        Button registerbtn = (Button) findViewById(R.id.registerbtn);

        test = getSharedPreferences("token",MODE_PRIVATE);
        if(test.getBoolean("Auto_Login_enabled", false)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        loginbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject postDataParam = new JSONObject();
                try {
                    postDataParam.put("id", edit_ID.getText().toString());
                    postDataParam.put("password", edit_Password.getText().toString());
                    //로그인 요청
                    String result = new LoginRequest(LoginActivity.this).execute(postDataParam).get();

                    //결과값 받기.
                    JSONObject jsonObject = new JSONObject(result);
                    String success = jsonObject.getString("success");

                    if (success.equals("true")) {
                        String token = jsonObject.getString("token");
                        String data = jsonObject.getString("data");
                        JSONObject jsonObject2 = new JSONObject(data);
                        String id = jsonObject2.getString("id");
                        String username = jsonObject2.getString("username");
                        String email = jsonObject2.getString("email");
                        SharedPreferences test = getSharedPreferences("token", MODE_PRIVATE);
                        SharedPreferences.Editor editor = test.edit();
                        editor.putString("token", token);
                        editor.putString("id", id);
                        editor.putString("username", username);
                        editor.putString("email", email);
                        editor.putBoolean("Auto_Login_enabled", true);
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 다시 확인하세요", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("TEST", "JSONEXception");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        registerbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}