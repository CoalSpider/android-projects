package com.ben.game.collisionEngine.Bounds;

import com.ben.game.objects.GameObject;
import com.ben.game.util.Quaternion;
import com.ben.game.util.Vec2f;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 6/17/2017.
 * <p>
 * Base class for 2d/3d bounds
 */

public class Bounds {
    // center point / current translation from 0,0,0
    private Vec3f center3d;
    // center point / current translation from 0,0
    private Vec2f center2d;
    // orientation in 3d space
    private Quaternion orientation;
    // enum type for the bounds
    private final BoundsType type;
    // 3d velocity
    private Vec3f velocity3d;
    // 2d velocity
    private Vec2f velocity2d;

    public Bounds(Vec3f center, Quaternion orientation, BoundsType type) {
        setCenter(center.x(),center.y(),center.z());
        setOrientation(orientation);
        this.type = type;
        setVelocity(0, 0, 0);

    }

    public Bounds(Vec2f center, float rotationInRadians, BoundsType type) {
        setCenter(0,0,0);
        setCenter(center);
        setOrientation(0, 1, 0, rotationInRadians);
        this.type = type;
        setVelocity(0, 0, 0);
    }

    public BoundsType getBoundsType() {
        return type;
    }

    public Vec3f get3dCenter() {
        return center3d;
    }

    public Vec2f get2dCenter() {
        return center2d;
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public Vec2f getVelocity2d() {
        return velocity2d;
    }

    public Vec3f getVelocity3d() {
        return velocity3d;
    }

    /***** CENTER (POSITION) SETTERS *****/

    public void setCenter(float x, float z) {
        this.center2d = new Vec2f(x, z);
        this.center3d = new Vec3f(x, this.center3d.y(), z);
    }

    public void setCenter(float x, float y, float z) {
        this.center2d = new Vec2f(x, z);
        this.center3d = new Vec3f(x, y, z);
    }

    public void setCenter(Vec3f center) {setCenter(center.x(), center.y(), center.z());}

    public void setCenter(Vec2f center) {setCenter(center.x(), center.y());}

    /***** ORIENTATION SETTERS *****/

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }

    public void setOrientation(float x, float y, float z, float angleInRadians)
    {setOrientation(Quaternion.fromAxisAngle(x, y, z, angleInRadians));}

    /***** VELOCITY SETTERS *****/

    public void setVelocity(float x, float z) {
        this.velocity2d = new Vec2f(x, z);
        this.velocity3d = new Vec3f(x, velocity3d.y(), z);
    }

    public void setVelocity(float x, float y, float z){
        this.velocity2d = new Vec2f(x, z);
        this.velocity3d = new Vec3f(x, y, z);
    }

    public void setVelocity(Vec2f v) {
        setVelocity(v.x(),v.y());
    }

    public void setVelocity(Vec3f v) {
        setVelocity(v.x(),v.y(),v.z());
    }

    /********** ROTATE **********/

    public void rotate(Quaternion quaternion) {
        this.orientation = this.orientation.mult(quaternion);
    }

    public void rotate(float x, float y, float z, float angleInRadians) {
        rotate(Quaternion.fromAxisAngle(x, y, z, angleInRadians));
    }

    /********* TRANSLATE ***********/

    public void translate(float x, float y, float z){
        setCenter(center3d.add(new Vec3f(x,y,z)));
    }

    public void translate(float x, float z){
        setCenter(center2d.add(new Vec2f(x,z)));
    }

    public void translate(Vec3f translation){
        setCenter(center3d.add(translation));
    }
    public void translate(Vec2f translation){
        setCenter(center2d.add(translation));
    }

    /********* DO WE WANT SCALING??? **********/

    @Override
    public String toString() {
        return "Bounds: Center:" + this.center3d + ",Vel:" + this.velocity3d;
    }
}

