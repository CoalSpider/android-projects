package com.ben.game.collisionEngine.Bounds;

import android.util.Log;

import com.ben.game.util.Quaternion;
import com.ben.game.util.Vec2f;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 6/17/2017.
 */
public class Rectangle extends Bounds {
    private final Vec2f extents;
    private final Vec2f[] verts;
    private boolean changeSinceLastCall;

    private Rectangle(Vec2f center, Vec2f extents) {
        super(center, 0, BoundsType.RECTANGLE);
        this.extents = extents;
        verts = new Vec2f[4];
        calculateVerts();
        changeSinceLastCall = false;
    }

    private Rectangle(Vec2f center, float rotationInRadians, Vec2f extents) {
        super(center, rotationInRadians, BoundsType.RECTANGLE);
        this.extents = extents;
        verts = new Vec2f[4];
        calculateVerts();
        changeSinceLastCall = false;
    }

    public static Rectangle fromMinMax(float[] minMax) {
        return fromMinMax(minMax, 0);
    }

    public static Rectangle fromMinMax(float[] minMax, float rotationInRadians) {
        /* min max points {minX,minY,minZ,maxX,maxY,maxZ} */
        float dx = minMax[3] - minMax[0];
        float dy = minMax[4] - minMax[1];
        float dz = minMax[5] - minMax[2];
        float cX = minMax[0] + dx / 2f;
        float cY = minMax[1] + dy / 2f;
        float cZ = minMax[2] + dz / 2f;
        float extX = Math.abs(dx) / 2f;
        float extZ = Math.abs(dz) / 2f;
        Vec2f center = new Vec2f(cX, cZ);
        Vec2f extents = new Vec2f(extX, extZ);
        Rectangle rect = new Rectangle(center, rotationInRadians, extents);
    //    rect.setCenter(new Vec3f(cX, cY, cZ));
        return rect;
    }

    private void calculateVerts() {
        if(extents==null){
            throw new RuntimeException("extents null Rectangle not initialized");
        }
        float minX = -extents.x();
        float minY = -extents.y();
        float maxX = extents.x();
        float maxY = extents.y();
        // get rotated points (assuming there is only rotation around y)
        Quaternion q = getOrientation().toAxisAngle();
        if (q.x() != 0 || q.z() != 0) {
            System.err.println("rotation around something other than up axis " + q);
            System.err.println("this may lead to unexpected results");
        }
        float cos = (float) Math.cos(q.w());
        float sin = (float) Math.sin(q.w());
        // winding is counterclockwise
        Vec2f p0 = getNewPoint(minX, minY, cos, sin, get2dCenter());
        Vec2f p1 = getNewPoint(maxX, minY, cos, sin, get2dCenter());
        Vec2f p2 = getNewPoint(maxX, maxY, cos, sin, get2dCenter());
        Vec2f p3 = getNewPoint(minX, maxY, cos, sin, get2dCenter());
        //    System.out.println(p0+","+p1+","+p2+","+p3);
        verts[0] = p0;
        verts[1] = p1;
        verts[2] = p2;
        verts[3] = p3;
    }

    @Override
    public void setCenter(float x, float z) {
        super.setCenter(x, z);
        changeSinceLastCall = true;
    }

    @Override
    public void setCenter(float x, float y, float z) {
        super.setCenter(x, y, z);
        changeSinceLastCall = true;
    }

    @Override
    public void setCenter(Vec3f center) {
        super.setCenter(center);
        changeSinceLastCall = true;
    }

    @Override
    public void setCenter(Vec2f center) {
        super.setCenter(center);
        changeSinceLastCall = true;
    }

    @Override
    public void setOrientation(Quaternion orientation) {
        super.setOrientation(orientation);
        changeSinceLastCall = true;
    }

    @Override
    public void setOrientation(float x, float y, float z, float angleInRadians) {
        super.setOrientation(x, y, z, angleInRadians);
        changeSinceLastCall = true;
    }

    @Override
    public void rotate(Quaternion quaternion) {
        super.rotate(quaternion);
        changeSinceLastCall = true;
    }

    @Override
    public void rotate(float x, float y, float z, float angleInRadians) {
        super.rotate(x, y, z, angleInRadians);
        changeSinceLastCall = true;
    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(x, y, z);
        changeSinceLastCall = true;
    }

    @Override
    public void translate(float x, float z) {
        super.translate(x, z);
        changeSinceLastCall = true;
    }

    @Override
    public void translate(Vec3f translation) {
        super.translate(translation);
        changeSinceLastCall = true;
    }

    @Override
    public void translate(Vec2f translation) {
        super.translate(translation);
        changeSinceLastCall = true;
    }

    public Vec2f[] getVerts() {
        if(changeSinceLastCall){
            calculateVerts();
            changeSinceLastCall = false;
        }
        return verts;
    }

    public Vec2f getExtents() {
        return extents;
    }

    /**
     * @return the point px,py rotated about the origin and translated
     **/
    private Vec2f getNewPoint(float px, float py, float cosAngle, float sinAngle,
                              Vec2f translation) {
        // xnew = p.x*cos(angle) - p.y*sin(angle) + cx
        // ynew = p.x*sin(angle) + p.y*cos(angle) + cy
        float xNew = px * cosAngle - py * sinAngle + translation.x();
        float yNew = px * sinAngle + py * cosAngle + translation.y();
        return new Vec2f(xNew, yNew);
    }
}
