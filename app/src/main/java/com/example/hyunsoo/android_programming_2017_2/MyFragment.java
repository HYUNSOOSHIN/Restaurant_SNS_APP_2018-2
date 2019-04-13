package com.example.hyunsoo.android_programming_2017_2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    String username;
    String email;
    String user_image;
    Bitmap bitmap;

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_my,container,false);

        TextView text_name = view.findViewById(R.id.my_userName);
        TextView text_follower = view.findViewById(R.id.follower);
        TextView text_following = view.findViewById(R.id.following);
        TextView text_posts = view.findViewById(R.id.posts);

        //유저정보 받아오는 코드
        SharedPreferences test = getActivity().getSharedPreferences("token", getActivity().MODE_PRIVATE);
        String token[] = new String[1];
        token[0] = test.getString("token","");
        //Toast.makeText(getContext(), token[0],Toast.LENGTH_LONG).show();
        try {
            String result = new MyInfoReceive(getActivity()).execute(token).get();
            //결과값 받기.
            JSONObject jsonObject = new JSONObject(result);
            String data = jsonObject.getString("data");
            JSONObject jsonObject2 = new JSONObject(data);

            username = jsonObject2.getString("username");
            email = jsonObject2.getString("email");
            JSONArray follower =  jsonObject2.getJSONArray("follower");
            int follower_num = follower.length();
            JSONArray following =  jsonObject2.getJSONArray("following");
            int following_num = following.length();
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
                    ImageView icon = (ImageView) view.findViewById(R.id.my_userImage);
                    icon.setImageBitmap(bitmap);
                }catch(
                        InterruptedException e)

                {
                    e.printStackTrace();
                }
            } else{
                ImageView icon = (ImageView) view.findViewById(R.id.my_userImage);
                icon.setImageResource(R.drawable.temp_image);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button profilebtn = view.findViewById(R.id.profile_btn);
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Profile_editActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("email",email);
                intent.putExtra("imageurl", user_image);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        final FragmentTabHost host;

        host = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        host.setup(getContext(),getChildFragmentManager(), R.id.content);

        TabHost.TabSpec tabSpec1 = host.newTabSpec("tab1"); // 구분자
        tabSpec1.setIndicator("내 게시물"); // 탭 이름
        host.addTab(tabSpec1, My_1Fragment.class, null);

        TabHost.TabSpec tabSpec2 = host.newTabSpec("tab2");
        tabSpec2.setIndicator("내 맛집");
        host.addTab(tabSpec2, My_2Fragment.class, null);

        host.getTabWidget().getChildAt(0);
        host.getTabWidget().getChildAt(1);

        host.setCurrentTab(0);
        TextView temp = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        temp.setTextColor(Color.parseColor("#000000"));

        return view;
    }
}
