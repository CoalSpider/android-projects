package com.ben.testapp.util;

/**
 * Created by Ben on 8/7/2017.
 */

/** A simple container class for orientation using a pitch, roll, and yaw,
 * To keep with opengl conventions x is left/right, y is up/down, z is forward/backwards
 * This means pitch is the x axis, yaw is the y axis, and roll is the z axis
 * Default value is 0 pitch, 0 yaw, 0 roll**/
public class Gyroscope {
    private static final float twoPI=(float)(Math.PI*2.0);
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private float roll = 0.0f;

    /** @return rotation around x axis in radians **/
    public float getPitch() {
        return pitch;
    }

    /** @return rotation around y axis in radians **/
    public float getYaw() {
        return yaw;
    }

    /** @return rotation around z axis in radians **/
    public float getRoll() {
        return roll;
    }

    /**
     * This method keeps the input between 0 rad and 2PI rad. Ex: 8 rad -> 3.4 rad
     * @param pitch sets the rotation around the x axis, assumed to be in radians **/
    public void setPitch(float pitch) {
        this.pitch = pitch%twoPI;
    }

    /**
     * This method keeps the input between 0 rad and 2PI rad. Ex: 8 rad -> 3.4 rad
     * @param yaw sets the rotation around the y axis, assumed to be in radians **/
    public void setYaw(float yaw) {
        this.yaw = yaw%twoPI;
    }

    /**
     * This method keeps the input between 0 rad and 2PI rad. Ex: 8 rad -> 3.4 rad
     * @param roll sets the rotation around the z axis, assumed to be in radians **/
    public void setRoll(float roll) {
        this.roll = roll%twoPI;
    }
}