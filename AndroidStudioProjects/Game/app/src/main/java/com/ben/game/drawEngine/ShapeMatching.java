package com.ben.game.drawEngine;

import android.util.Log;

import com.ben.game.util.DistanceUtil;
import com.ben.game.util.Vec2f;

import java.util.List;

/**
 * Created by Ben on 4/26/2017.
 */

public class ShapeMatching {
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

    private static final PointSet circle = PointSetBuilder.buildSet(ShapeEnum.CIRCLE);
    private static final PointSet square = PointSetBuilder.buildSet(ShapeEnum.SQUARE);
    private static final PointSet triangle = PointSetBuilder.buildSet(ShapeEnum.TRIANGLE);

    public ShapeEnum matchShape(List<Vec2f> points) {
        PointSet drawing = new PlayerDrawingPointSet(points);
        // check lines
        boolean isHorizontal = isHorizontal(drawing.getPoints());
        Log.e("match", "isHorizontal=" + isHorizontal);
        boolean isVertical = isVertical(drawing.getPoints());
        Log.e("match", "isVertical=" + isVertical);
        boolean isNegDiag = isDiag(drawing.getPoints(), true);
        Log.e("match", "isNegativeDiagonal=" + isNegDiag);
        boolean isPosDiag = isDiag(drawing.getPoints(), false);
        Log.e("match", "isPositiveDiagonal=" + isPosDiag);
        // check circle
        PointSet drawing1 = new PlayerDrawingPointSet(circle.scaleAndTranslate(drawing));
        float circleMatch = circle.getChiSquaredTestResult(drawing1);
        drawing1 = new PlayerDrawingPointSet(square.scaleAndTranslate(drawing));
        float squareMatch = square.getChiSquaredTestResult(drawing1);
        drawing1 = new PlayerDrawingPointSet(circle.scaleAndTranslate(drawing));
        float triangleMatch = triangle.getChiSquaredTestResult(drawing1);
        Log.e("chiSquared", "circle = " + circleMatch);
        // check square
        Log.e("chiSquared", "square = " + squareMatch);
        // check triangle
        Log.e("chiSquared", "triangle = " + triangleMatch);
        /** TODO: 0.04 seems good
         * TODO: square needs different scaling so rects are less accepted
         * TODO: circle needs editing to not match with squares as easy
         * TODO: triangle needs bump upwards (10+) to match properly **/
        if (Math.min(squareMatch, Math.min(triangleMatch, circleMatch)) >
                MATCH_VAL_MAX) {
            return null;
        } else {
            if(circleMatch < squareMatch && circleMatch < triangleMatch){
                return ShapeEnum.CIRCLE;
            } else if(squareMatch < triangleMatch){
                return ShapeEnum.SQUARE;
            } else {
                return ShapeEnum.TRIANGLE;
            }
        }
    }

    private boolean isHorizontal(List<Vec2f> points) {
        float startY = points.get(0).y();
        for (Vec2f p : points) {
            float dy = Math.abs(p.y() - startY);
            if (dy > HORIZONTAL_DEVIATION) {
                Log.e("horizontal", "" + dy);
                return false;
            }
        }
        return true;
    }

    private boolean isVertical(List<Vec2f> points) {
        float startX = points.get(0).x();
        for (Vec2f p : points) {
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
    private boolean isDiag(List<Vec2f> points, boolean isTopLeftToBottomRight) {
        Vec2f p1 = points.get(0);
        Vec2f p2 = points.get(points.size() - 1);
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
        for (Vec2f p : points) {
            float dist = DistanceUtil.pointAndLine(p,p1,p2);
            if (dist > DIAG_DEVIATION) {
                Log.e("isDiag", "deviation to great..." + dist);
                return false;
            }
        }
        return true;
    }
}
