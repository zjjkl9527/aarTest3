package com.zjj.aartest3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.hhqj.player.SpherePlayerView;

public class MainActivity extends AppCompatActivity {
        private SpherePlayerView mSpherePlayerSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
/*        mSpherePlayerSurfaceView = new SpherePlayerView(this);
        setContentView(mSpherePlayerSurfaceView);
        mSpherePlayerSurfaceView.setListener(new SpherePlayerView.ControlListener() {

            @Override
            public void horizontalFullScreen(boolean b) {
                if(b){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }
            }

            @Override
            public void back() {
                finish();
            }

            @Override
            public void switchQuality(String s) {
                System.out.println(s);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSpherePlayerSurfaceView = new SpherePlayerView(this);
        setContentView(mSpherePlayerSurfaceView);
        mSpherePlayerSurfaceView.setListener(new SpherePlayerView.ControlListener() {

            @Override
            public void horizontalFullScreen(boolean b) {
                if(b){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }

            }

            @Override
            public void back() {
                finish();
            }

            @Override
            public void switchQuality(String s) {
                System.out.println(s);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}