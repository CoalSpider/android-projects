package com.ben.testapp.util;

import android.opengl.Matrix;

/**
 * Created by Ben on 8/4/2017.
 */

public class PlayerCamera {
    private float eyeX = 0.0f;
    private float eyeY = 2.0f; // 12
    private float eyeZ = 0.0f;
    private float centerX = 0.0f; // 6
    private float centerY = 0.0f;
    private float centerZ = 0.0f; // 6
    private float upX = 0.0f;
    private float upY = 1.0f;
    private float upZ = 0.0f;

    private final float[] lookAtMatrix = new float[16];
    private boolean lookAtNeedsToUpdate = true;

    private Gyroscope gyroscope = new Gyroscope();
    private Projection projection = new Projection();

    public void setWidthHeight(float width, float height) {
        projection.setWidthHeight(width, height);
        setLookAtMatrix();
        lookAtNeedsToUpdate = false;
    }

    public float[] getProjectionMatrix() {
        return projection.getProjectionMatrix();
    }

    public float[] getLightProjectionMatrix() {
        return projection.getLightProjectionMatrix();
    }

    public float[] getLookAtMatrix() {
        if (lookAtNeedsToUpdate) {
            setLookAtMatrix();
        }
        lookAtNeedsToUpdate = false;
        return lookAtMatrix;
    }

    private void setLookAtMatrix() {
        updateLookAtDir();
        updateUpVector();
        Matrix.setLookAtM(lookAtMatrix, 0,
                eyeX, eyeY, eyeZ,
                centerX, centerY, centerZ,
                upX, upY, upZ);
    }


    public void setTranslation(float x, float y, float z) {
        eyeX = x;
        eyeY = y;
        eyeZ = z;
        lookAtNeedsToUpdate = true;
    }

    /**
     * @param pitch the angle along x axis
     * @param yaw   the angle along y axis
     * @param roll  the angle along z axis
     **/
    public void setPitchYawRoll(float pitch, float yaw, float roll) {
        gyroscope.setPitch(pitch);
        gyroscope.setYaw(yaw);
        gyroscope.setRoll(roll);
        lookAtNeedsToUpdate = true;
    }

    /**
     * The look at dir is a directional vector + eye pos so we are always looking
     * just a little in front of the eye
     **/
    private void updateLookAtDir() {
        float pitch = gyroscope.getPitch();
        float yaw = gyroscope.getYaw();
        centerX = eyeX + (float) (Math.cos(pitch) * Math.cos(yaw));
        centerY = eyeY + (float) (Math.sin(pitch));
        centerZ = eyeZ + (float) (Math.cos(pitch) * Math.sin(yaw));
    }

    /**
     * the up vector controls the cameras roll around the z axis
     * a vector of 0 1 0 is no roll
     **/
    private void updateUpVector() {
        float roll = gyroscope.getRoll();
        upY = (float) Math.cos(roll);
        upZ = (float) Math.sin(roll);
    }

    public float[] getCenterXYZ(){
        updateLookAtDir();
        updateUpVector();
        return new float[]{centerX,centerY,centerZ};
    }
    public float[] getEyeXYZ(){
        updateLookAtDir();
        updateUpVector();
        return new float[]{eyeX,eyeY,eyeZ};
    }
}

