package com.ben.lightingtest.lights;

import android.opengl.GLES20;

import com.ben.lightingtest.util.Vector3;

/**
 * Created by Ben on 7/12/2017.
 */

public class SpotLight extends PointLight {
    private int directionHandle=-1;
    private int cosCutoffHandle=-1;
    private int exponentHandle=-1;

    private float[] direction;
    private float cosCutoffInRadians;
    private float exponent;

    public static class Builder extends PointLight.Builder{
        private float[] direction;
        // range [0.0,1.0] in radians (0 to ~57.2958)
        private float cosCutoffInRadians;
        // range [0.0,90.0]
        private float exponent;

        public Builder(float[] spotDirection, float cosCutoffInRadians, float exponent){
            this.direction = spotDirection;
            this.cosCutoffInRadians = cosCutoffInRadians;
            this.exponent = exponent;
        }

        @Override
        public Builder constantAttenuation(float attenuation) {
            super.constantAttenuation(attenuation);
            return this;
        }

        @Override
        public Builder linearAttenuation(float attenuation) {
            super.linearAttenuation(attenuation);
            return this;
        }

        @Override
        public Builder quadraticAttenuation(float attenuation) {
            super.quadraticAttenuation(attenuation);
            return this;
        }

        @Override
        public Builder ambientColor(float r, float g, float b, float a) {
            super.ambientColor(r, g, b, a);
            return this;
        }

        @Override
        public Builder diffuseColor(float r, float g, float b, float a) {
            super.diffuseColor(r, g, b, a);
            return this;
        }

        @Override
        public Builder specularColor(float r, float g, float b, float a) {
            super.specularColor(r, g, b, a);
            return this;
        }

        @Override
        public SpotLight build(){
            return new SpotLight(this);
        }
    }

    protected SpotLight(Builder builder){
        super(builder);
        direction = builder.direction;
        cosCutoffInRadians = builder.cosCutoffInRadians;
        exponent = builder.exponent;
    }

    @Override
    public void setHandles(int programHandle,int index, String structName) {
        super.setHandles(programHandle,index,structName);
        String indexString = "["+index+"]";
        directionHandle = GLES20.glGetUniformLocation(programHandle,
                structName+indexString+".spotDirection");
        cosCutoffHandle = GLES20.glGetUniformLocation(programHandle,
                structName+indexString+".spotCosCutoff");
        exponentHandle = GLES20.glGetUniformLocation(programHandle,
                structName+indexString+".spotExponent");

        isPassedIn(directionHandle,"spotDirectionHandle");
        isPassedIn(cosCutoffHandle,"spotCosCutoffHandle");
        isPassedIn(exponentHandle,"spotExponentHandle");
    }

    @Override
    public void passDataToOpenGL() {
        super.passDataToOpenGL();
        GLES20.glUniform3f(directionHandle, direction[0], direction[1],
                direction[2]);
        GLES20.glUniform1f(cosCutoffHandle, cosCutoffInRadians);
        GLES20.glUniform1f(exponentHandle, exponent);
    }

    public float[] getDirection() {
        return direction;
    }

    public void setDirection(float x, float y, float z){
        direction[0] = x;
        direction[1] = y;
        direction[2] = z;
        Vector3.normalize(direction);
    }
}