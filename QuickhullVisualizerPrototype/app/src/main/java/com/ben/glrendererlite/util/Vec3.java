package com.ben.glrendererlite.util;

/**
 * Created by Ben on 8/23/2017.
 */
public class Vec3 {
    private float x,y,z;
    private boolean isSelected;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        isSelected = false;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public static Vec3 add(Vec3 a, Vec3 b) {
        return new Vec3(
                a.x + b.x,
                a.y + b.y,
                a.z + b.z
        );
    }

    public static Vec3 sub(Vec3 a, Vec3 b) {
        return new Vec3(
                a.x - b.x,
                a.y - b.y,
                a.z - b.z
        );
    }

    public static Vec3 negate(Vec3 a){
        return new Vec3(
                -a.x,
                -a.y,
                -a.z
        );
    }

    public static Vec3 scaleMult(Vec3 a, float s){
        return new Vec3(
                a.x * s,
                a.y * s,
                a.z * s
        );
    }

    public static Vec3 cross(Vec3 a, Vec3 b){
        float x = a.y*b.z - a.z*b.y;
        float y = a.z*b.x - a.x*b.z;
        float z = a.x*b.y - a.y*b.x;
        return new Vec3(x,y,z);
    }

    public static float dot(Vec3 a, Vec3 b){
        return a.x*b.x + a.y*b.y + a.z*b.z;
    }

    public static Vec3 normalize(Vec3 a){
        float len = len(a);
        return new Vec3(
                a.x/len,
                a.y/len,
                a.z/len
        );
    }

    public static float len(Vec3 a){
        return (float)Math.sqrt(lenSqrd(a));
    }

    public static float lenSqrd(Vec3 a){
        return dot(a,a);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString(){
        return x+","+y+","+z;
    }
}
