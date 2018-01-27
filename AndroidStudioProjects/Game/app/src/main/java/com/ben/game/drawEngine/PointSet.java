package com.ben.game.drawEngine;

import com.ben.game.util.Util;
import com.ben.game.util.Vec2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 5/2/2017.
 * <p>
 * A set of 2d points that will be matched against a player drawing. The set of 2d
 * points.
 */

abstract class PointSet {
    // this is actually a pixel size as the player is drawing to a texture
    private static final float MAX_DIST_FROM_CENTER = 250;
    // bin counts for logPolarSqrd histogram r = radius, t = theta(angle)
    // 25 for r and 60 for t seems to work modify as needed
    private static final int R_BIN_COUNT = 5 * 5;
    private static final int T_BIN_COUNT = 12 * 5;
    // length of each bin along the t axis
    private static final float T_BIN_SIZE = (float) Math.toRadians(360) / (float)
            T_BIN_COUNT;
    // length of each bin along the r axis
    private static final float R_BIN_SIZE = MAX_DIST_FROM_CENTER / (float) R_BIN_COUNT;
    private List<Vec2f> points;
    // the histogram for this point set
    private float[] histogram;
    // the width of the point set (maxX-minX), used for scaling
    private float width;
    // the width of the point set (maxX-minX), used for scaling
    private float height;
    // the center of the point set
    private float centerX;
    private float centerY;

    protected PointSet(List<Vec2f> points) {
        this.points = points;
        float[] minMax = getMinMax(points);
        this.width = minMax[2] - minMax[0];
        this.height = minMax[3] - minMax[1];
        this.centerX = minMax[0] + this.width/2f;
        this.centerY = minMax[1] + this.height/2f;
    }

    /**
     * generates a histrogram in log polar sqrd space for the purposes of matching
     * this point set to the player drawn point set
     *
     * @param points a list of points centered around 0,0 and mapped to -maxX/2,
     *               -maxY/2, maxX/2,maxY/2
     **/
    private float[] generateHistrogram(List<Vec2f> points) {
        // histogram uses 12 bins for r, 5 bins for theta
        // get bin size
        float[] binCount = new float[R_BIN_COUNT * T_BIN_COUNT];
        // loop through points assigning to bins
        for (Vec2f p : points) {
            Vec2f logPolar =
                    Util.cartesianToLogPolarSqrd(centerX,centerY,p.x(), p.y());
            float r = logPolar.x();
            float rBin = R_BIN_SIZE;
            int indexR = 0;
            while (r > rBin) {
                indexR++;
                rBin += R_BIN_SIZE;
            }
            float t = logPolar.y();
            float tBin = T_BIN_SIZE;
            int indexT = 0;
            while (t > tBin) {
                indexT++;
                tBin += T_BIN_SIZE;
            }
            // y*maxX+x
            int index = indexR * R_BIN_COUNT + indexT;
            binCount[index] = binCount[index] + 1;
        }
        for (int i = 0; i < binCount.length; i++) {
            // normalize histogram so the bins add up to 1 (this is used to deal with
            // matching a 360 point circle to a 600+ point player drawing)
            if (binCount[i] > 0.0f) {
                binCount[i] = binCount[i] / (float) points.size();
            }
        }
        // bin count populated
        return binCount;
    }

    /**
     * @return new float[]{minX,minY,maxX,maxY}
     **/
    private float[] getMinMax(List<Vec2f> points) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (Vec2f p : points) {
            float x = p.x();
            float y = p.y();
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        return new float[]{minX,minY,maxX,maxY};
    }

    /**
     * scales the given set of points to the target set of points to match the width
     * and height of this set of points and returns the result;
     *
     * @return a list of Point2d's that have been scaled and translated to correspond
     * to the current pointset
     **/
    List<Vec2f> scaleAndTranslate(PointSet set) {
        List<Vec2f> modifedPoints = new ArrayList<>();
        float widthP = set.getWidth();
        float heightP = set.getHeight();
        float widthT = this.getWidth();
        float heightT = this.getHeight();
        float scaleX = widthP / widthT;
        float scaleY = heightP / heightT;
        for (Vec2f p : set.getPoints()) {
            // translate to 0,0
            float newX = p.x() - set.getCenterX();
            float newY = p.y() - set.getCenterY();
            // scale to be the with and height of this point set
            newX /= scaleX;
            newY /= scaleY;
            // translate to the center of this point set
            newX += this.getCenterX();
            newY += this.getCenterY();
            modifedPoints.add(new Vec2f(newX, newY));
        }
        // return the scaled and translated points
        return modifedPoints;
    }

    /** @return the result of a matching test between this and the given point set**/
    float getChiSquaredTestResult(PointSet p){
        float[] histogram1 = this.getHistogram();
        float[] histogram2 = p.getHistogram();
        float sum = 0;
        for (int i = 0; i < histogram1.length; i++) {
            float a = histogram1[i];
            float b = histogram2[i];
            if (a != 0 && b != 0) {
                sum += ((a - b) * (a - b)) / (a + b);
            }
        }
        return sum;
    }

    protected List<Vec2f> getPoints() {
        return points;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float[] getHistogram() {
        if (histogram == null) {
            this.histogram = generateHistrogram(this.points);
        }
        return histogram;
    }
}
class CirclePointSet extends PointSet{
    public CirclePointSet(List<Vec2f> points) {
        super(points);
    }
}
class TrianglePointSet extends PointSet{
    public TrianglePointSet(List<Vec2f> points) {
        super(points);
    }
}
class SquarePointSet extends PointSet{
    public SquarePointSet(List<Vec2f> points) {
        super(points);
    }
}
class PlayerDrawingPointSet extends PointSet{
    public PlayerDrawingPointSet(List<Vec2f> points) {
        super(points);
    }
}