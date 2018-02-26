package com.ben.testapp.userInterface;

import android.view.MotionEvent;

/**
 * Created by Ben on 8/13/2017.
 */

public class SwordPlane implements EventConsumer{
    private float startX,startY,endX,endY;
    /** TODO: limit input to some square area of the screen **/

    @Override
    public void onActionDown(MotionEvent event) {
        startX = event.getX();
        startY = event.getY();
    }

    @Override
    public void onActionMove(MotionEvent event) {
        // do nothing... for now
    }

    @Override
    public void onActionUp(MotionEvent event) {
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onActionCancel(MotionEvent event) {
        // do nothing
    }

    @Override
    public void onActionOutside(MotionEvent event) {
        // do nothing
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }
}
