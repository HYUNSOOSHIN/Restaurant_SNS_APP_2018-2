package com.example.hyunsoo.android_programming_2017_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        // Inflate the layout for this fragment
        ImageButton imageButton = view.findViewById(R.id.imagebtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UploadActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //유저정보 받아오는 코드
        SharedPreferences test = getActivity().getSharedPreferences("token", getActivity().MODE_PRIVATE);
        String token[] = new String[1];
        token[0] = test.getString("token","");

        //게시물 목록 받아오기
        try {
            String result = new FollowPostsRequest(getActivity()).execute(token).get();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            Log.e("ddddd","홈 프래그먼트"+dataArray.toString());
            // 데이터원본준비 -> 목록 받아서 add
            ArrayList<MyItem> data = new ArrayList<MyItem>();

            for(int i=0; i<dataArray.length(); i++) {
                JSONObject jsonObject2 = dataArray.getJSONObject(i);

                MyItem myItem = new MyItem(jsonObject2.getString("image"),jsonObject2.getString("username"),jsonObject2.getString("text"));
                data.add(myItem);
            }

            // 어댑터생성
            MyAdapter adapter = new MyAdapter(getContext(), R.layout.home_listview, data);

            // 어댑터연결
            ListView list = (ListView) view.findViewById(R.id.home_listview);
            list.setAdapter(adapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
