package com.ben.glrendererlite.util;

import java.nio.FloatBuffer;

/**
 * Created by Ben on 8/25/2017.
 */

public class DynamicFloatBuffer {
    private static final int INITIAL_CAPACITY = 256;
    private static final float EXPANSION = 1.25f;

    private float[] data;
    private FloatBuffer buffer;

    private int dataIndex = 0;

    public DynamicFloatBuffer(){
        data = new float[INITIAL_CAPACITY];
        buffer = Util.toFloatBuffer(data);
    }

    public void appendData(float... newData){
        setData(dataIndex,newData);
    }

    public void setData(int index, float... newData){
        dataIndex = index;

        for(float f : newData){
            if(dataIndex >= data.length) {
                expand();
            }
            data[dataIndex] = f;
            dataIndex++;
        }

        clearTrailingData();

        buffer.put(data).position(0);
    }

    private void clearTrailingData(){
        for(int i = dataIndex; i < data.length; i++){
            data[i] = 0;
        }
    }

    private void expand(){
        expandArray();
        expandBuffer();
    }

    private void expandArray(){
        int newSize = (int)(data.length*EXPANSION);
        float[] expandedData = new float[newSize];
        System.arraycopy(data,0,expandedData,0,data.length);
        data = expandedData;
    }

    private void expandBuffer(){
        // rewrap
        buffer = Util.toFloatBuffer(data);
    }

    public FloatBuffer getBuffer() {
        return buffer;
    }

    public float[] getData() {
        return data;
    }
}
