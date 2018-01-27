package com.ben.drawcontrolsprototype.model;

import android.content.Context;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ben on 4/27/2017.
 */

abstract class VertexBufferObject {
    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;

    abstract public void render(int positionHandle, int textureHandle, int normalHandle);

    abstract public void release();


    /** @return new FloatBuffer[]{positionBuffer, colorBuffer, textureBuffer, normalBuffer} **/
    FloatBuffer[] getBuffersVCTN(float[] posDat, float[] colorDat, float[] texDat, float[] normDat) {
        final FloatBuffer positionBuffer;
        final FloatBuffer colorBuffer;
        final FloatBuffer normalBuffer;
        final FloatBuffer textureBuffer;

        positionBuffer = ByteBuffer.allocateDirect(posDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(colorDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer = ByteBuffer.allocateDirect(texDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer = ByteBuffer.allocateDirect(normDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        positionBuffer.put(posDat).position(0);
        colorBuffer.put(colorDat).position(0);
        textureBuffer.put(texDat).position(0);
        normalBuffer.put(normDat).position(0);

        return new FloatBuffer[]{positionBuffer,colorBuffer,textureBuffer,normalBuffer};
    }

    /**Interleaving on position and color buffer: x y z r g b a
     * WARNING this only works if color is specified by vertex
     *
     * @return new FloatBuffer[]{positionColorBuffer, textureBuffer, normalBuffer} **/
    FloatBuffer[] getBuffersVCTN(float[] posColDat, float[] texDat, float[] normDat) {
        final FloatBuffer positionColorBuffer;
        final FloatBuffer normalBuffer;
        final FloatBuffer textureBuffer;

        positionColorBuffer = ByteBuffer.allocateDirect(posColDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer = ByteBuffer.allocateDirect(texDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer = ByteBuffer.allocateDirect(normDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        positionColorBuffer.put(posColDat).position(0);
        textureBuffer.put(texDat).position(0);
        normalBuffer.put(normDat).position(0);

        return new FloatBuffer[]{positionColorBuffer,textureBuffer,normalBuffer};
    }
    /**Interleaving on position color texture and normal data: px py pz r g b a tx ty nx ny nz
     *
     * @return new FloatBuffer[]{positionColorBuffer, textureBuffer, normalBuffer} **/
    FloatBuffer getInterleavedVTNBuffers(float[] posDat, float[] colDat, float[] texDat, float[] normDat) {
        int bufferSize = posDat.length+colDat.length+texDat.length+normDat.length;
        final FloatBuffer interleavedBuffer;

        interleavedBuffer = ByteBuffer.allocateDirect(bufferSize * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        int positionOffset = 0;
        int colorOffset = 0;
        int textureOffset = 0;
        int normalOffset = 0;
        int positionDataSize = 3;
        int colorDataSize = 4;
        int textureDataSize = 2;
        int normalDataSize = 3;
        for(int i = 0; i < bufferSize; i++){
            interleavedBuffer.put(posDat,positionOffset,positionDataSize);
            positionOffset += positionDataSize;
            interleavedBuffer.put(colDat,colorOffset,colorDataSize);
            colorOffset += colorDataSize;
            interleavedBuffer.put(texDat,textureOffset,textureDataSize);
            textureOffset += textureDataSize;
            interleavedBuffer.put(normDat,normalOffset,normalDataSize);
            normalOffset += normalDataSize;
        }

        return interleavedBuffer;
    }

    /**Interleaving on position texture and normal data: px py pz tx ty nx ny nz
     *
     * @return new FloatBuffer[]{positionColorBuffer, textureBuffer, normalBuffer} **/
    FloatBuffer getInterleavedVTNBuffers(float[] posDat, float[] texDat, float[] normDat) {
        int bufferSize = posDat.length+texDat.length+normDat.length;
        final FloatBuffer interleavedBuffer;

        interleavedBuffer = ByteBuffer.allocateDirect(bufferSize * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        int positionOffset = 0;
        int textureOffset = 0;
        int normalOffset = 0;
        int positionDataSize = 3;
        int textureDataSize = 2;
        int normalDataSize = 3;
        for(int i = 0; i < posDat.length; i++){
            interleavedBuffer.put(posDat,positionOffset,positionDataSize);
            positionOffset += positionDataSize;
            interleavedBuffer.put(texDat,textureOffset,textureDataSize);
            textureOffset += textureDataSize;
            interleavedBuffer.put(normDat,normalOffset,normalDataSize);
            normalOffset += normalDataSize;
        }

        return interleavedBuffer;
    }


    /** @return new FloatBuffer[]{positionBuffer, textureBuffer, normalBuffer} **/
    FloatBuffer[] getBuffersVTN(float[] posDat, float[] texDat, float[] normDat) {
        final FloatBuffer positionBuffer;
        final FloatBuffer normalBuffer;
        final FloatBuffer textureBuffer;

        positionBuffer = ByteBuffer.allocateDirect(posDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer = ByteBuffer.allocateDirect(texDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer = ByteBuffer.allocateDirect(normDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        positionBuffer.put(posDat).position(0);
        textureBuffer.put(texDat).position(0);
        normalBuffer.put(normDat).position(0);

        return new FloatBuffer[]{positionBuffer,textureBuffer,normalBuffer};
    }

    ShortBuffer getIndexBuffer(short[] drawOrder){
        final ShortBuffer indexBuffer;

        indexBuffer = ByteBuffer.allocateDirect(drawOrder.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();

        indexBuffer.put(drawOrder).position(0);

        return indexBuffer;
    }
}
