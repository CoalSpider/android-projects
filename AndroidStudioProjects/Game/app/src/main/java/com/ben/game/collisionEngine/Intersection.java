package com.ben.game.collisionEngine;

import com.ben.game.util.Vec2f;

/**
 * Created by Ben on 6/17/2017.
 */

class Intersection {
    private final float overlap;
    private final Vec2f normal;
    private Vec2f line;

    Intersection(float overlap, Vec2f normal) {
        this.overlap = overlap;
        this.normal = normal;
        // explicitly set to null
        line = null;
    }

    Intersection(float overlap, Vec2f normal, Vec2f line) {
        this.overlap = overlap;
        this.normal = normal;
        this.line = line;
    }

    float getOverlap() {
        return overlap;
    }

    Vec2f getNormal() {
        return normal;
    }

    /** @return the 2d line of intersection. For example the side of a rectangle for this intersection**/
    Vec2f getLine(){
        return line;
    }

    Vec2f getPushVector(){
        // check if already normalized
        if(normal.lenSqrd() == 1){
            return normal.scaleMult(overlap);
        } else {
            return normal.norm().scaleMult(overlap);
        }
    }
}
