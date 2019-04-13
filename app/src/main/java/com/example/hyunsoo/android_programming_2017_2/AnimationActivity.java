package com.example.hyunsoo.android_programming_2017_2;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AnimationActivity extends AppCompatActivity {

    FrameLayout mFrame;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_animation);

        mFrame = (FrameLayout) findViewById(R.id.activity_animation);
        imageView = (ImageView) findViewById(R.id.logoImage);
    }

    protected void onResume() {
        super.onResume();

        startanimationativity();
        startanimation();
    }

    private void startanimationativity() {
        imageView.setBackgroundResource(R.drawable.frame_anim);
        AnimationDrawable countdownAnim =
                (AnimationDrawable) imageView.getBackground();
                countdownAnim.start();
    }

    private void startanimation() {
        Animation one_anim = AnimationUtils.loadAnimation(this, R.anim.one);
        imageView.startAnimation(one_anim);
        one_anim.setAnimationListener(animationListener);
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    };

}
