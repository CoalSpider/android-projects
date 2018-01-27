package com.ben.testapp.util;

/**
 * Created by Ben on 7/21/2017.
 */

/**
 * Class for dealing with 3 component vectors.
 * <p>
 * The methods of this class will throw a IllegalArgumentException if any
 * vector argument has a length < 3.
 * <p>
 * If a vector with more than 3 components is given as a argument to any method
 * the vector is treated as if it only had 3 components.
 * <p>
 * Ie: every component after the 3rd is ignored.
 **/
public class Vector3 {

    private Vector3() {}

    /**
     * Adds rhv to lhv.
     *
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static void addVV(float[] resultVec, float[] lhv, float[] rhv) throws
            IllegalArgumentException {
        invalidLengthCheck(resultVec, "resultVec");
        invalidLengthCheck(lhv, "lhv");
        invalidLengthCheck(rhv, "rhv");
        resultVec[0] = lhv[0] + rhv[0];
        resultVec[1] = lhv[1] + rhv[1];
        resultVec[2] = lhv[2] + rhv[2];
    }

    /**
     * Subtracts rhv from lhv.
     *
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static void subtractVV(float[] resultVec, float[] lhv, float[] rhv)
            throws IllegalArgumentException {
        invalidLengthCheck(resultVec, "resultVec");
        invalidLengthCheck(lhv, "lhv");
        invalidLengthCheck(rhv, "rhv");
        resultVec[0] = lhv[0] - rhv[0];
        resultVec[1] = lhv[1] - rhv[1];
        resultVec[2] = lhv[2] - rhv[2];
    }

    /**
     * Dots lhv by rhv and returns the result
     *
     * @throws IllegalArgumentException if lhv and rhv are of different sizes or
     *                                  any arguments length < 3
     **/
    public static float dotVV(float[] lhv, float[] rhv) throws
            IllegalArgumentException {
        invalidLengthCheck(lhv, "lhv");
        invalidLengthCheck(rhv, "rhv");
        sameLengthCheck(lhv, rhv);
        return lhv[0] * rhv[0] + lhv[1] * rhv[1] + lhv[2] * rhv[2];
    }

    /**
     * Crosses lhv by rhv.
     *
     * @throws IllegalArgumentException if any argument has a length that < 3
     **/
    public static void crossVV(float[] resultVec, float[] lhv, float[] rhv) throws
            IllegalArgumentException {
        invalidLengthCheck(resultVec, "resultVec");
        invalidLengthCheck(lhv, "lhv");
        invalidLengthCheck(rhv, "rhv");
        //v0 = ay*bz-az*by
        //v1 = az*bx-ax*bz
        //v2 = ax*by-ay*bx
        resultVec[0] = lhv[1] * rhv[2] - lhv[2] * rhv[1];
        resultVec[1] = lhv[2] * rhv[0] - lhv[0] * rhv[2];
        resultVec[2] = lhv[0] * rhv[1] - lhv[1] * rhv[0];
    }

    /**
     * Converts the given 3 component vector to a 4 component vector with the 4th
     * component == 1.0f.
     *
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static float[] vector3ToVector4(float[] vector3) throws
            IllegalArgumentException {
        invalidLengthCheck(vector3);
        return new float[]{vector3[0], vector3[1], vector3[2], 1.0f};
    }

    /**
     * @return the length squared of the given 3 component vector
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static float lengthSquared(float[] vector3)
            throws IllegalArgumentException {
        invalidLengthCheck(vector3);
        return vector3[0] * vector3[0] + vector3[1] * vector3[1] + vector3[2] *
                vector3[2];
    }

    /**
     * @return the length of the given 3 component vector
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static float length(float[] vector3) throws IllegalArgumentException {
        invalidLengthCheck(vector3);
        return (float) Math.sqrt(lengthSquared(vector3));
    }

    /**
     * normalizes the given 3 component vector.
     *
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static void normalize(float[] vector3) throws IllegalArgumentException {
        invalidLengthCheck(vector3);
        float len = length(vector3);
        vector3[0] = vector3[0] / len;
        vector3[1] = vector3[1] / len;
        vector3[2] = vector3[2] / len;
    }

    /** inverts the given 3 component vector
     *
     * @throws IllegalArgumentException if any argument has a length < 3**/
    public static void negate(float[] vector3) throws IllegalArgumentException{
        invalidLengthCheck(vector3);
        vector3[0] = -vector3[0];
        vector3[1] = -vector3[1];
        vector3[2] = -vector3[2];
    }

    /**
     * multiplies a 3 component vector by a scalar and stores the result into
     * resultVec.
     *
     * @throws IllegalArgumentException if any argument has a length < 3
     **/
    public static void multiplyByScalar(float[] resultVec, float[] vector, float
            scalar) throws IllegalArgumentException {
        invalidLengthCheck(resultVec, "resultVec");
        invalidLengthCheck(vector, "vector");
        resultVec[0] = vector[0] * scalar;
        resultVec[1] = vector[1] * scalar;
        resultVec[2] = vector[2] * scalar;
    }

    private static void sameLengthCheck(float[] vector1, float[] vector2) {
        if (vector1.length != vector2.length)
            throw new IllegalArgumentException("vectors must be of same size");
    }

    private static void invalidLengthCheck(float[] vector) throws
            IllegalArgumentException {
        if (vector.length < 3)
            throw new IllegalArgumentException("vector length >= 3 required");
    }

    private static void invalidLengthCheck(float[] vector, String msg) throws
            IllegalArgumentException {
        if (vector.length < 3)
            throw new IllegalArgumentException(msg + " length >= 3 required");
    }
}
