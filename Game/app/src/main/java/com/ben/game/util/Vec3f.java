package com.ben.game.util;

/** vector class for 3 component vectors**/
public class Vec3f{
    private final float x,y,z;
    public Vec3f(float x,float y,float z){this.x=x;this.y=y;this.z=z;}
    /* ******************/
    /* * BASE METHODS */
    /* ******************/
    public float lenSqrd(){return x*x+y*y+z*z;}
    public float len(){return(float)Math.sqrt(x*x+y*y+z*z);}
    public Vec3f norm(){float d=len();return new Vec3f(x/d,y/d,z/d);}
    public Vec3f add(Vec3f b){return new Vec3f(x+b.x,y+b.y,z+b.z);}
    public Vec3f sub(Vec3f b){return new Vec3f(x-b.x,y-b.y,z-b.y);}
    public Vec3f cross(Vec3f b){return new Vec3f(y*b.z-z*b.y,z*b.x-x*b.z,x*b.y-y*b.x);}
    public float dot(Vec3f b){return x*b.x+y*b.y+z*b.z;}
    public Vec3f scaleMult(float s){return new Vec3f(x*s,y*s,z*s);}
    public float[] toArray3(){return new float[]{x,y,z};}
    /** creates array with 4th component set to 1**/
    public float[] toArray4(){return new float[]{x,y,z,1};}
    public MutVec3f toMutable(){return new MutVec3f(x,y,z);}
    @Override public String toString(){return "V("+x+","+y+","+z+")";}
    public float x(){return x;}
    public float y(){return y;}
    public float z(){return z;};

    /** using v + q.w * t + cross(q.xyz,t) where t == 2*cross(q.xyz,v)
     *
     * 31 operations**/
    public Vec3f rotateByQuaternion(Quaternion q){
        // t = 2 * cross(q.xyz, v)
        // v' = v + (q.w * t) + cross(q.xyz, t)
        float q2,tx,ty,tz;
        q2 = 2*q.w();
        tx = (q.y()*z - q.z()*y) * q2;
        ty = (q.z()*x - q.x()*z) * q2;
        tz = (q.x()*y - q.y()*x) * q2;
        float x = this.x + (q.w()*tx) + (q.y()*tz - q.z()*ty);
        float y = this.y + (q.w()*ty) + (q.z()*tx - q.x()*tz);
        float z = this.z + (q.w()*tz) + (q.x()*ty - q.y()*tx);
        return new Vec3f(x,y,z);
    }

    /** using 3x3 3x1 matrix multiplcation (claim is that this is how unity does it)
     *
     * 39 operations **/
    private Vec3f rotateByQuaternion2(Quaternion q){
        float num = q.x() * 2f;
        float num2 = q.y() * 2f;
        float num3 = q.z() * 2f;
        float num4 = q.x() * num;
        float num5 = q.y() * num2;
        float num6 = q.z() * num3;
        float num7 = q.x() * num2;
        float num8 = q.x() * num3;
        float num9 = q.y() * num3;
        float num10 = q.w() * num;
        float num11 = q.w() * num2;
        float num12 = q.w() * num3;
        float x = (1f - (num5 + num6)) * x() + (num7 - num12) * y() + (num8 + num11) * z();
        float y = (num7 + num12) * x() + (1f - (num4 + num6)) * y() + (num9 - num10) * z();
        float z = (num8 - num11) * x() + (num9 + num10) * y() + (1f - (num4 + num5)) * z();
        return new Vec3f(x,y,z);
    }

