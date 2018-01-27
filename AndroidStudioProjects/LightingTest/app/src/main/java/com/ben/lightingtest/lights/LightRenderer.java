package com.ben.lightingtest.lights;

import android.nfc.tech.NfcA;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ben.lightingtest.Camera;
import com.ben.lightingtest.shaderPrograms.PointProgram;

/**
 * Created by Ben on 7/22/2017.
 */

public class LightRenderer {
    private static final String lightStructName = "dirLights";
    private static final String pointLightStructName = "pointLights";
    private static final String spotLightStructName = "spotLights";
    private static final int MAX_LIGHTS = 3;

    private Light[] lights = new Light[MAX_LIGHTS];

    public LightRenderer(){
        init();
    }

    public void init(){
        lights[0] = new PointLight
                .Builder()
                .diffuseColor(0,0,0,1)
                .ambientColor(0,0,0,1)
                .constantAttenuation(0.3f)
                .linearAttenuation(0.6f)
                .quadraticAttenuation(0.9f)
                .build();
        lights[1] = new SpotLight
                .Builder(new float[]{0,-1,0},0.25f,5)
                .diffuseColor(0,0,0,1)
                .constantAttenuation(0.0f)
                .linearAttenuation(0.0f)
                .quadraticAttenuation(0.25f)
                .build();
        lights[2] = new Light
                .Builder()
                .ambientColor(0.5f,0.5f,0.5f,1.0f)
                .diffuseColor(1.0f,1.0f,1.0f,1.0f)
                .build();
    }

    public void updateLights(float angleInDegrees){
        double angleInRad = Math.toRadians(angleInDegrees);
    /*    ModelPosition position = lights[0].getLightPosition();
        position.setIdentityM();
        position.translateM(0.0f, 0.5f, -2.0f);
        position.rotateM(angleInDegrees, 0.0f, 1.0f, 0.0f);
        position.translateM(0.0f, 0.0f, 2.0f);
        float x = (float) Math.cos(angleInRad);
        float z = (float) Math.sin(angleInRad);
        if(lights[0] instanceof SpotLight)
            ((SpotLight) lights[0]).setDirection(x, 0.0f, z);


        ModelPosition position2 = lights[1].getLightPosition();
        position2.setIdentityM();
        position2.translateM(0.0f, 0.5f, -2.0f);
        position2.rotateM(-angleInDegrees, 0.0f, 1.0f, 0.0f);
        position2.translateM(0.0f, 0.0f, 2.0f);
        float x1 = (float) Math.cos(-angleInRad);
        float z1 = (float) Math.sin(-angleInRad);
        if(lights[1] instanceof SpotLight)
            ((SpotLight) lights[1]).setDirection(x1, 0.0f, z1); */

        ModelPosition position3 = lights[2].getLightPosition();
        position3.setIdentityM();
        position3.translateM(0.0f, 1.5f, -2.0f);
        position3.rotateM(-angleInDegrees, 0.0f, 1.0f, 0.0f);
        position3.translateM(0.0f, 0.0f, 3.0f);
        float x2 = (float) Math.cos(angleInRad/2.0);
        float z2 = (float) Math.sin(angleInRad);
        if(lights[1] instanceof SpotLight)
            ((SpotLight) lights[1]).setDirection(x2, 0.0f, z2);
    }

    public void setHandles(int programHandle){
        for(int i = 0; i < MAX_LIGHTS; i++){
            if(lights[i] instanceof SpotLight){
                System.err.println("spot light");
                lights[i].setHandles(programHandle,i,spotLightStructName);
                continue;
            }
            if(lights[i] instanceof PointLight){
                System.err.println("point light");
                lights[i].setHandles(programHandle,i,pointLightStructName);
                continue;
            }
            System.err.println("reg light");
            lights[i].setHandles(programHandle,i,lightStructName);
        }
    }

    public void passDataToOpenGL(){
        for(Light light : lights)
            light.passDataToOpenGL();
    }

    public void drawLights(PointProgram pointProgram){
        GLES20.glUseProgram(pointProgram.getProgramHandle());
        for(Light light : lights)
            drawLights(light,pointProgram);
    }

    private void drawLights(Light l, PointProgram pointProgram) {
        int pointPositionHandle = pointProgram.getPositionHandle();
        int pointMVPMatrixHandle = pointProgram.getPointMVPHandle();
        float[] lightPos = l.getLightPosition().getPosInModelSpace();
        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, lightPos[0],
                lightPos[1], lightPos[2]);

        // Since we are not using a buffer object, disable vertex arrays for this
        // attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        float[] mMVMatrix = new float[16];
        float[] mMVPMatrix = new float[16];
        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVMatrix, 0, Camera.getViewMatrix(), 0,
                l.getLightPosition().getModelMatrix(), 0);
        Matrix.multiplyMM(mMVPMatrix, 0, Camera.getProjectionMatrix(), 0, mMVMatrix,
                0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    public Light[] getLights() {
        return lights;
    }
}
