package com.ben.glrendererlite;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private CustomSufaceView mGLView;
    private GLRenderer renderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLView = (CustomSufaceView)findViewById(R.id.GLSurface);
        renderer = new GLRenderer(this);
        mGLView.setRenderer(renderer);

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
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}
