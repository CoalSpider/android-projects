package com.ben.testapp;

import android.util.Log;

import com.ben.testapp.common.RenderConstants;
import com.ben.testapp.lights.Light;
import com.ben.testapp.util.PlayerCamera;
import com.ben.testapp.util.RenderProgram;
import com.ben.testapp.model.SceneModel;
import com.ben.testapp.model.VAORenderer;

import static android.opengl.GLES20.*;

import java.util.List;

/**
 * Created by Ben on 7/26/2017.
 */

class DepthMap {
    private static final String TAG = "DepthMap";

    private int lightMVPUniform;
    private int positionAttrib;

    private int[] fboId;
    private int[] shadowTextureId;
    private int[] depthTextureId;

    private RenderProgram depthMapProgram;

    private Light light;

    private int shadowMapWidth;
    private int shadowMapHeight;

    void generateShadowFBO(int width, int height, MainActivity activity){
        shadowMapWidth = Math.round(width * activity.getmShadowMapRatio());
        shadowMapHeight = Math.round(height * activity.getmShadowMapRatio());

        fboId = new int[1];
        depthTextureId = new int[1];
        shadowTextureId = new int[1];

        // create a framebuffer object
        glGenFramebuffers(1, fboId, 0);

        // create render buffer and bind 16-bit depth buffer
        glGenRenderbuffers(1, depthTextureId, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, depthTextureId[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, shadowMapWidth, shadowMapHeight);

        // Try to use a texture depth component
        glGenTextures(1, shadowTextureId, 0);
        glBindTexture(GL_TEXTURE_2D, shadowTextureId[0]);

        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Remove artifact on the edges of the shadowmap
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0]);

        if(GLRenderer.getHasOESTextureExtension()){
            // Use a depth texture
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null);

            // Attach the depth texture to FBO depth attachment point
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowTextureId[0], 0);
        } else {
            glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, shadowMapWidth, shadowMapHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

            // specify texture as color attachment
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, shadowTextureId[0], 0);

            // attach the texture to FBO depth attachment point
            // (not supported with gl_texture_2d)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthTextureId[0]);
        }

        // check FBO status
        int FBOstatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if(FBOstatus != GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    void setHandles() {
        //shadow handles
        int shadowMapProgram = depthMapProgram.getProgram();
        lightMVPUniform = glGetUniformLocation(shadowMapProgram,
                RenderConstants.U_MVP_MATRIX);
        positionAttrib = glGetAttribLocation(shadowMapProgram, RenderConstants
                .A_SHADOW_POSITION);
    }

    void renderShadowMap(List<SceneModel> sceneModelList, PlayerCamera playerCamera) {
        bindFrameBuffer();
        setViewPort();
        clearDepthAndColorBuffers();
        setCurrentProgram();
        renderModels(sceneModelList, playerCamera);

        resetThingsSoWeCanUseAnotherTexture();
    }

    private void resetThingsSoWeCanUseAnotherTexture(){
        // bind default framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void bindFrameBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0]);
    }

    private void setViewPort(){
        glViewport(0, 0, shadowMapWidth, shadowMapHeight);
    }

    private void clearDepthAndColorBuffers(){
        // Clear color and buffers
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    private void setCurrentProgram(){
        glUseProgram(depthMapProgram.getProgram());
    }

    private void renderModels(List<SceneModel> sceneModelList,PlayerCamera playerCamera){
        glUseProgram(depthMapProgram.getProgram());
        for(int i = 0; i < sceneModelList.size(); i++){
            renderDynamicShadow(sceneModelList.get(i),playerCamera);
        }
    }

    /** TODO: move mLightMvpMatrix_dynamicShapes out of the cube model class
     * it needs to be saved once per model drawcall
     * /
     /**
     * The shadow render is doing one thing.
     * Getting the depth of things from a light source
     *
     * How do we extend this to handle more than one light?
     * **/
    private void renderDynamicShadow(SceneModel sceneModel, PlayerCamera playerCamera){
        // calculate lightProjection * lightView * modelMatrix
        sceneModel.setLightMVPMatrix(light.getLightViewMatrix(),playerCamera);
        // pass light MVP to shader
        glUniformMatrix4fv(lightMVPUniform, 1, false, sceneModel.getLightMVPMatrix(), 0);
        // render, shadowPosAttrib is just a handle to the shader vertex data attrib
        VAORenderer.setPositionShaderHandle(positionAttrib);
        VAORenderer.renderShadow(sceneModel.getModel());
    }

    void setLight(Light light) {
        this.light = light;
    }

    void setDepthMapProgram(RenderProgram depthMapProgram) {
        this.depthMapProgram = depthMapProgram;
    }

    int getShadowMapHeight() {
        return shadowMapHeight;
    }

    int getShadowMapWidth() {
        return shadowMapWidth;
    }

    int[] getShadowTextureId() {
        return shadowTextureId;
    }

    public int[] getDepthTextureId() {
        return depthTextureId;
    }
}
