package com.ben.testapp.util;

/**
 * Created by Ben on 8/10/2017.
 */

public class Interpolator {
    private static final float DOT_THRESHOLD = 0.9995f;

    private Interpolator() {}

    /**
     * Vectors passed in are normalized (if they are not already).
     * If you do not want the given vector to be normalized make sure to pass in a copy
     * <pre>
     * dot = dot(v0,v1)
     * if(abs(dot) > threshold){
     *      return {@link #nLerpQuat(float[], float[], float)};
     * }else{
     *      if(dot < 0){
     *          flip one vector and reverse the dot product;
     *      }
     *      clamp(dot,-1,1)
     *      theta_0 = acos(dot)
     *      theta = theta_0*t;
     *      v2 = v1 - v0*dot
     *      return v0*cos(theta) + v2*sin(theta)
     * }
     * </pre>
     **/
    public static float[] sLerpQuat(float[] v0, float[] v1, float t) {
        // Only normalize quaternions are valid rotations.
        // Normalize to avoid undefined behavior. Unless were already notmalized
        if (Quaternion.lengthSquared(v0) != 1) {
            Quaternion.normalize(v0);
        }
        if (Quaternion.lengthSquared(v1) != 1) {
            Quaternion.normalize(v1);
        }

        // Compute the cosine of the angle between the two vectors.
        float dot = Quaternion.dotQQ(v0, v1);

        if (Math.abs(dot) > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.
            return nLerpQuat(v0, v1, t);
        }

        float[] v1Copy = {v1[0], v1[1], v1[2], v1[3]};
        // If the dot product is negative, the quaternions
        // have opposite handed-ness and slerp won't take
        // the shorter path. Fix by reversing one quaternion
        if (dot < 0.0f) {
            Quaternion.multiplyQS(v1Copy, v1Copy, -1);
            dot = -dot;
        }

        Math.max(-1, Math.min(dot, 1));          // Robustness: Stay within domain of
        // acos()
        float theta_0 = (float) Math.acos(dot); // theta_0 = angle between input vectors
        float theta = theta_0 * t;               // theta = angle between v0 and result
        float cosTheta = (float) Math.cos(theta);
        float sinTheta = (float) Math.sin(theta);
        // Quaternion v2 = v1 – v0*dot;
        // v0*cos(theta) + v2*sin(theta)
        float[] result = new float[4];
        result[0] = v0[0] * cosTheta + (v1Copy[0] - v0[0] * dot) * sinTheta;
        result[1] = v0[1] * cosTheta + (v1Copy[1] - v0[1] * dot) * sinTheta;
        result[2] = v0[2] * cosTheta + (v1Copy[2] - v0[2] * dot) * sinTheta;
        result[3] = v0[3] * cosTheta + (v1Copy[3] - v0[3] * dot) * sinTheta;
        return result;
    }

    /**
     * @return return v0 + t*(v1-v0);
     **/
    public static float[] lerpQuat(float[] v0, float[] v1, float t) {
        float dot = Quaternion.dotQQ(v0, v1);
        float[] v1Copy = new float[4];
        System.arraycopy(v1,0,v1Copy,0,v1Copy.length);
        // If the dot product is negative, the quaternions
        // have opposite handed-ness and slerp won't take
        // the shorter path. Fix by reversing one quaternion
        if (dot < 0.0f) {
            Quaternion.multiplyQS(v1Copy, v1Copy, -1);
        }
        float[] result = new float[4];
        result[0] = v0[0] + t * (v1Copy[0] - v0[0]);
        result[1] = v0[1] + t * (v1Copy[1] - v0[1]);
        result[2] = v0[2] + t * (v1Copy[2] - v0[2]);
        result[3] = v0[3] + t * (v1Copy[3] - v0[3]);
        return result;
    }

