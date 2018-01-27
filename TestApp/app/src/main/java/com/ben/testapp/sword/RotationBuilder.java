package com.ben.testapp.sword;

import com.ben.testapp.util.Quaternion;

import static com.ben.testapp.util.Quaternion.fromAxisAngle;

/**
 * Created by Ben on 8/13/2017.
 */

public class RotationBuilder {
    private float[] composition = new float[]{0,0,0,1};
    RotationBuilder rotX(float degs){
        float[] x = fromAxisAngle(1,0,0,(float)Math.toRadians(degs));
        Quaternion.multiplyQQ(composition,composition,x);
        return this;
    }
    RotationBuilder rotY(float degs){
        float[] y = fromAxisAngle(0,1,0,(float)Math.toRadians(degs));
        Quaternion.multiplyQQ(composition,composition,y);
        return this;
    }
    RotationBuilder rotZ(float degs){
        float[] z = fromAxisAngle(0,0,1,(float)Math.toRadians(degs));
        Quaternion.multiplyQQ(composition,composition,z);
        return this;
    }

    float[] getComposition() {
        float[] compCopy = new float[4];
        System.arraycopy(composition,0,compCopy,0,4);
        return compCopy;
    }

    void clearCurrentRotation(){
        Quaternion.identity(composition);
    }
}
