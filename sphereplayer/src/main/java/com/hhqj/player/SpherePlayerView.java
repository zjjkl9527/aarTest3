package com.hhqj.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

public class SpherePlayerView extends RelativeLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private CheckBox mCBScreen;
    private CheckBox mCBPlayer;
    private CheckBox mCBGyro;
    private ConstraintLayout mBottomConstraintLayout;
    private ConstraintLayout mTopConstraintLayout;
    private Button mBtnBack;
    private Button mBtnQuality;
    private RadioButton mBtn4k;
    private RadioButton mBtn1080p;
    private Context mContext;
    private SphereSurfaceView mSphereSurfaceView;
    private SpherePlayer mSpherePlayer;
    private LinearLayout mLinearLayout;
    private ControlListener mControlListener = null;
    private ConstraintLayout mParentConstraintLayout;
    private RadioGroup mRadioGroup;
    private ConstraintLayout mConstraintLayout;

    private ConstraintLayout mTopBackgroundConstraintLayout;
    private ConstraintLayout mBottomBackgroundConstraintLayout;
    private ConstraintLayout mDefinitionConstraintLayout;

    public SpherePlayerView(Context context) {
        super(context);
        init(context);
    }

    public SpherePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }



    private void init(Context context){
        mContext = context;

        View view = View.inflate(mContext,R.layout.activity,this);
        mCBScreen = findViewById(R.id.cb_screen);
        mCBScreen.setOnCheckedChangeListener(this);

        mCBPlayer = findViewById(R.id.cb_play);
        mCBPlayer.setOnCheckedChangeListener(this);

        mCBGyro = findViewById(R.id.cb_gyro);
        mCBGyro.setOnCheckedChangeListener(this);

        mBtnBack = findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);

        mBtnQuality = findViewById(R.id.btn_quality);
        mBtnQuality.setOnClickListener(this);

        mLinearLayout = findViewById(R.id.ll_definition);

        mBottomConstraintLayout = findViewById(R.id.cl_bottom);
        mTopConstraintLayout = findViewById(R.id.cl_top);

        mParentConstraintLayout = findViewById(R.id.cl);

        mBtn4k = findViewById(R.id.btn_4k);
        mBtn1080p = findViewById(R.id.btn_1080p);

        mRadioGroup = findViewById(R.id.rg);
        mBtn4k.setOnClickListener(this);
        mBtn1080p.setOnClickListener(this);

        mTopBackgroundConstraintLayout = findViewById(R.id.cl_topBackground);
        mBottomBackgroundConstraintLayout = findViewById(R.id.cl_bottomBackground);
        mDefinitionConstraintLayout = findViewById(R.id.cl_definition);


        mSphereSurfaceView = findViewById(R.id.ssv);

        mSpherePlayer = new SpherePlayer(mSphereSurfaceView);
        uiMonitor();

        mSphereSurfaceView.setOnTouchListener(new OnTouchListener() {
            boolean clickable = false;
            float startX;
            float startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getPointerCount()) {
                    case 1:
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                startX = event.getX();
                                startY = event.getY();
                                clickable = true;
                                break;
                            case MotionEvent.ACTION_MOVE:

                                float x = event.getX();
                                float y = event.getY();
                                float deltaX = startX - x;
                                float deltaY = startY - y;
                                if(deltaX!=0||deltaY!=0){
                                    clickable = false;
                                }
                                startX = x;
                                startY = y;

                                break;
                            case MotionEvent.ACTION_UP:
                                if(clickable&&mLinearLayout.getVisibility()==GONE&&mSpherePlayer.isPlaying()){
                                    if(mBottomConstraintLayout.getVisibility()==VISIBLE){
                                        mBottomConstraintLayout.setVisibility(GONE);
                                        mBottomBackgroundConstraintLayout.setVisibility(GONE);
                                    }else{
                                        mBottomConstraintLayout.setVisibility(VISIBLE);
                                        mBottomBackgroundConstraintLayout.setVisibility(VISIBLE);
                                    }

                                    if(mTopConstraintLayout.getVisibility()==VISIBLE){
                                        mTopConstraintLayout.setVisibility(GONE);
                                        mTopBackgroundConstraintLayout.setVisibility(GONE);
                                    }else {
                                        mTopConstraintLayout.setVisibility(VISIBLE);
                                        mTopBackgroundConstraintLayout.setVisibility(VISIBLE);
                                    }
                                }
                                break;
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        mSphereSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("main","created");

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("main","changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                Log.d("main","destroy");
                    mSpherePlayer.stop();
            }
        });

    }


    public interface ControlListener{
        void horizontalFullScreen(boolean b);
        void back();
        void switchQuality(String s);

    }

    public void setListener(ControlListener controlListener){
        mControlListener = controlListener;
    }


    private void uiMonitor(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mSpherePlayer.isPlaying()&&mBottomConstraintLayout.getVisibility()==VISIBLE&&mLinearLayout.getVisibility()==GONE){
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                },8000,8000);
            }
        }).start();
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView==mCBPlayer){
            if(isChecked){
                mSpherePlayer.play("rtmp://202.69.69.180:443/webcast/bshdlive-pc");
            }else {
                mSpherePlayer.stop();
            }
        }

        if(buttonView==mCBScreen){
            if(mControlListener!=null)
            mControlListener.horizontalFullScreen(isChecked);
        }


        if(buttonView==mCBGyro) {
            if (isChecked) {
                mSpherePlayer.setGyroEnable(true, mContext);
            } else {
                mSpherePlayer.setGyroEnable(false, mContext);
            }
        }


    }
        private Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what==0){
                    mTopConstraintLayout.setVisibility(GONE);
                    mTopBackgroundConstraintLayout.setVisibility(GONE);
                    mBottomConstraintLayout.setVisibility(GONE);
                    mBottomBackgroundConstraintLayout.setVisibility(GONE);
                }
                if(msg.what==1){
                    mBtnQuality.setText("4K");
                }
                if(msg.what==2){
                    mBtnQuality.setText("1080P");
                }
                return false;
            }
        });
    @Override
    public void onClick(View v) {
        if(v==mBtnQuality){
            if(mLinearLayout.getVisibility()==GONE){
                mLinearLayout.setVisibility(VISIBLE);
                mDefinitionConstraintLayout.setVisibility(VISIBLE);
            }else {
                mLinearLayout.setVisibility(GONE);
                mDefinitionConstraintLayout.setVisibility(GONE);
            }

        }
        if(v==mBtnBack){
            if(mControlListener!=null)
            mControlListener.back();
        }

        if(v==mBtn4k){
            if(mControlListener!=null){
                mControlListener.switchQuality("4K");
                mHandler.sendEmptyMessage(1);
            }

        }
        if(v==mBtn1080p){
            if(mControlListener!=null){
                mControlListener.switchQuality("1080P");
                mHandler.sendEmptyMessage(2);
            }

        }

    }



}
