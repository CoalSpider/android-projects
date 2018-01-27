package com.ben.game.collisionEngine.Bounds;

import com.ben.game.collisionEngine.Bounds.Bounds;
import com.ben.game.collisionEngine.Bounds.BoundsType;
import com.ben.game.objects.GameObject;
import com.ben.game.util.Vec2f;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 6/17/2017.
 */
public class Polygon extends Bounds {
    private final Vec2f[] verts;

    public Polygon(Vec2f center, float rotationInRadians, Vec2f...verts) {
        super(center, rotationInRadians, BoundsType.POLYGON);
        this.verts = verts;
    }

    public Vec2f[] getVerts() {
        return verts;
    }
}
