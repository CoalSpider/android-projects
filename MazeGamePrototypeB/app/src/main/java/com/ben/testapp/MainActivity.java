package com.ben.testapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ben.testapp.common.ShadowBiasType;
import com.ben.testapp.common.ShadowType;
import com.ben.testapp.userInterface.UserInterface;

public class MainActivity extends AppCompatActivity {

    private CustomGLSurfaceView mGLView;
    private UserInterface UI;
    private GLRenderer renderer;

    private ShadowBiasType biasType = ShadowBiasType.CONSTANT;

    private ShadowType shadowType = ShadowType.SIMPLE;
    /**
     * Shadow map size:
     * 	- displayWidth * SHADOW_MAP_RATIO
     * 	- displayHeight * SHADOW_MAP_RATIO
     */
    private float mShadowMapRatio = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGLView = (CustomGLSurfaceView)findViewById(R.id.GLSurface);
        UI = (UserInterface)findViewById(R.id.PlayerUIView);
        renderer = new GLRenderer(this,UI);
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
    /*
    * Creates the menu and populates it via xml
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opengl_shadow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shadow_type_simple:
                this.setShadowType(ShadowType.SIMPLE);
                item.setChecked(true);
                return true;
            case R.id.shadow_type_pcf:
                this.setShadowType(ShadowType.PCF);
                item.setChecked(true);
                return true;
            case R.id.bias_type_constant:
                this.setBiasType(ShadowBiasType.CONSTANT);
                item.setChecked(true);
                return true;
            case R.id.bias_type_dynamic:
                this.setBiasType(ShadowBiasType.DYNAMIC);
                item.setChecked(true);
                return true;

            case R.id.depth_map_size_0:
                this.setmShadowMapRatio(0.5f);

                // we need to run opengl calls on GLSurface thread
                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.generateShadowFBO();
                    }
                });

                item.setChecked(true);
                return true;
            case R.id.depth_map_size_1:
                this.setmShadowMapRatio(1.0f);

                // we need to run opengl calls on GLSurface thread
                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.generateShadowFBO();
                    }
                });

                item.setChecked(true);
                return true;
            case R.id.depth_map_size_2:
                this.setmShadowMapRatio(1.5f);

                // we need to run opengl calls on GLSurface thread
                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.generateShadowFBO();
                    }
                });

                item.setChecked(true);
                return true;
            case R.id.depth_map_size_3:
                this.setmShadowMapRatio(2.0f);

                // we need to run opengl calls on GLSurface thread
                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.generateShadowFBO();
                    }
                });

                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    public ShadowBiasType getBiasType() {
        return biasType;
    }

    private void setBiasType(ShadowBiasType biasType) {
        this.biasType = biasType;
    }

    public ShadowType getShadowType() {
        return shadowType;
    }

    private void setShadowType(ShadowType shadowType) {
        this.shadowType = shadowType;
    }

    public float getmShadowMapRatio() {
        return mShadowMapRatio;
    }

    private void setmShadowMapRatio(float mShadowMapRatio) {
        this.mShadowMapRatio = mShadowMapRatio;
    }
}

