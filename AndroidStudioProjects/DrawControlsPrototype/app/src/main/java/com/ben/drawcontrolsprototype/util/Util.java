package com.ben.drawcontrolsprototype.util;

import android.opengl.Matrix;

import com.ben.drawcontrolsprototype.renderer.Camera;
import com.ben.drawcontrolsprototype.renderer.TextureHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 4/26/2017.
 */

public class Util {
    /**
     * floating point error wont allow for exact 0 most of the time so we use this to
     * check for zeros
     **/
    private static final float EPSILON = 1e-8f;

    /**
     * Casts a ray from screenXY into the world and returns the vector normalized
     *
     * @param x      screenX (player touch x)
     * @param y      screenY (player touch y)
     * @param camera the current camera
     **/
    public static float[] castRayIntoWorld(float x, float y, Camera camera) {
        float halfWidth = camera.getWidth() * 0.5f;
        float halfHeight = camera.getHeight() * 0.5f;
        // camera space coords
        float dx = x - halfWidth;
        float dy = halfHeight - y;
        float dz = -halfHeight / (float) Math.tan(camera.getVerticalFOV() * 0.5f);
        float[] normalVector = {dx, dy, dz, 1};
        // multply by inverted camera to world (view) matrix
        float[] invertedView = new float[16];
        Matrix.invertM(invertedView, 0, camera.getViewMatrix(), 0);
        Matrix.multiplyMV(normalVector, 0, invertedView, 0, normalVector, 0);
        // normalize
        float length = (float) Math.sqrt(normalVector[0] * normalVector[0] +
                normalVector[1] * normalVector[1] + normalVector[2] * normalVector[2]);
        normalVector[0] = normalVector[0] / length;
        normalVector[1] = normalVector[1] / length;
        normalVector[2] = normalVector[2] / length;
        return normalVector;
    }

    /**
     * Ray Triangle Intersection using the Moller-Trumbore algorithm.
     * Note this algorithm does no backface culling
     *
     * @param v1     triangle vertex 1
     * @param v2     triangle vertex 2
     * @param v3     triangle vertex 3
     * @param origin the ray's origin
     * @param dir    the ray's normalized direction vector
     * @return new float[]{w,u,v,intersectX,intersectY,intersectZ} or null
     **/
    public static float[] rayTriangleIntersect(float[] v1, float[] v2, float[] v3,
                                               float[] origin, float[] dir) {
        float[] edge1, edge2; // edge1, edge2
        float[] pVec, qVec, tVec; // pqv
        float det, invDet, u, v, t;
        // find vectors for two edges sharing v1
        edge1 = Vector3d.subVV(v2, v1);
        edge2 = Vector3d.subVV(v3, v1);
        //Begin calculating determinant - also used to calculate u parameter
        pVec = Vector3d.crossVV(dir, edge2);
        //if determinant is near zero, ray lies in plane of triangle or ray is
        // parallel to plane of triangle
        det = Vector3d.dotVV(edge1, pVec);
        // NOT CULLING
        final float EPSILON = (float) 1e-8;
        if (det > -EPSILON && det < EPSILON) {
            return null;
        }
        invDet = 1f / det;
        // calculate distance from v1 to ray origin
        tVec = Vector3d.subVV(origin, v1);
        // calculate u parameter and test bound
        u = Vector3d.dotVV(tVec, pVec) * invDet;
        // The intersection lies outside the triangle
        if (u < 0f || u > 1f) {
            return null;
        }
        // prepare to test v param
        qVec = Vector3d.crossVV(tVec, edge1);
        // calculate v param and test bound
        v = Vector3d.dotVV(dir, qVec) * invDet;
        // intersection lies outside the triangle
        if (v < 0f || u + v > 1f) {
            return null;
        }
        t = Vector3d.dotVV(edge2, qVec) * invDet;
        if (t > EPSILON) { // ray intersection
            float[] intersect = Vector3d.addVV(origin, Vector3d.multVS(dir, t));
            float w = 1 - u - v;
            return new float[]{
                    // intersection point
                    intersect[0], intersect[1], intersect[2],
                    // barycentric coordinates
                    // proper order for opengl ES 2.0 = w u v DO NOT CHANGE
                    w, u, v
            };
        }
        return null;
    }
    // using modified Bresenham line algorithm: credit to https://github
    // .com/ArminJo/STMF3-Discovery-Demos/blob/master/lib/graphics/src/thickLine.cpp
    // for a working example

