package com.ben.game.util;
// i^2 == j^2 == k^2 == ijk == -1
// ij == k, ji == -k
// jk == i, kj == -i
// ki == j, ik == -j

/** custom quaternion class form is w,xi,yj,zk **/
public class Quaternion{
    private final float w,x,y,z;
    public Quaternion(float w,float x,float y,float z){this.w=w;this.x=x;this.y=y;this.z=z;}
    // return length
    public float norm(){return(float)Math.sqrt(x*x+y*y+z*z+w*w);}
    // q1*s == (s1,v1)*s; q3 == (s1*s,v1*s);
    public Quaternion scale(float s){return new Quaternion(w*s,x*s,y*s,z*s);}
    // q1 norm == (s1,v1); q3 == (s1/norm,v1/norm);
    public Quaternion unit(){float d=1/norm();return new Quaternion(w*d,x*d,y*d,z*d);}
    // q1*q2 = (s1,v1)+(s2,v2); q3 == (s1+s2,v1+v2);
    public Quaternion add(Quaternion b){return new Quaternion(w+b.w,x+b.x,y+b.y,z+b.z);}
    // q1*q2 = (s1,v1)-(s2,v2); q3 == (s1-s2,v1-v2);
    public Quaternion sub(Quaternion b){return new Quaternion(w-b.w,x-b.x,y-b.y,z-b.z);}
    // q1*q2 == (s1,v1)*(s2,v2); q3 == (s1*s2 - v1 dot v2, s1*v2 + s2*v1 + v1 cross v2);
    public Quaternion mult(Quaternion b){return new Quaternion(w*b.w-(x*b.x+y*b.y+z*b.z),w*b.x+b.w*x+y*b.z-z*b.y,w*b.y+b.w*y+z*b.x-x*b.z,w*b.z+b.w*z+x*b.y-y*b.x);}
    // q1' == (s1,v1)'; q3 == (s1,-1*v1);
    public Quaternion conj(){return new Quaternion(w,-x,-y,-z);}
    // q1 == (s1,v1); q3 = (1/s1,1/v1)
    public Quaternion invert(){float d=norm();d=1/(d*d);return new Quaternion(w*d,-x*d,-y*d,-z*d);}
    // q1 == (s1,v1); q1->conj() q3 = (s1/norm^2,v1/norm^2)
    public Quaternion recip(){Quaternion c=conj();float d=norm();float dd=d*d;return new Quaternion(c.w/dd,c.x/dd,c.y()/dd,c.z/dd);}
    // returns the row major version (should work as is for opengl)
    public float[] toRotationMatrixRM(){return new float[]{1-2*y*y-2*z*z,2*x*y-2*z*w,2*x*z+2*y*w,0,2*x*y+2*z*w,1-2*x*x-2*z*z,2*y*z-2*x*w,0,2*x*z-2*y*w,2*y*z+2*x*w,1-2*x*x-2*y*y,0,0,0,0,1};}
    // returns the column major version
    public float[] toRotationMatrixCM(){return new float[]{1-2*y*y-2*z*z,2*x*y+2*z*w,2*x*z-2*y*w,0,2*x*y-2*z*w,1-2*x*x-2*z*z,2*y*z+2*x*w,0, 2*x*z+2*y*w,2*y*z-2*x*w,1-2*x*x-2*y*y,0,0,0,0,1};}
    // creates a quaternion from a axis angle
    public static Quaternion fromAxisAngle(float x, float y, float z, float angleInRadians){angleInRadians/=2f;if(angleInRadians==0){return new Quaternion(0,x,y,z);}float w=(float)Math.cos(angleInRadians);float tmp=(float)Math.sin(angleInRadians);return new Quaternion(w,x*tmp,y*tmp,z*tmp);}
    // returns the quaternion in axis angle representation where w == angle and xyz == axis
    public Quaternion toAxisAngle(){float angle=2*(float)Math.acos(w);if(w==0){return new Quaternion(w,x,y,z);}float tmp=(float)Math.sqrt(1-w*w);if(tmp==0){return new Quaternion(w,x,y,z);}return new Quaternion(angle,x*tmp,y*tmp,z*tmp);}
    // returns if the quaternion == 1,0,0,0
    public static boolean isIdentity(Quaternion q){return q.w==1f&&q.x==0f&&q.y==0f&&q.z==0f;}
    // string representation == w + xi + yj + zk
    @Override public String toString() {return w+" + "+x+"i + "+y+"j + "+z+"k";}
    public float w(){return w;}
    public float x(){return x;}
    public float y(){return y;}
    public float z(){return z;}
}