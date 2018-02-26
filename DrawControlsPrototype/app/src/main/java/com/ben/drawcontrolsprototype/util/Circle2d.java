package com.ben.drawcontrolsprototype.util;

/**
 * Created by Ben on 4/27/2017.
 */
class Circle2d{
    private float cX;
    private float cY;
    private float r;

    Circle2d(float cX, float cY, float r){
        this.cX = cX;
        this.cY = cY;
        this.r = r;
    }

    float cX() {
        return cX;
    }

    float cY() {
        return cY;
    }

    float r() { return r; }
}