    /** using q*v*conj(q)
     *
     * 59 operations**/
    private Vec3f rotateByQuaternion3(Quaternion q) {
        // defined as q*p*conj(q)
        Quaternion p = new Quaternion(0,x(), y(),z());
        p = q.mult(p).mult(q.conj());
        return new Vec3f(p.x(),p.y(),p.z());
    }
    /* ******************/
    /* * MODIFY WITH MUTABLE */
    /* ******************/
    public Vec3f add(MutVec3f b){return new Vec3f(x+b.x(),y+b.y(),z+b.z());}
    public Vec3f sub(MutVec3f b){return new Vec3f(x-b.x(),y-b.y(),z-b.y());}
    public Vec3f cross(MutVec3f b){return new Vec3f(y*b.z()-z*b.y(),z*b.x()-x*b.z(),x*b.y()-y*b.x());}
    public float dot(MutVec3f b){return x*b.x()+y*b.y()+z*b.z();}
    /* ******************/
    /* * STATIC METHODS */
    /* ******************/
    public static Vec3f fromArray(float[] arr){return new Vec3f(arr[0],arr[1],arr[2]);}
    public static Vec3f add(Vec3f a,Vec3f b){return new Vec3f(a.x+b.x,a.y+b.y,a.z+b.z);}
    public static Vec3f sub(Vec3f a,Vec3f b){return new Vec3f(a.x-b.x,a.y-b.y,a.z-b.z);}
    public static Vec3f cross(Vec3f a,Vec3f b){return new Vec3f(a.y*b.z-a.z*b.y,a.z*b.x-a.x*b.z,a.x*b.y-a.y*b.x);}
    public static float dot(Vec3f a, Vec3f b){return a.x*b.x+a.y*b.y+a.z*b.z;}
    public static Vec3f scaleMult(Vec3f a,float s){return new Vec3f(a.x*s,a.y*s,a.z*s);}
    public static Vec3f rotateByQuat(Vec3f v, Quaternion q){float q2,tx,ty,tz;q2=2*q.w();tx=(q.y()*v.z-q.z()*v.y)*q2;ty=(q.z()*v.x-q.x()*v.z)*q2;tz=(q.x()*v.y-q.y()*v.x)*q2;return new Vec3f(v.x+q.w()*tx+q.y()*tz-q.z()*ty,v.y+q.w()*ty+q.z()*tx-q.x()*tz,v.z+q.w()*tz+q.x()*ty-q.y()*tx);}
    /* static with left arg as mutable */
    public static Vec3f add(MutVec3f a,Vec3f b){return new Vec3f(a.x()+b.x,a.y()+b.y,a.z()+b.z);}
    public static Vec3f sub(MutVec3f a,Vec3f b){return new Vec3f(a.x()-b.x,a.y()-b.y,a.z()-b.z);}
    public static Vec3f cross(MutVec3f a,Vec3f b){return new Vec3f(a.y()*b.z-a.z()*b.y,a.z()*b.x-a.x()*b.z,a.x()*b.y-a.y()*b.x);}
    public static float dot(MutVec3f a, Vec3f b){return a.x()*b.x+a.y()*b.y+a.z()*b.z;}
    public static Vec3f scaleMult(MutVec3f a,float s){return new Vec3f(a.x()*s,a.y()*s,a.z()*s);}
    public static Vec3f rotateByQuat(MutVec3f v, Quaternion q){float q2,tx,ty,tz;q2=2*q.w();tx=(q.y()*v.z()-q.z()*v.y())*q2;ty=(q.z()*v.x()-q.x()*v.z())*q2;tz=(q.x()*v.y()-q.y()*v.x())*q2;return new Vec3f(v.x()+q.w()*tx+q.y()*tz-q.z()*ty,v.y()+q.w()*ty+q.z()*tx-q.x()*tz,v.z()+q.w()*tz+q.x()*ty-q.y()*tx);}
    /* static with right arg as mutable */
    public static Vec3f add(Vec3f a,MutVec3f b){return new Vec3f(a.x+b.x(),a.y+b.y(),a.z+b.z());}
    public static Vec3f sub(Vec3f a,MutVec3f b){return new Vec3f(a.x-b.x(),a.y-b.y(),a.z-b.z());}
    public static Vec3f cross(Vec3f a,MutVec3f b){return new Vec3f(a.y*b.z()-a.z*b.y(),a.z*b.x()-a.x*b.z(),a.x*b.y()-a.y*b.x());}
    public static float dot(Vec3f a, MutVec3f b){return a.x*b.x()+a.y*b.y()+a.z*b.z();}

}
