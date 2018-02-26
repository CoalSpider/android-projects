package com.ben.lightingtest.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ModelUtil {
    public static final int BYTES_PER_FLOAT = 4;

    /**
     * <p>
     * Returns a float[] of color data for a model. This will create a mono colored
     * model ie: every vertex has the same color
     * </p>
     * <p>
     * The size of the returned array is triangleCount*4;
     * </p>
     * <p>
     * if the color == -1 a rgba of {255,255,255,255} is returned
     * </p>
     **/
    public static float[] generateColorData(int triangleCount, int color) {
        float[] colorData = new float[triangleCount * 4];
        float alpha, red, green, blue;
        if (color != -1) {
            alpha = color >> 24 & 0xff;
            red = color >> 16 & 0xff;
            green = color >> 8 & 0xff;
            blue = color & 0xff;
        } else {
            alpha = 255;
            red = 255;
            green = 255;
            blue = 255;
        }
        // convert to floating point
        alpha /= 255f;
        red /= 255f;
        green /= 255f;
        blue /= 255f;
        for (int i = 0; i < colorData.length; i += 4) {
            colorData[i] = red;
            colorData[i + 1] = green;
            colorData[i + 2] = blue;
            colorData[i + 3] = alpha;
        }
        return colorData;
    }

    public static FloatBuffer toFloatBuffer(float[] data) {
        if (data == null) {
            return null;
        }
        FloatBuffer buff;
        buff = ByteBuffer
                .allocateDirect(data.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buff.put(data).position(0);
        return buff;
    }
}