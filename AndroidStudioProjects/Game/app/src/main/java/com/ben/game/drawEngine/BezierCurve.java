package com.ben.game.drawEngine;

import com.ben.game.util.Vec2f;

/**
 * Created by Ben on 5/7/2017.
 */

public class BezierCurve {
    // curve start
    float startX,startY;
    // curve end
    float endX,endY;
    // control point for a quadratic curve
    float ctrlPntX,ctrlPntY;
    // control point2 for a cubic curve
    float ctrlPnt2X,ctrlPnt2Y;

    // linear beszier curve (straight line)
    // B(t) = p0 + t(p1-p0) == (1-t)p0 + t*p1, 0 <= t && t <= 1
    Vec2f getPointLinearBezier(float t){
        if(t > 1 || t < 0){
            throw new IllegalArgumentException("t="+t+", t must be between 0 and 1");
        }
        float x = startX + t*(endX-startX);
        float y = startY + t*(endY-startY);
        return new Vec2f(x,y);
    }
    // quadratic bezier curve (1 control point)
    // B(t) = (1-t)((1-t)*p0+t*p1) + t((t-1)p1+t*p2), 0 <= t && t <= 1
    //     == ((1-t)^2)*p0 + 2(1-t)*t*p1 + t^2*p2, 0<= t && t <=1
    Vec2f getPointQuadraticBezier(float t){
        if(t > 1 || t < 0){
            throw new IllegalArgumentException("t="+t+", t must be between 0 and 1");
        }
        float x = (1-t)*(1-t)*startX + 2*(1-t)*t*ctrlPntX + t*t*endX;
        float y = (1-t)*(1-t)*startY + 2*(1-t)*t*ctrlPntY + t*t*endY;
        return new Vec2f(x,y);
    }
    // cubic bezier curve (2 control point)
    // B(t) = (1-t)^3 * p0 +
    //       3(1-t)^2 * t*p1 +
    //       3(1-t)*t^2 * p2 +
    //       t^3 * p3,
    // 0 <= t && t <= 1
    Vec2f getPointCubicBezier(float t){
        if(t > 1 || t < 0){
            throw new IllegalArgumentException("t="+t+", t must be between 0 and 1");
        }
        // (1-t) ^ 2
        float t2 = 1-(2*t)+(t*t);
        // (1-t) ^ 3
        float t3 = 1-(3*t)-(t*t)-(t*t*t);
        float threeT1 =3*(1-t);
        float threeT2 =3*t2;
        float x = t3*startX +
                threeT2*t*ctrlPntX +
                threeT1 * (t*t) * ctrlPnt2X +
                t*t*t * endX;
        float y = t3*startY +
                threeT2*t*ctrlPntY +
                threeT1 * (t*t) * ctrlPnt2Y +
                t*t*t * endY;
        return new Vec2f(x,y);
    }
}
