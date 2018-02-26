package com.ben.glrendererlite.util;

import android.opengl.Matrix;

/**
 * Created by Ben on 8/21/2017.
 */

public class Camera {
    private float eyeX = 0.0f;
    private float eyeY = 0.0f;
    private float eyeZ = 1.5f;
    private float centerX = 0.0f;
    private float centerY = 0.0f;
    private float centerZ = -5.0f;
    private float upX = 0.0f;
    private float upY = 1.0f;
    private float upZ = 0.0f;

    private float[] lookAtMatrix;
    private Projection projection;

    public Camera(){
        lookAtMatrix = new float[16];
        projection = new Projection();
    }

    public void setWidthHeight(float width, float height) {
        projection.setWidthHeight(width, height);
        setLookAtMatrix();
    }

    private void setLookAtMatrix() {
        Matrix.setLookAtM(lookAtMatrix, 0,
                eyeX, eyeY, eyeZ,
                centerX, centerY, centerZ,
                upX, upY, upZ);
    }

    public float[] getMVP(float[] modelMatrix){
        float[] MVMatrix = new float[16];
        Matrix.multiplyMM(MVMatrix,0,lookAtMatrix,0,modelMatrix,0);
        float[] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix,0,projection.getProjectionMatrix(),0,MVMatrix,0);
        return MVPMatrix;
    }
}

