package com.ben.liquidfuntutorials;


import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by Ben on 7/23/2017.
 */

public class ShaderProgram {

    // tag for Log debugging
    private static final String TAG = "ShaderProgram";

    // program/vertex/fragment handles
    private int mProgram, mVertexShader, mFragmentShader;

    // Takes in ids for files to be read
    public ShaderProgram(int vertexShaderID, int fragmentShaderID, Context context) {
        String vertexShader =
                RawResourceLoader.loadShaderFile(context, vertexShaderID);
        String fragmentShader =
                RawResourceLoader.loadShaderFile(context, fragmentShaderID);

        createAndLinkProgram(vertexShader, fragmentShader);
    }


    private void createAndLinkProgram(String vertexShader, String fragmentShader) {
        createProgram();
        compileVertexShader(vertexShader);
        compileFragmentShader(fragmentShader);
        attachShadersToProgram();
        linkProgram();
    }

    private void createProgram() {
        mProgram = GLES20.glCreateProgram();

        if (mProgram == 0)
            throw new RuntimeException(TAG + " could not create program");
    }

    private void compileVertexShader(String vertexShader) {
        mVertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);

        if (mVertexShader == 0)
            throw new RuntimeException(TAG + " error compiling vertex shader");
    }

    private void compileFragmentShader(String fragmentShader) {
        mFragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        if (mFragmentShader == 0)
            throw new RuntimeException(TAG + " error compiling fragment shader");
    }

    private void attachShadersToProgram() {
        GLES20.glAttachShader(mProgram, mVertexShader);
        GLES20.glAttachShader(mProgram, mFragmentShader);
    }

    private void linkProgram() {
        GLES20.glLinkProgram(mProgram);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link _program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(mProgram));
            GLES20.glDeleteProgram(mProgram);
            throw new RuntimeException(TAG + " error linking program");
        } else {
            // link suceeded detach and delete shaders
            GLES20.glDetachShader(mProgram, mVertexShader);
            GLES20.glDeleteShader(mVertexShader);
            GLES20.glDetachShader(mProgram, mFragmentShader);
            GLES20.glDeleteShader(mFragmentShader);
        }
    }

    /**
     * @return the handle for the compiled shader
     */
    private int compileShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);

        if (shader == 0) {
            throw new RuntimeException(TAG + " could not create shader ");
        }

        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);

        checkShaderCompStatus(shader, shaderType);

        return shader;
    }

    private void checkShaderCompStatus(int shader, int shaderType) {
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            throw new RuntimeException(TAG + " could not compile shader " + shaderType);
        }
    }


    public int getProgram() {
        return mProgram;
    }
}
