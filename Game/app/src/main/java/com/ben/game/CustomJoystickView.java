package com.ben.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ben.game.util.DistanceUtil;
import com.ben.game.util.Util;
import com.ben.game.util.Vec2f;

/**
 * Created by Ben Norman on 5/6/2017.
 * <p>
 * Joystick for movement for use under left thumb
 * This consists of two circles, the outer bounding circle and the inner dot for
 * visualization of where the joystick is pointing
 * </p>
 */

public class CustomJoystickView extends View {
    private static final float outerCircleRadius = 100;
    private static final float innerDotRadius = 40;
    // units per sec
    private static final float moveSpeed = 4f; // 2
    // degrees per sec
    private static final float rotationSpeed = 150f; // 75

    private float outerX, outerY, innerX, innerY;
    private float currentDegrees;

    private static boolean orientationChange = true;

    private boolean touchingJoystick = false;

    private Paint paint;
    private Canvas canvas;

    public CustomJoystickView(Context context) {
        super(context);
        init();
    }

    public CustomJoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomJoystickView(Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas = new Canvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (orientationChange) {
            outerX = outerCircleRadius;
            outerY = getMeasuredHeight() - outerCircleRadius;
            innerX = outerX;
            innerY = outerY;
            orientationChange = false;
        }
        paint.setColor(Color.argb(255, 255, 255, 255));
        if (touchingJoystick) {
            // draw outer
            paint.setColor(Color.argb(200, 255, 255, 255));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(outerX, outerY, outerCircleRadius, paint);

            // draw inner
            paint.setColor(Color.argb(200, 255, 0, 0));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(innerX, innerY, innerDotRadius, paint);
        } else {
            // draw outer
            paint.setColor(Color.argb(50, 255, 255, 255));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(outerX, outerY, outerCircleRadius, paint);

            // draw inner
            paint.setColor(Color.argb(50, 255, 0, 0));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(innerX, innerY, innerDotRadius, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        orientationChange = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d("CustomView", "Action was DOWN");
                float x = event.getX();
                float y = event.getY();
                float x2 = outerX;
                float y2 = outerY;
                float rad = outerCircleRadius;
                float dist = DistanceUtil.pointAndCircle(x2, y2, rad, x, y);
                if (dist <= rad) {
                    touchingJoystick = true;
                    return true;
                }
                return false;
            case (MotionEvent.ACTION_MOVE):
                if (touchingJoystick) {
                    moveInnerDot(event.getX(), event.getY());
                    // redraw
                    invalidate();
                    return true;
                }
                return false;
            case (MotionEvent.ACTION_UP):
                Log.d("CustomView", "Action was UP");
                if (touchingJoystick) {
                    resetInnerDot();
                    touchingJoystick = false;
                    invalidate();
                    return true;
                }
                return false;
            case (MotionEvent.ACTION_CANCEL):
                Log.d("CustomView", "Action was CANCEL");
                return false;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d("CustomView", "Movement occurred outside bounds of current " +
                        "screen " +
                        "element");
                return false;
        }
        return super.onTouchEvent(event);
    }


    private void moveInnerDot(float newX, float newY) {
        // convert to logPolar to get the angle and distance from the center
        Vec2f polar = Util.cartesianToPolar(outerX, outerY,
                newX, newY);
        float distance = polar.x();
        float angle = polar.y();
        // if the point would be outside the circle move it to be on the edge
        if (distance > outerCircleRadius) {
            distance = outerCircleRadius;
        }
        // set inner dot's position
        Vec2f newLoc = Util.polarToCartesian(outerX, outerY, distance, angle);
        innerX = newLoc.x();
        innerY = newLoc.y();
    }

    private void resetInnerDot() {
        innerX = outerX;
        innerY = outerY;
    }

    public Canvas getCanvas() {
        return canvas;
    }


    public void setCurrentDegrees(float currentDegrees) {
        this.currentDegrees = currentDegrees;
    }

    public float getCurrentDegrees() {
        return currentDegrees;
    }

    // x == left/right

    /**
     * @return percent inner dot is along x axis in joystick
     **/
    private float getPercentMove() {
        return Math.abs((outerY - innerY) / outerCircleRadius);
    }
    private float getPercentTurn(){
        return Math.abs(((outerX - innerX)) / outerCircleRadius);
    }

    float[] controlScheme(float precentSec) {
        float moveSpeed = this.moveSpeed * precentSec * getPercentMove();
        float rotationSpeed = this.rotationSpeed * precentSec * getPercentTurn();
        Vec2f polar = Util.cartesianToPolar(outerX, outerY,
                innerX, innerY);
        float dist = polar.x();
        // if the point is very close to the center do nothing
        if (dist < 1e-8f) {
            return new float[]{0, currentDegrees};
        }
        /// polar coords range from 0 -> 180 and -0 -> -180 we want the range to be 0
        /// to 360
        float angle = (float) Math.toDegrees(polar.y());
        final float degs = (angle < 0) ? angle + 360 : angle;
        float move = 0;
        float rotation = 0;
        // moving forward if angle is > 180 < 360
        // moving backwards if angle is > 0 < 180
        // rotating right angle is > 270 || < 90
        // rotating left angle is > 90  < 270

        // moving forward
        if (degs > 180 && degs < 360) {
            if (degs > 270) {
                move = (degs - 270) / 90f;
                // flip move percent
                move = 1 - move;
            } else if (degs < 270) {
                move = (degs - 180) / 90f;
            } else {
                move = 1;
            }
        } else
            // moving backwards
            if (degs > 0 && degs < 180) {
                if (degs > 90) {
                    move = (degs - 90) / 90f;
                    // flip move percent
                    move = 1 - move;
                } else if (degs < 90) {
                    move = degs / 90f;
                } else {
                    move = 1;
                }
                move *= -1;
            }
        move *= moveSpeed;

        // turning right
        if (degs > 270 || degs < 90) {
            if (degs > 270) {
                rotation = (degs - 270) / 90f;
            } else if (degs < 90) {
                rotation = degs / 90f;
                // flip rotation percent
                rotation = 1 - rotation;
            } else {
                rotation = 1;
            }
        }

        // turning left
        if (degs > 90 && degs < 270) {
            if (degs > 180) {
                rotation = (degs - 180) / 90f;
                // flip rotation percent
                rotation = 1 - rotation;
            } else if (degs < 180) {
                rotation = (degs - 90) / 90f;
            } else {
                rotation = 1;
            }
            rotation *= -1;
        }

        currentDegrees += rotation * rotationSpeed;
        // limit current angle to be between 0 and 360 degrees
        if (currentDegrees > 360) {
            currentDegrees = 0 + rotationSpeed;
        }
        if (currentDegrees < 0) {
            currentDegrees = 360 - rotationSpeed;
        }
        return new float[]{move, currentDegrees};
    }
}
