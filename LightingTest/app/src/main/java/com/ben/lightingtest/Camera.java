package com.ben.lightingtest;

import android.opengl.Matrix;

public class Camera {
    // width and height (can change if screen orientation changes)
    private static final float DEFAULT_WIDTH = 0;
    private static final float DEFAULT_HEIGHT = 0;
    private static float width = DEFAULT_WIDTH;
    private static float height = DEFAULT_HEIGHT;

    // eye position (behind origin)
    private static float eyeX = 0.0f;
    private static float eyeY = 1.0f; // base 1
    private static float eyeZ = 1.5f; // base 1.5

    // center of view pos
    private static float centerX = 0.0f;
    private static float centerY = 0.0f;
    private static float centerZ = -5.0f;

    // up vector
    private static float upX = 0.0f;
    private static float upY = 1.0f;
    private static float upZ = 0.0f;

    // aspect ratio
    private static float ratio = width / height;

    // the 6 clipping planes
    private static final float bottom = -1.0f;
    private static final float top = 1.0f;
    private static final float near = 1f;
    private static final float far = 100.0f;
    // can change if the width and/or height change
    private static float left = -ratio;
    private static float right = ratio;


    // view matrix
    private static float[] viewMatrix = null;
    // projection matrix
    private static float[] projectionMatrix = null;


    public static float getWidth() {
        if (width == DEFAULT_WIDTH) {
            throw new RuntimeException("camera width not set, make sure to set camera" +
                    " width before use");
        }
        return width;
    }

    public static float getHeight() {
        if (height == DEFAULT_HEIGHT) {
            throw new RuntimeException("camera height not set, make sure to set " +
                    "camera height before use");
        }
        return height;
    }

    public static float[] getViewMatrix() {
        if (viewMatrix == null) {
            viewMatrix = new float[16];
            Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
                    centerZ, upX, upY, upZ);
        }
        return viewMatrix;
    }

    public static float[] getProjectionMatrix(){
        if(projectionMatrix == null){
            projectionMatrix = new float[16];
            Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        }
        return projectionMatrix;
    }

    /**
     * Sets the width to the specified width, the aspect ratio, left clipping plane,
     * and right clipping plane are also updated
     **/
    static void setWidth(float width) {
        Camera.width = width;
        updatedDimensionHelper();
    }

    /**
     * Sets the height to the specified height, the aspect ratio, left clipping plane,
     * and right clipping plane are also updated
     **/
    static void setHeight(float height) {
        Camera.height = height;
        updatedDimensionHelper();
    }

    /**
     * updates all variables that depend on the width and/or height
     *
     * <li>1. aspect ratio</li>
     * <li>2. left clipping plane</li>
     * <li>3. right clipping plane</li>
     * <li>4. projection matrix</li>
     **/
    private static void updatedDimensionHelper() {
        if(width == DEFAULT_WIDTH || height == DEFAULT_HEIGHT){
            return;
        }
        Camera.ratio = getWidth() / getHeight();
        left = -ratio;
        right = ratio;
        if(projectionMatrix == null){
            projectionMatrix = new float[16];
        }
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public static void setLookAtM(float[] newLookAt){
        viewMatrix = newLookAt;
    }

    public static void setProjection(float[] newProjection){
        projectionMatrix = newProjection;
    }

    private static float[] lightProjectionMatrix;
    public static float[] getLightProjection(){
        if(lightProjectionMatrix == null){
            lightProjectionMatrix = new float[16];
            float sclar = 1.0f;
            Matrix.frustumM(lightProjectionMatrix,0,left*sclar,right*sclar,bottom*sclar,top*sclar,near,far);
        }
        return lightProjectionMatrix;
    }
}