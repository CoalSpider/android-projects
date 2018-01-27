package com.ben.drawcontrolsprototype.model;

import android.util.Log;

import com.ben.drawcontrolsprototype.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ben on 4/27/2017.
 */

public class Pillar {
    private static VertexBufferObject object;
    private float[] modelMatrix;

    public Pillar() {
        if (object == null) {
            object = VBOFactory.createVBO(R.raw.skull_pillar);
        }
        modelMatrix = new float[16];
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public void render(int positionHandle, int textureHandle, int normalHandle){
        object.render(positionHandle,textureHandle,normalHandle);
    }

    public void release(){
        object.release();
    }
}
