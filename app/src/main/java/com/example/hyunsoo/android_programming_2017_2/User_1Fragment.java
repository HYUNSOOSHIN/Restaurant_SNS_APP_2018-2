package com.example.hyunsoo.android_programming_2017_2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class User_1Fragment extends Fragment {


    public User_1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_1,container,false);

        Intent intent = getActivity().getIntent();
        String name = intent.getStringExtra("name");
        Log.e("ddddd","유저이름 "+name);

        //게시물 목록 받아오기
        try {
            JSONObject postDataParam = new JSONObject();
            postDataParam.put("username", name);

            String result = new MypostRequest(getActivity()).execute(postDataParam).get();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray dataArray =  jsonObject.getJSONArray("data");

            // 데이터원본준비 -> 목록 받아서 add
            ArrayList<String> data = new ArrayList<String>();

            for(int i=0; i<dataArray.length(); i++) {
                JSONObject jsonObject2 = dataArray.getJSONObject(i);

                data.add(jsonObject2.getString("image"));
            }

            GridView gridview = (GridView) view.findViewById(R.id.user1_gridview);
            gridview.setAdapter(new GridViewAdapter(getContext(),data));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(getContext(), "" + (position)+"번째 선택" , Toast.LENGTH_SHORT).show();
                    //인텐트로 게시물 페이지로 이동하는거 만들어야함.
                }
            });

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
