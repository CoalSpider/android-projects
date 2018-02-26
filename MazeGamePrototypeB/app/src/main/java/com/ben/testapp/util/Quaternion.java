package com.ben.testapp.util;

/**
 * Created by Ben on 7/21/2017.
 */

import android.util.Log;

/**
 * A custom quaternion class.
 * Defined as float[]{x, y, z ,w};
 * <p>
 * All methods that take a float[4] (Quaternion) as a argument will throw a
 * IllegalArgumentException
 * if the length of the argument < 4
 *
 **/
/** TODO: write test cases to check that methods return correct results**/
public class Quaternion {
    private Quaternion() {}

    /** lhQ + rhQ.
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void addQQ(float[] resultQuat, float[] lhQ, float[] rhQ)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(lhQ,"lhQ");
        sizeCheck(rhQ,"rhQ");
        resultQuat[0] = lhQ[0] + rhQ[0];
        resultQuat[1] = lhQ[1] + rhQ[1];
        resultQuat[2] = lhQ[2] + rhQ[2];
        resultQuat[3] = lhQ[3] + rhQ[3];
    }
    /** lhQ - rhQ.
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void subtractQQ(float[] resultQuat, float[] lhQ, float[] rhQ)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(lhQ,"lhQ");
        sizeCheck(rhQ,"rhQ");
        resultQuat[0] = lhQ[0] - rhQ[0];
        resultQuat[1] = lhQ[1] - rhQ[1];
        resultQuat[2] = lhQ[2] - rhQ[2];
        resultQuat[3] = lhQ[3] - rhQ[3];
    }

    /** lhQ * rhQ. 
     *  lhQ * rhQ != rhQ * lhQ
     *  Logically equivilent to multiplcation by rhQ then lhQ
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void multiplyQQ(float[] resultQuat, float[] lhQ, float[] rhQ)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(lhQ,"lhQ");
        sizeCheck(rhQ,"rhQ");
        if(lengthSquared(lhQ)!=1){
            normalize(lhQ);
        }
        if(lengthSquared(rhQ)!=1){
            normalize(rhQ);
        }
        float[] result = new float[4];
        float x0 = lhQ[0];
        float y0 = lhQ[1];
        float z0 = lhQ[2];
        float w0 = lhQ[3];
        float x1 = rhQ[0];
        float y1 = rhQ[1];
        float z1 = rhQ[2];
        float w1 = rhQ[3];
        // quat as Vector + angle (V,w)
        // multiplcation == w1*v0 + w0*v1 + v1 cross v0, w1*w0 - v1 dot v0
        // x == w1*x0 + w0*x1 + y1*z0 - z1*y0
        // y == w1*y0 + w0*y1 + z1*x0 - x1*z0
        // z == w1*z0 + w0*z1 + x1*y0 - y1*x0
        // w == w1*w0 - (x0*x1 + y0*y1 + z0*z1)
        result[0] = w0*x1 + w1*x0 + y1*z0 - z1*y0;
        result[1] = w0*y1 + w1*y0 + z1*x0 - x1*z0;
        result[2] = w0*z1 + w1*z0 + x1*y0 - y1*x0;
        result[3] = w0*w1 - (x1*x0 + y1*y0 + z1*z0);
        
        // v1 cross v0
        // if v0 == a and v1 == b
        //x = y1*z0-z1*y0
        //y = z1*x0-x1*z0
        //z = x1*y0-y1*x0

        System.arraycopy(result,0,resultQuat,0,result.length);
    }

    /** Multiplies quaternion by scalar
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void multiplyQS(float[] resultQuat, float[] quaternion, float scalar)
            throws IllegalArgumentException {
        sizeCheck(resultQuat,"resultQuat");
        sizeCheck(quaternion,"quaternion");
        resultQuat[0] = quaternion[0]*scalar;
        resultQuat[1] = quaternion[1]*scalar;
        resultQuat[2] = quaternion[2]*scalar;
        resultQuat[3] = quaternion[3]*scalar;
    }
    /** @return the dot product of two quaternions
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float dotQQ(float[] quaternion1, float[] quaternion2){
        sizeCheck(quaternion1);
        sizeCheck(quaternion2);
        float x = quaternion1[0]*quaternion2[0];
        float y = quaternion1[1]*quaternion2[1];
        float z = quaternion1[2]*quaternion2[2];
        float w = quaternion1[3]*quaternion2[3];
        return x+y+z+w;
    }

    /** conjugates the given quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void conjugate(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        quaternion[0] = -quaternion[0];
        quaternion[1] = -quaternion[1];
        quaternion[2] = -quaternion[2];
    //    quaternion[3] = quaternion[3];
    }

    /** inverts the given quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void invert(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        float oneOverNormSquared = 1/ lengthSquared(quaternion);
        quaternion[0] = -quaternion[0]*oneOverNormSquared;
        quaternion[1] = -quaternion[1]*oneOverNormSquared;
        quaternion[2] = -quaternion[2]*oneOverNormSquared;
    //    resultQuat[3] = quaternion[3]*oneOverNormSquared;
    }

    /** @return the length of the quaternion ("norm")
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float length(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        return (float)Math.sqrt(lengthSquared(quaternion));
    }

    /** @return the length squared of the quaternion ("norm squared")
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float lengthSquared(float[] quaternion)
            throws IllegalArgumentException {
        sizeCheck(quaternion);
        return
                quaternion[0]*quaternion[0] +
                quaternion[1]*quaternion[1] +
                quaternion[2]*quaternion[2] +
                quaternion[3]*quaternion[3];
    }

    /** units the given quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void normalize(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion,"resultQuat");
        sizeCheck(quaternion,"quaternion");
        float norm = length(quaternion);
        quaternion[0] = quaternion[0]/norm;
        quaternion[1] = quaternion[1]/norm;
        quaternion[2] = quaternion[2]/norm;
        quaternion[3] = quaternion[3]/norm;
    }

    /** reciprocals the given quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void reciprocal(float[] quat) throws IllegalArgumentException {
        sizeCheck(quat);
        float[] conjugateOfQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        conjugate(conjugateOfQuat);
        float normSquared = lengthSquared(quat);
        quat[0] = conjugateOfQuat[0]/normSquared;
        quat[1] = conjugateOfQuat[1]/normSquared;
        quat[2] = conjugateOfQuat[2]/normSquared;
        quat[3] = conjugateOfQuat[3]/normSquared;
    }

    /** sets the given quaternion to the identity (0,0,0,1)
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static void identity(float[] quaternion) throws IllegalArgumentException {
        sizeCheck(quaternion);
        quaternion[0] = 0;
        quaternion[1] = 0;
        quaternion[2] = 0;
        quaternion[3] = 1;
    }

    /** @return a 4x4 rotation matrix in row major order from the given quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float[] toRotationMatrixRM(float[] quat)
            throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        normalize(unitQuat);

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
    /** @return a 4x4 rotation matrix in row major order from the given
     * quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float[] toRotationMatrixRM2(float[] quat)
            throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        normalize(unitQuat);

        float x = unitQuat[0];
        float y = unitQuat[1];
        float z = unitQuat[2];
        float w = unitQuat[3];

        return new float[]{
                w*w + x*x - y*y - z*z,  2*y*x - 2*w*z,          2*z*x + 2*w*y,          0,
                2*x*y + 2*w*z,          w*w - x*x + y*y - z*z,  2*z*y - 2*w*x,          0,
                2*x*z - 2*w*y,          2*y*z + 2*w*x,          w*w - x*x - y*y + z*z,  0,
                0,                      0,                      0,                      1};
    }

    /** @return a 4x4 rotation matrix in column major order from the given
     * quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float[] toRotationMatrixCM(float[] quat)
            throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        normalize(unitQuat);

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
    /** @return a 4x4 rotation matrix in column major order from the given
     * quaternion
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float[] toRotationMatrixCM2(float[] quat)
            throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        normalize(unitQuat);

        float x = unitQuat[0];
        float y = unitQuat[1];
        float z = unitQuat[2];
        float w = unitQuat[3];

        return new float[]{
                w*w + x*x - y*y - z*z,  2*x*y + 2*w*z,      2*x*z - 2*w*y,      0,
                2*y*x - 2*w*z,      w*w - x*x + y*y - z*z,  2*y*z + 2*w*x,      0,
                2*z*x + 2*w*y,      2*z*y - 2*w*x,      w*w - x*x - y*y + z*z,  0,
                0,                  0,                  0,                      1};
    }

    /** @return a axis angle where f[0,1,2] == axis and f[3] == angle
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float[] toAxisAngle(float[] quat) throws IllegalArgumentException {
        sizeCheck(quat);
        // make sure were normalized
        float[] unitQuat = new float[]{quat[0],quat[1],quat[2],quat[3]};
        normalize(unitQuat);

        float x = unitQuat[0];
        float y = unitQuat[1];
        float z = unitQuat[2];
        float w = unitQuat[3];

        float angle = (float)(2 * Math.acos(w));
        float tmp = (float)Math.sqrt(1 - w*w);
        if(tmp < 1e-5){
           // Log.e("Quaternion","w == 0 returning new float[]{x,y,z,0");
            float[] result = new float[]{x,y,z,0};
            Vector3.normalize(result);
            return result;
        } else {
            float[] result = new float[]{x/tmp,y/tmp,z/tmp,angle};
            Vector3.normalize(result);
            return result;
        }
    }

    /** axisAngle is assumed to be in the form x,y,z,angle
     * @return a quaternion in the form x + yi + zj + wk from a axis angle
     * @throws IllegalArgumentException if any arguments size < 4 **/
    public static float[] fromAxisAngle(float[] axisAngle)
            throws IllegalArgumentException {
        sizeCheck(axisAngle);
        // make sure axis is normalized
        // only touches the first 3 components as per the specification
        Vector3.normalize(axisAngle);

        float halfAngle = axisAngle[3]/2;
        float sinHalfAngle = (float)Math.sin(halfAngle);
        float cosHalfAngle = (float)Math.cos(halfAngle);

        float x = axisAngle[0] * sinHalfAngle;
        float y = axisAngle[1] * sinHalfAngle;
        float z = axisAngle[2] * sinHalfAngle;

        float[] result = new float[]{x,y,z,cosHalfAngle};
        Quaternion.normalize(result);
        return result;
    }

