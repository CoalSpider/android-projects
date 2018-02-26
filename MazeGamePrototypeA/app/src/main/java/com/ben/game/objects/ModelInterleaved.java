package com.ben.game.objects;

import android.content.Context;

import com.ben.game.util.ObjParser;

import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 5/4/2017.
 */

public class ModelInterleaved extends ModelBase {
    private FloatBuffer interleavedBuf;

    private ModelInterleaved(Builder builder){
        try {
            ObjParser.parse(builder.getContext(), builder.getObjFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] posDat = ObjParser.getExpandedVertexData();
        float[] colDat = null;
        float[] norDat = ObjParser.getExpandedNormalData();
        float[] texDat = null;
        this.setTrisCount(posDat.length / 3);
        if(builder.getColor() != -1){
            colDat = getColorData(getTrisCount(), builder.getColor());
        }
        if(builder.getTexture() != -1){
            texDat = ObjParser.getExpandedTextureData();
        }
        this.interleavedBuf = this.getInterleavedBuffers(posDat, colDat, norDat,
                texDat);
    }

    static class Builder extends ModelBuilder{
        public Builder(Context context, String objFile) {
            super(context, objFile);
        }

        @Override
        ModelInterleaved build() {
            return new ModelInterleaved(this);
        }
    }

    public FloatBuffer getInterleavedBuf() {
        return interleavedBuf;
    }
}
