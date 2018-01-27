package com.ben.lightingtest.shaderPrograms;

import android.app.Activity;
import android.opengl.GLES20;

import com.ben.lightingtest.util.AssetResourceReader;
import com.ben.lightingtest.util.ShaderHelper;

/**
 * Created by Ben on 7/14/2017.
 */

public class MainProgram {
    private static final String vertShader = "multi_light_vertex_shader";
    private static final String fragShader = "multi_light_fragment_shader";
    private static final String[] params = {"a_Position","a_Color","a_Normal"};

    private int programHandle = -1;
    // attribs
    private int positionHandle = -1;
    private int colorHandle = -1;
    private int normalHandle = -1;
    // uniforms
    private int modelMVPHandle = -1;
    private int modelMVHandle = -1;
    private int depthMVP = -1;
    private int uniformTextureHandle = -1;

    public void loadProgram(Activity activity) {
        createAndLink(activity);
        passInVariables();
    }

    private void createAndLink(Activity activity){
        // compile object shader
        final String vertexShader = AssetResourceReader.readShaderFile(activity,
                vertShader);
        final String fragmentShader = AssetResourceReader.readShaderFile(activity,
                fragShader);
        final int vertexShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        programHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle,
                fragmentShaderHandle,params);
    }

    private void passInVariables(){
        modelMVPHandle = GLES20.glGetUniformLocation(programHandle, "u_MVP");
        modelMVHandle = GLES20.glGetUniformLocation(programHandle, "u_MV");
        depthMVP = GLES20.glGetUniformLocation(programHandle,"u_DepthMVP");
        uniformTextureHandle = GLES20.glGetUniformLocation(programHandle,"u_ShadowMap");

        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        colorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        normalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");

        if(modelMVPHandle==-1)throw new RuntimeException("modelMVPHandle");
        if(modelMVHandle==-1)throw new RuntimeException("modelMVHandle");
        if(uniformTextureHandle==-1)throw new RuntimeException("uniformTextureHandle");
        if(depthMVP==-1)throw new RuntimeException("depthMVP");
        if(positionHandle==-1)throw new RuntimeException("positionHandle");
        if(colorHandle==-1)throw new RuntimeException("colorHandle");
        if(normalHandle==-1)throw new RuntimeException("normalHandle");
    }

    public int getProgramHandle() {
        return programHandle;
    }

    public int getPositionHandle() {
        return positionHandle;
    }

    public int getColorHandle() {
        return colorHandle;
    }

    public int getNormalHandle() {
        return normalHandle;
    }

    public int getModelMVPHandle() {
        return modelMVPHandle;
    }

    public int getModelMVHandle() {
        return modelMVHandle;
    }

    public int getDepthMVP() {
        return depthMVP;
    }

    public int getUniformTextureHandle() {
        return uniformTextureHandle;
    }
}
