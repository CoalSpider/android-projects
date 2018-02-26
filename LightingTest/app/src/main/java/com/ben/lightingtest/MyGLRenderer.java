package com.ben.lightingtest;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.ben.lightingtest.lights.LightRenderer;
import com.ben.lightingtest.shaderPrograms.MainProgram;
import com.ben.lightingtest.shaderPrograms.PointProgram;
import com.ben.lightingtest.shaderPrograms.ShadowMapProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    // model matrix
    private float[] mModelMatrix = new float[16];
    // MV matrix
    private final float[] mMVMatrix = new float[16];
    // MVP matrix
    private final float[] mMVPMatrix = new float[16];

    // camera holding the view and projection matrix
    private Camera camera;
    private Activity activity;
    private Model model;
    // shader programs
    private MainProgram mainProgram;
    private PointProgram pointProgram;
    private LightRenderer lightRenderer;
    private ShadowMapProgram shadowMapProgram;

    private GLSurfaceView view;
    MyGLRenderer(GLSurfaceView view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // set clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // cull back faces
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        int color = Color.argb(255, 0, 125, 0);
        int texture = R.drawable.wall_texture;
        String objFile = "lighting_test_level_smooth";
        model = new Model(color, texture, objFile);
        model.loadModel(activity);

        camera = new Camera();

        mainProgram = new MainProgram();
        pointProgram = new PointProgram();
        lightRenderer = new LightRenderer();

        shadowMapProgram = new ShadowMapProgram();

        mainProgram.loadProgram(activity);

        lightRenderer.setHandles(mainProgram.getProgramHandle());

        pointProgram.loadProgram(activity);
        shadowMapProgram.loadProgram(activity);
        shadowMapProgram.generateTexture(view.getWidth(),view.getHeight());

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // fill the surface
        GLES20.glViewport(0, 0, width, height);
        camera.setHeight(height);
        camera.setWidth(width);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        lightRenderer.updateLights(angleInDegrees);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -2.0f);

        GLES20.glUseProgram(shadowMapProgram.getProgramHandle());

        shadowMapProgram.shadowPass(lightRenderer.getLights()[2],model);

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mainProgram.getProgramHandle());

        initMatricies();

        lightRenderer.passDataToOpenGL();

        GLES20.glUniformMatrix4fv(mainProgram.getDepthMVP(),0,false,shadowMapProgram.getDepthMVP(),0);

        // set active texture to 0
          GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // bind texture tho this unit
          GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMapProgram.getDepthTextureHandle());
        // tell sampler to use this texture
          GLES20.glUniform1i(mainProgram.getUniformTextureHandle(), 0);

        drawVBO(model);

        lightRenderer.drawLights(pointProgram);
    }

    private void initMatricies() {
        int mvHandle = mainProgram.getModelMVHandle();
        int mvpHandle = mainProgram.getModelMVPHandle();
        // model * view
        Matrix.multiplyMM(mMVMatrix, 0, Camera.getViewMatrix(), 0, mModelMatrix, 0);
        // pass in MV matrix
        GLES20.glUniformMatrix4fv(mvHandle, 1, false, mMVMatrix, 0);
        // projection * modelView
        Matrix.multiplyMM(mMVPMatrix, 0, Camera.getProjectionMatrix(), 0, mMVMatrix,
                0);
        // pass in MVP matrix
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mMVPMatrix, 0);
    }

    private void drawVBO(Model model) {
        int vertexSize = 3;
        int colorSize = 4;
        int normalSize = 3;
        int textureSize = 2;
        int posHandle = mainProgram.getPositionHandle();
        int colHandle = mainProgram.getColorHandle();
        int norHandle = mainProgram.getNormalHandle();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, model.getVertexBufferIndex());
        GLES20.glVertexAttribPointer(posHandle, vertexSize, GLES20.GL_FLOAT,
                false,
                vertexSize * 4, 0);
        GLES20.glEnableVertexAttribArray(posHandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, model.getColorBufferIndex());
        GLES20.glVertexAttribPointer(colHandle, colorSize, GLES20.GL_FLOAT, false,
                colorSize * 4, 0);
        GLES20.glEnableVertexAttribArray(colHandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, model.getNormalBufferIndex());
        GLES20.glVertexAttribPointer(norHandle, normalSize, GLES20.GL_FLOAT, false,
                normalSize * 4, 0);
        GLES20.glEnableVertexAttribArray(norHandle);

     /*   GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, model.getTextureBufferIndex());
          GLES20.glVertexAttribPointer(mTextureHandle, textureSize, GLES20.GL_FLOAT,
                 false, textureSize * 4, 0);
        GLES20.glEnableVertexAttribArray(mTextureHandle); */

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, model.getTriangleCount());

     //   GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }


}