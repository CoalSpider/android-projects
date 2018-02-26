package com.ben.testapp.userInterface;

import android.view.MotionEvent;

/**
 * Created by Ben on 8/13/2017.
 */

public interface EventConsumer {
    void onActionDown(MotionEvent event);
    void onActionMove(MotionEvent event);
    void onActionUp(MotionEvent event);
    void onActionCancel(MotionEvent event);
    void onActionOutside(MotionEvent event);
}
