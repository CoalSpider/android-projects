package com.ben.drawcontrolsprototype.util;

/**
 * Created by Ben on 4/27/2017.
 */
class Line2d {
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    Line2d(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    float x1() {return x1;}

    float y1() {return y1;}

    float x2() {return x2;}

    float y2() {return y2;}
}
