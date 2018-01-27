package com.ben.testapp.util;

import android.opengl.Matrix;

/**
 * Created by Ben on 8/8/2017.
 */
class Projection {
    private float width;
    private float height;
    private float ratio = width / height;

    private final float bottom = -1.0f;
    private final float top = 1.0f;
    private final float near = 1.0f;
    private final float far = 100.0f;
    private float left = -ratio;
    private float right = ratio;
    private final float[] projectionMatrix = new float[16];
    private final float[] lightProjectionMatrix = new float[16];

    void setWidthHeight(float width, float height) {
        this.width = width;
        this.height = height;
        ratio = width / height;
        left = -ratio;
        right = ratio;
        setProjectionMatrix();
        setLightProjectionMatrix();
    }

    private void setProjectionMatrix() {
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private static final float scale = 2.1f;

    private void setLightProjectionMatrix() {
        Matrix.frustumM(lightProjectionMatrix, 0, scale * left, scale * right, scale
                * bottom, scale * top, near, far);
    }

    float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    float[] getLightProjectionMatrix() {
        return lightProjectionMatrix;
    }
}
