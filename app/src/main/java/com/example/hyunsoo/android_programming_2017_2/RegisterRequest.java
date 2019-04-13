package com.example.hyunsoo.android_programming_2017_2;

import android.app.Activity;

import java.net.MalformedURLException;
import java.net.URL;

//회원가입
public class RegisterRequest extends PostRequest {
    //final static String Url = "http://192.168.200.125:3000";

    public RegisterRequest(Activity activity) {
        super(activity);
    }

    @Override
    protected void onPreExecute() {
        try {
            String ip = activity.getResources().getString(R.string.ip_address);
            url = new URL(ip + "/member/signup");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
