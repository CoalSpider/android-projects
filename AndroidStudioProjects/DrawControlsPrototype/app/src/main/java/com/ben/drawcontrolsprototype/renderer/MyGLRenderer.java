package com.ben.drawcontrolsprototype.renderer;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.ben.drawcontrolsprototype.R;
import com.ben.drawcontrolsprototype.model.Pillar;
import com.ben.drawcontrolsprototype.model.Quad;
import com.ben.drawcontrolsprototype.util.Point2d;
import com.ben.drawcontrolsprototype.util.ShapeMatching;
import com.ben.drawcontrolsprototype.util.TouchPicker;
import com.ben.drawcontrolsprototype.util.Util;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Camera camera = new Camera();

    private Quad quad = new Quad();

    private Context mActivityContext;

    public MyGLRenderer(Context context) {
        mActivityContext = context;
    }

    // ligh pos
    private int mLightPosHandle;
    // transform matrix
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    // model position info
    private int mPositionHandle;
    // model color info
    private int mColorHandle;
    // model texture info
    private int mTextureUniformHandle;
    // model normal info
    private int mNormalHandle;
    // model texture coord handle
    private int mTextureCoordinateHandle;
    // model texture data handle
    private int mTextureDataHandle;
    // pillar texture handle
    private int mTextureDataHandle2;
    // program handle
    private int mProgramHandle;

    /**
     * This method is called when the surface is first created. if we lose our
     * surface context and it is later recreated by the system.
     **/
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color to gray (0.5, 0.5, 0.5, 0.5)
        // GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        // GLES20.glClearColor(1f, 1f, 1f, 0.5f);
        GLES20.glClearColor(0f, 0f, 0f, 0.5f);
        // use culling
   //     GLES20.glEnable(GLES20.GL_CULL_FACE);
        // enable depth testing
    //    GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        final String vertexShader = ShaderHelper.getShader(mActivityContext, R.raw
                .vertex_shader);
        final String fragmentShader = ShaderHelper.getShader(mActivityContext, R.raw
                .fragment_shader);

        int vertexShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_FRAGMENT_SHADER, fragmentShader);


        // create a program object and store the handle to it
//        String[] attributes = new String[]{"a_Position", "a_Color", "a_Normal",
//                "a_TexCoordinate"};
        String[] attributes = new String[]{"a_Position", "a_Normal","a_TexCoordinate"};
        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle,
                fragmentShaderHandle, attributes);

        // load textures
        mTextureDataHandle = TextureHelper.loadEditableTexture(mActivityContext, R
                .drawable
                .circle_test1);
//        mTextureDataHandle2 = TextureHelper.loadTexture(mActivityContext, R.drawable
 //               .skull);

        // generate mipmap
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        // set filtering for larger than original size
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        // rebind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        // set filtering for smaller than original size
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);

        // Bind the texture to this unit.
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle2);
        // set filtering for larger than original size
 //       GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
