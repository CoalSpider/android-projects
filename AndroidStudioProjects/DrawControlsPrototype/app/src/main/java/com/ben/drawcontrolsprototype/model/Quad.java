package com.ben.drawcontrolsprototype.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ben on 4/13/2017.
 *
 * Simple Quad
 */

public class Quad{

    /** vertices with color components (x,y,z + r,g,b,a) **/
    private float[] verts = {
            // X, Y, Z,
            // R, G, B, A

            // v0 red bottom left
            -1f, -1f, 0f,
            1f, 0f, 0f, 1f,

            // v1 blue bottom right
            1f, -1f, 0f,
            0f, 0f, 1f, 1f,

            // v2 green top right
            1f, 1f, 0f,
            0f, 1f, 0f, 1f,

            // v3 white top left
            -1f, 1f, 0f,
            1f, 1f, 1f, 1f
    };

    /** textureCoordinates for the quad **/
    private final float[] texCoords = {
            // v0
            0.0f, 0.0f,
            // v1
            1.0f, 0.0f,
            // v2
            1.0f, 1.0f,
            // v3
            0.0f, 1.0f
    };

    /** draw order of quad, counterclockwise winding**/
    private final short[] drawOrder = {
            // bottom left -> bottom right - top right
            0, 1, 2,
            // bottom left -> top right -> top left
            0, 2, 3
    };

    public float[] modelMatrix;


    public static final int mBytesPerFloat = 4;
    public static final int mBytesPerShort = 2;

    // elements per vertex (x,y,z, r,g,b,a)
    public static final int mStrideBytes = 7 * mBytesPerFloat;
    // offset of the position data
    public static final int mPositionOffset = 0;
    // size of position data in elements
    public static final int mPositionDataSize = 3;
    // offset of the color date
    public static final int mColorOffset = 3;
    // size of color data in elements
    public static final int mColorDataSize = 4;
    // size of texture data
    public static final int mTexDataSize = 2;

    private final FloatBuffer vertBuffer;
    private final FloatBuffer texBuffer;
    private final ShortBuffer drawOrderBuffer;

    public Quad(){
        vertBuffer = ByteBuffer.allocateDirect(verts.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertBuffer.put(verts).position(0);

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length *
                mBytesPerShort).order(ByteOrder.nativeOrder()).asShortBuffer();

        drawOrderBuffer.put(drawOrder).position(0);

        texBuffer = ByteBuffer.allocateDirect(texCoords.length *
                mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texBuffer.put(texCoords).position(0);
    }

    /** TODO: normals **/

    public float[] getVerts(){
        return verts;
    }

    public float[] getTexCoords(){
        return texCoords;
    }

    public short[] getDrawOrder(){
        return drawOrder;
    }

    public FloatBuffer getVertBuffer(){
        return vertBuffer;
    }

    public FloatBuffer getTexBuffer(){
        return texBuffer;
    }

    public ShortBuffer getDrawOrderBuffer(){
        return drawOrderBuffer;
    }

    public float[] getModelMatrix(){return modelMatrix;}

    public void setModelMatrix(float[] modelMatrix){this.modelMatrix = modelMatrix;}

}
