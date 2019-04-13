package com.example.hyunsoo.android_programming_2017_2;

import android.app.Activity;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kwanwoo on 2017. 10. 17..
 */

public class MyInfoReceive extends GetRequest {
    //final static String Url = "http://192.168.200.125:3000";

    public MyInfoReceive(Activity activity) {
        super(activity);
    }

    @Override
    protected void onPreExecute() {
        try {
            String ip = activity.getResources().getString(R.string.ip_address);
            url = new URL(ip+"/me");  // http://serverURLStr/get-data
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        //Toast.makeText(activity,jsonString,Toast.LENGTH_SHORT).show();
    }

//    protected ArrayList<Book> getArrayListFromJSONString(String jsonString) {
//        ArrayList<Book> output = new ArrayList();
//        try {
//
//            JSONArray jsonArray = new JSONArray(jsonString);
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//
//                Book book = new Book(jsonObject.getString("_id"),
//                        jsonObject.getString("title"),
//                        jsonObject.getString("content"),
//                        jsonObject.getString("author"));
//
//                output.add(book);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in processing JSONString.", e);
//            e.printStackTrace();
//        }
//        return output;
//    }

}

