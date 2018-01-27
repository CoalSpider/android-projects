package com.ben.testapp;

import android.app.Activity;

import com.ben.testapp.common.ShadowBiasType;
import com.ben.testapp.util.RenderProgram;
import com.ben.testapp.common.ShadowType;

/**
 * Created by Ben on 7/30/2017.
 */

class RenderProgramsHolder {
    /**
     * Handles to vertex and fragment shader programs
     */
    private RenderProgram mSimpleShadowProgram;
    private RenderProgram mPCFShadowProgram;
    private RenderProgram mSimpleShadowDynamicBiasProgram;
    private RenderProgram mPCFShadowDynamicBiasProgram;
    private RenderProgram mDepthMapProgram;

    private Activity activity;
    private boolean hasOESTextureExtension;

    private int currentShaderProgram;

    RenderProgramsHolder(Activity activity, boolean hasOESTextureExtension) {
        this.activity = activity;
        this.hasOESTextureExtension = hasOESTextureExtension;
        initRenderPrograms();
    }

    private void initRenderPrograms() {
        //Load shaders and create program used by OpenGL for rendering
        if (hasOESTextureExtension) {
            initOESPrograms();
        } else {
            initNonOESPrograms();
        }
        // default program
        currentShaderProgram = mSimpleShadowProgram.getProgram();
    }

    private void initOESPrograms() {
        // OES_depth_texture is available -> shaders are simplier
        mSimpleShadowProgram = new RenderProgram(R.raw.oes_v_with_shadow, R.raw
                .oes_f_simple_shadow, activity);

        mPCFShadowProgram = new RenderProgram(R.raw.oes_v_with_shadow, R.raw
                .oes_f_pcf_shadow, activity);

        mSimpleShadowDynamicBiasProgram = new RenderProgram(R.raw.oes_v_with_shadow,
                R.raw.oes_f_simple_shadow_dynamic_bias, activity);

        mPCFShadowDynamicBiasProgram = new RenderProgram(R.raw.oes_v_with_shadow, R
                .raw.oes_f_pcf_shadow_dynamic_bias, activity);

        mDepthMapProgram = new RenderProgram(R.raw.oes_v_depth_map, R.raw
                .oes_f_depth_map, activity);
    }

    private void initNonOESPrograms() {
        mSimpleShadowProgram = new RenderProgram(R.raw.v_with_shadow, R.raw
                .f_simple_shadow, activity);

        mPCFShadowProgram = new RenderProgram(R.raw.v_with_shadow, R.raw
                .f_pcf_shadow, activity);

        mSimpleShadowDynamicBiasProgram = new RenderProgram(R.raw.v_with_shadow, R
                .raw.f_simple_shadow_dynamic_bias, activity);

        mPCFShadowDynamicBiasProgram = new RenderProgram(R.raw.v_with_shadow, R.raw
                .f_pcf_shadow_dynamic_bias, activity);

        // If there is no OES_depth_texture extension depth values must be coded in
        // rgba texture and later decoded at calculation of shadow
        mDepthMapProgram = new RenderProgram(R.raw.v_depth_map, R.raw.f_depth_map,
                activity);
    }

    void setRenderProgram(MainActivity mShadowsActivity) {
        if (mShadowsActivity.getShadowType() == ShadowType.SIMPLE)
            if (mShadowsActivity.getBiasType() == ShadowBiasType.CONSTANT)
                currentShaderProgram = mSimpleShadowProgram.getProgram();
            else
                currentShaderProgram = mSimpleShadowDynamicBiasProgram.getProgram();
        else
        if (mShadowsActivity.getBiasType() == ShadowBiasType.CONSTANT)
            currentShaderProgram = mPCFShadowProgram.getProgram();
        else
            currentShaderProgram = mPCFShadowDynamicBiasProgram.getProgram();
    }

    int getCurrentShaderProgram() {
        return currentShaderProgram;
    }

    RenderProgram getmDepthMapProgram() {
        return mDepthMapProgram;
    }
}
