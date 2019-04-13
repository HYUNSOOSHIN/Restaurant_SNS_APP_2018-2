package com.example.hyunsoo.android_programming_2017_2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RestaurantActivity extends Activity {

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("telephone");
        String address = intent.getStringExtra("address");
        String business_hours = intent.getStringExtra("business_hours");
        final String image = intent.getStringExtra("image");

        TextView mname = findViewById(R.id.rest_name);
        TextView maddress = findViewById(R.id.rest_address);
        TextView mphone= findViewById(R.id.rest_phone);
        TextView mtime = findViewById(R.id.rest_time);
        TextView mre = findViewById(R.id.rest_re);
        TextView mfollower = findViewById(R.id.rest_follower);

        mname.setText(name);
        mphone.setText(phone);
        maddress.setText(address);
        mtime.setText(business_hours);
        mre.setText("0");
        mfollower.setText("0");


        if(!image.equals("null")) {
            Log.e("ddddd","image_url: "+image);
            Thread mThread = new Thread() {
                public void run() {
                    try {
                        URL url = new URL(image);
                        //web에서 이미지 가져오고 imageview에 저장할 bitmap 생성
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setDoInput(true);// 서버로부터 응답 수신
                        con.connect();

                        InputStream is = con.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();//쓰레드실행
            try

            {
                mThread.join();
                ImageView icon = (ImageView) findViewById(R.id.rest_image);
                icon.setImageBitmap(bitmap);
            }catch(
                    InterruptedException e)

            {
                e.printStackTrace();
            }
        } else{
            ImageView icon = (ImageView) findViewById(R.id.rest_image);
            icon.setImageResource(R.drawable.temp_image);
        }


        ImageButton callbtn = (ImageButton) findViewById(R.id.callbtn);

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t= (TextView) findViewById(R.id.rest_phone);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+t.getText()));
                startActivity(intent);
            }
        });




    }
}
