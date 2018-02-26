package com.ben.testapp.collision;

import com.ben.testapp.util.Vector3;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ben on 9/9/2017.
 */
public class CollisionTest {

    private class Box{
        float[] center;
        float eX;
        float eY;
        float eZ;
        float minX;
        float minY;
        float minZ;
        float maxX;
        float maxY;
        float maxZ;
        Box(float[] center,float eX,float eY,float eZ){
            this.center = center;
            this.eX = eX;
            this.eY = eY;
            this.eZ = eZ;
            minX = center[0]-eX;
            minY = center[1]-eY;
            minZ = center[2]-eZ;
            maxX = center[0]+eX;
            maxY = center[1]+eY;
            maxZ = center[2]+eZ;
        }
    }
    @Before
    public void setup(){
    }

    @Test
    public void collisionDataTest(){
        Box a = new Box(new float[]{0,0,0},1,1,1);
        Box b = new Box(new float[]{1,0,0},1,1,1);
        float xOverlap = calcOverlap(a.minX,b.minX,a.maxX,b.maxX);
        float yOverlap = calcOverlap(a.minY,b.minY,a.maxY,b.maxY);
        float zOverlap = calcOverlap(a.minZ,b.minZ,a.maxZ,b.maxZ);
        System.out.println(xOverlap+","+yOverlap+","+zOverlap);
        float x = (a.minX < b.minX) ? -1 : 1;
        System.out.println("normal("+x+",0,0)");
        assertTrue(lineOverlap(a.minX,b.minX,a.maxX,b.maxX));
        Vector3.addVV(a.center,a.center,new float[]{x,0,0});
        System.out.println("new center for a == " + a.center[0]+","+a.center[1]+","+a.center[2]);
    }
    private boolean lineOverlap(float minA,float minB,float maxA, float maxB){
        if(minB <= maxA && minB >= minA){
            return true;
        }
        if(minA <= maxB && minA >= maxA){
            return true;
        }
        return false;
    }

    private float calcOverlap(float minA, float minB, float maxA, float maxB){
        if(minA < minB){
            return maxA-minB;
        } else {
            return maxB-minA;
        }
    }
}