package com.ben.game.collisionEngine;

import com.ben.game.collisionEngine.Bounds.Bounds;
import com.ben.game.collisionEngine.Bounds.BoundsType;
import com.ben.game.collisionEngine.Bounds.Circle;
import com.ben.game.collisionEngine.Bounds.Polygon;
import com.ben.game.collisionEngine.Bounds.Rectangle;
import com.ben.game.mazeEngine.MazeGenerator3d;
import com.ben.game.objects.GameObject;
import com.ben.game.util.Vec2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/17/2017.
 */

public class IntersectionResolver {
    public static final float DRAG_SCALAR = 0.9f;

    // CIRCLE, AXIS_RECT, RECTANGLE, POLYGON, SPHERE,AXIS_CUBE,CUBE,POLY3D
    public static void resolve(List<GameObject> objects) {
        List<GameObject>[][] wallGrid = MazeGenerator3d.getWallGrid();
        /* OK knights new plan! make a grid of static geometry, we then check moving
         *  targets agianst only a small subset of the static geometry. It doest
         *  matter with mobs because there will only be a few per maze (10 max
         *  probably)
         *
         *  This can be reduced from 9 to 4 if we check bounds but it may be slower */
       /** TODO: check that we are properly finding long walls to collide with **/


        List<GameObject> nonStatics = new ArrayList<>();
        for (GameObject go : objects) {
            if (go.isStatic() == false) {nonStatics.add(go);}
        }
        // resolve non static collisions
        if (nonStatics.size() > 1) {
            for (int i = 0; i < nonStatics.size(); i++) {
                Bounds boundA = nonStatics.get(i).getBounds();
                for (int j = i + 1; j < nonStatics.size(); j++) {
                    Bounds boundB = nonStatics.get(j).getBounds();
                    doStuff(nonStatics.get(i),nonStatics.get(j));
                }
            }
        }
        ////int iterCount = 0;
        // solve wall collisions
        for (GameObject go : nonStatics) {
            Bounds a = go.getBounds();
            int wallLen = MazeGenerator3d.getWallLen();
            float x = go.getBounds().get2dCenter().x() - MazeGenerator3d.getStartX();
            float y = go.getBounds().get2dCenter().y() - MazeGenerator3d.getStartY();
            int gridX = (int)(x/wallLen);
            int gridY = (int)(y/wallLen);
           // System.out.println("posShift="+gridX+","+gridY);
          //  printCollisionGridIndex(a);
            int minX = Math.max(gridX - 1, 0);
            int maxX = Math.min(gridX + 1, wallGrid[0].length-1);
            int minY = Math.max(gridY - 1, 0);
            int maxY = Math.min(gridY + 1, wallGrid[0].length-1);
           // System.out.println(minX+","+maxX+","+minY+","+maxY);
            for (int i = minY; i <= maxY; i++) {
                for (int j = minX; j <= maxX; j++) {
                    if (wallGrid[j][i] == null) {continue;}
                    for (int k = 0; k < wallGrid[j][i].size(); k++) {
                        Bounds b = wallGrid[j][i].get(k).getBounds();
                    //    if(intersection((Circle)go.getBounds(),(Rectangle)b)){
                    //        System.out.println("wallGrid2["+i+","+j+"]");
                    //    }
                        /////iterCount++;
                        doStuff(go,wallGrid[j][i].get(k));
                    }
                }
            }
        }
        ////System.out.println("loop 1 = " + iterCount);
    }
    /** TEST CODE TO BE REMOVED **/
    private static void printCollisionGridIndex(Bounds a){
        List<GameObject>[][] wallGrid = MazeGenerator3d.getWallGrid();
        for(int i = 0; i < wallGrid.length; i++){
            for(int j = 0; j < wallGrid.length; j++){
                if(wallGrid[i][j] == null)continue;
                for(int k = 0; k < wallGrid[i][j].size(); k++){
                    Bounds b = wallGrid[i][j].get(k).getBounds();
                    if(intersection((Circle)a,(Rectangle)b)){
                        System.out.println("wallGrid["+i+","+j+"]");
                    }
                }
            }
        }
    }

