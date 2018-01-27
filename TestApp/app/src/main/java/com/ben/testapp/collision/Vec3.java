package com.ben.testapp.collision;

/**
 * Created by Ben on 8/18/2017.
 */
class Vec3 {
    private float x;
    private float y;
    private float z;

    Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    static float lenSqrd(Vec3 a){
        return a.x*a.x + a.y*a.y + a.z*a.z;
    }
    static float len(Vec3 a){
        return (float)Math.sqrt(lenSqrd(a));
    }

    static Vec3 add(Vec3 a, Vec3 b){
        return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    static Vec3 sub(Vec3 a, Vec3 b) {
        return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    static Vec3 scaleMult(Vec3 a, float s){
        return new Vec3(a.x*s, a.y*s, a.z*s);
    }

    static float dot(Vec3 a, Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    static Vec3 cross(Vec3 a, Vec3 b) {
        return new Vec3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    static Vec3 tripleProduct(Vec3 a, Vec3 b, Vec3 c){
        return cross(cross(a,b),c);
    }

    static Vec3 negate(Vec3 a) {
        return new Vec3(-a.x, -a.y, -a.z);
    }

    static Vec3 normalize(Vec3 a){
        float len = len(a);
        return new Vec3(a.x/len,a.y/len,a.z/len);
    }

    void setTo(Vec3 v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
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

    boolean equals(Vec3 v){
        return v.x==x && v.y==y && v.z==z;
    }
    @Override
    public String toString(){
        return "V("+x+","+y+","+z+")";
    }
}