    /**
     * modified Bresenham with optional overlap (esp. for drawThickLine())
     * Overlap draws additional pixel when changing minor direction - for standard
     * bresenham overlap = LINE_OVERLAP_NONE (0)
     * <pre>
     * Sample line:
     *
     *  00+
     *   -0000+
     *       -0000+
     *           -00
     * </pre>
     * 0 pixels are drawn for normal line without any overlap
     * + pixels are drawn if LINE_OVERLAP_MAJOR
     * - pixels are drawn if LINE_OVERLAP_MINOR
     *
     * @return the xy pairs to draw
     **/
    public static List<Point2d> drawLine(int x0, int y0, int x1, int y1, boolean lineOverlapMajor,
                                         boolean
                                  lineOverlapMinor) {
        float stepX;
        float stepY;
        float dxTimes2;
        float dyTimes2;
        float err;
        // clip to display
        if (x0 >= TextureHelper.getWidth()) {
            x0 = TextureHelper.getWidth() - 1;
        }
        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 >= TextureHelper.getHeight()) {
            y0 = TextureHelper.getWidth() - 1;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (x1 >= TextureHelper.getWidth()) {
            x1 = TextureHelper.getWidth() - 1;
        }
        if (x1 < 0) {
            x1 = 0;
        }
        if (y1 >= TextureHelper.getHeight()) {
            y1 = TextureHelper.getWidth() - 1;
        }
        if (y1 < 0) {
            y1 = 0;
        }
        List<Point2d> xyPairs = new ArrayList<>();
        if (x0 == x1 && y0 == y1) {
            return xyPairs;
        }
        // vertical line
        if (x0 == x1) {
            while (y0 != y1) {
                xyPairs.add(new Point2d(x0,y0));
                y0 += (y0 < y1) ? 1 : -1;
            }
            return xyPairs;
        // horizontal line
        } else if (y0 == y1) {
            while (x0 != x1) {
                xyPairs.add(new Point2d(x0,y0));
                x0 += (x0 < x1) ? 1 : -1;
            }
            return xyPairs;
        }
        float dx = x1 - x0;
        float dy = y1 - y0;
        if (dx < 0) {
            dx = -dx;
            stepX = -1;
        } else {
            stepX = +1;
        }
        if (dy < 0) {
            dy = -dy;
            stepY = -1;
        } else {
            stepY = 1;
        }
        dxTimes2 = dx * 2;
        dyTimes2 = dy * 2;
        xyPairs.add(new Point2d(x0,y0));
        if (dx > dy) {
            // half step in y
            err = dyTimes2 - dx;
            while (x0 != x1) {
                // step in main dir
                x0 += stepX;
                if (err >= 0) {
                    // overlap major
                    if (lineOverlapMajor) {
                        xyPairs.add(new Point2d(x0,y0));
                    }
                    y0 += stepY;
                    // overlap minor
                    if (lineOverlapMinor) {
                        xyPairs.add(new Point2d(x0 - stepX,y0));
                    }
                    err -= dxTimes2;
                }
                err += dyTimes2;
                xyPairs.add(new Point2d(x0,y0));
            }
        } else {
            err = dxTimes2 - dy;
            while (y0 != y1) {
                y0 += stepY;
                if (err >= 0) {
                    // overlap major
                    if (lineOverlapMajor) {
                        xyPairs.add(new Point2d(x0,y0));
                    }
                    x0 += stepX;
                    // overlap minor
                    if (lineOverlapMinor) {
                        xyPairs.add(new Point2d(x0,y0 - stepY));
                    }
                    err -= dyTimes2;
                }
                err += dxTimes2;
                xyPairs.add(new Point2d(x0,y0));
            }
        }
        return  xyPairs;
    }
}
