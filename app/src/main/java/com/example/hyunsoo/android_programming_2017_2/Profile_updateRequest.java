package com.example.hyunsoo.android_programming_2017_2;

import android.app.Activity;

import java.net.MalformedURLException;
import java.net.URL;

public class Profile_updateRequest extends Post_tokenRequest {
    public Profile_updateRequest(Activity activity) {
        super(activity);
    }

    @Override
    protected void onPreExecute() {
        try {
            String ip = activity.getResources().getString(R.string.ip_address);
            url = new URL(  ip+ "/member/update");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

