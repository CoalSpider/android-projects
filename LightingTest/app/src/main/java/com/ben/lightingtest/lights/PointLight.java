package com.ben.lightingtest.lights;

import android.opengl.GLES20;

/**
 * Created by Ben on 7/12/2017.
 */

public class PointLight extends Light {
    private int constAttenHandle=-1;
    private int linearAttenHandle=-1;
    private int quadAttenHandle=-1;

    private float constantAttenuation;
    private float linearAttenuation;
    private float quadraticAttenuation;

    public static class Builder extends Light.Builder{
        private float constantAttenuation = 0;
        private float linearAttenuation = 0;
        private float quadraticAttenuation = 0;

        public Builder constantAttenuation(float attenuation){
            constantAttenuation = attenuation;
            return this;
        }

        public Builder linearAttenuation(float attenuation){
            linearAttenuation = attenuation;
            return this;
        }

        public Builder quadraticAttenuation(float attenuation){
            quadraticAttenuation = attenuation;
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
        public PointLight build(){
            return new PointLight(this);
        }
    }

    protected PointLight(Builder builder){
        super(builder);
        constantAttenuation = builder.constantAttenuation;
        linearAttenuation = builder.linearAttenuation;
        quadraticAttenuation = builder.quadraticAttenuation;
    }

    @Override
    public void setHandles(int programHandle, int index, String structName) {
        super.setHandles(programHandle,index,structName);
        String indexString = "["+index+"]";
        constAttenHandle = GLES20.glGetUniformLocation(programHandle,
                structName+indexString+".constantAttenuation");
        linearAttenHandle = GLES20.glGetUniformLocation(programHandle,
                structName+indexString+".linearAttenuation");
        quadAttenHandle = GLES20.glGetUniformLocation(programHandle,
                structName+indexString+".quadraticAttenuation");

        isPassedIn(constAttenHandle,"constAttenHandle");
        isPassedIn(linearAttenHandle,"linearAttenHandle");
        isPassedIn(quadAttenHandle,"quadAttenHandle");
    }

    @Override
    public void passDataToOpenGL() {
        super.passDataToOpenGL();
        GLES20.glUniform1f(constAttenHandle, constantAttenuation);
        GLES20.glUniform1f(linearAttenHandle, linearAttenuation);
        GLES20.glUniform1f(quadAttenHandle, quadraticAttenuation);
    }
}
