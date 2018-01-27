package com.ben.glrendererlite;

import com.ben.glrendererlite.util.Vec3;

/**
 * Created by Ben on 8/22/2017.
 */

public class Edge {
    private Vec3 start;
    private Vec3 end;
    public Edge(Vec3 start, Vec3 end){
        this.start = start;
        this.end = end;
    }

    public Vec3 getNormal(){
        return new Vec3(end.y()-start.y(),-(end.x()-start.x()),0);
    }

    public Vec3 getStart() {
        return start;
    }

    public Vec3 getEnd() {
        return end;
    }
    @Override
    public String toString(){
        return "S:"+start+" E:"+end;
    }
}
