package com.ben.game.objects;

import android.content.Context;
import android.opengl.GLES20;

import com.ben.game.util.ObjParser;

import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 5/4/2017.
 */

public class ModelVBOI extends ModelBase{
    private int intlvdBufIndx;
    private float[] minMax;
    private float[] modelCenter;

    private ModelVBOI(Builder builder){
        try {
            ObjParser.parse(builder.getContext(), builder.getObjFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] posDat = ObjParser.getExpandedVertexData();
        float x = Float.MAX_VALUE;
        float y = Float.MAX_VALUE;
        float z = Float.MAX_VALUE;
        float X = -Float.MAX_VALUE;
        float Y = -Float.MAX_VALUE;
        float Z = -Float.MAX_VALUE;
        for(int i = 0; i < posDat.length; i+=3){
            float xx = posDat[i];
            float yy = posDat[i+1];
            float zz = posDat[i+2];
            x = (xx < x) ? xx : x;
            X = (xx > X) ? xx : X;
            y = (yy < y) ? yy : y;
            Y = (yy > Y) ? yy : Y;
            z = (zz < z) ? zz : z;
            Z = (zz > Z) ? zz : Z;
        }
        minMax = new float[]{x,y,z,X,Y,Z};
        modelCenter = new float[]{x+(X-x)/2f,y+(Y-y)/2f,z+(Z-z)/2f};
        float[] norDat = ObjParser.getExpandedNormalData();
        this.setTrisCount(posDat.length/3);
        float[] texDat = null;
        float[] colDat = null;
        if(builder.getTexture() != -1){
            texDat = ObjParser.getExpandedTextureData();
        }
        colDat = this.getColorData(this.getTrisCount(),builder.getColor());

        FloatBuffer buffer = this.getInterleavedBuffers(posDat,colDat,norDat,texDat);

        int[] buffers = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() *
                BYTES_PER_FLOAT, buffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        intlvdBufIndx = buffers[0];
    }
    public static class Builder extends ModelBuilder{
        public Builder(Context context, String objFile){
            super(context,objFile);
        }
        @Override
        public ModelVBOI build(){
            return new ModelVBOI(this);
        }
    }

    public int getIntlvdBufIndx() {
        return intlvdBufIndx;
    }

    /** @return min max points {minX,minY,minZ,maxX,maxY,maxZ}**/
    public float[] getMinMax() {
        return minMax;
    }
    /** @return the centerpoint of the model **/
    public float[] getModelCenter() {
        return modelCenter;
    }
}
