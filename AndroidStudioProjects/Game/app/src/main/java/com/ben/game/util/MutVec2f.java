package com.ben.game.util;

/** mutable version of Vec2f**/
public class MutVec2f {
    private float x,y;
    public MutVec2f(float x, float y){this.x=x;this.y=y;}
    /* ******************/
    /* * BASE METHODS */
    /* ******************/
    public float lenSqrd(){return x*x+y*y;}
    public float len(){return(float)Math.sqrt(x*x+y*y);}
    public MutVec2f norm(){float d=len();x/=d;y/=d;return this;}
    public MutVec2f add(MutVec2f b){x+=b.x;y+=b.y;return this;}
    public MutVec2f sub(MutVec2f b){x-=b.x;y-=b.y;return this;}
    public float dot(MutVec2f b){return x*b.x+y*b.y;}
    public MutVec2f scaleMult(float s){x*=s;y*=s;return this;}
    public Vec2f toImmutable(){return new Vec2f(x,y);}
    public float[] toArray(){return new float[]{x,y};}
    @Override public String toString(){return "mV("+x+","+y+")";}
    public float x(){return x;}
    public float y(){return y;}
    /* ******************/
    /* * MODIFY WITH IMMUTABLE */
    /* ******************/
    public MutVec2f add(Vec2f b){x+=b.x();y+=b.y();return this;}
    public MutVec2f sub(Vec2f b){x-=b.x();y-=b.y();return this;}
    public float dot(Vec2f b){return x*b.x()+y*b.y();}
    /* ******************/
    /* * STATIC METHODS */
    /* ******************/
    public static MutVec2f fromArray(float[] arr){return new MutVec2f(arr[0],arr[1]);}
    public static MutVec2f add(MutVec2f a,MutVec2f b){return new MutVec2f(a.x+b.x,a.y+b.y);}
    public static MutVec2f sub(MutVec2f a,MutVec2f b){return new MutVec2f(a.x-b.x,a.y-b.y);}
    public static float dot(MutVec2f a, MutVec2f b){return a.x*b.x+a.y*b.y;}
    public static MutVec2f scaleMult(MutVec2f a,float s){return new MutVec2f(a.x*s,a.y*s);}
    /* static with left arg immutable */
    public static MutVec2f add(Vec2f a,MutVec2f b){return new MutVec2f(a.x()+b.x,a.y()+b.y);}
    public static MutVec2f sub(Vec2f a,MutVec2f b){return new MutVec2f(a.x()-b.x,a.y()-b.y);}
    public static float dot(Vec2f a, MutVec2f b){return a.x()*b.x+a.y()*b.y;}
    public static MutVec2f scaleMult(Vec2f a,float s){return new MutVec2f(a.x()*s,a.y()*s);}
    /* static with right arg immutable */
    public static MutVec2f add(MutVec2f a,Vec2f b){return new MutVec2f(a.x+b.x(),a.y+b.y());}
    public static MutVec2f sub(MutVec2f a,Vec2f b){return new MutVec2f(a.x-b.x(),a.y-b.y());}
    public static float dot(MutVec2f a, Vec2f b){return a.x*b.x()+a.y*b.y();}

}
