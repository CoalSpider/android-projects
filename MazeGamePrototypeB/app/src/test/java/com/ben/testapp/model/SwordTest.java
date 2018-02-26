package com.ben.testapp.model;

import com.ben.testapp.util.PlayerCamera;
import com.ben.testapp.util.Quaternion;
import com.ben.testapp.util.Vector3;

import org.junit.Before;
import org.junit.Test;

import static com.ben.testapp.util.Quaternion.*;

/**
 * Created by Ben on 8/9/2017.
 */
public class SwordTest {
    PlayerCamera playerCamera;

    @Before
    public void setUp() {
        playerCamera = new PlayerCamera();
    }

    @Test
    public void quatCompositionTest(){
        float[] quat = fromAxisAngle(1,0,0,(float)Math.toRadians(90));
        float[] quat1 = fromAxisAngle(0,0,1,(float)Math.toRadians(45));
        float[] quat2 = fromAxisAngle(1,0,0,(float)Math.toRadians(-45));
        float[] result = new float[4];
        Quaternion.multiplyQQ(result,quat,quat1);
        Quaternion.multiplyQQ(result,result,quat2);
        float[] axisAngle = toAxisAngle(result);
        System.out.println(Math.toDegrees(axisAngle[3]));
        printArr(axisAngle);
    }

    @Test
    public void orientToLook(){
        // direction we are already facing (without rotation)
        float[] forward = new float[]{0,0,1};
        float[] pointOnPlane = new float[]{1,1,1};
        float[] swordPos = {0,0,0};
        // direction we want to be facing (to sword plane)
        float[] target = new float[3];
        Vector3.subtractVV(target,pointOnPlane,swordPos);
        Vector3.normalize(target);

        // axis and angle of rotation
        float[] axis = new float[3];
        Vector3.crossVV(axis,forward,target);
        float sinAngle = Vector3.length(axis);
        float cosAngle = Vector3.dotVV(forward,target);
        float angle = (float)Math.atan2(sinAngle,cosAngle);
        Vector3.normalize(axis);
        float[] quat = Quaternion.fromAxisAngle(axis[0],axis[1],axis[2],angle);
        printArr(axis);
        System.out.println(angle);
        printArr(quat);
    }
    private float[] eulerAngleToAxisAngle(float xRot,float yRot,float zRot){
        float c1 = (float)Math.cos(xRot/2f);
        float s1 = (float)Math.sin(xRot/2f);
        float c2 = (float)Math.cos(yRot/2f);
        float s2 = (float)Math.sin(yRot/2f);
        float c3 = (float)Math.cos(zRot/2f);
        float s3 = (float)Math.sin(zRot/2f);
        float c1c2 = c1*c2;
        float s1s2 = s1*s2;
        float w =c1c2*c3 - s1s2*s3;
        float x =c1c2*s3 + s1s2*c3;
        float y =s1*c2*c3 + c1*s2*s3;
        float z =c1*s2*c3 - s1*c2*s3;
        float angle = 2 * (float)Math.acos(w);
        float lenSqrd = x*x+y*y+z*z;
        if (lenSqrd < 0.001) { // when all euler angles are zero angle =0 so
            // we can set axis to anything to avoid divide by zero
            x=1;
            y=z=0;
        } else {
            float len = (float)Math.sqrt(lenSqrd);
            x /= len;
            y /= len;
            z /= len;
        }
        return new float[]{x,y,z,w};
    }

