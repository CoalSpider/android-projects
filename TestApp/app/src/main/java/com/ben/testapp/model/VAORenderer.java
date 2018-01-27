package com.ben.testapp.model;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.ben.testapp.common.ObjComponentSize.*;

/**
 * Created by Ben on 7/24/2017.
 */

public class VAORenderer {
    private static int positionShaderHandle;
    private static int colorShaderHandle;
    private static int normalShaderHandle;
    private static int textureShaderHandle;

    private VAORenderer() {}

    public static void setPositionShaderHandle(int positionShaderHandle) {
        VAORenderer.positionShaderHandle = positionShaderHandle;
    }

    public static void setColorShaderHandle(int colorShaderHandle) {
        VAORenderer.colorShaderHandle = colorShaderHandle;
    }

    public static void setNormalShaderHandle(int normalShaderHandle) {
        VAORenderer.normalShaderHandle = normalShaderHandle;
    }

    public static void setTextureShaderHandle(int textureShaderHandle) {
        VAORenderer.textureShaderHandle = textureShaderHandle;
    }

    public static void renderShadow(VAO VAO) {
        resetPassEnable(positionShaderHandle,VERTEX.size(), VAO.getVertexBuffer());

        drawArrays(VAO.getTriangleCount());
    }

    public static void render(VAO VAO) {
        resetPassEnable(positionShaderHandle,VERTEX.size(), VAO.getVertexBuffer());

        resetPassEnable(normalShaderHandle,NORMAL.size(), VAO.getNormalBuffer());

        if(VAO.isColored()){
            resetPassEnable(colorShaderHandle, COLOR.size(), VAO.getColorBuffer());
        } else {
            glDisableVertexAttribArray(colorShaderHandle);
        }

        if(VAO.isTextured()){
            resetPassEnable(textureShaderHandle,TEXTURE.size(), VAO.getTextureBuffer());
        } else {
            glDisableVertexAttribArray(textureShaderHandle);
        }

        drawArrays(VAO.getTriangleCount());
    }

    private static void resetPassEnable(int shaderVertexHandle, int size, FloatBuffer data){
        setBufferPositionToZero(data);
        passVertexBufferData(shaderVertexHandle,size,data);
        enableVertexArray(shaderVertexHandle);
    }

    private static void setBufferPositionToZero(FloatBuffer buffer){
        buffer.position(0);
    }

    private static void passVertexBufferData(int shaderVertexHandle, int size,
                                             FloatBuffer bufferData) {
        glVertexAttribPointer(shaderVertexHandle, size, GL_FLOAT,
                false, 0, bufferData);
        glEnableVertexAttribArray(shaderVertexHandle);
    }

    private static void enableVertexArray(int shaderVertexHandle) {
        glEnableVertexAttribArray(shaderVertexHandle);
    }

    private static void drawArrays(int triangleCount) {
        glDrawArrays(GL_TRIANGLES, 0, triangleCount);
    }
}