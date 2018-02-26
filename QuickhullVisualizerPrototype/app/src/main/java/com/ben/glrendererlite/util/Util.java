package com.ben.glrendererlite.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 8/22/2017.
 */

public class Util {
    public static FloatBuffer toFloatBuffer(float[] data){
        int size = data.length*4;
        FloatBuffer floatBuffer =
                ByteBuffer.allocateDirect(size)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
        floatBuffer.put(data).position(0);
        return floatBuffer;
    }
}
