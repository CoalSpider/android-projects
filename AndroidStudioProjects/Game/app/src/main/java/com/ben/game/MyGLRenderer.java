package com.ben.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.ben.game.collisionEngine.Bounds.Circle;
import com.ben.game.collisionEngine.Bounds.Rectangle;
import com.ben.game.collisionEngine.IntersectionResolver;
import com.ben.game.util.AssetResourceReader;
import com.ben.game.util.Quaternion;
import com.ben.game.mazeEngine.MazeGenerator3d;
import com.ben.game.mazeEngine.MazeType;
import com.ben.game.util.ShaderHelper;
import com.ben.game.util.TextureHelper;
import com.ben.game.util.Vec2f;
import com.ben.game.util.Vec3f;
import com.ben.game.drawEngine.Rune;
import com.ben.game.drawEngine.ShapeEnum;
import com.ben.game.drawEngine.ShapeMatching;
import com.ben.game.objects.GameObject;
import com.ben.game.objects.Player;
import com.ben.game.util.Util;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private final MainActivity mainActivity;
    private final CustomJoystickView customJoystickView;

    // model matrix
    private float[] mModelMatrix = new float[16];
    // MV matrix
    private final float[] mMVMatrix = new float[16];
    // MVP matrix
    private final float[] mMVPMatrix = new float[16];
    // modelView matrix handle
    private int mMVMatrixHandle;
    // modelViewProjection matrix handle
    private int mMVPMatrixHandle;

    // handle to object shader program
    private int mProgramHandle;

    // model handles
    private int mPositionHandle;
    private int mColorHandle;
    private int mNormalHandle;

    // light handles
    private int mLightPositionHandle;
    // handle to light shader program
    private int mLightProgramHandle;
    // lighting
    private final float[] mLightModelMatrix = new float[16];
    private final float[] mLightPosInWorldSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] mLightPosInModelSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] mLightPosInEyeSpace = {0.0f, 0.0f, 0.0f, 1.0f};

    // texture
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;

    // camera holding the view and projection matrix
    private Camera camera;

    // objects
    private Player player;
    private GameObject endPillar;
    private Rune rune;
    private GameObject floor;
    private GameObject ceiling;
    private List<GameObject> objects;
    private List<GameObject> colObjects;
    private MazeGenerator3d generator;


    MyGLRenderer(final MainActivity activity, final
    CustomJoystickView view) {
        mainActivity = activity;
        customJoystickView = view;
    }

    // fps tracker
    private long start = SystemClock.uptimeMillis();
    private long totalFrameTime;
    private float totalFPS;
    private int count;

    private void printFrameTime() {
        long elapsedTime = SystemClock.uptimeMillis() - start;
        totalFrameTime += elapsedTime;
        // get fps in seconds (millisec / 1000)
        totalFPS += 1 / (elapsedTime / 1000f);
        if (count == 100) {
            Log.i("FPS", "averageFPS = " + totalFPS / (float)count);
            Log.i("FPS", "averageFrameTime = " + totalFrameTime / (float)count);
            totalFPS = 0f;
            totalFrameTime = 0l;
            count = 0;
        }
        count++;
        start = SystemClock.uptimeMillis();
    }
    private void loadMaze(Context context){
        Log.i("tag", "loading MAZE");
        // load the maze
        List<GameObject> walls = generator.loadOldMaze(mainActivity);
        Log.e("WALLSIZE", " = " + walls.size());
        for (GameObject g : walls) {
            objects.add(g);
            colObjects.add(g);
            g.setCenter(g.get3dCenter());
        }
    }
    private void loadFloor(Context context){
        Log.i("tag", "loading FLOOR");
        String objFile = "ten_by_ten_quad";
        int color = Color.argb(255, 0, 255, 0);
        int texture = R.drawable.bumpy_bricks_public_domain;
        floor = new GameObject();
        floor.loadModel(context, objFile, color, texture);
        Rectangle r = Rectangle.fromMinMax(floor.getVboi().getMinMax());
        floor.setBounds(r);
        floor.setCenter(0, 0, 0);
        floor.setOrientation(0, 1, 0, (float) Math.toRadians(180));
        Matrix.scaleM(floor.getModelMatrix(), 0, 10, 10, 10);
        objects.add(floor);
        floor.setStatic(true);
    }
    private void loadCeiling(Context context){
        Log.i("tag", "loading CEILING");
        String objFile = "ten_by_ten_quad";
        int color = Color.argb(255, 0, 255, 0);
        int texture = R.drawable.bumpy_bricks_public_domain;
        ceiling = new GameObject();
        ceiling.loadModel(context, objFile, color, texture);
        Rectangle r = Rectangle.fromMinMax(ceiling.getVboi().getMinMax());
        ceiling.setBounds(r);
        ceiling.setCenter(0, MazeGenerator3d.getWallLen(), 0);
        ceiling.setOrientation(0, 0, 1, (float) Math.toRadians(180));
        Matrix.scaleM(ceiling.getModelMatrix(), 0, 10, 10, 10);
        objects.add(ceiling);
        ceiling.setStatic(true);
    }
    private void loadPlayer(Context context){
        Log.i("tag", "PLAYER");
        // the player does not render atm
        String objFile = "player_bounds";
        int color = Color.argb(255, 0, 0, 255);
        int texture = R.drawable.skull;
        player = new Player();
        player.loadModel(context, objFile, color, texture);
        float[] minMax = player.getVboi().getMinMax();
        for (int i = 0; i < minMax.length; i++) {
            minMax[i] *= 0.9f;
        }
        player.setBounds(Circle.fromMinMax(minMax));
        if (resumedCenterPos != null) {
            player.setCenter(resumedCenterPos);
        } else {
            float x = MazeGenerator3d.getStartCellX();
            float z = MazeGenerator3d.getStartCellY();
            player.setCenter(x, 1.5f, z);
        }
        if (resumedOrientation != null) {
            float degrees = (float) Math.toDegrees(resumedOrientation.toAxisAngle().w
                    ());
            customJoystickView.setCurrentDegrees(degrees);
            player.setOrientation(resumedOrientation);
        }
        objects.add(player);
        colObjects.add(0, player);
    }
    private void loadRepawnPillar(Context context){
        Log.i("tag", "ENDPILLAR");
        String objFile = "player_bounds";
        int color = Color.argb(255, 255, 255, 255);
        int texture = R.drawable.bumpy_bricks_public_domain;
        endPillar = new GameObject();
        endPillar.loadModel(context, objFile, color, texture);
        float[] minMax = player.getVboi().getMinMax();
        for (int i = 0; i < minMax.length; i++) {
            minMax[i] *= 0.5f;
        }
        endPillar.setBounds(Circle.fromMinMax(minMax));
        float x = MazeGenerator3d.getEndCellX();
        float z = MazeGenerator3d.getEndCellY();
        endPillar.setCenter(x, 1.5f, z);
        objects.add(endPillar);
    }
    private void loadDrawingMechanics(Context context){
        Log.i("tag", "rune");
        // init rune
        rune = new Rune(context);
        matching = new ShapeMatching();
    }

    private void loadObjects(Context context) {
        objects = new ArrayList<>();
        colObjects = new ArrayList<>();
        generator = new MazeGenerator3d(MazeType.RECURSIVE_BACKTRACKING, 10, 10);
        loadMaze(context);
     //   loadFloor(context);
     //   loadCeiling(context);
        loadPlayer(context);
        loadRepawnPillar(context);
        loadDrawingMechanics(context);
    }

    private void loadPreferences() {
        Context context = mainActivity.getApplicationContext();
        SharedPreferences sharPref = mainActivity.getPreferences(Context
                .MODE_PRIVATE);
        float cX = sharPref.getFloat(context.getString(R.string.PlayerCenterX), 0);
        float cY = sharPref.getFloat(context.getString(R.string.PlayerCenterY), 0);
        float cZ = sharPref.getFloat(context.getString(R.string.PlayerCenterZ), 0);

        float oX = sharPref.getFloat(context.getString(R.string.PlayerOrientationX),
                0);
        float oY = sharPref.getFloat(context.getString(R.string.PlayerOrientationY),
                0);
        float oZ = sharPref.getFloat(context.getString(R.string.PlayerOrientationZ),
                0);
        float oW = sharPref.getFloat(context.getString(R.string.PlayerOrientationW),
                0);

        // if we havnt saved yet
        if (cX == 0 && cY == 0 && cZ == 0) {
            return;
        }
        if (oX == 0 && oY == 0 && oZ == 0 && oW == 0) {
            return;
        }

        resumedCenterPos = new Vec3f(cX, cY, cZ);
        resumedOrientation = new Quaternion(oW, oX, oY, oZ);
    }

    /**
     * This method is called when the surface is first created. if we lose our
     * surface context and it is later recreated by the system.
     **/
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // we only need one shared pref file for now
        loadPreferences();

        Log.i("MyGLRenderer", "surface created");
        camera = new Camera();

        Context context = mainActivity.getApplicationContext();

        loadObjects(context);

        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // cull back faces
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // GLES20.glEnable(GLES20.GL_BLEND);
        //  GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // compile object shader
        final String vertexShader = AssetResourceReader.readShaderFile(mainActivity,
                "pixel_vertex_shader");
        final String fragmentShader = AssetResourceReader.readShaderFile(mainActivity,
                "pixel_fragment_shader");
        final int vertexShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle,
                fragmentShaderHandle,
                new String[]{"a_Position", "a_Color", "a_Normal",
                        "a_TexCoordinate"});

        // compile light shader
        final String lightVertexShader =
                AssetResourceReader.readShaderFile(mainActivity,"point_vertex_shader");
        final String lightFragmentShader =
                AssetResourceReader.readShaderFile(mainActivity,"point_fragment_shader");
        final int lightVertexShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_VERTEX_SHADER, lightVertexShader);
        final int lightFragmentShaderHandle = ShaderHelper.compileShader
                (GLES20.GL_FRAGMENT_SHADER, lightFragmentShader);
        mLightProgramHandle = ShaderHelper.createAndLinkProgram
                (lightVertexShaderHandle, lightFragmentShaderHandle, new
                        String[]{"a_Position"});

        // load texture
        mTextureDataHandle = TextureHelper.loadEditableTexture(context, R.drawable
                .wall_texture);


        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");

        mLightPositionHandle = GLES20.glGetUniformLocation(mProgramHandle,
                "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle,
                "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle,
                "a_TexCoordinate");
    }

    void onPause() {
        // we only need one shared pref file for now
        Context context = mainActivity.getApplicationContext();
        SharedPreferences sharedPref = mainActivity.getPreferences(Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(context.getString(R.string.PlayerCenterX), player.get3dCenter
                ().x());
        editor.putFloat(context.getString(R.string.PlayerCenterY), player.get3dCenter
                ().y());
        editor.putFloat(context.getString(R.string.PlayerCenterZ), player.get3dCenter
                ().z());

        editor.putFloat(context.getString(R.string.PlayerOrientationX), player
                .getOrientation().x());
        editor.putFloat(context.getString(R.string.PlayerOrientationY), player
                .getOrientation().y());
        editor.putFloat(context.getString(R.string.PlayerOrientationZ), player
                .getOrientation().z());
        editor.putFloat(context.getString(R.string.PlayerOrientationW), player
                .getOrientation().w());
        // put stuff
        editor.apply();
    }

    private Vec3f resumedCenterPos = null;
    private Quaternion resumedOrientation = null;

    /**
     * This is called whenever the surface changes; for example, when switching from
     * portrait to landscape. It is also called after the surface has been created.
     **/
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.i("MyGLRenderer", "surface changed");
        // set viewport to same size as the surface
        GLES20.glViewport(0, 0, width, height);

        camera.setWidth(width);
        camera.setHeight(height);
    }

    private long oldTime = 0;

    /**
     * This is called whenever it’s time to draw a new frame.
     **/
    @Override
    public void onDrawFrame(GL10 unused) {
        customJoystickView.draw(customJoystickView.getCanvas());
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        // set active texture to 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // bind texture tho this unit
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        // tell sampler to use this texture
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        Matrix.setIdentityM(mLightModelMatrix, 0);
        // Calculate position of the light. Rotate and then push into the distance.
        //  Matrix.translateM(mLightModelMatrix, 0, 0.0f, 1.0f, -2.0f);
        //  Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        //  Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
        // set light to be on player
        Matrix.translateM(mLightModelMatrix,0,player.get3dCenter().x(),player.get3dCenter().y(),player.get3dCenter().z());
        Matrix.rotateM(mLightModelMatrix,0,player.getOrientation().toAxisAngle().w(),0,1,0);

        // set light matrices
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0,
                mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, camera.getViewMatrix(), 0,
                mLightPosInWorldSpace, 0);

        float timeElapsed = (System.nanoTime() - oldTime) * (1e-9f);
        updateVel(timeElapsed);
        oldTime = System.nanoTime();
        IntersectionResolver.resolve(colObjects);
        // reset player
        resetPlayerAtEnd();
        update();
        draw();

        printFrameTime();
    }

    private void resetPlayerAtEnd() {
        Circle a = (Circle) player.getBounds();
        Circle b = (Circle) endPillar.getBounds();
        float r = a.getRadius() + b.getRadius();
        float dx = b.get2dCenter().x() - a.get2dCenter().x();
        float dy = b.get2dCenter().y() - a.get2dCenter().y();
        if (dx * dx + dy * dy < r * r) {
            repopMaze();
        }
    }

    private void repopMaze() {
        objects.clear();
        List<GameObject> walls = generator.loadNewMaze(mainActivity);
        for (GameObject o : walls) {
            objects.add(o);
        }
        objects.add(player);
        objects.add(floor);
        //  objects.add(ceiling);
        objects.add(endPillar);

        float x = MazeGenerator3d.getStartCellX();
        float y = MazeGenerator3d.getStartCellY();
        player.setCenter(x, y);
        float degs = MazeGenerator3d.getStartDirection();
        customJoystickView.setCurrentDegrees(degs);
        player.setOrientation(0,1,0,(float)Math.toRadians(degs));

        x = MazeGenerator3d.getEndCellX();
        y = MazeGenerator3d.getEndCellY();
        endPillar.setCenter(x, y);
    }

    private void updateVel(float percentSec) {
        float[] controlScheme = customJoystickView.controlScheme(percentSec);
        float angle = (float) Math.toRadians(controlScheme[1]);
        player.setOrientation(0, 1, 0, angle);
        float x = (float) Math.cos(angle) * controlScheme[0];
        float y = (float) Math.sin(angle) * controlScheme[0];
        player.setVelocity(new Vec3f(x, 0, y));
        player.setCenter(player.get3dCenter().add(player.getVelocity3d()));

    }

    private void update() {
        for (GameObject object : colObjects) {
            if (object.getVelocity3d().lenSqrd() != 0) {
                object.translate(object.getVelocity3d());
            }
        }
        Vec3f t = player.get3dCenter();
        Quaternion o = player.getOrientation();
        camera.setTranslation(new Vec3f(t.x(), t.y(), t.z()));
        camera.setOrientation(new Quaternion(o.w(), o.x(), o.y(), o.z()));
    }

    private void draw() {
        for (GameObject object : objects) {
            mModelMatrix = object.getModelMatrix();
            drawModelVBOI(object);
        }


        if (startDraw) {
            drawRune();
        }

        // Draw a point to indicate the light.
        GLES20.glUseProgram(mLightProgramHandle);
        drawLight();
    }

    private void drawLight() {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation
                (mLightProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation
                (mLightProgramHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0],
                mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this
        // attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, camera.getViewMatrix(), 0,
                mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, camera.getProjectionMatrix(), 0, mMVPMatrix,
                0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    private void drawRune(Rune rune) {
        // Pass in the position information
        rune.getPosBuf().position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, rune.getPosBuf());
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        rune.getColBuf().position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                0, rune.getColBuf());
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass in the normal information
        rune.getNorBuf().position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false,
                0, rune.getNorBuf());
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        // pass in texture info
        rune.getTexBuf().position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT,
                false, 0, rune.getTexBuf());
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        initMatries();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, rune.getTrisCount());
    }

    private void initMatries() {
        // model * view
        Matrix.multiplyMM(mMVMatrix, 0, camera.getViewMatrix(), 0, mModelMatrix, 0);
        // pass in MV matrix
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

        // projection * modelView
        Matrix.multiplyMM(mMVPMatrix, 0, camera.getProjectionMatrix(), 0, mMVMatrix,
                0);

        // pass in MVP matrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform3f(mLightPositionHandle, mLightPosInEyeSpace[0],
                mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
    }

    private void drawModelVBOI(GameObject gobj) {
        int stride = (3 + 4 + 3 + 2) * 4;
        int posOff = 0;
        int colOff = (3) * 4;
        int norOff = (3 + 4) * 4;
        int texOff = (3 + 4 + 3) * 4;
        // pass in the position info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gobj.getInterleavedBuffIndx());
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                stride, posOff);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // pass in color info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gobj.getInterleavedBuffIndx());
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, stride,
                colOff);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        // pass in normal info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gobj.getInterleavedBuffIndx());
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false,
                stride, norOff);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        // pass in texture info
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, gobj.getInterleavedBuffIndx());
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT,
                false, stride, texOff);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        initMatries();

        // draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, gobj.getTrisCount());

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    /**
     * draws the player rune
     **/
    private void drawRune() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, -1.0f, 0.0f, 0.0f);

        // update rune model matrix
        rune.setModelMatrix(mModelMatrix);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, TextureHelper.getEditableBitmap()
                , 0);


        ////////////////////// DRAW RUNE //////////////////////////////
        drawRune(rune);
        ///////////////////// END DRAW RUNE //////////////////////////


        if (swap) {
            drawToTexture();
            swap = false;
        } else {
            xyPairs.clear();
        }
        if (clearScreen) {
            clear();
            clearScreen = false;
        }
        if (endDraw) {
            checkDraw();
            endDraw = false;
            startDraw = false;
        }
    }

    /**
     * draws to the rune texture
     **/
    private void drawToTexture() {
        if (xyPairs.size() < 4) {
            return;
        }

        for (int i = 0; i < xyPairs.size() - 3; i += 4) {
            float x1 = xyPairs.get(i);
            float y1 = xyPairs.get(i + 1);
            float x2 = xyPairs.get(i + 2);
            float y2 = xyPairs.get(i + 3);
            float[] tex1 = rune.pick(camera, x1, y1);
            if (tex1 == null) {
                continue;
            }
            float[] tex2 = rune.pick(camera, x2, y2);
            if (tex2 == null) {
                continue;
            }
            final int width = TextureHelper.getEditableBitmap().getWidth();
            final int height = TextureHelper.getEditableBitmap().getHeight();
            final int startX = (int) (width * tex1[0]);
            final int startY = (int) (height * tex1[1]);
            final int endX = (int) (width * tex2[0]);
            final int endY = (int) (height * tex2[1]);
            // create line
            List<Vec2f> points = Util.drawLine(width, height, startX, startY, endX,
                    endY,
                    true, true);
            for (Vec2f p2 : points) {
                drawPixel(p2);
            }
        }
        xyPairs.clear();

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, TextureHelper.getEditableBitmap()
                , 0);
    }

    private void drawPixel(Vec2f p) {
        rune.addPoint(p);
        drawPixel(p.x(), p.y());

    }

    private static final int brushWidth = 2;
    private void drawPixel(float x, float y) {
        int width = TextureHelper.getEditableBitmap().getWidth();
        int height = TextureHelper.getEditableBitmap().getHeight();
        for (int i = 0; i < brushWidth; i++) {
            for (int j = 0; j < brushWidth; j++) {
                if (x + i < 0 || x + i > height) {
                    continue;
                }
                if (y + j < 0 || y + j > width) {
                    continue;
                }
                TextureHelper.setPixel((int) x + i, (int) y + j);
            }
        }
    }

    private final List<Float> xyPairs = new ArrayList<>();
    private boolean swap = false;
    private boolean clearScreen = false;
    private boolean endDraw = false;
    private boolean startDraw = false;

    void swapTexture(float[] screenXYz) {
        xyPairs.add(screenXYz[0]);
        xyPairs.add(screenXYz[1]);
        xyPairs.add(screenXYz[2]);
        xyPairs.add(screenXYz[3]);
        //   Log.e("swapXYZ", "swap");
        swap = true;
    }

    private void clear() {
        TextureHelper.resetEditableMap();
        rune.getPoints().clear();
    }

    private void checkDraw() {
        if (rune.getPoints().size() > 1) {
            ShapeEnum shapeEnum = matching.matchShape(rune.getPoints());
            if (shapeEnum == null) {
                Log.e("checkDraw", "no match");
                return;
            }
            switch (shapeEnum) {
                case CIRCLE:
                    Log.e("checkDraw", "circle");
                    break;
                case TRIANGLE:
                    Log.e("checkDraw", "square");
                    break;
                case SQUARE:
                    Log.e("checkDraw", "triangle");
                    break;
                default:
                    Log.e("checkDraw", "no match");
                    break;
            }
        }
        clear();
    }

    void clearScreen() {
        clearScreen = true;
    }

    private ShapeMatching matching;

    void endDraw() {
        endDraw = true;
    }

    void startDraw() {
        startDraw = true;
    }
}