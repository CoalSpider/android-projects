package com.ben.testapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ben.testapp.userInterface.UserInterface;

public class CustomGLSurfaceView extends GLSurfaceView {
	private GLRenderer glRenderer;

    public CustomGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Create a OpenGL ES 2.0  context
        // DO NOT CHANGE UNLESS MANIFEST.XML HAS CHANGED
        setEGLContextClientVersion(2);
        // perserve on pause
        //   setPreserveEGLContextOnPause(true);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousRotationXInDegrees;
    private float previousRotationYInDegrees;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousRotationXInDegrees;
                float dy = y - previousRotationYInDegrees;

                glRenderer.setRotationX(
                        glRenderer.getRotationX() +
                        (dx * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                
                glRenderer.setRotationY(
                        glRenderer.getRotationY() +
                        (dy * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
        }

        previousRotationXInDegrees = x;
        previousRotationYInDegrees = y;
        return true;
    }
    
    public void setRenderer(GLRenderer renderer) {
    	glRenderer = renderer;
		super.setRenderer(renderer); 
    }
}
