package com.ben.lightingtest.lights;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by Ben on 7/12/2017.
 */

public class Light {
    // handle to light shader program
    private int programHandle;
    // light position handle for shader
    private int positionHandle = -1;
    // handle to colors
    private int ambientColorHandle = -1;
    private int diffuseColorHandle = -1;
    private int specularColorHandle = -1;

    // positional data
    private ModelPosition position = new ModelPosition();
    // opengl uses format rgba
    private float[] ambientColor;
    private float[] diffuseColor;
    private float[] specularColor;

    public static class Builder{
        private float[] ambientColor = {0.0f, 0.0f, 0.0f, 0.0f};
        private float[] diffuseColor = {0.0f, 0.0f, 0.0f, 0.0f};
        private float[] specularColor = {0.0f, 0.0f, 0.0f, 0.0f};

        public Builder ambientColor(float r, float g, float b, float a){
            ambientColor = new float[]{r,g,b,a};
            return this;
        }

        public Builder diffuseColor(float r, float g, float b, float a){
            diffuseColor = new float[]{r,g,b,a};
            return this;
        }

        public Builder specularColor(float r, float g, float b, float a){
            specularColor = new float[]{r,g,b,a};
            return this;
        }

        public Light build(){
            return new Light(this);
        }
    }

    protected Light(Builder builder){
        ambientColor = builder.ambientColor;
        diffuseColor = builder.diffuseColor;
        specularColor = builder.specularColor;
    }

    public void setHandles(int programHandle, int index, String structName) {
        this.programHandle = programHandle;
        String indexString = "[" + index + "]";
        positionHandle = GLES20.glGetUniformLocation(programHandle,
                structName + indexString + ".lightPos");
        ambientColorHandle = GLES20.glGetUniformLocation(programHandle,
                structName + indexString + ".ambientColor");
        diffuseColorHandle = GLES20.glGetUniformLocation(programHandle,
                structName + indexString + ".diffuseColor");
        specularColorHandle = GLES20.glGetUniformLocation(programHandle,
                structName + indexString + ".specularColor");

        // make sure all variables were found in the shader
        isPassedIn(positionHandle, "positionHandle");
        isPassedIn(ambientColorHandle, "ambientColorHandle");
        isPassedIn(diffuseColorHandle, "diffuseColorHandle");
        isPassedIn(specularColorHandle, "specularColorHandle");

        Light l = new Light
                .Builder()
                .ambientColor(0,1,0,0)
                .build();
    }

    protected void isPassedIn(int glLocation, String variableName) throws
            RuntimeException {
        if (glLocation == -1) {
            Log.e("LIGHT", "malformed shader variables");
            throw new RuntimeException(variableName + " has no location in shader");
        }
    }

    public void passDataToOpenGL() {
        float[] eyePos = position.getPosInEyeSpace();
        GLES20.glUniform3f(positionHandle, eyePos[0], eyePos[1], eyePos[2]);
        GLES20.glUniform4f(ambientColorHandle, ambientColor[0], ambientColor[1],
                ambientColor[2], ambientColor[3]);
        GLES20.glUniform4f(diffuseColorHandle, diffuseColor[0], diffuseColor[1],
                diffuseColor[2], diffuseColor[3]);
        GLES20.glUniform4f(specularColorHandle, specularColor[0], specularColor[1],
                specularColor[2], specularColor[3]);
    }

    public ModelPosition getLightPosition(){
        return position;
    }
}