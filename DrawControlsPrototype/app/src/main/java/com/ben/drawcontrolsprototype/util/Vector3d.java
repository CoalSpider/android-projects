package com.ben.drawcontrolsprototype.util;

/**
 * Created by Ben on 4/26/2017.
 */

/** Class providing static methods for basic 3d vector mathematics **/
public class Vector3d {
    /**
     * @return the addition of vector v1 to v0
     **/
    public static float[] addVV(float[] v0, float[] v1) {
        return new float[]{v0[0] + v1[0], v0[1] + v1[1], v0[2] + v1[2]};
    }

    /**
     * @return the subtraction of vector v1 from vector v0
     **/
    public static float[] subVV(float[] v0, float[] v1) {
        return new float[]{v0[0] - v1[0], v0[1] - v1[1], v0[2] - v1[2]};
    }

    /**
     * @return the dot product of vector v0 and vector v1
     **/
    public static float dotVV(float[] v0, float[] v1) {
        return v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2];
    }

    /**
     * @return vector v0 multiplied by scalar s
     **/
    public static float[] multVS(float[] v0, float s) {
        return new float[]{v0[0] * s, v0[1] * s, v0[2] * s};
    }

    /**
     * @return the cross product of vector v0 and vector v1
     **/
    public static float[] crossVV(float[] v0, float[] v1) {
        // y*z - z*y
        // z*x - x*z
        // x*y - y*x
        float x = v0[1] * v1[2] - v0[2] * v1[1];
        float y = v0[2] * v1[0] - v0[0] * v1[2];
        float z = v0[0] * v1[1] - v0[1] * v1[0];
        return new float[]{x, y, z};
    }
}
