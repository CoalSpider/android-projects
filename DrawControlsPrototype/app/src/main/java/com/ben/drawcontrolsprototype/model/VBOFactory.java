package com.ben.drawcontrolsprototype.model;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.ben.drawcontrolsprototype.R;

import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by Ben on 4/27/2017.
 */

public class VBOFactory {
    private static Context context;

    public VBOFactory(Context context) {
        Log.e("VBOFactory", "init");
        if (context == null) {
            throw new NullPointerException("context is null VBOFactory const");
        }
        this.context = context;
    }

    static VertexBufferObject createVBO(int resourceID) {
        if (context == null) {
            throw new NullPointerException("context is null VBOFactory");
        }
        switch (resourceID) {
            case R.raw.quad:
                return new QuadObject(context);
            case R.raw.skull_pillar:
                return new PillarObject(context);
        }
        return null;
    }
}

class QuadObject extends VertexBufferObject {
    private final int posBufferIdx;
    private final int norBufferIdx;
    private final int texBufferIdx;
    // triangles in object * 3 vertex per triangle
    private final int trisCount;

    QuadObject(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null PillarObject");
        }
        try {
            ObjParser.parse(context, R.raw.skull_pillar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] posDat = ObjParser.getExpandedVertexData();
        float[] texDat = ObjParser.getExpandedTextureData();
        float[] norDat = ObjParser.getExpandedNormalData();
        trisCount = posDat.length;
        // null arrays so they can be garbage collected
        ObjParser.clear();
        FloatBuffer[] floatBuffers = getBuffersVTN(posDat, texDat, norDat);
        FloatBuffer posBuffer = floatBuffers[0];
        FloatBuffer texBuffer = floatBuffers[1];
        FloatBuffer norBuffer = floatBuffers[2];

        final int[] buffers = new int[3];
        GLES20.glGenBuffers(3, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, posBuffer.capacity() *
                BYTES_PER_FLOAT, posBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texBuffer.capacity() *
                BYTES_PER_FLOAT, texBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, norBuffer.capacity() *
                BYTES_PER_FLOAT, norBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        posBufferIdx = buffers[0];
        texBufferIdx = buffers[1];
        norBufferIdx = buffers[2];

        /** code was in example but it doesnt seem needed as were leaving scope**/
        posBuffer.limit(0);
        posBuffer = null;
        texBuffer.limit(0);
        texBuffer = null;
        norBuffer.limit(0);
        norBuffer = null;
    }

    @Override
    public void render(int positionHandle, int textureHandle, int normalHandle) {
        // pass in position
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,posBufferIdx);
        GLES20.glEnableVertexAttribArray(positionHandle);
        // pass in texture
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texBufferIdx);
        GLES20.glEnableVertexAttribArray(textureHandle);
        // pass in normals
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,norBufferIdx);
        GLES20.glEnableVertexAttribArray(normalHandle);

        // clear currently bound buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        // render
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,trisCount);
    }

    @Override
    public void release() {
        // delete buffers from memory
        final int[] buffersToDelete = new int[] {posBufferIdx,texBufferIdx,norBufferIdx};
        GLES20.glDeleteBuffers(buffersToDelete.length,buffersToDelete,0);
    }
}

/**
 * Pillar object with no indexing
 **/
class PillarObject extends VertexBufferObject {
    private final int posBufferIdx;
    private final int norBufferIdx;
    private final int texBufferIdx;
    // triangles in object * 3 vertex per triangle
    private final int trisCount;

    PillarObject(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null PillarObject");
        }
        try {
            ObjParser.parse(context, R.raw.skull_pillar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] posDat = ObjParser.getExpandedVertexData();
        float[] texDat = ObjParser.getExpandedTextureData();
        float[] norDat = ObjParser.getExpandedNormalData();
        trisCount = posDat.length;
        // null arrays so they can be garbage collected
        ObjParser.clear();
        FloatBuffer[] floatBuffers = getBuffersVTN(posDat, texDat, norDat);
        FloatBuffer posBuffer = floatBuffers[0];
        FloatBuffer texBuffer = floatBuffers[1];
        FloatBuffer norBuffer = floatBuffers[2];

        final int[] buffers = new int[3];
        GLES20.glGenBuffers(3, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, posBuffer.capacity() *
                BYTES_PER_FLOAT, posBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texBuffer.capacity() *
                BYTES_PER_FLOAT, texBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, norBuffer.capacity() *
                BYTES_PER_FLOAT, norBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        posBufferIdx = buffers[0];
        texBufferIdx = buffers[1];
        norBufferIdx = buffers[2];

        /** code was in example but it doesnt seem needed as were leaving scope**/
        posBuffer.limit(0);
        posBuffer = null;
        texBuffer.limit(0);
        texBuffer = null;
        norBuffer.limit(0);
        norBuffer = null;
    }

    @Override
    public void render(int positionHandle, int textureHandle, int normalHandle) {
        // pass in position
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,posBufferIdx);
        GLES20.glEnableVertexAttribArray(positionHandle);
        // pass in texture
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texBufferIdx);
        GLES20.glEnableVertexAttribArray(textureHandle);
        // pass in normals
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,norBufferIdx);
        GLES20.glEnableVertexAttribArray(normalHandle);

        // clear currently bound buffer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        // render
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,trisCount);
    }

    @Override
    public void release() {
        // delete buffers from memory
        final int[] buffersToDelete = new int[] {posBufferIdx,texBufferIdx,norBufferIdx};
        GLES20.glDeleteBuffers(buffersToDelete.length,buffersToDelete,0);
    }
}