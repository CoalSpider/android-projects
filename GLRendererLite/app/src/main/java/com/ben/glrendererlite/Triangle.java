package com.ben.glrendererlite;

import android.util.Pair;

import com.ben.glrendererlite.util.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.ben.glrendererlite.util.Vec3.*;

/**
 * Created by Ben on 8/23/2017.
 */

public class Triangle {
    private Vec3 a;
    private Vec3 b;
    private Vec3 c;
    private Vec3 centroid;
    private Vec3 normal;

    private List<Vec3> assignedPoints;

    private Vec3 farthestAssignedPoint;

    public Triangle(Vec3 a, Vec3 b, Vec3 c){
        this.a = a;
        this.b = b;
        this.c = c;
        centroid = computeCentroid();
        // set the triangle to face away from origin
        // ie: when looking from the origin to the triangle it will be wound
        // clockwise, in opengl this is the default backface
        lookAwayFrom(new Vec3(0,0,0));
        assignedPoints = new ArrayList<>();
    }

    private Vec3 computeCentroid(){
        float avgX = a.x()+b.x()+c.x();
        float avgY = a.y()+b.y()+c.y();
        float avgZ = a.z()+b.z()+c.z();
        avgX/=3;
        avgY/=3;
        avgZ/=3;
        return new Vec3(avgX,avgY,avgZ);
    }

    /** Modifies the normal to point away from the given point if its not already
     * The winding is also changed so the counter clock wise face is not facing the given point
     * For opengl the default front face is counter clock wise**/
    public void lookAwayFrom(Vec3 point){
        Vec3 AB = sub(b,a);
        Vec3 AC = sub(c,a);
        Vec3 normal = cross(AB,AC);
        Vec3 AP = sub(point,a);
        if(dot(AP,normal) >= 0){
            Vec3 tmp = new Vec3(c.x(),c.y(),c.z());
            Vec3 tmp2 = new Vec3(b.x(),b.y(),b.z());
            b = tmp;
            c = tmp2;
            AB = sub(b,a);
            AC = sub(c,a);
            normal = cross(AB,AC);
        }
        this.normal = normalize(normal);
        this.centroid = computeCentroid();
    }

    public void assignPoint(Vec3 a){
        if(a.isSelected()) return;
        assignedPoints.add(a);
        if(farthestAssignedPoint == null){
            farthestAssignedPoint = a;
        } else {
            if(Math.abs(dot(a,normal)) > Math.abs(dot(farthestAssignedPoint,normal))){
                farthestAssignedPoint = a;
            }
        }
    }

    public List<Vec3> getAssignedPoints() {
        return assignedPoints;
    }

    public Vec3 getFarthestAssignedPoint() {
        return farthestAssignedPoint;
    }

    public Vec3 getCentroid() {
        return centroid;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public Vec3 a() {
        return a;
    }

    public Vec3 b() {
        return b;
    }

    public Vec3 c() {
        return c;
    }
}
