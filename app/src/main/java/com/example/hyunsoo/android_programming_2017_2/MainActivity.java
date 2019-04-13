package com.example.hyunsoo.android_programming_2017_2;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HomeFragment homeFragment = new HomeFragment();
        final SearchFragment searchFragment = new SearchFragment();
        final LocationFragment locationFragment = new LocationFragment();
        final NewFragment newFragment = new NewFragment();
        final MyFragment myFragment = new MyFragment();

        BottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                switch(tabId){
                    case R.id.tab_home:
                        fragmentTransaction.replace(R.id.container, homeFragment).commit();
                        break;

                    case R.id.tab_search:
                        fragmentTransaction.replace(R.id.container, searchFragment).commit();
                        break;

                    case R.id.tab_location:
                        fragmentTransaction.replace(R.id.container, locationFragment).commit();
                        break;

                    case R.id.tab_new:
                        fragmentTransaction.replace(R.id.container, newFragment).commit();
                        break;

                    case R.id.tab_my:
                        fragmentTransaction.replace(R.id.container, myFragment).commit();
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
