package com.ben.game.collisionEngine;

import com.ben.game.util.Quaternion;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 5/10/2017.
 */

public class Util {

    /**
     * Rotate 90 degrees around y == getQuaternion(90,0,1,0)
     *
     * @return a quaternion in the form new float[]{x,y,z,w}
     **/
    public static Quaternion getQuaternion(float x, float y, float z, float angleInRadians) {
        // q = cos (theta/2) + ((vx*i + vy*j + vz*k) * sin(theta/2))
        // where ijk are unit vectors representing the three cartesian axes
        // q = cos(a/2) + i*(x*sin(s/2)) + j(y*sin(a/2)) + k(z*sin(a/2))
        // where a == rotation angle
        // x,y,z == rotation axis
        angleInRadians /= 2f;
        float w = (float) Math.cos(angleInRadians);
        float tmp = (float) Math.sin(angleInRadians);
        float x1 = x * tmp;
        float y1 = y * tmp;
        float z1 = z * tmp;
        return new Quaternion(w, x1, y1, z1);
    }
    // p + 2w(v x p) + 2(v x (v x p))
    // 1 == Px,Py,Pz
    // v x p
    // 2x = 2Pw*Vy*Pz - 2Pw*Vz*Py
    // 2y = 2Pw*Vz*Px - 2Pw*Vx*Pz
    // 2z = 2Pw*Vx*Py - 2Pw*Vy*Px
    // 3x =
    // 3y =
    // 3z =

   public static Vec3f vecQuatMult(Vec3f v, Quaternion q){
        // defined as q*p*q^01
        // p + 2w(vxp)+(2x(vxp))
        Vec3f p = new Vec3f(q.x(),q.y(),q.z());
        Vec3f vCrossP = v.cross(p);
        Vec3f result =
                p
                .add(vCrossP.scaleMult(2*q.w()))
                .add(v.cross(vCrossP).scaleMult(2));
        return result;
    }
}