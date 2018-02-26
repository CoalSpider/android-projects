package com.ben.testapp.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import static com.ben.testapp.common.ByteSize.*;
/**
 * Created by Ben on 7/24/2017.
 */

class ModelUtil {
    private static final ByteOrder NATIVE_BYTE_ORDER = ByteOrder.nativeOrder();

    static DoubleBuffer toDoubleBuffer(double[] data){
        int size = data.length* DOUBLE.size();
        DoubleBuffer doubleBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(NATIVE_BYTE_ORDER)
                        .asDoubleBuffer();
        doubleBuffer.put(data).position(0);
        return doubleBuffer;
    }

    static LongBuffer toLongBuffer(long[] data){
        int size = data.length*LONG.size();
        LongBuffer longBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(NATIVE_BYTE_ORDER)
                        .asLongBuffer();
        longBuffer.put(data).position(0);
        return longBuffer;
    }

    static FloatBuffer toFloatBuffer(float[] data){
        int size = data.length*FLOAT.size();
        FloatBuffer floatBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(NATIVE_BYTE_ORDER)
                        .asFloatBuffer();
        floatBuffer.put(data).position(0);
        return floatBuffer;
    }

    static IntBuffer toIntBuffer(int[] data){
        int size = data.length*INT.size();
        IntBuffer intBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(NATIVE_BYTE_ORDER)
                        .asIntBuffer();
        intBuffer.put(data).position(0);
        return intBuffer;
    }

    static ShortBuffer toShortBuffer(short[] data){
        int size = data.length*SHORT.size();
        ShortBuffer shortBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(NATIVE_BYTE_ORDER)
                        .asShortBuffer();
        shortBuffer.put(data).position(0);
        return shortBuffer;
    }

    static ByteBuffer toByteBuffer(byte[] data){
        int size = data.length*BYTE.size();
        ByteBuffer byteBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(NATIVE_BYTE_ORDER);
        byteBuffer.put(data).position(0);
        return byteBuffer;
    }

    static float[] generateColorData(int triangleCount, float[] rgbaColor){
        if(rgbaColor.length != 4){
            throw new IllegalArgumentException("rgba color size != 4");
        }
        float[] colorData = new float[triangleCount*4];
        for(int i = 0; i < colorData.length; i+=4){
            colorData[i] = rgbaColor[0];
            colorData[i+1] = rgbaColor[1];
            colorData[i+2] = rgbaColor[2];
            colorData[i+3] = rgbaColor[3];
        }
        return colorData;
    }
}