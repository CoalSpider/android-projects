package com.ben.game.util;

/** vector class for 3 component vectors**/
public class Vec2f {
    private final float x,y;
    public Vec2f(float x, float y){this.x=x;this.y=y;}
    /* ******************/
    /* * BASE METHODS */
    /* ******************/
    public float lenSqrd(){return x*x+y*y;}
    public float len(){return(float)Math.sqrt(x*x+y*y);}
    public Vec2f norm(){float d=len();return new Vec2f(x/d,y/d);}
    public Vec2f add(Vec2f b){return new Vec2f(x+b.x,y+b.y);}
    public Vec2f sub(Vec2f b){return new Vec2f(x-b.x,y-b.y);}
    public float dot(Vec2f b){return x*b.x+y*b.y;}
    public Vec2f scaleMult(float s){return new Vec2f(x*s,y*s);}
    public float[] toArray(){return new float[]{x,y};}
    public MutVec2f toMutable(){return new MutVec2f(x,y);}
    @Override public String toString(){return "V("+x+","+y+")";}
    public float x(){return x;}
    public float y(){return y;}
    /* ******************/
    /* * MODIFY WITH MUTABLE */
    /* ******************/
    public Vec2f add(MutVec2f b){return new Vec2f(x+b.x(),y+b.y());}
    public Vec2f sub(MutVec2f b){return new Vec2f(x-b.x(),y-b.y());}
    public float dot(MutVec2f b){return x*b.x()+y*b.y();}
    /* ******************/
    /* * STATIC METHODS */
    /* ******************/
    public static Vec2f fromArray(float[] arr){return new Vec2f(arr[0],arr[1]);}
    public static Vec2f add(Vec2f a,Vec2f b){return new Vec2f(a.x+b.x,a.y+b.y);}
    public static Vec2f sub(Vec2f a,Vec2f b){return new Vec2f(a.x-b.x,a.y-b.y);}
    public static float dot(Vec2f a, Vec2f b){return a.x*b.x+a.y*b.y;}
    public static Vec2f scaleMult(Vec2f a,float s){return new Vec2f(a.x*s,a.y*s);}
    /* left arg as mutable*/
    public static Vec2f add(MutVec2f a,Vec2f b){return new Vec2f(a.x()+b.x,a.y()+b.y);}
    public static Vec2f sub(MutVec2f a,Vec2f b){return new Vec2f(a.x()-b.x,a.y()-b.y);}
    public static float dot(MutVec2f a, Vec2f b){return a.x()*b.x+a.y()*b.y;}
    public static Vec2f scaleMult(MutVec2f a,float s){return new Vec2f(a.x()*s,a.y()*s);}
    /* right arg as mutable */
    public static Vec2f add(Vec2f a,MutVec2f b){return new Vec2f(a.x+b.x(),a.y+b.y());}
    public static Vec2f sub(Vec2f a,MutVec2f b){return new Vec2f(a.x-b.x(),a.y-b.y());}
    public static float dot(Vec2f a, MutVec2f b){return a.x*b.x()+a.y*b.y();}
}