    /**
     * @return normalized {@link #lerpQuat(float[], float[], float)}
     **/
    public static float[] nLerpQuat(float[] start, float[] end, float t) {
        float[] lerp = lerpQuat(start, end, t);
        Quaternion.normalize(lerp);
        return lerp;
    }
    /**
     * @return normalized {@link #lerpQuat(float[], float[], float)}
     **/
    public static float[] nLerpQuatLockY(float[] start, float[] end, float t) {
        float[] lerp = lerpQuat(start, end, t);
        lerp[2] = start[2];
        Quaternion.normalize(lerp);
        return lerp;
    }

    /**
     * Uses the same formula as {@link #sLerpQuat(float[], float[], float)} with 3d
     * instead of 4d vectors
     **/
    public static float[] sLerpVec3(float[] v0, float[] v1, float t) {
        if (Vector3.lengthSquared(v0) != 1) {
            Vector3.normalize(v0);
        }
        if (Vector3.lengthSquared(v1) != 1) {
            Vector3.normalize(v1);
        }

        float dot = Vector3.dotVV(v0, v1);

        if (Math.abs(dot) > DOT_THRESHOLD) {
            return nLerpVec3(v0, v1, t);
        }

        float[] v1Copy = {v1[0], v1[1], v1[2]};
        if (dot < 0.0f) {
            Vector3.multiplyByScalar(v1Copy, v1Copy, -1);
            dot = -dot;
        }

        Math.max(-1, Math.min(dot, 1));
        float theta_0 = (float) Math.acos(dot);
        float theta = theta_0 * t;
        float cosTheta = (float) Math.cos(theta);
        float sinTheta = (float) Math.sin(theta);
        float[] result = new float[4];
        result[0] = v0[0] * cosTheta + (v1Copy[0] - v0[0] * dot) * sinTheta;
        result[1] = v0[1] * cosTheta + (v1Copy[1] - v0[1] * dot) * sinTheta;
        result[2] = v0[2] * cosTheta + (v1Copy[2] - v0[2] * dot) * sinTheta;
        return result;
    }

    /**
     * @return normalized {@link #lerpVec3(float[], float[], float)}
     **/
    public static float[] nLerpVec3(float[] v0, float[] v1, float t) {
        float[] lerp = lerpVec3(v0, v1, t);
        Vector3.normalize(lerp);
        return lerp;
    }

    /**
     * uses the same formula as {@link #lerpQuat(float[], float[], float)} with 3d
     * instead of 4d vectors
     **/
    public static float[] lerpVec3(float[] v0, float[] v1, float t) {
        float[] result = new float[3];
        result[0] = v0[0] + t * (v1[0] - v0[0]);
        result[1] = v0[1] + t * (v1[1] - v0[1]);
        result[2] = v0[2] + t * (v1[2] - v0[2]);
        return result;
    }


    /**
     * function:
     * <pre>
     * t2 = (1 - cos(t*PI)/2;
     * return v0*(1-t2) + v1*t2
     * </pre>
     **/
    public static float[] cosineVec3(float[] v0, float[] v1, float t) {
        float t2 = (float) (1 - Math.cos(t * Math.PI)) / 2.0f;
        float[] result = new float[3];
        result[0] = v0[0] * (1 - t2) + v1[0] * t2;
        result[1] = v0[1] * (1 - t2) + v1[1] * t2;
        result[2] = v0[2] * (1 - t2) + v1[2] * t2;
        return result;
    }

