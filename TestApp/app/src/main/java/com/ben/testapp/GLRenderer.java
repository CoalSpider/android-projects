package com.ben.testapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ben.testapp.AI.Scout;
import com.ben.testapp.collision.Collision;
import com.ben.testapp.collision.CollisionData;
import com.ben.testapp.common.ShadowType;
import com.ben.testapp.lights.Light;
import com.ben.testapp.maze.Maze;
import com.ben.testapp.model.SceneModel;
import com.ben.testapp.sword.Sword;
import com.ben.testapp.model.VAORenderer;
import com.ben.testapp.userInterface.SwordPlane;
import com.ben.testapp.userInterface.UserInterface;
import com.ben.testapp.util.FPSCounter;
import com.ben.testapp.util.PlayerCamera;
import com.ben.testapp.util.Texture;
import com.ben.testapp.util.Vector3;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.ben.testapp.ShaderHandleHolder.*;

public class GLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GLRenderer";

    private final MainActivity mainActivity;

    private float touchRotationXInDegrees;
    private float touchRotationYInDegrees;

    private int viewDisplayWidth;
    private int viewDisplayHeight;

    private static boolean hasOESTextureExtension = false;

    private RenderProgramsHolder renderProgramsHolder;
    private FPSCounter FPSCounter;
    private Light light;
    private DepthMap depthMap;
    private Maze maze;
    private PlayerCamera playerCamera;
    private UserInterface userInterface;
    private SceneModel centerCube;
    private List<SceneModel> sceneModelList;

    GLRenderer(final MainActivity mainActivity, final UserInterface userInterface) {
        this.mainActivity = mainActivity;
        this.userInterface = userInterface;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        setOESTextureExtensionStatus();

        renderProgramsHolder = new RenderProgramsHolder(
                mainActivity,hasOESTextureExtension);

        FPSCounter = new FPSCounter();

        light = new Light();

        depthMap = new DepthMap();
        depthMap.setDepthMapProgram(renderProgramsHolder.getmDepthMapProgram());
        depthMap.setLight(light);

        maze = new Maze();
        playerCamera = new PlayerCamera();

        sceneModelList = new ArrayList<>();
        loadScene();

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    private void setOESTextureExtensionStatus(){
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        if (extensions.contains("OES_depth_texture")){
            Log.e("OES_DEPTH_TEXTURE","setting support to false");
            hasOESTextureExtension = false;
            // hasOESTextureExtension = true;
        }
    }

    private Scout scout;
    private Sword sword;
    private void loadScene(){
        addMCube();

        addGroundPlane(new float[]{0,-5,0},25,new float[]{0.5f,0.5f,0.5f,1f});

        maze.loadMaze(mainActivity,5,5);
        sceneModelList.addAll(maze.getWalls());
        float[] start = maze.getMazeStart();
        userInterface.getMovementHelper().setTranslationXYZ(start[0],start[1],start[2]);

        float[] start2 = maze.getMazeStart();
        scout = new Scout(new float[]{start2[0],start2[1],start2[2]},1);
        scout.loadModel(mainActivity,"enemy_one",new float[]{1f,1f,0f,1f},null);
        sceneModelList.add(scout);
        scout.setMaze(maze);

        Texture swordTex = new Texture(R.drawable.sword_tex,false);
        swordTex.loadTexture(mainActivity);
        sword = new Sword(new float[]{start2[0],start2[1],start2[2]},0.2f);
        sword.loadModel(mainActivity,"sword",null,swordTex);
        sceneModelList.add(sword);

        userInterface.addObserver(sword);
    }

    private void addWall(float[] xyzCenter, float scale, float[] rgbaColor){
        addModel("corner_wall",xyzCenter,scale,rgbaColor,null);
    }

    private void addMCube(){
        Texture mCubeTex = new Texture(R.drawable.brick_tex,false);
        mCubeTex.loadTexture(mainActivity);
        centerCube = new SceneModel(new float[] {0.0f, 0.0f, 0.0f}, 1.5f);
        centerCube.loadModel(mainActivity,"cube_tex",null,mCubeTex);
      //  sceneModelList.add(centerCube);
    }

    private void addModel(String name, float[] xyzCenter, float scale, float[] rgbaColor, Texture texture){
        SceneModel model = new SceneModel(xyzCenter,scale);
        model.loadModel(mainActivity,name,rgbaColor,texture);
        sceneModelList.add(model);
    }

    private void addGroundPlane(float[] xyzCenter, float scale, float[] rgbaColor){
        addModel("plane",xyzCenter,scale,rgbaColor,null);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        viewDisplayWidth = width;
        viewDisplayHeight = height;

        GLES20.glViewport(0, 0, viewDisplayWidth, viewDisplayHeight);

        // Generate buffer where depth values are saved for shadow calculation
        generateShadowFBO();

        playerCamera.setWidthHeight(viewDisplayWidth, viewDisplayHeight);
    }

    void generateShadowFBO() {
        depthMap.generateShadowFBO(viewDisplayWidth, viewDisplayHeight, mainActivity);
    }

    private Collision col = new Collision();
    @Override
    public void onDrawFrame(GL10 unused) {
        // Write FPS information to console
        FPSCounter.logFrame();

        renderProgramsHolder.setRenderProgram(mainActivity);

        setShaderHandles(renderProgramsHolder.getCurrentShaderProgram(),depthMap);

        /******************UPDATE CAMERA / MOVE PLAYER ********************/
        userInterface.getMovementHelper().update();
        float currRot = userInterface.getMovementHelper().getCurrentRotation();
        float[] translate = userInterface.getMovementHelper().getTranslationXYZ();
        playerCamera.setPitchYawRoll(0,(float)Math.toRadians(currRot),0);
        playerCamera.setTranslation(translate[0],translate[1],translate[2]);
        /**********************************************************/
        sword.playAnim(12f/1000f);
        sword.setXYZ(playerCamera.getCenterXYZ());
        sword.setOrient((float)Math.toRadians(currRot));
        /*********************ENEMY UPDATE *************************/
        // update is 12ms
        scout.update(12f/1000f);
        /*********************************************************/
        /** TODO: collision detection **/
        List<CollisionData> collisions = col.getCollisions(sceneModelList);
        for(CollisionData data : collisions){
            SceneModel a = data.getA();
            SceneModel b = data.getB();
            Vector3.normalize(data.getCollisionNormal());
            float[] pushVector = new float[3];
            Vector3.multiplyByScalar(pushVector,data.getCollisionNormal(), data.getPenetration());
            a.push(pushVector);
        }

        float[] lightRotationMatrix = getLightRotationMatrix();
        light.setLightRotation(lightRotationMatrix);
        light.setLookAtM();

        float[] cubeRotationMatrix = getCubeRotationMatrix();
        centerCube.setNewRotation(cubeRotationMatrix);

        // Cull front faces for shadow generation to avoid self shadowing
        GLES20.glCullFace(GLES20.GL_FRONT);
        //GLES20.glCullFace(GLES20.GL_BACK);
        depthMap.renderShadowMap(sceneModelList,playerCamera);

        // Cull back faces for normal render
        GLES20.glCullFace(GLES20.GL_BACK);
        renderScene();

        // Print openGL errors to console
        int debugInfo = GLES20.glGetError();

        if (debugInfo != GLES20.GL_NO_ERROR) {
            String msg = "OpenGL error: " + debugInfo;
            Log.w(TAG, msg);
        }
    }

    private float[] lightRotationMatrix = new float[16];
    private float[] getLightRotationMatrix(){
        // light rotates around Y axis in every 12 seconds
        long elapsedMilliSec = SystemClock.elapsedRealtime();
        long rotationCounter = elapsedMilliSec % 12000L;

        float lightRotationDegree = (360.0f / 12000.0f) * ((int)rotationCounter);

        Matrix.setIdentityM(lightRotationMatrix, 0);
        Matrix.rotateM(lightRotationMatrix, 0, lightRotationDegree, 0.0f, 1.0f, 0.0f);
        return lightRotationMatrix;
    }

    private float[] cubeRotationMatrix = new float[16];
    //Cube rotation with touch events
    private float[] cubeRotationX = new float[16];
    private float[] cubeRotationY = new float[16];
    private float[] getCubeRotationMatrix(){
        Matrix.setRotateM(cubeRotationX, 0, touchRotationXInDegrees, 0, 1.0f, 0);
        Matrix.setRotateM(cubeRotationY, 0, touchRotationYInDegrees, 1.0f, 0, 0);

        Matrix.multiplyMM(cubeRotationMatrix, 0, cubeRotationX, 0, cubeRotationY, 0);
        return cubeRotationMatrix;
    }

    private void renderScene() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(renderProgramsHolder.getCurrentShaderProgram());

        GLES20.glViewport(0, 0, viewDisplayWidth, viewDisplayHeight);

        if(mainActivity.getShadowType() == ShadowType.PCF) {
            //pass stepsize to map nearby points properly to depth map texture - used in PCF algorithm
            GLES20.glUniform1f(shader_MapStepXUniform, (float) (1.0 / depthMap.getShadowMapWidth()));
            GLES20.glUniform1f(shader_MapStepYUniform, (float) (1.0 / depthMap.getShadowMapHeight()));
        }

        //pass in texture where depth map is stored
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(shader_DepthMapTextureUniform, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthMap.getShadowTextureId()[0]);

        for(int i = 0; i < sceneModelList.size(); i++){
            setupAndPassInDataToGPU(sceneModelList.get(i),light);

            renderSceneModel(sceneModelList.get(i));
        }
    }

    // the following fields are used only in
    // setupAndPassInDataToGPU(SceneModel sceneModel, Light light)
    private static float[] tempMVMatrix = new float[16];
    private static float[] tempMVPMatrix = new float[16];
    private static float[] invertedMVMatrix = new float[16];
    private static float[] transposeOfInvertedMVMatrix = new float[16];
    private static float[] depthBiasMVP = new float[16];
    private static final float[] BIAS = {
            0.5f,0.0f,0.0f,0.0f,
            0.0f,0.5f,0.0f,0.0f,
            0.0f,0.0f,5.0f,0.0f,
            0.5f,0.5f,0.5f,0.5f
    };
    private void setupAndPassInDataToGPU(SceneModel sceneModel, Light light){
        // calc model * view matrix
        multiplyMM(tempMVMatrix, playerCamera.getLookAtMatrix(), sceneModel.getModelMatrix());
        // calc model * view * projection matrix
        multiplyMM(tempMVPMatrix,playerCamera.getProjectionMatrix(),tempMVMatrix);
        // calc normal correction matrix
        Matrix.invertM(invertedMVMatrix,0,tempMVMatrix,0);
        Matrix.transposeM(transposeOfInvertedMVMatrix,0,invertedMVMatrix,0);
        // bias fix is needed for depth textures
        if (hasOESTextureExtension){
            multiplyMM(depthBiasMVP, BIAS, sceneModel.getLightMVPMatrix());
            System.arraycopy(depthBiasMVP, 0, sceneModel.getLightMVPMatrix(), 0, 16);
        }

        // pass in model * view matrix
        passInMatrix4fv(shader_MVMatrixUniform,tempMVMatrix);
        // pass in model * view * projection matrix
        passInMatrix4fv(shader_MVPMatrixUniform,tempMVPMatrix);
        // pass in transpose of inverted model view matrix (normal correction)
        passInMatrix4fv(shader_NormalMatrixUniform,transposeOfInvertedMVMatrix);
        // pass in lights position in eye/camera/view space
        passInVec3f(shader_LightPosUniform,light.getLightPosInEyeSpace(playerCamera));
        // pass in light model * view * projection matrix
        passInMatrix4fv(shader_ShadowProjMatrixUniform, sceneModel.getLightMVPMatrix());

        if(sceneModel.isTextured()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glUniform1i(shader_ModelTextureUniform, 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sceneModel.getTexture().getGlGeneratedId());
            // enable model texture
            GLES20.glUniform1i(shader_HasTextureUniform,1);
        } else {
            // disable model texture
            GLES20.glUniform1i(shader_HasTextureUniform,0);
        }
    }

    /** wrapper for {@link Matrix#multiplyMM(float[], int, float[], int, float[], int)}
     * all offsets are == 0**/
    private void multiplyMM(float[] result, float[] lhs, float[] rhs){
        Matrix.multiplyMM(result,0,lhs,0,rhs,0);
    }

    /** wrapper for {@link GLES20#glUniformMatrix4fv(int, int, boolean, float[], int)}
     * count == 1, transpose == false, offset == 0 **/
    private void passInMatrix4fv(int loc, float[] matrix){
        GLES20.glUniformMatrix4fv(loc,1,false,matrix,0);
    }

    /** wrapper for {@link GLES20#glUniform3fv(int, int, float[], int)}
     * count == 1, offset == 0**/
    private void passInVec3f(int loc, float[] vec3f){
        GLES20.glUniform3fv(loc,1,vec3f,0);
    }

    private void renderSceneModel(SceneModel sceneModel){
        VAORenderer.setPositionShaderHandle(shader_PostitionAttribute);
        VAORenderer.setColorShaderHandle(shader_ColorAttribute);
        VAORenderer.setNormalShaderHandle(shader_NormalAttribute);
        VAORenderer.setTextureShaderHandle(shaderTextureCoordAttribute);
        VAORenderer.render(sceneModel.getModel());
    }

    /**
     * Returns the X rotation angle in degrees of the cube.
     */
    float getRotationX() {
        return touchRotationXInDegrees;
    }

    /**
     * Sets the X rotation angle in degrees of the cube.
     */
    void setRotationX(float rotationX) {
        touchRotationXInDegrees = rotationX;
    }

    /**
     * Returns the Y rotation angle in degrees of the cube.
     */
    float getRotationY() {
        return touchRotationYInDegrees;
    }

    /**
     * Sets the Y rotation angle int degrees of the cube.
     */
    void setRotationY(float rotationY) {
        touchRotationYInDegrees = rotationY;
    }

    static boolean getHasOESTextureExtension(){
        return hasOESTextureExtension;
    }
}