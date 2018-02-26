package com.ben.drawcontrolsprototype.util;

import android.util.Log;

import com.ben.drawcontrolsprototype.util.DistanceUtil;
import com.ben.drawcontrolsprototype.util.Line2d;
import com.ben.drawcontrolsprototype.util.Point2d;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 4/26/2017.
 */

public class ShapeMatching {
    // TODO LOAD GEOMETRY FROM FILE
    private static final int radius = 150 / 2;
    private static final int centerX = 250 / 2;
    private static final int centerY = 250 / 2;
    private static List<Point2d> circle = new ArrayList<>();
    private static List<Point2d> square = new ArrayList<>();
    private static List<Point2d> triangle = new ArrayList<>();
    /**
     * MAGIC NUMBERS!!!! THEY CONTROL THE TOLERANCE
     **/
    // distance from best fit line in pixels on a 250x250 graphic
    private static final float HORIZONTAL_DEVIATION = 10;
    // distance from best fit line in pixels on a 250x250 graphic
    private static final float VERTICAL_DEVIATION = 10;
    // squared distance from best fit line on a 250x250 graphic
    private static final float DIAG_DEVIATION = 10;
    // slope variance from 1.0 on a 250x250 graphic
    private static final float DIAG_MIN_SLOPE = 0.5f;
    // slope variance from 1.0 on a 250x250 graphic
    private static final float DIAG_MAX_SLOPE = 1.5f;
    // max distance returned from chiSquared with 25 radius bins 60 theta bins on a
    // 250x250 graphic
    private static final float MATCH_VAL_MAX = 0.04f;

    public ShapeMatching() {
        generateCircle();
        generateSquare();
        generateTriangle();
    }

    private float[] histCircle = null;
    private float[] histSquare = null;
    private float[] histTriangle = null;

    public List<Point2d> match(List<Point2d> points, float maxX, float maxY) {
        if(points.size() == 0){
            return null;
        }
        if (histCircle == null) {
            histCircle = histogram(circle, maxX, maxY);
            histSquare = histogram(square, maxX, maxY);
            histTriangle = histogram(triangle, maxX, maxY);
        }
        // check lines
        boolean isHorizontal = isHorizontal(points);
        Log.e("match", "isHorizontal=" + isHorizontal);
        boolean isVertical = isVertical(points);
        Log.e("match", "isVertical=" + isVertical);
        boolean isNegDiag = isDiag(points, true);
        Log.e("match", "isNegativeDiagonal=" + isNegDiag);
        boolean isPosDiag = isDiag(points, false);
        Log.e("match", "isPositiveDiagonal=" + isPosDiag);
        // check circle
        List<Point2d> modified = scaleAndTranslate(circle, points);
        float[] histPoints = histogram(modified, maxX, maxY);
        float circleMatch = chiSquared(histCircle, histPoints);
        modified = scaleAndTranslate(square, points);
        histPoints = histogram(modified, maxX, maxY);
        float squareMatch = chiSquared(histSquare, histPoints);
        modified = scaleAndTranslate(triangle, points);
        /** TODO: better fudging!!!**/
        for(Point2d p : modified){
            modified.set(modified.indexOf(p),new Point2d(p.x(),p.y()+10));
        }
        histPoints = histogram(modified, maxX, maxY);
        float triangleMatch = chiSquared(histTriangle, histPoints);
        Log.e("chiSquared", "circle = " + circleMatch);
        // check square
        Log.e("chiSquared", "square = " + squareMatch);
        // check triangle
        Log.e("chiSquared", "triangle = " + triangleMatch);
        /** TODO: 0.04 seems good
         * TODO: square needs different scaling so rects are less accepted
         * TODO: circle needs editing to not match with squares as easy
         * TODO: triangle needs bump upwards to match properly **/
        if (Math.min(squareMatch, Math.min(triangleMatch, circleMatch)) >
                MATCH_VAL_MAX) {
            Log.e("match", "no match");
        } else {
            Log.e("match", "match");
        }
        return modified;
    }

    private boolean isHorizontal(List<Point2d> points) {
        float startY = points.get(0).y();
        for (Point2d p : points) {
            float dy = Math.abs(p.y() - startY);
            if (dy > HORIZONTAL_DEVIATION) {
                Log.e("horizontal", "" + dy);
                return false;
            }
        }
        return true;
    }

    private boolean isVertical(List<Point2d> points) {
        float startX = points.get(0).x();
        for (Point2d p : points) {
            float dx = Math.abs(p.x() - startX);
            if (dx > VERTICAL_DEVIATION) {
                Log.e("vertical", "" + dx);
                return false;
            }
        }
        return true;
    }