//                GLES20.GL_LINEAR);
        // rebind
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle2);
        // set filtering for smaller than original size
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
//                GLES20.GL_LINEAR_MIPMAP_LINEAR);

    }

    Pillar pillar;

    /**
     * This is called whenever the surface changes; for example, when switching from
     * portrait to landscape. It is also called after the surface has been created.
     **/
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // set viewport to same size as the surface
        GLES20.glViewport(0, 0, width, height);

        camera.setWidth(width);
        camera.setHeight(height);
    }

    private boolean swap = false;
    private boolean confirmDraw = false;
    private boolean clearScreen = false;
    private List<Point2d> points = new ArrayList<>();

    private ShapeMatching matcher2 = new ShapeMatching();

    private TouchPicker picker = new TouchPicker();

    /**
     * This is called whenever it’s time to draw a new frame.
     **/
    @Override
    public void onDrawFrame(GL10 unused) {
        if (pillar == null) {
            pillar = new Pillar();
        }
        // | operator = 1 if either bit is 1 zero otherwise
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle,
                "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle,
                "a_TexCoordinate");

        // translate light
        // other stuff for light
        float[] model = pillar.getModelMatrix();
        Matrix.setIdentityM(model, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, camera.getViewMatrix(), 0, model, 0);

        // pass in model*view matrix
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, camera.getProjectionMatrix(), 0, mMVPMatrix, 0);
        System.arraycopy(temp, 0, mMVPMatrix, 0, 16);

        // pass in model*view*projection matrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // pass in light position in eye space

        // set active texture
        // bind texture to this unit
        // tell texture uniform sampler to use this texutre in shader by binding to
        // texutre unit 0

        // Pass in the texture information
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the
        // shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // draw the pillar
        pillar.render(mPositionHandle, mTextureUniformHandle, mNormalHandle);

        if (clearScreen) {
            clearDrawing();
        }
        if (confirmDraw) {
            matchDrawing();
        }
        if (swap) {
            drawToTexture();
            swap = false;
        }
    }

    void matchDrawing() {
        //  int matcherResult = matcher.match(points);
        //    int matcherResult = matcher2.match(points,150,150);
        List<Point2d> p = matcher2.match(points, 150, 150);
        for (Point2d pnt : p) {
            drawPixel(pnt);
            swap = true;
        }
        confirmDraw = false;
    }

    void clearDrawing() {
        TextureHelper.resetMap();
        clearScreen = false;
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, TextureHelper.getBitmap(), 0);
        points.clear();
    }

    /**
     * @return if swap should be set to false
     **/
    void drawToTexture() {
        float[] tex0 = picker.pick2(quad, camera, texX, texY);
        if (tex0 == null) {
            return;
        }
        texX = tex0[0];
        texY = tex0[1];
        int x = (int) (TextureHelper.getWidth() * (texX));
        int y = (int) (TextureHelper.getHeight() * (texY));
        Point2d p = new Point2d(x, y);
        drawPixel(p);
        points.add(p);

        if (xyPairs.size() < 4) {
            return;
        }

        for (int i = 0; i < xyPairs.size() - 3; i += 4) {
            float x1 = xyPairs.get(i);
            float y1 = xyPairs.get(i + 1);
            float x2 = xyPairs.get(i + 2);
            float y2 = xyPairs.get(i + 3);
            float[] tex1 = picker.pick2(quad, camera, x1, y1);
            float[] tex2 = picker.pick2(quad, camera, x2, y2);
            if (tex1 == null || tex2 == null) {
                continue;
            }
            final int startX = (int) (TextureHelper.getWidth() * (tex1[0]));
            final int startY = (int) (TextureHelper.getHeight() * (tex1[1]));
            final int endX = (int) (TextureHelper.getWidth() * (tex2[0]));
            final int endY = (int) (TextureHelper.getHeight() * (tex2[1]));

            List<Point2d> points = Util.drawLine(startX, startY, endX, endY,
                    true, true);
            for (Point2d p2 : points) {
                drawPixel(p2);
            }
        }
        xyPairs.clear();

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, TextureHelper.getBitmap(), 0);
        return;
    }

    private void drawPixel(Point2d p) {
        points.add(p);
        drawPixel(p.x(), p.y(), 2);

    }

    private void drawPixel(float x, float y, int brushWidth) {
        for (int i = 0; i < brushWidth; i++) {
            for (int j = 0; j < brushWidth; j++) {
                TextureHelper.setPixel((int) x + i, (int) y + j,
                        Color.WHITE);
            }
        }
    }


    // stores final combined matrix, passed to shader program
    private float[] mMVPMatrix = new float[16];

    /**
     * draws a quad from the given vertex data
     *
     * @param aQuadBuffer the buffer containing the vertex data
     **/
    private void drawQuad(final FloatBuffer aQuadBuffer) {
        // reset model matrix
        quad.setModelMatrix(new float[16]);
        // Draw the triangle facing straight on.
        Matrix.setIdentityM(quad.getModelMatrix(), 0);
        // pass in the position info
        aQuadBuffer.position(Quad.mPositionOffset);
        GLES20.glVertexAttribPointer(
                mPositionHandle,
                Quad.mPositionDataSize,
                GLES20.GL_FLOAT,
                false,
                Quad.mStrideBytes,
                aQuadBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // pass in color info
        aQuadBuffer.position(Quad.mColorOffset);
        GLES20.glVertexAttribPointer(
                mColorHandle,
                Quad.mColorDataSize,
                GLES20.GL_FLOAT,
                false,
                Quad.mStrideBytes,
                aQuadBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // pass in texture info
        quad.getTexBuffer().position(0);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle,
                Quad.mTexDataSize,
                GLES20.GL_FLOAT,
                false,
                Quad.mBytesPerFloat * Quad.mTexDataSize,
                quad.getTexBuffer());
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // multiply the view matrix by the model matrix and store result in MVP matrix
        Matrix.multiplyMM(mMVPMatrix, 0, camera.getViewMatrix(), 0, quad
                .getModelMatrix(), 0);
        // multiply the model view matrix by the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, camera.getProjectionMatrix(), 0, mMVPMatrix,
                0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, quad.getDrawOrder().length,
                GLES20.GL_UNSIGNED_SHORT, quad.getDrawOrderBuffer());
    }

    Quad getQuad() {
        return this.quad;
    }

    Camera getCamera() {
        return this.camera;
    }

    private float texX;
    private float texY;
    private final List<Float> xyPairs = new ArrayList<>();

    void swapTexture(float x, float y) {
        swap = true;
        this.texX = x;
        this.texY = y;
    }

    void swapTexture(float[] screenXYz) {
        xyPairs.add(screenXYz[0]);
        xyPairs.add(screenXYz[1]);
        xyPairs.add(screenXYz[2]);
        xyPairs.add(screenXYz[3]);
        //   Log.e("swapXYZ", "swap");
        swap = true;
    }

    void setConfirmDraw() {
        confirmDraw = true;
    }

    void setClearScreen() {
        clearScreen = true;
    }
}