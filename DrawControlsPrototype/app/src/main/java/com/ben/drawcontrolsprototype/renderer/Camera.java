package com.ben.drawcontrolsprototype.renderer;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by Ben on 4/13/2017.
 * <p>
 * TEST CLASS
 */

public class Camera {
    // width and height (can change if screen orientation changes)
    private final static float DEFAULT_WIDTH = 0;
    private final static float DEFAULT_HEIGHT = 0;
    private float width = DEFAULT_WIDTH;
    private float height = DEFAULT_HEIGHT;

    // eye position (behind origin)
    private final float eyeX = 0.0f;
    private final float eyeY = 0.0f;
    private final float eyeZ = 1.5f; // base 1.5

    // center of view pos
    private final float centerX = 0.0f;
    private final float centerY = 0.0f;
    private final float centerZ = -5.0f;

    // up vector
    private final float upX = 0.0f;
    private final float upY = 1.0f;
    private final float upZ = 0.0f;

    // aspect ratio
    private float ratio = width / height;

    // the 6 clipping planes
    private final float bottom = -1.0f;
    private final float top = 1.0f;
    private final float near = 1.0f;
    private final float far = 100.0f;
    // can change if the width and/or height change
    private float left = -ratio;
    private float right = ratio;
    // wrapping array
    private final float[] clippingPlanes = {
            left, right, bottom, top, near, far
    };


    // view matrix
    private float[] viewMatrix = null;
    // projection matrix
    private float[] projectionMatrix = null;


    public float getWidth() {
        if (width == DEFAULT_WIDTH) {
            throw new RuntimeException("camera width not set, make sure to set camera" +
                    " width before use");
        }
        return width;
    }

    public float getHeight() {
        if (height == DEFAULT_HEIGHT) {
            throw new RuntimeException("camera height not set, make sure to set " +
                    "camera height before use");
        }
        return height;
    }

    public final float[] getViewMatrix() {
        if (viewMatrix == null) {
            viewMatrix = new float[16];
            Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
                    centerZ, upX, upY, upZ);
        }
        return viewMatrix;
    }

    public final float[] getProjectionMatrix(){
        if(projectionMatrix == null){
            projectionMatrix = new float[16];
            Log.e("udh","fustrumM set2");
            Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        }
        return projectionMatrix;
    }

    /**
     * @return the aspect ratio (width / height)
     **/
    public float getAspectRatio() {
        return this.ratio;
    }

    /**
     * @return the six clipping planes in the the following order: left, right,
     * bottom, top, near, far
     **/
    public float[] getClipPlanes() {
        return clippingPlanes;
    }

    /**
     * @return the horizontal field of view in radians
     **/
    public float getHorizontalFOV() {
        float halfWidth = (right - left) / 2.0f;
        float halfFOV = (float) Math.atan(halfWidth / near);
        return halfFOV * 2;
    }

    /**
     * @return the vertical field of view in radians
     **/
    public float getVerticalFOV() {
        float halfHeight = (top - bottom) / 2.0f;
        float halfFOV = (float) Math.atan(halfHeight / near);
        return halfFOV * 2;
    }

    /**
     * Sets the width to the specified width, the aspect ratio, left clipping plane,
     * and right clipping plane are also updated
     **/
    void setWidth(float width) {
        this.width = width;
        updatedDimensionHelper();
    }

    /**
     * Sets the height to the specified height, the aspect ratio, left clipping plane,
     * and right clipping plane are also updated
     **/
    void setHeight(float height) {
        this.height = height;
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
    private void updatedDimensionHelper() {
        if(width == DEFAULT_WIDTH || height == DEFAULT_HEIGHT){
            return;
        }
        this.ratio = getWidth() / getHeight();
        left = -ratio;
        clippingPlanes[0] = left;
        right = ratio;
        clippingPlanes[1] = right;
        if(projectionMatrix == null){
            projectionMatrix = new float[16];
        }
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }
}
