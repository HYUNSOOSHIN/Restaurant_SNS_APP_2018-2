package com.example.hyunsoo.android_programming_2017_2;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback{

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;

    public LocationFragment() {
        // Required empty public constructor
    }

    final private int REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Location mCurrentLocation;
    GoogleMap mgooglemap;
    private MapView mapView = null;
    View view;

    Boolean yes;
    String address;
    String phone;
    String image;
    String business_hours;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location,container,false);

        //플로팅버튼
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ddddd","플로팅");
                anim();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"1km 이내",Toast.LENGTH_LONG).show();
                anim();
                try {
                    getAddressfordistance(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"3km 이내",Toast.LENGTH_LONG).show();
                anim();
                try {
                    getAddressfordistance(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Button searchbtn = view.findViewById(R.id.location_button);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });

        //Fragment내에서는 mapView로 지도를 실행
        mapView = (MapView)view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        if (!checkLocationPermissions()) {
            requestLocationPermissions(REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION);
        } else
            getLastLocation();

        return view;
    }

    //플로팅버튼애니메이션
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            isFabOpen = true;
        }
    }

    private boolean checkLocationPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions(int requestCode) {
        ActivityCompat.requestPermissions(
                getActivity(),            // MainActivity 액티비티의 객체 인스턴스를 나타냄
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},        // 요청할 권한 목록을 설정한 String 배열
                requestCode    // 사용자 정의 int 상수. 권한 요청 결과를 받을 때
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_FOR_LAST_KNOWN_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {
                    Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT);
                }
            }
        }
    }


    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        Task task = mFusedLocationClient.getLastLocation();       // Task<Location> 객체 반환
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    mCurrentLocation = location;
                    LatLng currentlocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation,15));
                    mgooglemap.addMarker(
                            new MarkerOptions().
                                    position(currentlocation).
                                    title("현재위치"));
                    //updateUI();
                } else
                    Toast.makeText(getContext(),
                            "gg",
                            Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgooglemap = googleMap;
    }

    //검색한 위치에 마커달고 표시.
    private void getAddress() {
        mgooglemap.clear();
        getLastLocation();

        final EditText input = (EditText) view.findViewById(R.id.location_edittext);

        try {
            JSONObject postDataParam = new JSONObject();
            postDataParam.put("restaurant", input.getText().toString());
            String result = new RestaurantInfoRequest(getActivity()).execute(postDataParam).get();
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString("success");
            Log.e("ddddd",success);

            if(success=="true"){ //디비에 저장되어있는 맛집이면
                String data = jsonObject.getString("data");
                Log.e("ddddd",data);
                JSONObject dataa = new JSONObject(data);
                final String mname = dataa.getString("restaurant");
                final String mphone = dataa.getString("telephone");
                final String mimage = dataa.getString("image");
                final String maddress = dataa.getString("address");
                final String mbusiness_hours = dataa.getString("business_hours");
                Log.e("ddddd",mname+mphone+mimage+maddress+mbusiness_hours);
                try {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
                    List<Address> addresses = geocoder.getFromLocationName(maddress, 1);
                    if (addresses.size() > 0) {
                        Address bestResult = (Address) addresses.get(0);

                        LatLng where = new LatLng(bestResult.getLatitude(), bestResult.getLongitude());

                        mgooglemap.addMarker(
                                new MarkerOptions().
                                        position(where).
                                        title(mname));

                        mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(where, 15));
                        //result.setText(String.format("위도:%s 경도:%s", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

                        mgooglemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                anim();
                                Intent intent = new Intent(getContext(),RestaurantActivity.class);
                                intent.putExtra("name",mname);
                                intent.putExtra("telephone",mphone);
                                intent.putExtra("image",mimage);
                                intent.putExtra("address",maddress);
                                intent.putExtra("business_hours",mbusiness_hours);
                                startActivity(intent);

                                return false;
                            }
                        });

                    }
                } catch (IOException e) {
                    return;
                }
                Log.e("ddddd","끝");
            } else { //디비에 저장되어있지 않으면
                try {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
                    List<Address> addresses = geocoder.getFromLocationName(input.getText().toString(), 1);
                    if (addresses.size() > 0) {
                        Address bestResult = (Address) addresses.get(0);

                        LatLng where = new LatLng(bestResult.getLatitude(), bestResult.getLongitude());

                        mgooglemap.addMarker(
                                new MarkerOptions().
                                        position(where).
                                        title("미등록"));

                        mgooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(where, 15));
                        //result.setText(String.format("위도:%s 경도:%s", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

                        mgooglemap.setOnMarkerClickListener(new MyMarkerClickListener());

                    }
                } catch (IOException e) {
                    return;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //등록되어있지않은 맛집 클릭리스너
    class MyMarkerClickListener implements GoogleMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {

            yes = false;
            final String name = marker.getTitle();

            try {
                JSONObject post = new JSONObject();
                String result = new RestaurantAllRequest(getActivity()).execute(post).get();
                JSONArray dataarray = new JSONArray(result);
                Log.e("ddddd","dd: "+dataarray);

                for(int i =0; i<dataarray.length(); i++) {
                    JSONObject jsonObject = dataarray.getJSONObject(i);
                    final String mname = jsonObject.getString("restaurant");
                    final String mphone = jsonObject.getString("telephone");
                    final String mimage = jsonObject.getString("image");
                    final String maddress = jsonObject.getString("address");
                    final String mbusiness_hours = jsonObject.getString("business_hours");
                    if (name.equals(mname)) {
                        yes=true;
                        address=maddress;
                        phone=mphone;
                        image=mimage;
                        business_hours=mbusiness_hours;

                        break;
                    }
                }

                if(yes==true){
                    Intent intent = new Intent(getContext(),RestaurantActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("telephone",phone);
                    intent.putExtra("image",image);
                    intent.putExtra("address",address);
                    intent.putExtra("business_hours",business_hours);
                    startActivity(intent);
                } else{
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());

                    alertdialog.setTitle("맛집 등록");
                    alertdialog.setMessage("새로운 맛집으로 등록하시겠습니까?");

                    alertdialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), RestaurantAddActivity.class);
                            startActivity(intent);
                        }
                    });

                    alertdialog.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //"아니요" 누르면 아무것도 안함
                        }
                    });

                    AlertDialog alert = alertdialog.create();
                    alert.show();
                    //다이얼로그 생성코드 참조: http://codersdict.com/21
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    //거리에 따른 위치표시
    private void getAddressfordistance(int a) throws JSONException, ExecutionException, InterruptedException {
        Log.e("ddddd","거리로 위치표시 들어옴");
        mgooglemap.clear();
        getLastLocation();

        JSONObject post = new JSONObject();
        String result = new RestaurantAllRequest(getActivity()).execute(post).get();
        JSONArray dataarray = new JSONArray(result);
        Log.e("ddddd","dd: "+dataarray);


        for(int i =0; i<dataarray.length(); i++) {
            JSONObject jsonObject = dataarray.getJSONObject(i);
            final String mname = jsonObject.getString("restaurant");
            final String mphone = jsonObject.getString("telephone");
            final String mimage = jsonObject.getString("image");
            final String maddress = jsonObject.getString("address");
            final String mbusiness_hours = jsonObject.getString("business_hours");

            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
                List<Address> addresses = geocoder.getFromLocationName(maddress,1);
                if (addresses.size() >0) {
                    Address bestResult = (Address) addresses.get(0);

                    double distance_km = distance(bestResult.getLatitude(),bestResult.getLongitude(),mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

                    if(distance_km <= a){
                        LatLng where = new LatLng(bestResult.getLatitude(), bestResult.getLongitude());

                        mgooglemap.addMarker(
                                new MarkerOptions().
                                        position(where).
                                        title(mname).
                                        snippet(maddress).
                                        //icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_black_24dp)));
                                                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        mgooglemap.setOnMarkerClickListener(new MyMarkerClickListener());
                    }
                }
            } catch (IOException e) {
                return;
            }
        }


    }

    //위도, 경도를 이용한 거리계산 참조 : http://fruitdev.tistory.com/189
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
