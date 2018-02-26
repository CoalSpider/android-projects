package com.ben.drawcontrolsprototype.util;

import com.ben.drawcontrolsprototype.util.Circle2d;
import com.ben.drawcontrolsprototype.util.Line2d;
import com.ben.drawcontrolsprototype.util.Point2d;

/**
 * Created by Ben on 4/19/2017.
 */

public class DistanceUtil {

    /**
     * @param p1 point 1
     * @param p2 point 2
     * @return the distance squared between 2 points
     **/
    public static float pointAndPoint(Point2d p1, Point2d p2) {
        return pointAndPoint(p1.x(), p1.y(), p2.x(), p2.y());
    }

    /**
     * @param l the line
     * @param p the point
     * @return the distance squared between the given line and a given point
     **/
    public static float pointAndLine(Line2d l, Point2d p) {
        return pointAndLine(l.x1(), l.y1(), l.x2(), l.y2(), p.x(), p.y());
    }

    /**
     * @param x1 x of point 1
     * @param y1 y of point 1
     * @param x2 x of point 2
     * @param y2 y of point 2
     * @return the distance between 2 points
     **/
    public static float pointAndPoint(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param c the circle
     * @param p the point
     * @return the distance between the given point and the circles perimeter
     **/
    public static float pointAndCircle(Circle2d c, Point2d p) {
        return pointAndCircle(c.cX(), c.cY(), c.r(), p.x(), p.y());
    }

    /**
     * @param x1 lineStartX
     * @param y1 lineStartY
     * @param x2 lineEndX
     * @param y2 lineEndY
     * @param pX pointX
     * @param pY pointY
     * @return the distance between the given line and a given point
     **/
    public static float pointAndLine(float x1, float y1, float x2, float y2, float
            pX, float pY) {
        // dx/dy of line
        float dx = x2 - x1;
        float dy = y2 - y1;
        if(dx == 0 && dy == 0){
            return pointAndPoint(x1,y1,pX,pY);
        }
        // fudge for horizontal and vertical lines needed
        if (dx == 0) {
            return (float)Math.sqrt((pX - x2)*(pX - x2));
        }
        if (dy == 0) {
            return (float)Math.sqrt((pY - y2)*(pY - y2));
        }
        float m1 = dy / dx;
        float b1 = y1 - m1 * x1;
        // perpendicular slope / line
        float m2 = -(dx / dy);
        float b2 = pY - m2 * pX;
        // x = b2 - b1 / m1 - m2
        // get point where lines intersect
        float iX = (b2 - b1) / (m1 - m2);
        float iY = m2 * iX + b2;
        float distSqrd = (iX - pX) * (iX - pX) + (iY - pY) * (iY - pY);
        return (float)Math.sqrt(distSqrd);
    }

    /**
     * @param cX     circle center X
     * @param cY     circle center Y
     * @param radius the radius of the circle
     * @param pX     pointX
     * @param pY     pointY
     * @return the distance between the given point and the circles perimeter
     **/
    public static float pointAndCircle(float cX, float cY, float radius, float pX,
                                       float pY) {
        float dx = pX - cX;
        float dy = pY - cY;
        float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
        if (dist < radius) {
            // inside circle
            return radius - dist;
        } else {
            // outside circle
            return dist - radius;
        }
    }
}