    private void printArr(float[] arr) {
        String s = "";
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] < 1e-5 && arr[i] > -1e-5){
                s += "0,";
            } else {
                s += arr[i] + ", ";
            }
        }
        System.out.println(s);
    }

    @Test
    public void rotMatTest() {
        float[] axisAngle = new float[]{0, 0.6f, 0.8f, (float)Math.PI};
        float[] quat = Quaternion.fromAxisAngle(axisAngle);
        float[] rotMat = Quaternion.toRotationMatrixCM(quat);
        float[] expectedRotMat = new float[16];
        setRotateM(expectedRotMat,0,(float)Math.toDegrees(Math.PI),0,0.6f,0.8f);
        printArr(axisAngle);
        printArr(quat);
        printArr(rotMat);
        printArr(expectedRotMat);
    }

    private float[] axisAngleToRotMatRM(float x, float y, float z, float rad) {
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        float[] rotMat = new float[16];
        rotMat[0] = cos + x * x * (1 - cos);
        rotMat[1] = x * y * (1 - cos) - z * sin;
        rotMat[2] = x * z * (1 - cos) + y * sin;
        rotMat[3] = y * x * (1 - cos) + z * sin;
        rotMat[4] = cos + y * y * (1 - cos);
        rotMat[5] = y * z * (1 - cos) - x * sin;
        rotMat[6] = 0;
        rotMat[7] = z * x * (1 - cos) - y * sin;
        rotMat[8] = z * y * (1 - cos) + x * sin;
        rotMat[9] = cos + z * z * (1 - cos);
        rotMat[10] = 0;
        rotMat[11] = 0;
        rotMat[12] = 0;
        rotMat[13] = 0;
        rotMat[14] = 1;
        return rotMat;
    }
    private float[] axisAngleToRotMatCM(float x, float y, float z, float rad) {
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        float[] rotMat = new float[16];
        rotMat[0] = cos + x * x * (1 - cos);
        rotMat[1] = y * x * (1 - cos) + z * sin;
        rotMat[2] = z * x * (1 - cos) - y * sin;
        rotMat[3] = 0;
        rotMat[4] = x * y * (1 - cos) - z * sin;
        rotMat[5] = cos + y * y * (1 - cos);
        rotMat[6] = z * y * (1 - cos) + x * sin;
        rotMat[7] = 0;
        rotMat[8] = x * z * (1 - cos) + y * sin;
        rotMat[9] = y * z * (1 - cos) - x * sin;
        rotMat[10] = cos + z * z * (1 - cos);
        rotMat[11] = 0;
        rotMat[12] = 0;
        rotMat[13] = 0;
        rotMat[14] = 0;
        rotMat[15] = 1;
        return rotMat;
    }

    /** TODO: remove copied opengl method (I didnt feel like mocking it) **/
    public static void setRotateM(float[] rm, int rmOffset, float a, float x, float
            y, float z) {
        rm[rmOffset + 3] = 0;
        rm[rmOffset + 7] = 0;
        rm[rmOffset + 11] = 0;
        rm[rmOffset + 12] = 0;
        rm[rmOffset + 13] = 0;
        rm[rmOffset + 14] = 0;
        rm[rmOffset + 15] = 1;
        a *= (float) (Math.PI / 180.0f);
        float s = (float) Math.sin(a);
        float c = (float) Math.cos(a);
        if (1.0f == x && 0.0f == y && 0.0f == z) {
            rm[rmOffset + 5] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 6] = s;
            rm[rmOffset + 9] = -s;
            rm[rmOffset + 1] = 0;
            rm[rmOffset + 2] = 0;
            rm[rmOffset + 4] = 0;
            rm[rmOffset + 8] = 0;
            rm[rmOffset + 0] = 1;
        } else if (0.0f == x && 1.0f == y && 0.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 8] = s;
            rm[rmOffset + 2] = -s;
            rm[rmOffset + 1] = 0;
            rm[rmOffset + 4] = 0;
            rm[rmOffset + 6] = 0;
            rm[rmOffset + 9] = 0;
            rm[rmOffset + 5] = 1;
        } else if (0.0f == x && 0.0f == y && 1.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 5] = c;
            rm[rmOffset + 1] = s;
            rm[rmOffset + 4] = -s;
            rm[rmOffset + 2] = 0;
            rm[rmOffset + 6] = 0;
            rm[rmOffset + 8] = 0;
            rm[rmOffset + 9] = 0;
            rm[rmOffset + 10] = 1;
        } else {
            float len = length(x, y, z);
            if (1.0f != len) {
                float recipLen = 1.0f / len;
                x *= recipLen;
                y *= recipLen;
                z *= recipLen;
            }
            float nc = 1.0f - c;
            float xy = x * y;
            float yz = y * z;
            float zx = z * x;
            float xs = x * s;
            float ys = y * s;
            float zs = z * s;
            rm[rmOffset + 0] = x * x * nc + c;
            rm[rmOffset + 4] = xy * nc - zs;
            rm[rmOffset + 8] = zx * nc + ys;
            rm[rmOffset + 1] = xy * nc + zs;
            rm[rmOffset + 5] = y * y * nc + c;
            rm[rmOffset + 9] = yz * nc - xs;
            rm[rmOffset + 2] = zx * nc - ys;
            rm[rmOffset + 6] = yz * nc + xs;
            rm[rmOffset + 10] = z * z * nc + c;
        }
    }

    private static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}