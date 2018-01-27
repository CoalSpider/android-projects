package com.ben.glrendererlite;

import com.ben.glrendererlite.util.Vec3;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Ben on 8/25/2017.
 */

public class EdgeRemovalTest {
    // e0 S:0.49999997,0.49999997,0.49999997 E:-0.5,-0.5,-0.5
    // e1 S:-0.5,-0.5,-0.5 E:-0.5,0.49999997,-0.5
    // e2 S:-0.5,0.49999997,-0.5 E:0.49999997,0.49999997,0.49999997
    // e3 S:-0.5,-0.5,0.49999997 E:-0.5,-0.5,-0.5
    // e4 S:-0.5,-0.5,-0.5 E:0.49999997,0.49999997,0.49999997
    // e5 S:0.49999997,0.49999997,0.49999997 E:-0.5,-0.5,0.49999997
    Edge e0;
    Edge e1;
    Edge e2;
    Edge e3;
    Edge e4;
    Edge e5;
    Vec3 A;
    Vec3 B;
    Vec3 D;
    Vec3 E;
    @Before
    public void setup(){
        A = new Vec3(-0.5f,0.5f,-0.5f);
        B = new Vec3(0.5f,0.5f,0.5f);
        D = new Vec3(-0.5f,-0.5f,0.5f);
        E = new Vec3(-0.5f,-0.5f,-0.5f);
        e0 = new Edge(B,E);
        e1 = new Edge(E,A);
        e2 = new Edge(A,B);
        e3 = new Edge(D,E);
        e4 = new Edge(E,B);
        e5 = new Edge(B,D);
    }

    @Test
    public void edgesAreDifferent0(){
        assertFalse(edgeEquals(e0,e1));
        assertFalse(edgeEquals(e0,e2));
        assertFalse(edgeEquals(e0,e3));
        assertFalse(edgeEquals(e0,e4));
        System.out.println(e0);
        System.out.println(e5);
        assertFalse(edgeEquals(e0,e5));
    }

    private boolean edgeEquals(Edge a, Edge b){
        boolean start = vecEquals(a.getStart(),b.getStart());
        boolean end = vecEquals(a.getEnd(),b.getEnd());
        return start&&end;
    }

    private boolean vecEquals(Vec3 a, Vec3 b){
        boolean xEql = a.x()-b.x() < 1e-9f;
        boolean yEql = a.y()-b.y() < 1e-9f;
        boolean zEql = a.z()-b.z() < 1e-9f;
        return xEql && yEql && zEql;
    }
}
