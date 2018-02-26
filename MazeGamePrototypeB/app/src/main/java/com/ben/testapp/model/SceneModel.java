package com.ben.testapp.model;

import android.content.Context;
import android.opengl.Matrix;

import com.ben.testapp.collision.AxisBox;
import com.ben.testapp.util.PlayerCamera;
import com.ben.testapp.util.Quaternion;
import com.ben.testapp.util.Texture;
import com.ben.testapp.util.Vector3;

/**
 * Created by Ben on 7/24/2017.
 */

public class SceneModel {
    private VAO model;
    private AxisBox axisBox;

    private float[] modelMatrix = new float[16];
    private float[] translation = {0,0,0};
    private float[] scale = {1,1,1};
    private float[] rotation = new float[16];

    private float[] lightMVMatrix = new float[16];
    private float[] lightMVPMatrix = new float[16];

    private Texture texture;

    public SceneModel(float[] center, float size) {
        translation = center;
        scale = new float[]{size, size, size};
        Matrix.setIdentityM(rotation, 0);

        repopModelMatrix();
    }

    private float[] tempMatrix = new float[16];
    private void repopModelMatrix() {
        // set identity
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.setIdentityM(tempMatrix,0);
        // translate
        Matrix.translateM(modelMatrix,0,translation[0],translation[1],translation[2]);
        // rotate
        Matrix.multiplyMM(tempMatrix,0,modelMatrix,0,rotation,0);
        // scale
        Matrix.scaleM(tempMatrix,0,scale[0],scale[1],scale[2]);

        System.arraycopy(tempMatrix,0,modelMatrix,0,tempMatrix.length);
    }

    /** @param context the application context
     * @param name the obj file name (Do not include .obj extension)
     * @param color the models color, null == no color
     * @param texture the models texture null == no texture **/
    public void loadModel(Context context, String name, float[] color, Texture texture) {
        this.texture = texture;
        model = VAOLoader.loadVAO(context, name, color, texture);
        axisBox = model.getAxisBox();
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public void setNewRotation(float[] rotationMatrix) {
        rotation = rotationMatrix;
        repopModelMatrix();
    }

    public void setLightMVPMatrix(float[] lightViewMatrix, PlayerCamera playerCamera) {
        setLightMVMatrix(lightViewMatrix);
        Matrix.multiplyMM(lightMVPMatrix, 0, playerCamera.getLightProjectionMatrix(), 0,
                lightMVMatrix, 0);
    }

    /**
     * Light view matrix is actually a GL.lookAtM
     * This is based on the models position **/
    /**
     * so light MV is really (lightModel -> GL.lookAtM) * someModel matrix
     **/
    private void setLightMVMatrix(float[] lightViewMatrix) {
        Matrix.multiplyMM(lightMVMatrix, 0, lightViewMatrix, 0, modelMatrix, 0);
    }

    public float[] getLightMVPMatrix() {
        return lightMVPMatrix;
    }

    public boolean isTextured(){
        return model.isTextured();
    }

    public Texture getTexture(){
        return texture;
    }

    public VAO getModel() {
        return model;
    }



    /** TODO: bool flag so we can do multiple things and only reload once per frame **/
    protected void setTranslation(float[] translation) {
        this.translation = translation;
        repopModelMatrix();
    }

    protected float[] getTranslation() {
        return translation;
    }

    public AxisBox getAxisBox() {
        return axisBox;
    }

    public void push(float[] pushVector){
        float[] result = new float[3];
        Vector3.addVV(result,getTranslation(),pushVector);
        setTranslation(result);
    }
}
