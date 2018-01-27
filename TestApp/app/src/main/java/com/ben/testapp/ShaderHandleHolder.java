package com.ben.testapp;

import android.opengl.GLES20;

import com.ben.testapp.common.RenderConstants;

/**
 * Created by Ben on 7/30/2017.
 */

class ShaderHandleHolder {
    // Uniform locations for scene render program
    static int shader_MVPMatrixUniform;
    static int shader_MVMatrixUniform;
    static int shader_NormalMatrixUniform;
    static int shader_LightPosUniform;
    static int shader_ShadowProjMatrixUniform;
    static int shader_DepthMapTextureUniform;
    static int shader_HasTextureUniform;
    static int shader_ModelTextureUniform;
    // PCF algorithm only
    static int shader_MapStepXUniform;
    static int shader_MapStepYUniform;

    // Shader program attribute locations
    static int shader_PostitionAttribute;
    static int shader_NormalAttribute;
    static int shader_ColorAttribute;
    static int shaderTextureCoordAttribute;

    static void setShaderHandles(int mActiveProgram, DepthMap depthMap){
        depthMap.setHandles();
        // Set program handles for cube drawing.
        shader_MVPMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram,
                RenderConstants.U_MVP_MATRIX);
        shader_MVMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_MV_MATRIX);
        shader_NormalMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_NORMAL_MATRIX);
        shader_LightPosUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_LIGHT_POSITION);
        shader_ShadowProjMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_SHADOW_PROJ_MATRIX);
        shader_DepthMapTextureUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_SHADOW_TEXTURE);
        shader_PostitionAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.A_POSITION);
        shader_NormalAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.A_NORMAL);
        shader_ColorAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.A_COLOR);
        shader_MapStepXUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_SHADOW_X_PIXEL_OFFSET);
        shader_MapStepYUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_SHADOW_Y_PIXEL_OFFSET);
        shader_ModelTextureUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.U_MODEL_TEXTURE);
        shaderTextureCoordAttribute = GLES20.glGetAttribLocation(mActiveProgram,RenderConstants.A_TEX_COORD);
        shader_HasTextureUniform = GLES20.glGetUniformLocation(mActiveProgram,RenderConstants.U_HAS_TEXTURE);

        //checkHandles();
    }
    
    private static void checkHandles(){
        // if the unifrom is not used or missing from the shader throw a rutime exception
        handleChecker("shader_ModelTextureUniform", shader_ModelTextureUniform);
        handleChecker("shaderTextureCoordAttribute", shaderTextureCoordAttribute);
        handleChecker("shader_HasTextureUniform", shader_HasTextureUniform);
    }
    
    private static void handleChecker(String handleName, int handle){
        if(handle == -1){
            throw new RuntimeException(handleName + " not found in shaders");
        }
    }
}