    private static void doStuff(GameObject objA, GameObject objB) {
        Bounds boundA = objA.getBounds();
        Bounds boundB = objB.getBounds();
        int ordinalA = boundA.getBoundsType().ordinal();
        int ordinalB = boundB.getBoundsType().ordinal();
        if (ordinalA == 0 && ordinalB == 0) {
            // circle circle
            Circle a = (Circle) boundA;
            Circle b = (Circle) boundB;
            // circle circle intersection test
            if (intersection(a, b)) {
                resolveAndBounce(objA,a, b);
            }
        } else if (boundA.getBoundsType().ordinal() == 0 && ordinalB == 2) {
            // circle rectangle
            Circle a = (Circle) boundA;
            Rectangle b = (Rectangle) boundB;
            if (intersection(a, b)) {
                resolveAndSlide(objA,a, b);
            }
        } else if (ordinalA == 2 && ordinalB == 0) {
            // rectangle circle
        } else if (ordinalA == 2 && ordinalB == 2) {
            // rectangle rectangle
        }
    }

    private static boolean intersection(Circle a, Circle b) {
        float r = a.getRadius() + b.getRadius();
        float dx = b.get2dCenter().x() - a.get2dCenter().x();
        float dy = b.get2dCenter().y() - a.get2dCenter().y();
        return dx * dx + dy * dy < r * r;
    }

    private static boolean intersection(Circle a, Rectangle b) {
        Vec2f[] verts = b.getVerts();
        Vec2f v0 = verts[0];
        Vec2f v1 = verts[1];
        Vec2f v2 = verts[2];
        Vec2f v3 = verts[3];
        Vec2f aCenter = a.get2dCenter();
        float r2 = a.getRadius()*a.getRadius();
        float dist0 = linePointDistSqrd(aCenter, v3, v0);
        if (dist0 < r2) {return true;}
        float dist1 = linePointDistSqrd(aCenter, v0, v1);
        if (dist1 < r2) {return true;}
        float dist2 = linePointDistSqrd(aCenter, v1, v2);
        if (dist2 < r2) {return true;}
        float dist3 = linePointDistSqrd(aCenter, v2, v3);
        if (dist3 < r2) {return true;}
        return false;
    }

    private static void resolveAndBounce(GameObject objA, Circle a, Circle b) {
        Intersection i = getIntersection(a, b);
        if (i == null) {return;}
        Vec2f push = i.getPushVector();
      //  a.setCenter(a.get2dCenter().add(push));
        objA.setCenter(objA.get2dCenter().add(push));
        Vec2f newAVel = getNewVel(a, b);
        Vec2f newBVel = getNewVel(b, a);
        a.setVelocity(newAVel);
        b.setVelocity(newBVel);
    }

    private static void resolveAndBounce(GameObject objA, Circle a, Rectangle b) {
        Intersection i = getIntersection(a, b);
        if (i == null) {return;}
        Vec2f push = i.getPushVector();
       // a.setCenter(a.get2dCenter().add(push));
        objA.setCenter(objA.get2dCenter().add(push));
        Vec2f velocity = a.getVelocity2d();
        a.setVelocity(wallReflect(i.getNormal(), velocity));
    }

    private static void resolveAndSlide(GameObject objA, Circle a, Rectangle b) {
        Intersection i = getIntersection(a, b);
        if (i == null) {return;}
        Vec2f push = i.getPushVector();
       // a.setCenter(a.get2dCenter().add(push));
        objA.setCenter(objA.get2dCenter().add(push));
        Vec2f velocity = a.getVelocity2d();
        a.setVelocity(wallSlide(i.getLine(), velocity));
    }

    private static Intersection getIntersection(Circle a, Circle b) {
        Vec2f dir = a.get2dCenter().sub(b.get2dCenter());
        float lenSqrd = dir.lenSqrd();
        // circle a center == circle b center so push out in specific direction
        if (lenSqrd == 0) {
            return new Intersection(Math.max(a.getRadius(), b.getRadius()),
                    new Vec2f(1, 0));
        }
        float radius = a.getRadius() + b.getRadius();
        if (dir.lenSqrd() > radius * radius) {
            return null;
        }
        return new Intersection(radius - dir.len(), dir);
    }

