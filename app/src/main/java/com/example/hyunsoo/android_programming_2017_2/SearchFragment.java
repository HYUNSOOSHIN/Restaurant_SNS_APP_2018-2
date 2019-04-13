package com.example.hyunsoo.android_programming_2017_2;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search,container,false);
        // Inflate the layout for this fragment

        final EditText searchedit = view.findViewById(R.id.search_edittext);

        Button searchbtn = view.findViewById(R.id.search_button);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject postDataParam = new JSONObject();
                    postDataParam.put("username", searchedit.getText().toString());
                    String result =  new FinduserRequest(getActivity()).execute(postDataParam).get();
                    JSONArray jsonArray = new JSONArray(result);
                    Log.e("ddddd",jsonArray.toString());

                    // 데이터원본준비 -> 목록 받아서 add
                    ArrayList<Item> data = new ArrayList<Item>();

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                        Item myItem = new Item(jsonObject2.getString("image"),jsonObject2.getString("username"));
                        data.add(myItem);
                    }

                    // 어댑터생성
                    ListAdapter adapter = new ListAdapter(getContext(), R.layout.search_listview, data);

                    // 어댑터연결
                    ListView list = (ListView) view.findViewById(R.id.search_listview);
                    list.setAdapter(adapter);


                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String name = (String)((TextView) view.findViewById(R.id.list_username)).getText();

                            Log.e("ddddd",name);

                            Intent intent = new Intent(getContext(),UserInfoActivity.class);
                            intent.putExtra("name",name);
                            startActivity(intent);

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
