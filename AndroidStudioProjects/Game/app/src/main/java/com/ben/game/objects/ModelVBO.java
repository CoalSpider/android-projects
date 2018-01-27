package com.ben.game.objects;

import android.content.Context;
import android.opengl.GLES20;

import com.ben.game.util.ObjParser;

import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 5/4/2017.
 */

public class ModelVBO extends ModelBase {
    private int posBufIndx;
    private int colBufIndx;
    private int norBufIndx;
    private int texBufIndx;

    private ModelVBO(Builder builder) {
        try {
            ObjParser.parse(builder.getContext(), builder.getObjFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] posDat = ObjParser.getExpandedVertexData();
        float[] texDat = ObjParser.getExpandedTextureData();
        float[] norDat = ObjParser.getExpandedNormalData();
        this.setTrisCount(posDat.length / 3);
        float[] colDat = this.getColorData(this.getTrisCount(), builder.getColor());
        FloatBuffer[] floatBuffers = this.getBuffers(posDat, colDat, norDat,
                texDat);
        FloatBuffer posBuf = floatBuffers[0];
        FloatBuffer colBuf = floatBuffers[1];
        FloatBuffer norBuf = floatBuffers[2];
        FloatBuffer texBuf = floatBuffers[3];

        int[] buffers = new int[4];
        GLES20.glGenBuffers(4, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, posBuf.capacity() *
                BYTES_PER_FLOAT, posBuf, GLES20.GL_STATIC_DRAW);

        if(colBuf != null) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, colBuf.capacity() *
                    BYTES_PER_FLOAT, colBuf, GLES20.GL_STATIC_DRAW);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, norBuf.capacity() *
                BYTES_PER_FLOAT, norBuf, GLES20.GL_STATIC_DRAW);

        if(texBuf != null) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texBuf.capacity() *
                    BYTES_PER_FLOAT, texBuf, GLES20.GL_STATIC_DRAW);
        }
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        posBufIndx = buffers[0];
        colBufIndx = buffers[1];
        norBufIndx = buffers[2];
        texBufIndx = buffers[3];

        // clear buffers we no longer need
        posBuf.clear();
        colBuf.clear();
        norBuf.clear();
        texBuf.clear();
    }

    static class Builder extends ModelBuilder{
        public Builder(Context context, String objFile) {
            super(context, objFile);
        }

        @Override
        ModelVBO build() {
            return new ModelVBO(this);
        }
    }

    public int getColBufIndx() {
        return colBufIndx;
    }

    public int getPosBufIndx() {
        return posBufIndx;
    }

    public int getNorBufIndx() {
        return norBufIndx;
    }

    public int getTexBufIndx() {
        return texBufIndx;
    }
}
