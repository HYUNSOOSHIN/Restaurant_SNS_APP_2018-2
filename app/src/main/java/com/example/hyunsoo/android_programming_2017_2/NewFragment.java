package com.example.hyunsoo.android_programming_2017_2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewFragment extends Fragment {


    public NewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new,container,false);

        SharedPreferences test = getActivity().getSharedPreferences("token", getActivity().MODE_PRIVATE);
        String username = test.getString("username","");

        TextView text = view.findViewById(R.id.text);
        text.setText(username+"님의 알림페이지 입니다.");


        // Inflate the layout for this fragment
        return view;
    }

}
