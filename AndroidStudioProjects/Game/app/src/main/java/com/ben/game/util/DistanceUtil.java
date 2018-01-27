package com.ben.game.util;

import java.util.List;

/**
 * Created by Ben on 4/19/2017.
 */

public class DistanceUtil {
    public static float pointAndPoint(Vec2f p1, Vec2f p2) {
        return pointAndPoint(p1.x(), p1.y(), p2.x(), p2.y());
    }

    public static float pointAndPoint(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float pointAndLine(Vec2f p, Vec2f p1, Vec2f p2) {
        return pointAndLine(p1.x(),p1.y(),p2.x(),p2.y(), p.x(), p.y());
    }

    /** TODO: we have a better point line dist in eclipse collision code **/
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

    public static float pointAndCircle(Vec2f p, Vec2f center, float radius) {
        return pointAndCircle(center.x(), center.y(), radius, p.x(), p.y());
    }
    /** @return the distance between the outside of a circle and its center
     * if the distance is negative the point lies inside the circle **/
    public static float pointAndCircle(float cX, float cY, float radius, float pX,
                                       float pY) {
        float dx = pX - cX;
        float dy = pY - cY;
        float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
        return dist - radius;
    }

    public static float pointAndPolygon(Vec2f p, Vec2f...verts){
        throw new UnsupportedOperationException("Not implemented");
    }

    public static float pointAndPolygon(Vec2f p, List<Vec2f> verts){
        throw new UnsupportedOperationException("Not implemented");
    }

    public static float circleAndPolygon(Vec2f center, float radius, Vec2f...verts){
        throw new UnsupportedOperationException("Not implemented");
    }

    public static float circleAndPolygon(Vec2f center, float radius, List<Vec2f> verts){
        throw new UnsupportedOperationException("Not implemented");
    }

    public static float circleAndCircle(Vec2f c1, float r1, Vec2f c2, float r2){
        throw new UnsupportedOperationException("Not implemented");
    }

    /** TODO: same stuff but for 3d **/
}
