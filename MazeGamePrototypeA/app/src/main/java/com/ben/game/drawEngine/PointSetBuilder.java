package com.ben.game.drawEngine;


import com.ben.game.util.Vec2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 5/3/2017.
 */

class PointSetBuilder {
    // point sets are inscribed in a circle, these control the size and location of
    // that circle
    private static final float radius = 150 / 2f;
    // 250 == graphic size of the texture attached to a Rune object
    private static final float centerX = 250 / 2f;
    private static final float centerY = 250 / 2f;

    PointSetBuilder() {
        // default no-args
    }

    static PointSet buildSet(ShapeEnum name) {
        switch (name) {
            case CIRCLE:
                return generateCircle();
            case SQUARE:
                return generateSquare();
            case TRIANGLE:
                return generateTriangle();
            default:
                // do nothing
                break;
        }
        return null;
    }
    private static void addPoint(float x, float y, List<Vec2f> vects){
        vects.add(new Vec2f(x,y));
    }

    private static CirclePointSet generateCircle() {
        List<Vec2f> circlePoints = new ArrayList<>();
        for (int i = 0; i < 360; i += 1) {
            float x = radius * (float) Math.cos(Math.toRadians(i)) + centerX;
            float y = radius * (float) Math.sin(Math.toRadians(i)) + centerY;
            addPoint(x,y,circlePoints);
        }
        return new CirclePointSet(circlePoints);
    }

    private static SquarePointSet generateSquare() {
        List<Vec2f> squarePoints = new ArrayList<>();
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

        // top line, bottom line
        for (float i = leftX; i < rightX; i += stepX) {
            addPoint(i,topY,squarePoints);
            addPoint(i,bottomY,squarePoints);
        }

        // left line, right line
        for (float i = bottomY; i < topY; i += stepY) {
            addPoint(leftX,i,squarePoints);
            addPoint(rightX,i,squarePoints);
        }

        return new SquarePointSet(squarePoints);
    }

    private static TrianglePointSet generateTriangle() {
        List<Vec2f> trianglePoints = new ArrayList<>();
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
            addPoint(i,j,trianglePoints);
        }
        dx = (rightX - leftX) / 100f;
        // horizontal bottom line
        // generate 100 points for the side
        for (float i = leftX; i < rightX; i += dx) {
            addPoint(i,leftY,trianglePoints);
        }
        dx = (topX - leftX) / 100f;
        dy = (topY - leftY) / 100f;
        // left side
        // generate 100 points for the side
        for (float i = leftX, j = leftY; i < topX || j < topY; i += dx, j += dy) {
            addPoint(i,j,trianglePoints);
        }
        return new TrianglePointSet(trianglePoints);
    }
}
