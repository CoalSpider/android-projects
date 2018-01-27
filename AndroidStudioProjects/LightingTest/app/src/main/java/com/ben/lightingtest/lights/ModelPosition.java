package com.ben.lightingtest.lights;

import android.opengl.Matrix;

import com.ben.lightingtest.Camera;

/** class to hold positional data for a model
 * TODO: expand for quaternion rotation
 * TODO: expand for scaling
 * TODO: better name **/
public class ModelPosition{
    private final float[] modelMatrix = new float[16];
    private final float[] posInWorldSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] posInModelSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] posInEyeSpace = {0.0f, 0.0f, 0.0f, 1.0f};

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public float[] getPosInModelSpace() {
        return posInModelSpace;
    }

    public float[] getPosInWorldSpace() {
        Matrix.multiplyMV(posInWorldSpace, 0, modelMatrix, 0,
                getPosInModelSpace(), 0);
        return posInWorldSpace;
    }

    public float[] getPosInEyeSpace() {
        Matrix.multiplyMV(posInEyeSpace, 0, Camera.getViewMatrix(), 0,
                getPosInWorldSpace(), 0);
        return posInEyeSpace;
    }

    /**
     * wrapper for {@link Matrix#setIdentityM(float[], int)}
     *
     * @return the lightModelMatrix
     **/
    public float[] setIdentityM() {
        Matrix.setIdentityM(modelMatrix, 0);
        return modelMatrix;
    }

    /**
     * wrapper for {@link Matrix#translateM(float[], int, float, float, float)}
     *
     * @return the lightModelMatrix
     **/
    public float[] translateM(float x, float y, float z) {
        Matrix.translateM(modelMatrix, 0, x, y, z);
        return modelMatrix;
    }

    /**
     * wrapper for {@link Matrix#rotateM(float[], int, float, float, float, float)}
     *
     * @return the lightModelMatrix
     **/
    public float[] rotateM(float angleInDegrees, float x, float y, float z) {
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, x, y, z);
        return modelMatrix;
    }
}