    /**
     * Create line from start to end, get distance of point to that line
     *
     * @param isTopLeftToBottomRight true if diagonal must have a negative slope
     *                               false if the diagonal must have a positive slope
     **/
    private boolean isDiag(List<Point2d> points, boolean isTopLeftToBottomRight) {
        Point2d p1 = points.get(0);
        Point2d p2 = points.get(points.size() - 1);
        Line2d line2d = new Line2d(p1.x(), p1.y(), p2.x(), p2.y());
        float m1 = (p2.y() - p1.y()) / (p2.x() - p1.x());

        // isTopLeftToBottomRight is a flag that determines if we are looking for a
        // diagonal with a negative or positive slope

        // we want to return false if we found a diagonal with the wrong slope
        if (m1 > 0 && isTopLeftToBottomRight) {
            Log.e("isDiag", "wrong slope1");
            return false;
        } else if (m1 < 0 && !isTopLeftToBottomRight) {
            Log.e("isDiag", "wrong slope2");
            return false;
        }

        // if slope is not close to diagonal return false
        m1 = Math.abs(m1);
        if (m1 < DIAG_MIN_SLOPE || m1 > DIAG_MAX_SLOPE) {
            Log.e("isDiag", "outside max slope");
            return false;
        }

        // if any point is to far from the line return false
        for (Point2d p : points) {
            float dist = DistanceUtil.pointAndLine(line2d, p);
            if (dist > DIAG_DEVIATION) {
                Log.e("isDiag", "deviation to great..." + dist);
                return false;
            }
        }
        return true;
    }


    /**
     * @return new float[]{minX,minY,maxX,maxY}
     **/
    private float[] getMinMax(List<Point2d> points) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (Point2d p : points) {
            float x = p.x();
            float y = p.y();
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        return new float[]{minX, minY, maxX, maxY};
    }

    private List<Point2d> scaleAndTranslate(List<Point2d> target, List<Point2d>
            points) {
        List<Point2d> modifedPoints = new ArrayList<>();
        float[] minMaxP = getMinMax(points);
        float[] minMaxT = getMinMax(target);
        float widthP = Math.abs(minMaxP[2] - minMaxP[0]);
        float heightP = Math.abs(minMaxP[3] - minMaxP[1]);
        float widthT = Math.abs(minMaxT[2] - minMaxT[0]);
        float heightT = Math.abs(minMaxT[3] - minMaxT[1]);
        // translate to be centered at 0,0
        float centerX = minMaxP[0] + widthP / 2f;
        float centerY = minMaxP[1] + heightP / 2f;
        // scale to fit scale of target
        float scaleX = widthP / widthT;
        float scaleY = heightP / heightT;
        Log.e("sat", scaleX + "," + scaleY);
        for (Point2d p : points) {
            float newX = p.x() - centerX;
            float newY = p.y() - centerY;
            newX /= scaleX;
            newY /= scaleY;
            newX += 250 / 2;
            newY += 250 / 2;
            modifedPoints.add(new Point2d(newX, newY));
            //         modifedPoints.add(new Point2d(p.x(),p.y()));
        }
        return modifedPoints;
    }

    private void generateCircle() {
        if (circle.size() > 0) {
            circle.clear();
        }
        for (int i = 0; i < 360; i += 1) {
            float x = radius * (float) Math.cos(Math.toRadians(i)) + centerX;
            float y = radius * (float) Math.sin(Math.toRadians(i)) + centerY;
            circle.add(new Point2d(x, y));
        }
    }

    private void generateSquare() {
        if (square.size() > 0) {
            square.clear();
        }
        // topRight = +45 from zero
        // bottomRight = topRight+90
        // bottomLeft = bottomRight+90
        // topLeft = bottomLeft+90
        double topRad = Math.toRadians(45);
        double bottomRad = Math.toRadians(45 + 180);
        float rightX = radius * (float) Math.cos(topRad) + centerX;
        float topY = radius * (float) Math.sin(topRad) + centerY;
        float leftX = radius * (float) Math.cos(bottomRad) + centerX;
        float bottomY = radius * (float) Math.sin(bottomRad) + centerY;
        // 100 points per side
        float stepX = (rightX - leftX) / 100f;
        float stepY = (topY - bottomY) / 100f;
        int iter = 0;
        // top line, bottom line
        for (float i = leftX; i < rightX; i += stepX) {
            iter += 2;
            square.add(new Point2d(i, topY));
            square.add(new Point2d(i, bottomY));
        }
        // left line, right line
        for (float i = bottomY; i < topY; i += stepY) {
            iter += 2;
            square.add(new Point2d(leftX, i));
            square.add(new Point2d(rightX, i));
        }
    }

