package com.ben.game;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private MyGLSurfaceView mGLView;
    private CustomJoystickView mCustomJoyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCustomJoyView = (CustomJoystickView)findViewById(R.id.CustomView);

        mGLView = (MyGLSurfaceView)findViewById(R.id.GLSurface);
        mGLView.setRenderer(new MyGLRenderer(this,mCustomJoyView));

        final ActivityManager activityManager = (ActivityManager) getSystemService
                (Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            if (mGLView != null) {
                return;
            }
            // request OpenGL ES 2.0 compatible context
            mGLView.setEGLContextClientVersion(2);
        } else {
            /* Does not support OpenGL ES 1.x compatible version */
            return;
        }
    }

    void clearScreen(View view){
        Log.e("main","clear screen");
        mGLView.clearScreen();
    }

    void endDraw(View view){
        Log.e("main","end draw");
        mGLView.endDraw();
    }

    void startDraw(View view){
        Log.e("main","start draw");
        mGLView.startDraw();
    }

    @Override
    protected void onResume() {
        // activity must call GL surface views onResume() on activity onResume()
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        // activity must call the GL surface views onPause() on activity onPause()
        super.onPause();
        mGLView.onPause();
    }
}
