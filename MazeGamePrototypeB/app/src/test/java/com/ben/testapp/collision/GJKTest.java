package com.ben.testapp.collision;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ben on 8/18/2017.
 */
public class GJKTest {
    private PointCloud a;
    private PointCloud b;
    private PointCloud c;
    private PointCloud d;

    @Before
    public void setUp(){
        // base shape
        a = new PointCloud(generateCase(0,0,0));
        // half intersection
        b = new PointCloud(generateCase(0,0,+1));
        // not interscting a
        c = new PointCloud(generateCase(0,0,+3));
        // touching contact with a
        d = new PointCloud(generateCase(0,0,+2));
    }
    private Vec3[] generateCase(float xT, float yT, float zT){
        return new Vec3[]{
                new Vec3(-1+xT,-1+yT,-1+zT), //
                new Vec3(-1+xT,-1+yT, 1+zT), //
                new Vec3( 1+xT,-1+yT,-1+zT), //
                new Vec3(-1+xT, 1+yT,-1+zT), //
                new Vec3(-1+xT, 1+yT, 1+zT), //
                new Vec3( 1+xT,-1+yT, 1+zT), //
                new Vec3( 1+xT, 1+yT,-1+zT), //
                new Vec3( 1+xT, 1+yT, 1+zT), //
        };
    }

    @Test
    public void implementationTestTwoAABBCubes(){
        // this algorithm tests a large set of points
        // -1.9 to 10.0 on the x
        // -1.9 to 10.0 on the y
        // -1.9 to 10.0 on the z
        // -1.9 to 10.0 on the x+y
        // -1.9 to 10.0 on the x+z
        // -1.9 to 10.0 on the y+z
        assertTrue(new GJK(a,b).intersect());
        assertFalse(new GJK(a,c).intersect());
        assertFalse(new GJK(a,d).intersect());
        assertTrue(new GJK(a,new PointCloud(generateCase(0,0,+1.5f))).intersect());
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(0,0,i))).intersect());
        }
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(i,0,0))).intersect());
        }
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(0,i,0))).intersect());
        }
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(i,i,0))).intersect());
        }
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(i,0,i))).intersect());
        }
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(0,i,i))).intersect());
        }
        for(float i = -1.9f; i < 2.0f; i+=0.1f){
            assertTrue(new GJK(a,new PointCloud(generateCase(i,i,i))).intersect());
        }
        for(float i = 2.0f; i < 10.0f; i+=0.1f){
            assertFalse(new GJK(a,new PointCloud(generateCase(i,0,0))).intersect());
            assertFalse(new GJK(a,new PointCloud(generateCase(0,i,0))).intersect());
            assertFalse(new GJK(a,new PointCloud(generateCase(0,0,i))).intersect());
        }
    }

    @Test
    public void posXTest(){
        SupportPoint A = new SupportCube().support(a,b,new Vec3(1,0,0));
        SupportPoint B = new SupportCube().support(a,b,new Vec3(-1,0,0));
        System.out.println("+X = " + A+","+B);
        Vec3 t0 = doSimplex2(A.v(),B.v());
        System.out.println(t0);
        assertTrue(t0.x()==0&&t0.y()==0&&t0.z()>0);
    }
    @Test
    public void negXTest(){
        SupportPoint A = new SupportCube().support(a,b,new Vec3(-1,0,0));
        SupportPoint B = new SupportCube().support(a,b,new Vec3(1,0,0));
        System.out.println("-X = " + A+","+B);
        Vec3 t0 = doSimplex2(A.v(),B.v());
        System.out.println(t0);
        assertTrue(t0.x()==0&&t0.y()==0&&t0.z()>0);
    }
    @Test
    public void posYTest(){
        SupportPoint A = new SupportCube().support(a,b,new Vec3(0,1,0));
        SupportPoint B = new SupportCube().support(a,b,new Vec3(0,-1,0));
        System.out.println("+Y = " + A+","+B);
        Vec3 t0 = doSimplex2(A.v(),B.v());
        System.out.println(t0);
        assertTrue(t0.x()==0&&t0.y()==0&&t0.z()>0);
    }
    @Test
    public void negYTest(){
        SupportPoint A = new SupportCube().support(a,b,new Vec3(0,-1,0));
        SupportPoint B = new SupportCube().support(a,b,new Vec3(0,1,0));
        System.out.println("-Y = " + A+","+B);
        Vec3 t0 = doSimplex2(A.v(),B.v());
        System.out.println(t0);
        assertTrue(t0.x()==0&&t0.y()==0&&t0.z()>0);
    }
    @Test
    public void posZTest(){
        SupportPoint A = new SupportCube().support(a,b,new Vec3(0,0,1));
        SupportPoint B = new SupportCube().support(a,b,new Vec3(0,0,-1));
        System.out.println("+Z = " + A+","+B);
        Vec3 t0 = doSimplex2(A.v(),B.v());
        System.out.println(t0);
        assertTrue(t0.x()>00&&t0.y()==0&&t0.z()==0);
    }
    @Test
    public void negZTest(){
        SupportPoint A = new SupportCube().support(a,b,new Vec3(0,0,-1));
        SupportPoint B = new SupportCube().support(a,b,new Vec3(0,0,1));
        System.out.println("-Z = " + A+","+B);
        Vec3 t0 = doSimplex2(A.v(),B.v());
        System.out.println(t0);
        assertTrue(t0.x()>0&&t0.y()==0&&t0.z()==0);
    }

    private Vec3 doSimplex2(Vec3 A, Vec3 B){
        Vec3 AO = Vec3.negate(A);
        Vec3 AB = Vec3.sub(B,A);
        if(Vec3.dot(AB,AO) > 0){
            Vec3 newDir = Vec3.tripleProduct(AB,AO,AB);
            // there is a case where A B passes through the origin
            // remove point B and try a new random direction
            if(Vec3.lenSqrd(newDir) < 1e-9){
                // choose "random dir"
                return new Vec3(1,0,0);
            }
            return Vec3.tripleProduct(AB,AO,AB);
        } else {
            throw new RuntimeException("simplex == A");
        }
    }
}