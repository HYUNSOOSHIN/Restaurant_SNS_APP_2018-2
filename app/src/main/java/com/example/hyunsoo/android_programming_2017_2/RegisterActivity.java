package com.example.hyunsoo.android_programming_2017_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerbtn = findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit_ID = (EditText) findViewById(R.id.userID);
                EditText edit_Password = (EditText) findViewById(R.id.userPassword);
                EditText edit_Name = (EditText) findViewById(R.id.userName);
                EditText edit_Email = (EditText) findViewById(R.id.userEmail);

                JSONObject postDataParam = new JSONObject();
                try {
                    postDataParam.put("id", edit_ID.getText().toString());
                    postDataParam.put("password", edit_Password.getText().toString());
                    postDataParam.put("username", edit_Name.getText().toString());
                    postDataParam.put("email", edit_Email.getText().toString());

                    String result = new RegisterRequest(RegisterActivity.this).execute(postDataParam).get();
                    JSONObject jsonObject = new JSONObject(result);
                    String success = jsonObject.getString("success");

                    if(success.equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_LONG).show();
                    } else {
                        String data = jsonObject.getString("data");

                        if(data.equals("ID")){
                            Toast.makeText(getApplicationContext(),"아이디가 중복됩니다",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"닉네임이 중복됩니다",Toast.LENGTH_LONG).show();
                        }

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
    }
}
