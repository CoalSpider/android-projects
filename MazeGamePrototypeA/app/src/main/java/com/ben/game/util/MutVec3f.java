package com.ben.game.util;

/** mutable version of Vec3f**/
public class MutVec3f {
    private float x,y,z;
    public MutVec3f(float x,float y,float z){this.x=x;this.y=y;this.z=z;}
    /* ******************/
    /* * BASE METHODS */
    /* ******************/
    public float lenSqrd(){return x*x+y*y+z*z;}
    public float len(){return(float)Math.sqrt(x*x+y*y+z*z);}
    public MutVec3f norm(){float d=len();x/=d;y/=d;z/=d;return this;}
    public MutVec3f add(MutVec3f b){x+=b.x;y+=b.y;z+=b.z;return this;}
    public MutVec3f sub(MutVec3f b){x-=b.x;y-=b.y;z-=b.z;return this;}
    public MutVec3f cross(MutVec3f b){float tx,ty,tz;tx=y*b.z-z*b.y;ty=z*b.x-x*b.z;tz=x*b.y-y*b.x;x=tx;y=ty;z=tz;return this;}
    public float dot(MutVec3f b){return x*b.x+y*b.y+z*b.z;}
    public MutVec3f scaleMult(float s){x*=s;y*=s;z*=s;return this;}
    public float[] toArray3(){return new float[]{x,y,z};}
    /** creates array with 4th component set to 1**/
    public float[] toArray4(){return new float[]{x,y,z,1};}
    public Vec3f toImmutable(){return new Vec3f(x,y,z);}
    @Override public String toString(){return "mV("+x+","+y+","+z+")";}
    public float x(){return x;}
    public float y(){return y;}
    public float z(){return z;};

    /* * using v + q.w * t + cross(q.xyz,t) where t == 2*cross(q.xyz,v)
     *
     * 31 operations* */
    public MutVec3f rotateByQuaternion(Quaternion q){
        // t = 2 * cross(q.xyz, v)
        // v' = v + (q.w * t) + cross(q.xyz, t)
        float q2,tx,ty,tz;
        q2 = 2*q.w();
        tx = (q.y()*z - q.z()*y) * q2;
        ty = (q.z()*x - q.x()*z) * q2;
        tz = (q.x()*y - q.y()*x) * q2;
        x += (q.w()*tx) + (q.y()*tz - q.z()*ty);
        y += (q.w()*ty) + (q.z()*tx - q.x()*tz);
        z += (q.w()*tz) + (q.x()*ty - q.y()*tx);
        return this;
    }
    /* ******************/
    /* * MODIFY BY IMMUTABLE */
    /* ******************/
    public MutVec3f add(Vec3f b){x+=b.x();y+=b.y();z+=b.z();return this;}
    public MutVec3f sub(Vec3f b){x-=b.x();y-=b.y();z-=b.z();return this;}
    public MutVec3f cross(Vec3f b){float tx,ty,tz;tx=y*b.z()-z*b.y();ty=z*b.x()-x*b.z();tz=x*b.y()-y*b.x();x=tx;y=ty;z=tz;return this;}
    public float dot(Vec3f b){return x*b.x()+y*b.y()+z*b.z();}
    /* ******************/
    /* * STATIC METHODS */
    /* ***************** */
    public static MutVec3f fromArray(float[] arr){return new MutVec3f(arr[0],arr[1],arr[2]);}
    public static MutVec3f add(MutVec3f a,MutVec3f b){return new MutVec3f(a.x+b.x,a.y+b.y,a.z+b.z);}
    public static MutVec3f sub(MutVec3f a,MutVec3f b){return new MutVec3f(a.x-b.x,a.y-b.y,a.z-b.z);}
    public static MutVec3f cross(MutVec3f a,MutVec3f b){return new MutVec3f(a.y*b.z-a.z*b.y,a.z*b.x-a.x*b.z,a.x*b.y-a.y*b.x);}
    public static float dot(MutVec3f a, MutVec3f b){return a.x*b.x+a.y*b.y+a.z*b.z;}
    public static MutVec3f scaleMult(MutVec3f a,float s){return new MutVec3f(a.x*s,a.y*s,a.z*s);}
    public static MutVec3f rotateByQuat(MutVec3f v, Quaternion q){float q2,tx,ty,tz;q2=2*q.w();tx=(q.y()*v.z-q.z()*v.y)*q2;ty=(q.z()*v.x-q.x()*v.z)*q2;tz=(q.x()*v.y-q.y()*v.x)*q2;return new MutVec3f(v.x+q.w()*tx+q.y()*tz-q.z()*ty,v.y+q.w()*ty+q.z()*tx-q.x()*tz,v.z+q.w()*tz+q.x()*ty-q.y()*tx);}
    /* static with left arg immutable */
    public static MutVec3f add(Vec3f a,MutVec3f b){return new MutVec3f(a.x()+b.x,a.y()+b.y,a.z()+b.z);}
    public static MutVec3f sub(Vec3f a,MutVec3f b){return new MutVec3f(a.x()-b.x,a.y()-b.y,a.z()-b.z);}
    public static MutVec3f cross(Vec3f a,MutVec3f b){return new MutVec3f(a.y()*b.z-a.z()*b.y,a.z()*b.x-a.x()*b.z,a.x()*b.y-a.y()*b.x);}
    public static float dot(Vec3f a, MutVec3f b){return a.x()*b.x+a.y()*b.y+a.z()*b.z;}
    public static MutVec3f scaleMult(Vec3f a,float s){return new MutVec3f(a.x()*s,a.y()*s,a.z()*s);}
    public static MutVec3f rotateByQuat(Vec3f v, Quaternion q){float q2,tx,ty,tz;q2=2*q.w();tx=(q.y()*v.z()-q.z()*v.y())*q2;ty=(q.z()*v.x()-q.x()*v.z())*q2;tz=(q.x()*v.y()-q.y()*v.x())*q2;return new MutVec3f(v.x()+q.w()*tx+q.y()*tz-q.z()*ty,v.y()+q.w()*ty+q.z()*tx-q.x()*tz,v.z()+q.w()*tz+q.x()*ty-q.y()*tx);}
    /* static with right arg immutable */
    public static MutVec3f add(MutVec3f a,Vec3f b){return new MutVec3f(a.x+b.x(),a.y+b.y(),a.z+b.z());}
    public static MutVec3f sub(MutVec3f a,Vec3f b){return new MutVec3f(a.x-b.x(),a.y-b.y(),a.z-b.z());}
    public static MutVec3f cross(MutVec3f a,Vec3f b){return new MutVec3f(a.y*b.z()-a.z*b.y(),a.z*b.x()-a.x*b.z(),a.x*b.y()-a.y*b.x());}
    public static float dot(MutVec3f a, Vec3f b){return a.x*b.x()+a.y*b.y()+a.z*b.z();}
}
