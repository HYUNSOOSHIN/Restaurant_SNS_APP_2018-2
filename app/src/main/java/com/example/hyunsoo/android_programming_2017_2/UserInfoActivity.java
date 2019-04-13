package com.example.hyunsoo.android_programming_2017_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class UserInfoActivity extends FragmentActivity {


    String username;
    String email;
    String user_image;
    Bitmap bitmap;

    String followlist;
    int follower_num;
    int following_num;

    SharedPreferences test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        TextView text_name = findViewById(R.id.user_userName);
        final TextView text_follower = findViewById(R.id.user_follower);
        final TextView text_following = findViewById(R.id.user_following);
        TextView text_posts = findViewById(R.id.user_posts);

        try {

            JSONObject postDataParam = new JSONObject();
            postDataParam.put("username", name);

            String result = new UserInfoRecieve(UserInfoActivity.this).execute(postDataParam).get();

            //결과값 받기.
            JSONObject jsonObject = new JSONObject(result);
            String data = jsonObject.getString("data");
            JSONObject jsonObject2 = new JSONObject(data);
            Log.e("ddddd",jsonObject2.toString());

            username = jsonObject2.getString("username");
            email = jsonObject2.getString("email");
            JSONArray follower =  jsonObject2.getJSONArray("follower");
            follower_num = follower.length();
            followlist = follower.toString();
            JSONArray following =  jsonObject2.getJSONArray("following");
            following_num = following.length();
            JSONArray posts =  jsonObject2.getJSONArray("posts");
            int posts_num = posts.length();
            user_image = jsonObject2.getString("image");

            //뷰에 설정
            text_name.setText(username);
            text_follower.setText(""+follower_num);
            text_following.setText(""+following_num);
            text_posts.setText(""+posts_num);

            if(!user_image.equals("null")) {
                Log.e("ddddd","image_url: "+user_image);
                Thread mThread = new Thread() {
                    public void run() {
                        try {
                            URL url = new URL(user_image);
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
                    ImageView icon = (ImageView) findViewById(R.id.user_userImage);
                    icon.setImageBitmap(bitmap);
                }catch(
                        InterruptedException e)

                {
                    e.printStackTrace();
                }
            } else{
                ImageView icon = (ImageView) findViewById(R.id.user_userImage);
                icon.setImageResource(R.drawable.temp_image);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button followbtn = findViewById(R.id.followbtn);
        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //팔로우추가기능
                test = getSharedPreferences("token",MODE_PRIVATE);

                JSONObject postDataParam = new JSONObject();
                try {


                    postDataParam.put("token",test.getString("token",""));
                    postDataParam.put("followuser",username);
                    Log.e("ddddd", "팔로리트스: "+followlist);
                    if(!followlist.contains(test.getString("username",""))){
                        postDataParam.put("state","On");
                        String result = new AddFollowingRequest(UserInfoActivity.this).execute(postDataParam).get();
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray followerlist = jsonObject.getJSONArray("data");

                        followlist = followerlist.toString();
                        Toast.makeText(UserInfoActivity.this,"팔로우를 추가하였습니다.",Toast.LENGTH_LONG).show();
                        text_follower.setText(""+followerlist.length());
                    }  else{
                        postDataParam.put("state","OFF");
                        String result = new AddFollowingRequest(UserInfoActivity.this).execute(postDataParam).get();
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray followerlist = jsonObject.getJSONArray("data");

                        followlist = followerlist.toString();
                        Toast.makeText(UserInfoActivity.this,"팔로우를 취소하였습니다.",Toast.LENGTH_LONG).show();
                        text_follower.setText(""+followerlist.length());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        final FragmentTabHost host;

        host = (FragmentTabHost) findViewById(android.R.id.tabhost);
        host.setup(UserInfoActivity.this, getSupportFragmentManager(), R.id.content);

        TabHost.TabSpec tabSpec1 = host.newTabSpec("tab1"); // 구분자
        tabSpec1.setIndicator("그의 게시물"); // 탭 이름
        tabSpec1.setContent(new Intent(this,MainActivity.class).putExtra("name",name));
        host.addTab(tabSpec1, User_1Fragment.class, null);

        TabHost.TabSpec tabSpec2 = host.newTabSpec("tab2");
        tabSpec2.setIndicator("그의 맛집");
        host.addTab(tabSpec2, User_2Fragment.class, null);

        host.getTabWidget().getChildAt(0);
        host.getTabWidget().getChildAt(1);

        host.setCurrentTab(0);
        TextView temp = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        temp.setTextColor(Color.parseColor("#000000"));


    }
}
