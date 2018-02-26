package com.ben.testapp.util;

import org.junit.Test;

import static com.ben.testapp.util.Quaternion.fromAxisAngle;
import static com.ben.testapp.util.Quaternion.toAxisAngle;

/**
 * Created by Ben on 8/12/2017.
 */

public class QuaternionTest {
    @Test
    public void multiplcationTest(){
        float[] lhQ = new float[]{0.4f,0.8f,1.6f,0.2f};
        Quaternion.normalize(lhQ);
        float[] rhQ = new float[]{1.2f,0.3f,0.6f,0.9f};
        Quaternion.normalize(rhQ);
        float[] resultQuat = new float[4];
        float[] resultQuat2 = new float[4];
        float x0 = lhQ[0];
        float y0 = lhQ[1];
        float z0 = lhQ[2];
        float w0 = lhQ[3];
        float x1 = rhQ[0];
        float y1 = rhQ[1];
        float z1 = rhQ[2];
        float w1 = rhQ[3];
        // quat as Vector + angle (V,w)
        // multiplcation == w1*v0 + w0*v1 + v1 cross v0
        resultQuat2[0] = w0*x1 + w1*x0 + y0*z1 - z0*y1;
        resultQuat2[1] = w0*y1 + w1*y0 + z0*x1 - x0*z1;
        resultQuat2[2] = w0*z1 + w1*z0 + x0*y1 - y0*x1;
        resultQuat2[3] = w0*w1 - (x0*x1 + y0*y1 + z0*z1);
        // w1*w0 - v1 dot v0, w1*v0 + w0*v1 + v1 cross v0
        // w == w1*w0 - (x0*x1 + y0*y1 + z0*z1)
        // w1*v0 + w0*v1 + v1 cross v0
        // x == w1*x0 + w0*x1 + y1*z0 - z1*y0
        // y == w1*y0 + w0*y1 + z1*x0 - x1*z0
        // z == w1*z0 + w0*z1 + x1*y0 - y1*x0
        resultQuat[0] = w0*x1 + w1*x0 + y1*z0 - z1*y0;
        resultQuat[1] = w0*y1 + w1*y0 + z1*x0 - x1*z0;
        resultQuat[2] = w0*z1 + w1*z0 + x1*y0 - y1*x0;
        resultQuat[3] = w0*w1 - (x1*x0 + y1*y0 + z1*z0);

        // v1 cross v0
        // if v1 == a and v2 == b
        //x = y0*z1-z0*y1
        //y = z0*x1-x0*z1
        //z = x0*y1-y0*x1

        float[] result2 = new float[4];
        float[] axis = new float[4];
        float[] w1v0 = new float[4];
        Vector3.multiplyByScalar(w1v0,lhQ,w1);
        float[] w0v1 = new float[4];
        Vector3.multiplyByScalar(w0v1,rhQ,w0);
        float[] v1CrossV0 = new float[4];
        Vector3.crossVV(v1CrossV0,rhQ,lhQ);
        result2[0] = w1v0[0]+w0v1[0]+v1CrossV0[0];
        result2[1] = w1v0[1]+w0v1[1]+v1CrossV0[1];
        result2[2] = w1v0[2]+w0v1[2]+v1CrossV0[2];
        result2[3] = w1*w0 - Vector3.dotVV(lhQ,rhQ);

        printArr(resultQuat);
        printArr(resultQuat2);
        printArr(result2);
    }

    @Test
    public void resultTest(){
        float[] lhQ = new float[]{0.4f,0.8f,1.6f,0.2f};
        float[] rhQ = new float[]{1.2f,0.3f,0.6f,0.9f};
        float[] result = new float[4];
        Quaternion.multiplyQQ(result,lhQ,rhQ);
        printArr(result);
        Quaternion.multiplyQQ(lhQ,lhQ,rhQ);
        printArr(lhQ);
    }

    /** findings....
     * sucessive rotations of same amount(1,0,0,10degs) double
     * t1 == 1,0,0,10degs
     * t1*t1 = "---",20degs
     * (t1*t1)*t1 = "---",40degs
     * ((t1*t1)*t1)*t1 = "---",80degs
     * This is because we update the reference in every step
     * so t1 = 10
     * t1 * t1, store in t1 = 10 + 10 = 20
     * t1 is now 20
     * t1 * t1 , store in t1, 20 + 20 = 40
     * t1 is now 40
     * etc...
     * I wonder if this would be considered some sort of reference pollution/corruption
     * **/
    @Test
    public void successiveRotationTest(){
        float[] t1 = fromAxisAngle(1,0,0,toRad(10));
        float[] t2 = fromAxisAngle(1,0,0,toRad(10));
        float[] t3 = fromAxisAngle(1,0,0,toRad(10));
        float[] t4 = fromAxisAngle(1,0,0,toRad(10));
        float[] t1t2 = new float[4];
        Quaternion.multiplyQQ(t1t2,t1,t2);
        printArr(t1t2);
        printArr(fromAxisAngle(1,0,0,toRad(20)));
        float[] t1t2t3 = new float[4];
        Quaternion.multiplyQQ(t1t2t3,t1t2,t3);
        printArr(t1t2t3);
        printArr(fromAxisAngle(1,0,0,toRad(30)));
        float[] t1t2t3t4 = new float[4];
        Quaternion.multiplyQQ(t1t2t3t4,t1t2t3,t4);
        printArr(t1t2t3t4);
        printArr(fromAxisAngle(1,0,0,toRad(40)));

        System.out.println("mult with self");
        float[] t5 = fromAxisAngle(1,0,0,toRad(10));
        Quaternion.multiplyQQ(t5,t5,t5);
        printArr(t5);
        Quaternion.multiplyQQ(t5,t5,t5);
        printArr(t5);
        Quaternion.multiplyQQ(t5,t5,t5);
        printArr(t5);
        Quaternion.multiplyQQ(t5,t5,t5);
        printArr(t5);
    }

    StringBuilder builder = new StringBuilder();
    private void printArr(float[] arr){
        for(int i = 0; i < arr.length; i++){
            builder.append(arr[i]);
            builder.append(",");
        }
        System.out.println(builder.toString());
        builder.delete(0,builder.length());
    }
    private float toRad(float degs){
        return (float)Math.toRadians(degs);
    }
}
