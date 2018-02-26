package com.ben.game;

import android.opengl.Matrix;
import android.util.Log;

import com.ben.game.util.Quaternion;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 4/13/2017.
 * <p>
 * TEST CLASS
 */

public class Camera {
    // width and height (can change if screen orientation changes)
    private final static float DEFAULT_WIDTH = 0;
    private final static float DEFAULT_HEIGHT = 0;
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

    public static Vec3f translation;
    public static Quaternion orientation;

    /** TODO: c stick**/
    private void resetLookAt(){
        if(translation != null) {
            eyeX = translation.x();
             eyeY = translation.y();
           // eyeY = translation.y() + 15; // + >5 with -80 pitch for topdown
            eyeZ = translation.z();
        }
        if(orientation != null) {
            float yaw = orientation.toAxisAngle().w();
            // move camera backwards from center of player a bit
            // to allow corner look and prevent look through wall
            // DOES NOT WORK WHEN TEST DEVICE (galaxy tab 3) IS IN LANDSCAPE
            eyeX -= (float)Math.cos(yaw);
            eyeZ -= (float)Math.sin(yaw);
            float pitch = 0;
          //  float pitch = (float)Math.toRadians(-80);
            centerX = eyeX + (float)Math.cos(pitch)*(float) Math.cos(yaw);
            centerY = eyeY + (float)Math.sin(pitch);
            centerZ = eyeZ + (float)Math.cos(pitch)*(float) Math.sin(yaw);
        }
        if(viewMatrix == null){
            viewMatrix = new float[16];
        }
        Matrix.setLookAtM(viewMatrix, 0, eyeX,eyeY,eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    void setTranslation(Vec3f translation){
        this.translation = translation;
        resetLookAt();
    }
    void setTranslation(float x, float y , float z){
        this.translation = new Vec3f(x,y,z);
        resetLookAt();
    }

    void setOrientation(Quaternion orientation){
        this.orientation = orientation;
        resetLookAt();
    }
    void setOrientation(float x, float y, float z, float angleInRadians){
        this.orientation = Quaternion.fromAxisAngle(x,y,z,angleInRadians);
        resetLookAt();
    }

    // aspect ratio
    private float ratio = width / height;

    // the 6 clipping planes
    private final float bottom = -1.0f;
    private final float top = 1.0f;
    private final float near = 1f;
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

    public float[] getViewMatrix() {
        if (viewMatrix == null) {
            viewMatrix = new float[16];
            Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
                    centerZ, upX, upY, upZ);
        }
        return viewMatrix;
    }

    public float[] getProjectionMatrix(){
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
