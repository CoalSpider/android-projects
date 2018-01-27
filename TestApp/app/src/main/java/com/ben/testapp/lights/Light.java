package com.ben.testapp.lights;

import android.opengl.Matrix;

import com.ben.testapp.util.PlayerCamera;

/**
 * Created by Ben on 7/25/2017.
 */

public class Light {
    private final float[] lightViewMatrix = new float[16];


    private final float[] lightPosInEyeSpace = new float[16];

    private final float[] lightPosModelSpace = new float[]
            {-5.0f, 9.0f, 0.0f, 1.0f};

    private float[] actualLightPosition = new float[4];

    public float[] getLightViewMatrix() {
        return lightViewMatrix;
    }

    public float[] getLightPosInEyeSpace(PlayerCamera playerCamera) {
        Matrix.multiplyMV(lightPosInEyeSpace, 0, playerCamera.getLookAtMatrix(), 0,
                actualLightPosition, 0);
        return lightPosInEyeSpace;
    }

    /** TODO: check this code for bugs
     * As it was taken from a tutorial (this is actually the last bit left
     * outside of shaders that has NOT been changed)
     * To be honest I dont understand why these values were chosen**/
    public void setLookAtM() {
        //Set view matrix from light source position
        Matrix.setLookAtM(lightViewMatrix, 0,
                //lightX, lightY, lightZ, 
                actualLightPosition[0], actualLightPosition[1], actualLightPosition[2],
                //lookX, lookY, lookZ,
                //look in direction -y
                actualLightPosition[0], -actualLightPosition[1], actualLightPosition[2],
                //upX, upY, upZ
                //up vector in the direction of axisY
                -actualLightPosition[0], 0, -actualLightPosition[2]);

    }

    /**
     * TODO: replace rotation matrix with full model matrix (so we can translate light)
     **/
    public void setLightRotation(float[] rotationMatrix) {
        Matrix.multiplyMV(actualLightPosition, 0, rotationMatrix, 0,
                lightPosModelSpace, 0);

    }
}
