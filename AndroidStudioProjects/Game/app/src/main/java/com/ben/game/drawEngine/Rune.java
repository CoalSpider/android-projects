package com.ben.game.drawEngine;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.ben.game.Camera;
import com.ben.game.util.ObjParser;
import com.ben.game.util.Util;
import com.ben.game.util.Vec2f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 5/1/2017.
 * <p>
 * This is the object that the player will draw on, this is also the base object for
 * all "magic" in the game
 */
public class Rune {
    private float[] modelMatrix;
    private float[] vertexData;
    private float[] textureData;
    private static final int vertexDataSize = 3;
    private static final int textureDataSize = 2;

    private static final int BYTES_PER_FLOAT = 4;

    private static FloatBuffer posBuf;
    private static FloatBuffer colBuf;
    private static FloatBuffer norBuf;
    private static FloatBuffer texBuf;

    private static int trisCount;

    private List<Vec2f> points;

    public Rune(Context context) {
        modelMatrix = new float[16];
        if (posBuf == null || colBuf == null || norBuf == null || texBuf == null ||
                vertexData == null || textureData == null) {
            try {
                ObjParser.parse(context, "cube_rune_test");
            } catch (IOException e) {
                Log.e("TestQuad", "Error loading TestQuad");
                e.printStackTrace();
            }
            float[] posDat = ObjParser.getExpandedVertexData();
            vertexData = posDat;
            float[] texDat = ObjParser.getExpandedTextureData();
            textureData = texDat;
            float[] norDat = ObjParser.getExpandedNormalData();
            float[] colDat = new float[posDat.length / 3 * 4];
            for (int i = 0; i < colDat.length; i += 4) {
                //colDat[i] = (float)Math.random();
                // colDat[i + 1] = (float)Math.random();
                // colDat[i + 2] = (float)Math.random();
                colDat[i] = 1f;
                colDat[i + 1] = 0f;
                colDat[i + 2] = 0f;
                // slightly opaque
                colDat[i + 3] = 1f;
            }
            trisCount = posDat.length / 3;
            FloatBuffer[] floatBuffers = getBuffersVCTN(posDat, colDat, texDat, norDat);
            this.posBuf = floatBuffers[0];
            this.colBuf = floatBuffers[1];
            this.texBuf = floatBuffers[2];
            this.norBuf = floatBuffers[3];
        }

        points = new ArrayList<>();
    }

    public void addPoint(Vec2f p) {
        points.add(p);
    }

    /**
     * @return new FloatBuffer[]{positionBuffer, textureBuffer, normalBuffer}
     **/
    private FloatBuffer[] getBuffersVCTN(float[] posDat, float[] colDat, float[] texDat,
                                         float[] normDat) {
        final FloatBuffer positionBuffer;
        final FloatBuffer colorBuffer;
        final FloatBuffer normalBuffer;
        final FloatBuffer textureBuffer;

        positionBuffer = ByteBuffer.allocateDirect(posDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(colDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer = ByteBuffer.allocateDirect(texDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer = ByteBuffer.allocateDirect(normDat.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        positionBuffer.put(posDat).position(0);
        colorBuffer.put(colDat).position(0);
        textureBuffer.put(texDat).position(0);
        normalBuffer.put(normDat).position(0);

        return new FloatBuffer[]{positionBuffer, colorBuffer, textureBuffer,
                normalBuffer};
    }

    /**
     * The rune is assumed to have a single non repeating texture mapped to its surface
     * <p>
     * This method casts a ray into the world from screenX,screenY. Intersection with
     * the this rune is checked.
     * </p>
     * <p>
     * If there is a intersection we return the texture coordinate of the
     * intersection. This coordinate will later be colored to mark where the player
     * has drawn.
     * </p>
     * <p>
     * If there is no intersection null is returned;
     * </p>
     *
     * @param camera  the player camera
     * @param screenX the screen X where the player touched
     * @param screenY the screen Y where the player touched
     * @return the texture coordinate point on the rune
     **/
    public float[] pick(Camera camera, float screenX, float
            screenY) throws NullPointerException {
        if (camera == null) {
            throw new NullPointerException("camera is null");
        }
        if (vertexData == null) {
            throw new NullPointerException("vertex data is null");
        }
        float[] origin = new float[]{0, 0, 0, 1};
        float[] inverV = new float[16];
        Matrix.invertM(inverV, 0, camera.getViewMatrix(), 0);
        Matrix.multiplyMV(origin, 0, inverV, 0, origin, 0);
        float[] normal = Util.castRayIntoWorld(screenX, screenY, camera);
        for (int i = 0; i < vertexData.length; i += vertexDataSize * 3) {
            int index1 = i;
            int index2 = index1 + vertexDataSize;
            int index3 = index2 + vertexDataSize;
            // get vertex data
            float[] v1 = getVertexDat(vertexData, index1, true);
            float[] v2 = getVertexDat(vertexData, index2, true);
            float[] v3 = getVertexDat(vertexData, index3, true);
            // convert to world space
            Matrix.multiplyMV(v1, 0, modelMatrix, 0, v1, 0);
            Matrix.multiplyMV(v2, 0, modelMatrix, 0, v2, 0);
            Matrix.multiplyMV(v3, 0, modelMatrix, 0, v3, 0);
            // for each triangle in model
            float[] intersect = Util.rayTriangleIntersect(v1, v2, v3, origin, normal);
            if (intersect != null) {
                // get corresponding vertex
                int index4 = i / 3 * 2;
                int index5 = index4 + textureDataSize;
                int index6 = index5 + textureDataSize;
                // get tex coords
                float[] tex1 = getVertexDat(textureData, index4, false);
                float[] tex2 = getVertexDat(textureData, index5, false);
                float[] tex3 = getVertexDat(textureData, index6, false);
                // DO NOT CHANGE winding order for opengl is v1*w+v2*u+v3*v
                float w = intersect[3];
                float u = intersect[4];
                float v = intersect[5];
                float texX = tex1[0] * w + tex2[0] * u + tex3[0] * v;
                float texY = tex1[1] * w + tex2[1] * u + tex3[1] * v;
                return new float[]{texX, texY};
            }
        }
        return null;
    }

    /**
     * @param dat    an array of shape data
     * @param index  the index in the array
     * @param needsW flag to check if were accessing the texture or vertex data, true
     *               for vertex data false for texture data
     **/
    private float[] getVertexDat(float[] dat, int index, boolean needsW) {
        if (needsW) {
            return new float[]{dat[index], dat[index + 1], dat[index + 2], 1};
        } else {
            return new float[]{dat[index], dat[index + 1]};
        }
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public float[] getVertexData() {
        return vertexData;
    }

    public float[] getTextureData() {
        return textureData;
    }

    public int getVertexDataSize() {
        return vertexDataSize;
    }

    public int getTextureDataSize() {
        return textureDataSize;
    }

    public int getTrisCount() {
        return trisCount;
    }

    public FloatBuffer getPosBuf() {
        return posBuf;
    }

    public FloatBuffer getColBuf() {
        return colBuf;
    }

    public FloatBuffer getTexBuf() {
        return texBuf;
    }

    public FloatBuffer getNorBuf() {
        return norBuf;
    }

    public List<Vec2f> getPoints() {
        return points;
    }

    public void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }
}
