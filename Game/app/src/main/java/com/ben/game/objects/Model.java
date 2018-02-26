package com.ben.game.objects;

import android.content.Context;

import com.ben.game.util.ObjParser;

import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 5/4/2017.
 */

public class Model extends ModelBase {
    private FloatBuffer posBuf;
    private FloatBuffer colBuf;
    private FloatBuffer norBuf;
    private FloatBuffer texBuf;

    private Model(Builder builder) {
        try {
            ObjParser.parse(builder.getContext(), builder.getObjFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] posDat = ObjParser.getExpandedVertexData();
        float[] norDat = ObjParser.getExpandedNormalData();
        this.setTrisCount(posDat.length / 3);
        float[] texDat = null;
        float[] colDat = null;
        if (builder.getTexture() != -1) {
            texDat = ObjParser.getExpandedTextureData();
        }
        if (builder.getColor() != -1) {
            colDat = this.getColorData(this.getTrisCount(), builder.getColor());
        }
        FloatBuffer[] buffers = this.getBuffers(posDat,colDat,norDat,texDat);
        this.posBuf = buffers[0];
        this.colBuf = buffers[1];
        this.norBuf = buffers[2];
        this.texBuf = buffers[3];
    }

    static class Builder extends ModelBuilder {
        Builder(Context context, String objFile) {
            super(context, objFile);
        }

        @Override
        Model build() {
            return new Model(this);
        }
    }

    public FloatBuffer getPosBuf() {
        return posBuf;
    }

    public FloatBuffer getNorBuf() {
        return norBuf;
    }

    public FloatBuffer getColBuf() {
        return colBuf;
    }

    public FloatBuffer getTexBuf() {
        return texBuf;
    }
}
