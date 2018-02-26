package com.ben.game.collisionEngine.Bounds;

import com.ben.game.objects.GameObject;
import com.ben.game.util.Vec2f;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 6/17/2017.
 */
public class Circle extends Bounds {
    private float radius;

    public Circle(Vec2f center, float radius) {
        this(center,0,radius);
    }
    public Circle(Vec2f center, float rotationInRadians, float radius) {
        super(center, rotationInRadians, BoundsType.CIRCLE);
        this.radius = radius;
    }

    public static Circle fromMinMax(float[] minMax) {
        return fromMinMax(minMax,0);
    }

    public static Circle fromMinMax(float[] minMax, float rotationInRadians) {
        float dx = minMax[3] - minMax[0];
        float dy = minMax[4] - minMax[1];
        float dz = minMax[5] - minMax[2];
        float cX = minMax[0]+dx/2f;
        float cY = minMax[2]+dy/2f;
        float cZ = minMax[1]+dz/2f;
        float extX = Math.abs(dx/2f);
        float extZ = Math.abs(dz/2f);
        Vec2f center = new Vec2f(cX, cY);
        float radius = Math.max(extX,extZ);
        Circle c = new Circle(center, rotationInRadians, radius);
        c.setCenter(new Vec3f(cX, cY, cZ));
        return c;
    }

    public float getRadius() {
        return radius;
    }
}
