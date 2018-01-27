package com.ben.testapp.userInterface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ben.testapp.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ben on 8/3/2017.
 * <p>
 * This class holds the view overlay for the player move controls, health bar,
 * options menu, inventory, etc...
 */

public class UserInterface extends View implements Subject{
    private static float DENSITY;

    private Joystick joystick;
    private SwordPlane swordPlane;
    private Set<EventConsumer> eventConsumerList;
    private Set<Observer> observers;

    private MovementHelper movementHelper;

    public UserInterface(Context context, AttributeSet attrs) {
        super(context, attrs);

        DENSITY = getResources().getDisplayMetrics().density;

        joystick = new Joystick();
        movementHelper = new MovementHelper(joystick);
        swordPlane = new SwordPlane();
        eventConsumerList = new HashSet<>();

        eventConsumerList.add(joystick);
        eventConsumerList.add(swordPlane);

        observers = new HashSet<>();
    }

    static float convertPxToDp(float pixel){
        return pixel*DENSITY;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        joystick.onSizeChanged(w,h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        HealthBar.draw(canvas);
        joystick.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                for(EventConsumer consumer : eventConsumerList){
                    consumer.onActionDown(event);
                }
                invalidate();
                return true;
            case (MotionEvent.ACTION_MOVE):
                for(EventConsumer consumer : eventConsumerList){
                    consumer.onActionMove(event);
                }
                invalidate();
                return true;
            case (MotionEvent.ACTION_UP):
                for(EventConsumer consumer : eventConsumerList){
                    consumer.onActionUp(event);
                }
                for(Observer observer : observers){
                    observer.update(this);
                }
                invalidate();
                return true;
            case (MotionEvent.ACTION_CANCEL):
                for(EventConsumer consumer : eventConsumerList){
                    consumer.onActionCancel(event);
                }
                return false;
            case (MotionEvent.ACTION_OUTSIDE):
                for(EventConsumer consumer : eventConsumerList){
                    consumer.onActionOutside(event);
                }
                return false;
        }
        return super.onTouchEvent(event);
    }

    public MovementHelper getMovementHelper() {
        return movementHelper;
    }

    public SwordPlane getSwordPlane() {
        return swordPlane;
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }
}
