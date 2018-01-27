package com.ben.testapp.userInterface;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ben on 8/4/2017.
 */
public class MovementHelperTest {

    @Test
    public void rightLeftRotationTest(){
        for(int i = -89; i < 90; i++){
            assertTrue(rotConv(i) == 1);
        }
        for(int i = -180; i < -90; i++){
            assertTrue(rotConv(i) == -1);
        }
        for(int i = 91; i <= 180; i++){
            assertTrue(rotConv(i) == -1);
        }
    }

    @Test
    public void rotationMapping(){
        for(int i = -180; i <= 180; i++){
            System.out.print(i +" = ");
            System.out.println(rotConvPecnt(i));
        }
        assertTrue(rotConvPecnt(90)==0.0);
        assertTrue(rotConvPecnt(-90)==0.0);
        assertTrue(rotConvPecnt(180)==1.0);
        assertTrue(rotConvPecnt(-180)==1.0);
    }


    private float rotConvPecnt(float angInDegs){
        return ((float)(Math.cos(2* Math.toRadians(angInDegs)))+1)/2f;
    }

    // returns +1 for right (clockwise) , -1 for left (counterclockwise)
    private float rotConv(float angInDegs){
        if(angInDegs == 90 || angInDegs == -90) return 0;
        // should map -90 through 90 to rotate right
        return (angInDegs < 90 && angInDegs > -90) ? 1 : -1;
    }

    @Test
    public void translationUpDownTest(){
        for(int i = -179; i < -0; i++){
            assertTrue(traConv(i) == -1);
        }
        for(int i = 1; i < 180; i++){
            assertTrue(traConv(i) == 1);
        }
    }

    @Test
    public void translationMapping(){
        for(int i = -180; i <= 180; i++){
            System.out.print(i +" = ");
            System.out.println(traConvPecnt(i));
        }
        assertTrue(traConvPecnt(-90)==1.0);
        assertTrue(traConvPecnt(90)==1.0);
        assertTrue(traConvPecnt(45)==0.5);
        assertTrue(traConvPecnt(-45)==0.5);
        assertTrue(traConvPecnt(180)==0.0);
        assertTrue(traConvPecnt(-180)==0.0);
        assertTrue(traConvPecnt(0)==0.0);
        assertTrue(traConvPecnt(-0)==0.0);
    }

    private float traConvPecnt(float angInDegs){
        return 1 - ((float)(Math.cos(2* Math.toRadians(angInDegs)))+1)/2f;
    }

    // returns +1 for up (forwards), -1 for down (backwards)
    private float traConv(float angInDegs){
        if(angInDegs == 0 || angInDegs == 180 || angInDegs == -180) return 0;
        // should map -90 through 90 to rotate right
        return (angInDegs > 0) ? -1 : 1;
    }
}