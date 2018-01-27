package com.ben.glrendererlite.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ben on 8/25/2017.
 */
public class DynamicFloatBufferTest {
    @Test
    public void resizeTest(){
        DynamicFloatBuffer dfb = new DynamicFloatBuffer();
        assertTrue(dfb.getData().length == 256);
        for(int i = 0; i < 300; i++){
            dfb.appendData(i);
        }
        assertTrue(dfb.getData().length == (int)(256*1.25));

        printArr(dfb.getData());
    }

    @Test
    public void setDataTest(){
        DynamicFloatBuffer dfb = new DynamicFloatBuffer();
        float[] data = new float[300];
        for(int i = 0; i < data.length; i++){
            data[i] = i;
        }
        dfb.setData(0,data);
        assertTrue(dfb.getData().length == (int)(256*1.25));

        printArr(dfb.getData());
    }

    void printArr(float[] arr){
        StringBuilder builder = new StringBuilder();
        for(float f : arr){
            builder.append(f);
            builder.append(", ");
        }
        System.out.println(builder.toString());
    }
}