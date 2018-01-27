package com.ben.game.objects;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 5/4/2017.
 */

public abstract class ModelBase {
    public final static int BYTES_PER_FLOAT = 4;
    private int trisCount;

    float[] getColorData(int trisCount, int color) {
 //       Log.e("ModelBase", "color=" + color);
        float[] colDat = new float[trisCount * 4];
        float alpha = 255;
        float red = 255;
        float green = 255;
        float blue = 255;
        if(color != -1) {
            alpha = color >> 24 & 0xff;
            red = color >> 16 & 0xff;
            green = color >> 8 & 0xff;
            blue = color & 0xff;
        }
//        Log.e("Modelbase", "shift = " + red + "," + green + "," + blue + "," + alpha);
        red /= 255f;
        green /= 255f;
        blue /= 255f;
        alpha /= 255f;
//        Log.e("Modelbase", "div by 255 = " + red + "," + green + "," + blue + "," +
//                alpha);
        for (int i = 0; i < colDat.length; i += 4) {
            colDat[i] = red;
            colDat[i + 1] = green;
            colDat[i + 2] = blue;
            colDat[i + 3] = alpha;
        }
        return colDat;
    }

    /**
     * If an array is null interleaving on that array is skipped
     *
     * @return a FloatBuffer interleaving the given arrays in the form pos,
     * color,normal,texture (pX,pY,pZ,r,g,b,a,nX,nY,nZ,u,v)
     **/
    FloatBuffer getInterleavedBuffers(float[] posDat, float[] colDat, float[]
            normDat, float[] texDat) {
        if (colDat == null && texDat == null) {
            throw new IllegalArgumentException("no color or texture");
        }
        // interleaving pX,pY,pZ,r,g,b,a,nX,nY,nZ,u,v // 12*4 = 48bytes
        int posIndx = 0;
        int colIndx = 0;
        int norIndx = 0;
        int texIndx = 0;
        int size = posDat.length + normDat.length;
        size += (colDat != null) ? colDat.length : 0;
        size += (texDat != null) ? texDat.length : 0;
        int step = 3 + 3 + ((colDat != null) ? 4 : 0) + ((texDat != null) ? 2 : 0);
        float[] interleavedData = new float[size];
//        Log.e("InterleavedBuffers", "size=" + size + ", step=" + step);
        int i = 0;
        while(i < size){
            interleavedData[i + 0] = posDat[posIndx + 0];
            interleavedData[i + 1] = posDat[posIndx + 1];
            interleavedData[i + 2] = posDat[posIndx + 2];
            posIndx += 3;
            i += 3;
            if (colDat != null) {
                interleavedData[i + 0] = colDat[colIndx + 0];
                interleavedData[i + 1] = colDat[colIndx + 1];
                interleavedData[i + 2] = colDat[colIndx + 2];
                interleavedData[i + 3] = colDat[colIndx + 3];
                colIndx += 4;
                i += 4;
            }
            interleavedData[i + 0] = normDat[norIndx + 0];
            interleavedData[i + 1] = normDat[norIndx + 1];
            interleavedData[i + 2] = normDat[norIndx + 2];
            norIndx += 3;
            i += 3;
            if (texDat != null) {
                interleavedData[i + 0] = texDat[texIndx + 0];
                interleavedData[i + 1] = texDat[texIndx + 1];
                texIndx += 2;
                i += 2;
            }
        }
        final FloatBuffer buffer;

        buffer = floatArrayToFloatBuffer(interleavedData);

        return buffer;
    }

    /**
     * @return new FloatBuffer[]{positionBuffer, colorBuffer, normalBuffer,
     * textureBuffer}
     **/
    FloatBuffer[] getBuffers(float[] posDat, float[] colDat, float[] normDat,
                             float[] texDat) {
        final FloatBuffer positionBuffer;
        final FloatBuffer colorBuffer;
        final FloatBuffer normalBuffer;
        final FloatBuffer textureBuffer;

        positionBuffer = floatArrayToFloatBuffer(posDat);
        colorBuffer = floatArrayToFloatBuffer(colDat);
        normalBuffer = floatArrayToFloatBuffer(normDat);
        textureBuffer = floatArrayToFloatBuffer(texDat);

        return new FloatBuffer[]{positionBuffer, colorBuffer, normalBuffer,
                textureBuffer};
    }

    private FloatBuffer floatArrayToFloatBuffer(float[] data) {
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

    void setTrisCount(int trisCount) {
        this.trisCount = trisCount;
    }

    public int getTrisCount() {
        return trisCount;
    }
}
