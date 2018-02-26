package com.ben.testapp.collision;

/**
 * Created by Ben on 8/20/2017.
 */

public class SupportPoint {
    private Vec3 a;
    private Vec3 b;
    private Vec3 v;
    SupportPoint(Vec3 a, Vec3 b, Vec3 v){
        this.a = a;
        this.b = b;
        this.v = v;
    }

    public Vec3 a() {
        return a;
    }

    public Vec3 b() {
        return b;
    }

    public Vec3 v() {
        return v;
    }

    void setTo(SupportPoint supportPoint){
        a.setTo(supportPoint.a);
        b.setTo(supportPoint.b);
        v.setTo(supportPoint.v);
    }

    @Override
    public String toString(){
        return "Sa"+a+" Sb"+b+" Sm"+v;
    }

    public boolean equals(SupportPoint p){
        return p.a.equals(a)&&p.b.equals(b)&&p.v.equals(v);
    }
}
