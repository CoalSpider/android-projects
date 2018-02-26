package com.ben.testapp.model;

import android.content.Context;

import com.ben.testapp.collision.AxisBox;
import com.ben.testapp.util.Texture;

import java.nio.FloatBuffer;

/**
 * Created by Ben on 7/24/2017.
 */

class VAO {
    private static final String TAG = "VAO";

    private int triangleCount;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;

    private float[] color;
    private String objFile;
    private Texture texture;

    private AxisBox axisBox;

    // build a model from buffers
    VAO(VAO base, float[] color, Texture texture){
        this.texture = texture;
        triangleCount = base.getTriangleCount();
        vertexBuffer = base.getVertexBuffer();
        axisBox = base.getAxisBox();
        normalBuffer = base.getNormalBuffer();

        this.color = color;
        this.texture = texture;

        if(color != null) {
            float[] colorDat = ModelUtil.generateColorData(triangleCount, color);
            colorBuffer = ModelUtil.toFloatBuffer(colorDat);
        }

        if(texture != null)
            textureBuffer = base.getTextureBuffer();
    }

    VAO(Context context, String objFile, float[] color, Texture texture) {
        this.color = color;
        this.texture = texture;
        this.objFile = objFile;

        parseFile(context);
        initializeBuffers();

        ObjFileParser.nullStaticModelArrays();
    }

    private void parseFile(Context context) {
        ObjFileParser.parse(context, objFile);
    }

    private void initializeBuffers() {
        float[] vertexData = ObjFileParser.getExpandedVertexData();
        axisBox = AxisBox.fromVertexData(vertexData);
        triangleCount = vertexData.length / 3;
        float[] normalData = ObjFileParser.getExpandedNormalData();
        float[] textureData = ObjFileParser.getExpandedTextureData();

        vertexBuffer = ModelUtil.toFloatBuffer(vertexData);
        normalBuffer = ModelUtil.toFloatBuffer(normalData);

        if(texture != null)
            textureBuffer = ModelUtil.toFloatBuffer(textureData);

        if(color != null) {
            float[] colorData = ModelUtil.generateColorData(triangleCount, color);
            colorBuffer = ModelUtil.toFloatBuffer(colorData);
        }
    }


    FloatBuffer getColorBuffer() {
        return colorBuffer;
    }

    FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    int getTriangleCount() {
        return triangleCount;
    }

    boolean isTextured() {
        return texture!=null;
    }

    boolean isColored() {
        return color!=null;
    }

    AxisBox getAxisBox() {
        return axisBox;
    }
}
