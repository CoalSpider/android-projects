package com.ben.drawcontrolsprototype.util;

import android.opengl.Matrix;

import com.ben.drawcontrolsprototype.model.Quad;
import com.ben.drawcontrolsprototype.renderer.Camera;

/**
 * Created by Ben on 4/13/2017.
 * <p>
 * This class is used to determine what pixel was clicked in 3d space
 * This is used to prototype "drawing" onto a object in 3d space
 * <p>
 * For now the test object is a single quad facing the camera, later this may be a
 * more complex 3d model
 * <p/>
 */

public class TouchPicker {
    public float[] pick2(Quad quad, Camera camera, float screenX, float screenY) {
        if(quad == null || quad.getModelMatrix() == null){
            return null;
        }
        float[] origin = new float[]{0, 0, 0, 1};
        float[] inverV = new float[16];
        Matrix.invertM(inverV, 0, camera.getViewMatrix(), 0);
        Matrix.multiplyMV(origin, 0, inverV, 0, origin, 0);
        float[] normal = Util.castRayIntoWorld(screenX, screenY, camera);
        int vertStride = Quad.mPositionDataSize + Quad.mColorDataSize;
        float[] vertData = quad.getVerts();
        for (int i = 0; i < quad.getDrawOrder().length; i += 3) {
            int index1 = quad.getDrawOrder()[i];
            int index2 = quad.getDrawOrder()[i + 1];
            int index3 = quad.getDrawOrder()[i + 2];
            // get vertex data
            float[] v1 = getVertexDat(vertData, index1 * vertStride, true);
            float[] v2 = getVertexDat(vertData, index2 * vertStride, true);
            float[] v3 = getVertexDat(vertData, index3 * vertStride, true);
            // convert to world space
            Matrix.multiplyMV(v1, 0, quad.getModelMatrix(), 0, v1, 0);
            Matrix.multiplyMV(v2, 0, quad.getModelMatrix(), 0, v2, 0);
            Matrix.multiplyMV(v3, 0, quad.getModelMatrix(), 0, v3, 0);
            // for each triangle in model
            float[] intersect = Util.rayTriangleIntersect(v1, v2, v3, origin, normal);
            if (intersect != null) {
                float[] texData = quad.getTexCoords();
                int texStride = Quad.mTexDataSize;
                // get tex coords
                float[] tex1 = getVertexDat(texData, index1 * texStride, false);
                float[] tex2 = getVertexDat(texData, index2 * texStride, false);
                float[] tex3 = getVertexDat(texData, index3 * texStride, false);
                // DO NOT CHANGE winding order for opengl is v1*w+v2*u+v3*v
                float w = intersect[3];
                float u = intersect[4];
                float v = intersect[5];
                float texX = tex1[0] * w + tex2[0] * u + tex3[0] * v;
                float texY = tex1[1] * w + tex2[1] * u + tex3[1] * v;
                return new float[]{texX, texY};
            }
        }
        return null;
    }

    /**
     * @param dat    an array of shape data
     * @param index  the index in the array
     * @param needsW flag to check if were accessing the texture or vertex data, true
     *               for vertex data false for texture data
     **/
    private float[] getVertexDat(float[] dat, int index, boolean needsW) {
        if (needsW) {
            return new float[]{dat[index], dat[index + 1], dat[index + 2], 1};
        } else {
            return new float[]{dat[index], dat[index + 1]};
        }
    }


}
