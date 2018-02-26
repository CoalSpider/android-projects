package com.ben.lightingtest.util;

/**
 * Created by Ben on 7/21/2017.
 */

import android.opengl.Matrix;
import android.util.Log;

/**
 * A custom quaternion class.
 * Defined as float[]{x, y, z ,w};
 * <p>
 * All methods that take a float[4] (Quaternion) as a argument will throw a
 * IllegalArgumentException
 * if the length of the argument != 4
 **/
public class Quaternion {
    private Quaternion() {}

    /** Adds rhQ to lhQ.
     * @throws IllegalArgumentException if any arguments size != 4 **/
    private static void addQQ(float[] resultQuat, float[] lhQ, float[] rhQ)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(lhQ,"lhQ");
        sizeCheck(rhQ,"rhQ");
        resultQuat[0] = lhQ[0] + rhQ[0];
        resultQuat[1] = lhQ[1] + rhQ[1];
        resultQuat[2] = lhQ[2] + rhQ[2];
        resultQuat[3] = lhQ[3] + rhQ[3];
    }
    /** Subtracts rhQ from lhQ.
     * @throws IllegalArgumentException if any arguments size != 4 **/
    private static void subtractQQ(float[] resultQuat, float[] lhQ, float[] rhQ)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(lhQ,"lhQ");
        sizeCheck(rhQ,"rhQ");
        resultQuat[0] = lhQ[0] - rhQ[0];
        resultQuat[1] = lhQ[1] - rhQ[1];
        resultQuat[2] = lhQ[2] - rhQ[2];
        resultQuat[3] = lhQ[3] - rhQ[3];
    }

    /** Multiplies lhQ by rhQ.
     * @throws IllegalArgumentException if any arguments size != 4 **/
    private static void multiplyQQ(float[] resultQuat, float[] lhQ, float[] rhQ)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(lhQ,"lhQ");
        sizeCheck(rhQ,"rhQ");
        float x1 = lhQ[0];
        float y1 = lhQ[1];
        float z1 = lhQ[2];
        float w1 = lhQ[3];
        float x2 = rhQ[0];
        float y2 = rhQ[1];
        float z2 = rhQ[2];
        float w2 = rhQ[3];
        resultQuat[0] = w1*x2 + w2*x1 + y1*z2 - z1*y2;
        resultQuat[1] = w1*y2 + w2*y1 + z1*x2 - x1*z2;
        resultQuat[2] = w1*z2 + w2*z1 + x1*y2 - y1*x2;
        resultQuat[3] = w1*w2 - (x1*x2 + y1*y2 + z1*z2);
    }

    /** Multiplies quaternion by scalar
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static void multiplyQS(float[] resultQuat, float[] quaternion, float scalar)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(quaternion,"quaternion");
        resultQuat[0] = quaternion[0]*scalar;
        resultQuat[1] = quaternion[1]*scalar;
        resultQuat[2] = quaternion[2]*scalar;
        resultQuat[3] = quaternion[3]*scalar;
    }

    /** conjugates the given quaternion
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static void conjugate(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        quaternion[0] = -quaternion[0];
        quaternion[1] = -quaternion[1];
        quaternion[2] = -quaternion[2];
    //    quaternion[3] = quaternion[3];
    }

    /** inverts the given quaternion
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static void invert(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        float oneOverNormSquared = 1/normSquared(quaternion);
        quaternion[0] = -quaternion[0]*oneOverNormSquared;
        quaternion[1] = -quaternion[1]*oneOverNormSquared;
        quaternion[2] = -quaternion[2]*oneOverNormSquared;
    //    resultQuat[3] = quaternion[3]*oneOverNormSquared;
    }

    /** @return the normal of the quaternion ("length")
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static float norm(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        return (float)Math.sqrt(normSquared(quaternion));
    }

    /** @return the normal squared of the quaternion ("length squared")
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static float normSquared(float[] quaternion)
            throws IllegalArgumentException {
        sizeCheck(quaternion);
        return
                quaternion[0]*quaternion[0] +
                quaternion[1]*quaternion[1] +
                quaternion[2]*quaternion[2] +
                quaternion[3]*quaternion[3];
    }

    /** units the given quaternion
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static void unit(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion,"resultQuat");
        sizeCheck(quaternion,"quaternion");
        float norm = norm(quaternion);
        quaternion[0] = quaternion[0]/norm;
        quaternion[1] = quaternion[1]/norm;
        quaternion[2] = quaternion[2]/norm;
        quaternion[3] = quaternion[3]/norm;
    }

    /** reciprocals the given quaternion
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static void reciprocal(float[] quat) throws IllegalArgumentException {
        sizeCheck(quat);
        float[] conjugateOfQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        conjugate(conjugateOfQuat);
        float normSquared = normSquared(quat);
        quat[0] = conjugateOfQuat[0]/normSquared;
        quat[1] = conjugateOfQuat[1]/normSquared;
        quat[2] = conjugateOfQuat[2]/normSquared;
        quat[3] = conjugateOfQuat[3]/normSquared;
    }

    /** sets the given quaternion to the identity (0,0,0,1)
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static void identity(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        quaternion[0] = 0;
        quaternion[1] = 0;
        quaternion[2] = 0;
        quaternion[3] = 1;
    }

    /** @return a 4x4 rotation matrix in row major order from the given quaternion
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static float[] toRotationMatrixRM(float[] quat)
            throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        unit(unitQuat);

        float x = unitQuat[0];
        float y = unitQuat[1];
        float z = unitQuat[2];
        float w = unitQuat[3];

        return new float[]{
                1 - 2*y*y - 2*z*z,  2*x*y - 2*z*w,      2*x*z + 2*y*w,      0,
                2*x*y + 2*z*w,      1 - 2*x*x - 2*z*z,  2*y*z - 2*x*w,      0,
                2*x*z - 2*y*w,      2*y*z + 2*x*w,      1 - 2*x*x - 2*y*y,  0,
                0,                  0,                  0,                  1
        };
    }

    /** @return a 4x4 rotation matrix in column major order from the given
     * quaternion
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static float[] toRotationMatrixCM(float[] quat)
            throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        unit(unitQuat);

        float x = unitQuat[0];
        float y = unitQuat[1];
        float z = unitQuat[2];
        float w = unitQuat[3];

        return new float[]{
                1 - 2*y*y - 2*z*z,  2*x*y + 2*z*w,      2*x*z - 2*y*w,      0,
                2*x*y - 2*z*w,      1 - 2*x*x - 2*z*z,  2*y*z + 2*x*w,      0,
                2*x*z + 2*y*w,      2*y*z - 2*x*w,      1 - 2*x*x - 2*y*y,  0,
                0,                  0,                  0,                  1};
    }

    /** @return a axis angle where f[0,1,2] == axis and f[3] == angle
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static float[] toAxisAngle(float[] quat) throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        unit(unitQuat);

        float x = unitQuat[0];
        float y = unitQuat[1];
        float z = unitQuat[2];
        float w = unitQuat[3];

        if(w==0){
            Log.e("Quaternion","Quat(x,y,z,0) found returning axisAngle(x,y,z,180degs");
            return new float[]{x,y,z,(float)Math.toRadians(180)};
        }

        float tmp = (float)Math.sqrt(1 - w*w);
        if(tmp == 0){
            Log.e("Quaternion","Quat(0,0,0,1) found returning axisAngle (0,0,0,0) ");
            return new float[]{0,0,0,0};
        }

        float angle = 2*(float)Math.acos(w);
        return new float[]{x/tmp, y/tmp, z/tmp, angle};
    }

    /** The argument is assumed to be in the form x,y,z,angle
     * @return a quaternion in the form x + yi + zj + wk from a axis angle
     * @throws IllegalArgumentException if any arguments size != 4 **/
    public static float[] fromAxisAngle(float[] axisAngle)
            throws IllegalArgumentException {
        sizeCheck(axisAngle);
        // make sure were normalized
        float[] unitAxis =
                new float[]{axisAngle[0],axisAngle[1],axisAngle[2],axisAngle[3]};
        unit(unitAxis);

        float angle = axisAngle[3]/2f;
        if(angle == 0){
            Log.e("Quaternion","axisAngle(x,y,z,0) returning Quat(0,0,0,1)");
            return new float[]{0,0,0,1};
        }

        float cosHalfAngle = (float)Math.cos(angle);
        float sinHalfAngle = (float)Math.sin(angle);

        float x = axisAngle[0]/sinHalfAngle;
        float y = axisAngle[1]/sinHalfAngle;
        float z = axisAngle[2]/sinHalfAngle;

        return new float[]{x,y,z,cosHalfAngle};
    }

    /** @return a quaternion in the form x + yi + zj + wk from a axis angle **/
    public static float[] fromAxisAngle(float x, float y, float z,
                                        float angleInRadians){
        return fromAxisAngle(new float[]{x,y,z,angleInRadians});
    }


    private static void sizeCheck(float[] quaternion) throws IllegalArgumentException {
        if (quaternion.length != 4)
            throw new RuntimeException("non quaternion passed in. length != 4");
    }

    private static void sizeCheck(float[] quaternion, String msg)
            throws  RuntimeException{
        if (quaternion.length != 4)
            throw new RuntimeException("non quaternion passed in. " + msg +
                    " length != 4");
    }

    // reference comments yay!
    // where v == xyz vector and s == w
    // form is x y z w
        // addition
    // q1*q2 == (v1+v2,s1+s2);
        // subtraction
    // q1*q2 == (v1-v2,s1-s2);
        // multiplication
    // q1*q2 == (s1*v2 + s2*v1 + v1 cross v2,s1*s2 - v1 dot v2, );
        // conjugate
    // q1' == (-1*v1,s1);
        // inversion
    // q1 == (1/v1,1/s1)
        // reciprocal
    // q1 = (v1/norm^2,s1/norm^2)
        // identity
    // q1 = (0,0,0,1)
        // to row major rotation matrix
    /* 1 - 2*y*y - 2*z*z,  2*x*y - 2*z*w,      2*x*z + 2*y*w,      0,
       2*x*y + 2*z*w,      1 - 2*x*x - 2*z*z,  2*y*z - 2*x*w,      0,
       2*x*z - 2*y*w,      2*y*z + 2*x*w,      1 - 2*x*x - 2*y*y,  0,
       0,                  0,                  0,                  1*/
        // to column major rotation matrix
    /* 1 - 2*y*y - 2*z*z,  2*x*y + 2*z*w,      2*x*z - 2*y*w,      0,
       2*x*y - 2*z*w,      1 - 2*x*x - 2*z*z,  2*y*z + 2*x*w,      0,
       2*x*z + 2*y*w,      2*y*z - 2*x*w,      1 - 2*x*x - 2*y*y,  0,
       0,                  0,                  0,                  1*/
        // to axis angle
    // aa1 = (v1/sqrt(1-w*w), 2*acos(s1))
        // from axis angle
    // sq = v1/sin(s1/2), cos(s1/2)
}