    private static Intersection getIntersection(Circle a, Rectangle b) {
        Vec2f[] verts = b.getVerts();
        Vec2f v0 = verts[0];
        Vec2f v1 = verts[1];
        Vec2f v2 = verts[2];
        Vec2f v3 = verts[3];
        Vec2f aCenter = a.get2dCenter();
        Vec2f bCenter = b.get2dCenter();
        float dist0 = linePointDistSqrd(aCenter, v3, v0);
        float dist1 = linePointDistSqrd(aCenter, v0, v1);
        float dist2 = linePointDistSqrd(aCenter, v1, v2);
        float dist3 = linePointDistSqrd(aCenter, v2, v3);
        float min = Math.min(dist0, Math.min(dist1, Math.min(dist2, dist3)));
        // exit early no intersection
        if (min > a.getRadius() * a.getRadius()) {
            return null;
        }
        Vec2f normal;
        Vec2f line;
        if (dist0 == min) {
            normal = getOutwardNormal2(bCenter, v3, v0);
            line = v3.sub(v0);
        } else if (dist1 == min) {
            normal = getOutwardNormal2(bCenter, v0, v1);
            line = v0.sub(v1);
        } else if (dist2 == min) {
            normal = getOutwardNormal2(bCenter, v1, v2);
            line = v1.sub(v2);
        } else {
            normal = getOutwardNormal2(bCenter, v2, v3);
            line = v2.sub(v3);
        }
        float minOverlap = a.getRadius() - (float) Math.sqrt(min);
        return new Intersection(minOverlap, normal, line);
    }

    public static Intersection getIntersection(Circle a, Polygon b) {
        Vec2f[] verts = b.getVerts();
        Vec2f v0 = verts[verts.length - 1];
        Vec2f v1 = verts[0];
        Vec2f line = v0.sub(v1);
        Vec2f normal = getOutwardNormal(b.get2dCenter(), v0, v1);
        float minDist = linePointDistSqrd(a.get2dCenter(), v0, v1);
        float r2 = a.getRadius() * a.getRadius();
        for (int i = 0; i < verts.length - 1; i++) {
            v0 = verts[i];
            v1 = verts[i + 1];
            float dist = linePointDistSqrd(a.get2dCenter(), v0, v1);
            if (dist > r2) {
                continue;
            }
            if (dist < minDist) {
                minDist = dist;
                normal = getOutwardNormal(b.get2dCenter(), v0, v1);
                line = v0.sub(v1);
            }
        }
        //    System.out.println("minDist = "+minDist);
        if (minDist > r2) {
            return null;
        }
        float minOverlap = a.getRadius() - (float) Math.sqrt(minDist);
        return new Intersection(minOverlap, normal, line);
    }

    private static Vec2f getOutwardNormal(Vec2f center, Vec2f p0, Vec2f p1) {
        float dx = p1.x() - p0.x();
        float dy = p1.y() - p0.y();
        if (dx == 0 && dy == 0) {
            throw new RuntimeException("p0 and p1 are the same point");
        }
        // sign of dx and dy doesn't appear to matter but I have not tested fully
        Vec2f normal = new Vec2f(-dy, dx).norm();
        // dir from p0 to center
        Vec2f dir = center.sub(p0);
        // > 0 would be interior normal
        if (normal.dot(dir) > 0) {
            normal = normal.scaleMult(-1);
        }
        return normal;
        // produces same behavior but is many more calculations
        //	return linePointVec(center,p0,p1).sub(center).norm();
    }

    private static Vec2f getOutwardNormal2(Vec2f center, Vec2f p0, Vec2f p1) {
        float dx = p1.x() - p0.x();
        float dy = p1.y() - p0.y();
        if (dx == 0 && dy == 0) {
            throw new RuntimeException("p0 and p1 are the same point");
        }
        // sign of dx and dy doesn't appear to matter but I have not tested fully
        float len = (float) Math.sqrt(-dy * -dy + dx * dx);
        float nx = -dy / len;
        float ny = dx / len;
        // dir from p0 to center
        float dx2 = center.x() - p0.x();
        float dy2 = center.y() - p0.y();
        float dot = dx2 * nx + dy2 * ny;
        // > 0 would be interior normal
        if (dot > 0) {
            nx *= -1;
            ny *= -1;
        }
        return new Vec2f(nx, ny);
    }

    /**
     * @return a vector from p to the closest point between p and the line v,w
     **/
    private static Vec2f linePointVec3(Vec2f p, Vec2f v, Vec2f w) {
        Vec2f wSubV = w.sub(v);
        // Return minimum distance between line segment vw and point p
        float l2 = wSubV.lenSqrd(); // i.e. |w-v|^2 - avoid a sqrt
        if (l2 == 0.0) {
            return p; // v == w case
        }
        // line extending the segment, parameterized as v + t (w - v).
        // find projection of point p onto the line.
        // It falls where t = [(p-v) dot (w-v)] / |w-v|^2
        // clamp t from [0,1] to handle points outside the segment vw.
        float t = Math.max(0, Math.min(p.sub(v).dot(wSubV) / l2, 1));
        // Projection falls on the segment
        return v.add(wSubV.scaleMult(t));
    }

