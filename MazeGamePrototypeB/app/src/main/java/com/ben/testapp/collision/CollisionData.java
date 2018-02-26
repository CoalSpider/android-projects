package com.ben.testapp.collision;

import com.ben.testapp.model.SceneModel;

public class CollisionData{
    private SceneModel a;
    private SceneModel b;
    private float[] collisionNormal;
    private float penetration;

    CollisionData(SceneModel a, SceneModel b, float[] collisionNormal, float penetration) {
        this.collisionNormal = collisionNormal;
        this.penetration = penetration;
        this.a = a;
        this.b = b;
    }
    public float[] getCollisionNormal() {
        return collisionNormal;
    }

    public float getPenetration() {
        return penetration;
    }

    public SceneModel getA() {
        return a;
    }

    public SceneModel getB() {
        return b;
    }

    void setCollisionNormal(float[] collisionNormal) {
        this.collisionNormal = collisionNormal;
    }

    void setPenetration(float penetration) {
        this.penetration = penetration;
    }
}