package com.ben.lightingtest.shaderPrograms;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.ben.lightingtest.Camera;
import com.ben.lightingtest.Model;
import com.ben.lightingtest.lights.Light;
import com.ben.lightingtest.lights.PointLight;
import com.ben.lightingtest.lights.SpotLight;
import com.ben.lightingtest.util.AssetResourceReader;
import com.ben.lightingtest.util.ShaderHelper;
import com.ben.lightingtest.util.Vector3;

import java.nio.FloatBuffer;

/**
 * Created by Ben on 7/14/2017.
 */

public class ShadowMapProgram {
    private static final String vertShader = "shadow_vertex_shader";
    private static final String fragShader = "shadow_fragment_shader";
    private static final String[] params = {"a_Position"};

    private int programHandle = -1;
    private int lightMVPHandle = -1;
    private int positionHandle = -1;
    // save to texture
    private int depthTextureHandle = -1;
    // framebuffer
    private int frameBufferHandle = -1;
    // render buffer
    private int renderBufferHandle = -1;

    public void loadProgram(Activity activity) {
        createAndLink(activity);
        passInVariables();
    }

    private void createAndLink(Activity activity) {
        final String shadowVertexShader =
                AssetResourceReader.readShaderFile(activity, vertShader);
        final String shadowFragmentShader =
                AssetResourceReader.readShaderFile(activity, fragShader);
        final int shadowVertexShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_VERTEX_SHADER, shadowVertexShader);
        final int shadowFragmentShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_FRAGMENT_SHADER, shadowFragmentShader);
        programHandle = ShaderHelper.createAndLinkProgram
                (shadowVertexShaderHandle, shadowFragmentShaderHandle, params);
    }

    private void passInVariables() {
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        lightMVPHandle = GLES20.glGetUniformLocation(programHandle, "u_LightMVP");

        if (positionHandle == -1) throw new RuntimeException("shadowPositionHandle");
        if (lightMVPHandle == -1) throw new RuntimeException("lightMVPHandle");
    }

    public void generateTexture(int width, int height) {
        int[] fboId = new int[1];
        int[] depthTextureId = new int[1];
        int[] renderTextureId = new int[1];

        // gen framebuffer object
        GLES20.glGenFramebuffers(1, fboId, 0);

/* render buffer appears to not be needed for this to work */
        // create a render buffer and bind 16 bit depth buffer
        GLES20.glGenRenderbuffers(1, depthTextureId, 0);
        // Bind the render buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        // set the render buffer storage
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

        // gen the texture
        GLES20.glGenTextures(1, renderTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
        // set our texture parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // bind the framebuffer for further processing
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        // we are assuming that the depth texture extension is NOT avaliable
        GLES20.glTexImage2D( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // specify texture as color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);

        // attach the texture to FBO depth attachment point
        // (not supported with gl_texture_2d)
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthTextureId[0]);

/* render buffer appears to not be needed for this to work */
        // attach the texture to FBO depth attachment point
        // (not supported with gl_texture_2d)
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthTextureId[0]);


        // check FBO status
        int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("FBO setup", "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }

        frameBufferHandle = fboId[0];
     //   renderBufferHandle = depthTextureId[0];
        depthTextureHandle = renderTextureId[0];
    }

    /**
     * This pass assumes light and model modelmatricies have already been altered
     **/
    public void shadowPass(Light l, Model m) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        // pass mvp matrix to shader
        GLES20.glUniformMatrix4fv(lightMVPHandle, 0, false, calculateLightMVP(l), 0);

        int vertexSize = 3;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m.getVertexBufferIndex());
        GLES20.glVertexAttribPointer(positionHandle, vertexSize, GLES20.GL_FLOAT,
                false,
                vertexSize * 4, 0);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, m.getTriangleCount());

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private float[] depthMVP = new float[16];

    private float[] calculateLightMVP(Light light) {
        if (light instanceof SpotLight)
            return calculateSpotMVP((SpotLight) light);

        if (light instanceof PointLight)
            return calculatePointMVP((PointLight) light);

        return calculatePlainLightMVP(light);
    }

    private float[] calculatePlainLightMVP2(Light light){
        light.getLightPosition().setIdentityM();
        light.getLightPosition().translateM(-7, 1, -3);
        float[] modelMatrix = light.getLightPosition().getModelMatrix();

        float[] depthProjMatrix = Camera.getLightProjection();

        float[] depthViewMatrix = new float[16];

        float[] worldSpace = light.getLightPosition().getPosInWorldSpace();
        float cX,cY,cZ,eX,eY,eZ,upX,upY,upZ;
        cX = worldSpace[0];
        cY = worldSpace[1];
        cZ = worldSpace[2];
        eX = 0;
        eY = 0;
        eZ = -2;
        upX = 0;
        upY = 1;
        upZ = 0;
        Matrix.setLookAtM(depthViewMatrix,0,cX,cY,cZ,eX,eY,eZ,upX,upY,upZ);
        Camera.setProjection(depthProjMatrix);
        Camera.setLookAtM(depthViewMatrix);


        float[] depthMVP = new float[16];
        float[] depthMV = new float[16];
        Matrix.multiplyMM(depthMV, 0, depthViewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(depthMVP, 0, depthProjMatrix, 0, depthMV, 0);
        this.depthMVP = depthMVP;
        return depthMVP;
    }

    private float[] calculatePlainLightMVP(Light light) {
        light.getLightPosition().setIdentityM();
        light.getLightPosition().translateM(-10, 1, 0);

        float[] modelMatrix = light.getLightPosition().getModelMatrix();

        float[] depthProjMatrix = new float[16];
        // set ortho matrix so it can see the entire scene
        Matrix.orthoM(depthProjMatrix, 0, -10, 10, -10, 10, -10, 10);
        // float[] depthProjMatrix = Camera.getProjectionMatrix();

        float[] depthViewMatrix = new float[16];

        float[] worldSpace = light.getLightPosition().getPosInWorldSpace();
        float[] invDir = new float[]{worldSpace[0], worldSpace[1], worldSpace[2]};
        //invDir[2] = -invDir[2];
        Matrix.setLookAtM(depthViewMatrix, 0, -10, 1.0f, 0, 0, 0,
                0, 0, 1, 0);

    //    Camera.setLookAtM(depthViewMatrix);
    //    Camera.setProjection(depthProjMatrix);

        float[] depthMVP = new float[16];
        float[] depthMV = new float[16];
        Matrix.multiplyMM(depthMV, 0, depthViewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(depthMVP, 0, depthProjMatrix, 0, depthMV, 0);
        this.depthMVP = depthMVP;
        return depthMVP;
    }

    private float[] calculateSpotMVP(SpotLight spotLight) {
        float[] modelMatrix = spotLight.getLightPosition().getModelMatrix();

        float[] depthProjMatrix = Camera.getProjectionMatrix();

        float[] depthViewMatrix = new float[16];
        float[] worldSpace = spotLight.getLightPosition().getPosInWorldSpace();
        float[] invDir = new float[]{worldSpace[0], worldSpace[1], worldSpace[2]};
        Vector3.invert(invDir);
        float[] spotDir = spotLight.getDirection();
        Matrix.setLookAtM(depthViewMatrix, 0,
                invDir[0], invDir[1], invDir[2],
                spotDir[0], spotDir[1], spotDir[2], 0, 1, 0);

        float[] depthMVP = new float[16];
        float[] depthMV = new float[16];
        Matrix.multiplyMM(depthMV, 0, depthViewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(depthMVP, 0, depthProjMatrix, 0, depthMV, 0);
        this.depthMVP = depthMVP;
        return depthMVP;
    }

    private float[] calculatePointMVP(PointLight spotLight) {
        throw new UnsupportedOperationException("shadows for point lights not " +
                "implemneted");
    }

    public float[] getDepthMVP() {
        return depthMVP;
    }

    public int getPositionHandle() {
        return positionHandle;
    }

    public int getDepthTextureHandle() {
        return depthTextureHandle;
    }

    public int getFrameBufferHandle() {
        return frameBufferHandle;
    }

    public int getLightMVPHandle() {
        return lightMVPHandle;
    }

    public int getProgramHandle() {
        return programHandle;
    }
}
