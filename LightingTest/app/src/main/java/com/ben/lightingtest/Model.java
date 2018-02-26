package com.ben.lightingtest;

import android.content.Context;
import android.opengl.GLES20;

import com.ben.lightingtest.util.ModelUtil;

import java.io.IOException;
import java.nio.FloatBuffer;

public class Model {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer normalBuffer;
    private int vertexBufferIndex;
    private int colorBufferIndex;
    private int normalBufferIndex;
    private int textureBufferIndex;

    private int triangleCount;

    private int color;
    private int texture;
    private String objFile;

    Model(int color, int texture, String objFile) {
        this.color = color;
        this.texture = texture;
        this.objFile = objFile;
    }

    void loadModel(Context context) {
        try {
            ObjParser.parse(context, objFile);
        } catch (IOException e) {
            System.err.println("error loading model");
            e.printStackTrace();
        }
        float[] vertexData = ObjParser.getExpandedVertexData();
        float[] normalData = ObjParser.getExpandedNormalData();
        float[] textureData = ObjParser.getExpandedTextureData();
        triangleCount = vertexData.length / 3;
        float[] colorData = ModelUtil.generateColorData(triangleCount, color);
        // fill our buffers
        vertexBuffer = ModelUtil.toFloatBuffer(vertexData);
        colorBuffer = ModelUtil.toFloatBuffer(colorData);
        normalBuffer = ModelUtil.toFloatBuffer(normalData);
        textureBuffer = ModelUtil.toFloatBuffer(textureData);
        // bind the buffers for opengl
        bindBuffers();
    }

    private void bindBuffers() {
        int[] buffers = bindBuffers(vertexBuffer, colorBuffer, normalBuffer,
                textureBuffer);
        vertexBufferIndex = buffers[0];
        colorBufferIndex = buffers[1];
        normalBufferIndex = buffers[2];
        textureBufferIndex = buffers[3];

    }

    private int[] bindBuffers(FloatBuffer... bufferList) {
        int[] buffers = new int[bufferList.length];
        GLES20.glGenBuffers(bufferList.length, buffers, 0);
        for (int i = 0; i < bufferList.length; i++) {
            bindFloatBuffer(buffers, i, bufferList[i]);
        }
        return buffers;
    }

    private void bindFloatBuffer(int[] buffers, int index, FloatBuffer buffer) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[index]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() *
                ModelUtil.BYTES_PER_FLOAT, buffer, GLES20.GL_STATIC_DRAW);
    }

    public FloatBuffer getColorBuffer() {
        return colorBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public int getVertexBufferIndex() {
        return vertexBufferIndex;
    }

    public int getColorBufferIndex() {
        return colorBufferIndex;
    }

    public int getNormalBufferIndex() {
        return normalBufferIndex;
    }

    public int getTextureBufferIndex() {
        return textureBufferIndex;
    }

    public int getTriangleCount() {
        return triangleCount;
    }
}