    private static Vec2f linePointVec(Vec2f p, Vec2f v, Vec2f w) {
        float dx = w.x() - v.x();
        float dy = w.y() - v.y();
        float l2 = dx * dx + dy * dy;
        if (l2 == 0.0) {
            return p;
        }
        float dx2 = p.x() - v.x();
        float dy2 = p.y() - v.y();
        float temp = dx2 * dx + dy2 * dy;
        float t = Math.max(0, Math.min(temp / l2, 1));
        return new Vec2f(v.x() + (dx * t), v.y() + (dy * t));
    }

    /**
     * @return the closest point on the line from p0
     **/
    private static float linePointDist(Vec2f p0, Vec2f p1, Vec2f p2) {
        Vec2f proj = linePointVec(p0, p1, p2);
        return proj.sub(p0).len();
    }

    /**
     * @return the closest point on the line from p0
     **/
    private static float linePointDistSqrd3(Vec2f p0, Vec2f p1, Vec2f p2) {
        Vec2f proj = linePointVec(p0, p1, p2);
        return proj.sub(p0).lenSqrd();
    }

    /**
     * @return the closest point on the line from p0
     **/
    private static float linePointDistSqrd(Vec2f p0, Vec2f p1, Vec2f p2) {
        float dx = p2.x() - p1.x();
        float dy = p2.y() - p1.y();
        float l2 = dx * dx + dy * dy;
        if (l2 == 0.0) {
            return p0.lenSqrd();
        }
        float dx2 = p0.x() - p1.x();
        float dy2 = p0.y() - p1.y();
        float temp = dx2 * dx + dy2 * dy;
        float t = Math.max(0, Math.min(temp / l2, 1));
        float newX = p1.x() + (dx * t);
        float newY = p1.y() + (dy * t);
        float x = newX - p0.x();
        float y = newY - p0.y();
        return x * x + y * y;
    }


    /**
     * Bounces the given circles like in pool
     **/
    private static Vec2f getNewVel(Circle a, Circle b) {
        // vNew = v1 - (2*m2 / m1+m2) * (<v1-v2,x1-x2> / ||x1-x2||^2) * (x1-x2)
        // <?,?> == dot product
        // ||?|| == length
        // v1,v2 == velocities
        // x1,x2 == centers
        // m1,m2 == masses
        float massA = a.getRadius();
        float massB = b.getRadius();
        // mass == 1 if a.radius == b.radius
        float mass = (2 * massB) / (massA + massB);
        Vec2f dir = a.get2dCenter().sub(b.get2dCenter());
        float lenSqrd = dir.lenSqrd();
        float dot = a.getVelocity2d().sub(b.getVelocity2d()).dot(dir);
        Vec2f temp = dir.scaleMult(mass * (dot / lenSqrd));
        return a.getVelocity2d().sub(temp);
    }

    private static float scalarProj(Vec2f a, Vec2f b) {
        return a.dot(b) / b.dot(b);
    }

    private static Vec2f vectProj(Vec2f a, Vec2f b) {
        return b.scaleMult(scalarProj(a, b));
    }

    private static Vec2f vectRejection(Vec2f a, Vec2f b) {
        return a.sub(vectProj(a, b));
    }

    /**
     * Returns the new velocity projected along the given line. This produces a
     * "sliding" effect against walls
     *
     * @param line   the wall/line of intersection
     * @param incVel the incident velocity
     * @return the new velocity
     **/
    private static Vec2f wallSlide2(Vec2f line, Vec2f incVel) {
        Vec2f v = vectProj(incVel, line);
        if (v.lenSqrd() == 0) {
            return incVel;
        }
        return v.scaleMult(1.25f);
    }

    private static Vec2f wallSlide(Vec2f line, Vec2f incVel) {
        //b.scaleMult(a.dot(b) / b.dot(b));
        // where b == line and a == incVel
        float scalarProj =
                (incVel.x() * line.x() + incVel.y() * line.y()) / line.lenSqrd();
        float x = line.x() * scalarProj;
        float y = line.y() * scalarProj;
        if (x*x+y*y == 0) {
            return incVel;
        }
        return new Vec2f(x*1.25f,y*1.25f);
    }

    /**
     * Returns the new velocity reflected off the given normal. The produces a
     * "bounce" effect such as in pool;
     *
     * @param normal the normal of the wall/line
     * @param incVel the incident velocity
     * @return the new velocity
     **/
    private static Vec2f wallReflect(Vec2f normal, Vec2f incVel) {
        // vector - 2 * Vector2.Dot(vector, normal) * normal
        return incVel.sub(normal.scaleMult(2 * incVel.dot(normal)));
    }
}