    /* TODO: implement
     * With the following coefficients we get a Catmull-Rom spline
     * <pre>
     *     a0 = -0.5*v0 + 1.5*v1 - 1.5*v2 + 0.5*v3;
     *     a1 = v0 - 2.5*v1 + 2*v2 - 0.5*v3;
     *     a2 = -0.5*v0 + 0.5*v2;
     *     a3 = v1;
     * </pre>
     */
    /**
     * function:
     * <pre>
     *     float a0,a1,a2,a3,t2
     *     t2 = t*t;
     *     a0 = v3 - v2 - v0 + v1;
     *     a1 = v0 - v1 - a0;
     *     a2 = v2 - v0;
     *     a3 = v1;
     *     return a0*t*t2 + a1*t2 + a2*t + a3
     * </pre>
     **/
    public static float[] cubicVec3(float[] v0, float[] v1, float[] v2,
                                               float[] v3, float t) {
        float t2 = t*2;
        float[] a0 = new float[3];
        Vector3.subtractVV(a0,v3,v2);
        Vector3.subtractVV(a0,a0,v0);
        Vector3.addVV(a0,a0,v1);
        float[] a1 = new float[3];
        Vector3.subtractVV(a1,v0,v1);
        Vector3.subtractVV(a1,a1,a0);
        float[] a2 = new float[3];
        Vector3.subtractVV(a2,v1,v0);
        float[] a3 = {v1[0],v1[1],v1[2]};
        float[] result = new float[3];
        Vector3.multiplyByScalar(a0,a0,t*t2);
        Vector3.multiplyByScalar(a1,a1,t2);
        Vector3.multiplyByScalar(a2,a2,t);
        Vector3.addVV(result,a0,a1);
        Vector3.addVV(result,result,a2);
        Vector3.addVV(result,result,a3);
        return result;
    }

    /** function:
     * <pre>
     * float m0,m1,t2,t3;
     * float a0,a1,a2,a3;
     *
     * t2 = t * t;
     * t3 = t2 * t;
     * m0  = (v1-v0)*(1+bias)*(1-tension)/2;
     * m0 += (v2-v1)*(1-bias)*(1-tension)/2;
     * m1  = (v2-v1)*(1+bias)*(1-tension)/2;
     * m1 += (v3-v2)*(1-bias)*(1-tension)/2;
     * a0 =  2*t3 - 3*t2 + 1;
     * a1 =    t3 - 2*t2 + t;
     * a2 =    t3 -   t2;
     * a3 = -2*t3 + 3*t2;
     *
     * return a0*v1 + a1*m0 + a2*m1 + a3*v2;
     * </pre>**/
    private static float[] hermiteVec3(float[] v0, float[] v1, float[] v2, float[] v3, float t, float tension, float bias){
        float t2 = t*t;
        float t3 = t2*t;
        float constant1 = (1-tension) / 2.0f;
        float constant2 = (1+bias)*constant1;
        float constant3 = (1-bias)*constant1;
        float[] m0a = new float[3];
        Vector3.subtractVV(m0a,v1,v0);
        Vector3.multiplyByScalar(m0a,m0a,constant2);
        float[] m0b = new float[3];
        Vector3.subtractVV(m0b,v2,v1);
        Vector3.multiplyByScalar(m0b,m0b,constant3);
        float[] m0 = new float[3];
        Vector3.addVV(m0,m0a,m0b);
        float[] m1a = new float[3];
        Vector3.subtractVV(m1a,v2,v1);
        Vector3.multiplyByScalar(m1a,m1a,constant2);
        float[] m1b = new float[3];
        Vector3.subtractVV(m1b,v3,v2);
        Vector3.multiplyByScalar(m1b,m1b,constant3);
        float[] m1 = new float[3];
        Vector3.addVV(m1,m1a,m1b);
        float a0 =  2*t3 - 3*t2 + 1;
        float a1 =    t3 - 2*t2 + t;
        float a2 =    t3 -   t2;
        float a3 = -2*t3 + 3*t2;
        float[] result = new float[3];
        result[0] = a0*v1[0] + a1*m0[0] + a2*m1[0] + a3*v2[0];
        result[1] = a0*v1[1] + a1*m0[1] + a2*m1[1] + a3*v2[1];
        result[2] = a0*v1[2] + a1*m0[2] + a2*m1[2] + a3*v2[2];
        result[3] = a0*v1[3] + a1*m0[3] + a2*m1[3] + a3*v2[3];
        return result;
    }
}
