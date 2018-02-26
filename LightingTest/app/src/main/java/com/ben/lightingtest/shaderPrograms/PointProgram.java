package com.ben.lightingtest.shaderPrograms;

import android.app.Activity;
import android.opengl.GLES20;

import com.ben.lightingtest.util.AssetResourceReader;
import com.ben.lightingtest.util.ShaderHelper;

/**
 * Created by Ben on 7/14/2017.
 */

public class PointProgram {
    private static final String vertShader = "point_vertex_shader";
    private static final String fragShader = "point_fragment_shader";
    private static final String[] params = {"a_Position"};
    private int programHandle = -1;
    private int pointMVPHandle = -1;
    private int positionHandle = -1;

    public void loadProgram(Activity activity){
        createAndLink(activity);
        passInVariables();
    }

    private void createAndLink(Activity activity) {
        // compile light shader
        final String lightVertexShader =
                AssetResourceReader.readShaderFile(activity, vertShader);
        final String lightFragmentShader =
                AssetResourceReader.readShaderFile(activity, fragShader);
        final int lightVertexShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_VERTEX_SHADER, lightVertexShader);
        final int lightFragmentShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_FRAGMENT_SHADER, lightFragmentShader);
        programHandle = ShaderHelper.createAndLinkProgram
                (lightVertexShaderHandle, lightFragmentShaderHandle, params);
    }

    private void passInVariables(){
        pointMVPHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");

        if(pointMVPHandle == -1) throw new RuntimeException("pointMVPHandle");
        if(positionHandle == -1) throw new RuntimeException("positionHandle");
    }

    public int getProgramHandle() {
        return programHandle;
    }

    public int getPositionHandle() {
        return positionHandle;
    }

    public int getPointMVPHandle() {
        return pointMVPHandle;
    }
}