    /** @return a quaternion in the form x + yi + zj + wk from a axis angle **/
    public static float[] fromAxisAngle(float x, float y, float z, float angleInRadians){
        return fromAxisAngle(new float[]{x,y,z,angleInRadians});
    }

    /** @return a quaternion in the form x + yi + zj + wk from a axis angle **/
    public static float[] fromAxisAngle(float[] axis, float angleInRadians){
        return fromAxisAngle(new float[]{axis[0],axis[1],axis[2],angleInRadians});
    }

    /** @param eulerAngle assumed to be in the form pitch,yaw,roll (bank,attitude,heading)
     * @return a quaternion in the form x + yi + zj + wk from the given euler angle**/
    public static float[] fromEulerAngle(float[] eulerAngle){
        float halfRoll = eulerAngle[2]/2.0f;
        float halfYaw = eulerAngle[1]/2.0f;
        float halfPitch = eulerAngle[0]/2.0f;
        float c1 = (float)Math.cos(halfRoll);
        float c2 = (float)Math.cos(halfYaw);
        float c3 = (float)Math.cos(halfPitch);
        float s1 = (float)Math.sin(halfRoll);
        float s2 = (float)Math.sin(halfYaw);
        float s3 = (float)Math.sin(halfPitch);
        float x = s1*s2*c3 + c1*c2*s3;
        float y = s1*c2*c3 + c1*s2*s3;
        float z = c1*s2*c3 - s1*c2*s3;
        float w = c1*c2*c3 - s1*s2*s3;
        return new float[]{x,y,z,w};
    }