    private void generateTriangle() {
        if (triangle.size() > 0) {
            triangle.clear();
        }
        // 0 is the right side of circle
        // runs counter clockwise
        // point 1
        double topRad = Math.toRadians(0 + 90);
        float topX = radius * (float) Math.cos(topRad) + centerX;
        float topY = radius * (float) Math.sin(topRad) + centerY;
        // point 2
        double rightRad = Math.toRadians(120 + 90);
        float leftX = radius * (float) Math.cos(rightRad) + centerX;
        float leftY = radius * (float) Math.sin(rightRad) + centerY;
        // point 3
        double leftRad = Math.toRadians(240 + 90);
        float rightX = radius * (float) Math.cos(leftRad) + centerX;
        float rightY = radius * (float) Math.sin(leftRad) + centerY;

        float dx = (rightX - topX) / 100f;
        float dy = (topY - rightY) / 100f;
        // right side
        // generate 100 points for the side
        for (float i = rightX, j = rightY; i > topX || j < topY; i -= dx, j += dy) {
            triangle.add(new Point2d(i, j));
        }
        dx = (rightX - leftX) / 100f;
        // horizontal bottom line
        // generate 100 points for the side
        for (float i = leftX; i < rightX; i += dx) {
            triangle.add(new Point2d(i, leftY));
        }
        dx = (topX - leftX) / 100f;
        dy = (topY - leftY) / 100f;
        // left side
        // generate 100 points for the side
        for (float i = leftX, j = leftY; i < topX || j < topY; i += dx, j += dy) {
            triangle.add(new Point2d(i, j));
        }
    }

    // h sub i (k) =
    // average{q != p sub i : (q - p sub i) is an element of bin(k)}

    // translate so centerpoint (chosen point) is at 0,0

    private float chiSquared(float[] histogram1, float[] histogram2) {
        /** TODO: weighting of bins based on point totals **/
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

    // bin counts for logPolarSqrd histogram r = radius, t = theta(angle)
    // 25 for r and 60 for t seems to work modify as needed
    private final static int rBinCount = 5 * 5;
    private final static int tBinCount = 12 * 5;
    // x axis
    private final static float tBinSize = (float) Math.toRadians(360) / (float)
            tBinCount;

    /**
     * @param points a list of points centered around 0,0 and mapped to -maxX/2,
     *               -maxY/2, maxX/2,maxY/2
     **/
    private float[] histogram(List<Point2d> points, float maxX, float maxY) {
        // histogram uses 12 bins for r, 5 bins for theta
        // get bin size
        float rBinSize = maxX * 2 / (float) rBinCount; // y axis
        float[] binCount = new float[rBinCount * tBinCount];
        // loop through points assigning to bins
        for (Point2d p : points) {
            Point2d logPolar = cartesianToLogPolarSqrd(p.x(), p.y());
            float r = logPolar.x();
            float rBin = rBinSize;
            int indexR = 0;
            while (r > rBin) {
                indexR++;
                rBin += rBinSize;
            }
            float t = logPolar.y();
            float tBin = tBinSize;
            int indexT = 0;
            while (t > tBin) {
                indexT++;
                tBin += tBinSize;
            }
            // y*maxX+x
            int index = indexR * rBinCount + indexT;
            binCount[index] = binCount[index] + 1;
        }
        for (int i = 0; i < binCount.length; i++) {
            // normalize histogram
            if (binCount[i] > 0.0f) {
                binCount[i] = binCount[i] / (float) points.size();
            }
        }
        // bin count populated
        return binCount;
    }

    /**
     * @return new Point2d(distance,angle in radians)
     **/
    private Point2d cartesianToLogPolarSqrd(float x, float y) {
        // log((x-cX)^2+(y-cY)^2)
        float radius = (float) Math.log(x * x + y * y);
        // atan2(y - cY,x - cX)
        float angle = (float) Math.atan2(y, x);
        return new Point2d(radius, angle);
    }

    /**
     * @return new Point2d(distance,angle in radians)
     **/
    private Point2d cartesianToLogPolar(float x, float y) {
        // log(sqr((x-cX)^2+(y-cY)^2))
        float radius = (float) Math.log(Math.sqrt(x * x + y * y));
        // atan2(y - cY,x - cX)
        float angle = (float) Math.atan2(y, x);
        return new Point2d(radius, angle);
    }

    /**
     * @return new Point2d(x,y)
     **/
    private Point2d logPolarToCartesian(float radius, float angle) {
        float r = (float) Math.pow(Math.E, radius);
        float x = r * (float) Math.cos(angle);
        float y = r * (float) Math.sin(angle);
        return new Point2d(x, y);
    }
}
