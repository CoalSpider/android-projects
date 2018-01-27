package com.ben.lightingtest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MyGLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mGLView = (MyGLSurfaceView)findViewById(R.id.GLSurface);
        mGLView.setRenderer(new MyGLRenderer(mGLView,this));

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

    @Override
    protected void onResume() {
        // activity must call GL surface views onResume() on activity onResume()
        super.onResume();
    }

    @Override
    protected void onPause() {
        // activity must call the GL surface views onPause() on activity onPause()
        super.onPause();
    }
}
