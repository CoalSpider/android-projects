package com.ben.testapp.userInterface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by Ben on 8/3/2017.
 */

class Joystick implements EventConsumer {
    private static final Paint paintOuter = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint paintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float outerRadius = UserInterface.convertPxToDp(75);
    private static final float innerRadius = UserInterface.convertPxToDp(25);
    private static final float padding = UserInterface.convertPxToDp(10);
    private static final float outerCenterX = outerRadius+padding;
    private static final int activeAlpha = 255;
    private static final int notActiveAlpha = 255/2;

    private float outerCenterY;
    private float innerCenterX;
    private float innerCenterY;

    private boolean isActive;
    private float innerDotAngleFromCenter;
    private float innerDotDistanceFromCenter;

    static{
        paintInner.setColor(Color.RED);
        paintInner.setAlpha(notActiveAlpha);
        paintInner.setStyle(Paint.Style.FILL);

        paintOuter.setColor(Color.WHITE);
        paintInner.setAlpha(notActiveAlpha);
        paintOuter.setStrokeWidth(UserInterface.convertPxToDp(2));
        paintOuter.setStyle(Paint.Style.STROKE);
    }

    Joystick(){
        isActive = false;
        innerDotAngleFromCenter = 0;
        innerDotDistanceFromCenter = 0;
    }

    void draw(Canvas canvas){
        canvas.drawCircle(outerCenterX,outerCenterY,outerRadius,paintOuter);
        canvas.drawCircle(innerCenterX,innerCenterY,innerRadius,paintInner);
    }

    void onSizeChanged(int width, int height){
        innerCenterX = (outerRadius+padding);
        outerCenterY = height - (outerRadius+padding);
        innerCenterY = height - (outerRadius+padding);
    }

    @Override
    public void onActionDown(MotionEvent event) {
        if(eventInsideJoystickBounds(event)){
            makeOpaque();
            setInnerCenter(event);
            isActive = true;
        }
    }

    @Override
    public void onActionMove(MotionEvent event) {
        if(isActive){
            setInnerCenter(event);
        }
    }

    @Override
    public void onActionUp(MotionEvent event) {
        makeTransparent();
        resetCenter();
        isActive = false;
    }

    @Override
    public void onActionCancel(MotionEvent event) {
        // do nothing
    }

    @Override
    public void onActionOutside(MotionEvent event) {
        // do nothing
    }

    private void makeOpaque(){
        paintOuter.setAlpha(activeAlpha);
        paintInner.setAlpha(activeAlpha);
    }

    private void makeTransparent(){
        paintOuter.setAlpha(notActiveAlpha);
        paintInner.setAlpha(notActiveAlpha);
    }

    private void resetCenter(){
        innerCenterX = outerCenterX;
        innerCenterY = outerCenterY;
        innerDotAngleFromCenter = 0;
        innerDotDistanceFromCenter = 0;
    }

    private boolean eventInsideJoystickBounds(MotionEvent event){
        return pointInsideCircle(event.getX(),event.getY(),outerCenterX,outerCenterY,outerRadius);
    }

    private boolean pointInsideCircle(float x, float y, float centerX, float centerY, float radius){
        float dx = x - centerX;
        float dy = y - centerY;
        return (dx*dx+dy*dy) < radius*radius;
    }

    private void setInnerCenter(MotionEvent event){
        cartesianToPolar(outerCenterX,outerCenterY,event.getX(),event.getY());
        float radius = polarTempResult[0];
        float angle = polarTempResult[1];
        radius = (radius > outerRadius) ? outerRadius : radius;

        polarToCartesian(outerCenterX,outerCenterY,radius,angle);
        innerCenterX = cartesianTempResult[0];
        innerCenterY = cartesianTempResult[1];

        innerDotAngleFromCenter = angle;
        innerDotDistanceFromCenter = radius;
    }

    private float[] polarTempResult = new float[2];
    private void cartesianToPolar(float centerX, float centerY, float cartesianX, float cartesianY){
        float dx = cartesianX - centerX;
        float dy = cartesianY - centerY;
        float radius = (float)Math.sqrt(dx*dx+dy*dy);
        float angleInRadians = (float)Math.atan2(dy,dx);
        polarTempResult[0] = radius;
        polarTempResult[1] = angleInRadians;
    }

    private float[] cartesianTempResult = new float[2];
    /** @param polarAngle is assumed to be in radians **/
    private void polarToCartesian(float centerX, float centerY, float polarRadius, float polarAngle){
        float cartesianX = (float)(polarRadius*Math.cos(polarAngle)) + centerX;
        float cartesianY = (float)(polarRadius*Math.sin(polarAngle)) + centerY;
        cartesianTempResult[0] = cartesianX;
        cartesianTempResult[1] = cartesianY;
    }

    float getInnerDotAngleFromCenter() {
        return innerDotAngleFromCenter;
    }

    float getInnerDotDistanceFromCenter() {
        return innerDotDistanceFromCenter;
    }

    float getOuterRadius() {
        return outerRadius;
    }
}