    /** Converts a quaternion to a euler angle.
     * @param quaternion is assumed to be in form x,y,z,w.
     * @return a Euler angle in form pitch,yaw,roll (bank,attitude,heading)**/
    public static float[] toEulerAngle(float[] quaternion){
        sizeCheck(quaternion);
        float x = quaternion[0];
        float y = quaternion[1];
        float z = quaternion[2];
        float w = quaternion[3];
        float sqx = x*x;
        float sqy = y*y;
        float sqz = z*z;
        float sqw = w*w;
        float unit = sqx + sqy + sqz + sqw;
        float test = x*y + z*w;
        // heading, attitude, bank,
        float roll,yaw,pitch;
        if(test > 0.499*unit){ // singularity at north pole
            roll = (float)(2.0*Math.atan2(x,w));
            yaw = (float)(Math.PI/2.0);
            pitch = 0;
        } else if(test < -0.499*unit){ // singularity at south pole
            roll = (float)(-2*Math.atan2(x,w));
            yaw = (float)-Math.PI/2;
            pitch = 0;
        } else {
            roll = (float)Math.atan2(2*y*w - 2*x*z, sqx - sqy - sqz + sqw);
            yaw = (float)Math.asin(2*test/unit);
            pitch = (float)Math.atan2(2*x*w - 2*y*z, -sqx + sqy - sqz + sqw);
        }
        return new float[]{pitch,yaw,roll};
    }

    /** formula =
     * t = 2*cross(q.xyz,v)
     * result = v + q.w * t + cross(q.xyz,t)
     *
     * resultV3 = vector3 * quaternion **/
    public static void multiplyQV(float[] resultV3, float[] quaternion, float[] vec3){
        float[] t = new float[3];
        Vector3.crossVV(t,quaternion,vec3);
        Vector3.multiplyByScalar(t,t,2);

        float[] cross = new float[3];
        Vector3.crossVV(cross,quaternion,t);

        // the user might pass the same vector in for both resultV3 and vec3
        // so we need a temporary result
        float[] result = new float[3];
        Vector3.multiplyByScalar(result,t,quaternion[3]);
        Vector3.addVV(result,result,vec3);
        Vector3.addVV(result,result,cross);
        System.arraycopy(result,0,resultV3,0,resultV3.length);
    }


    private static void sizeCheck(float[] arr) throws IllegalArgumentException {
        if (arr.length < 4)
            throw new IllegalArgumentException("array length must be >= 4");
    }

    private static void sizeCheck(float[] arr, String msg)
            throws  RuntimeException{
        if (arr.length < 4)
            throw new IllegalArgumentException(msg +
                    " array length must be >= 4");
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
    // q1 = (v1/length^2,s1/length^2)
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