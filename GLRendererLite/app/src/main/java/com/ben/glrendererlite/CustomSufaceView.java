package com.ben.glrendererlite;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Ben on 8/21/2017.
 */

public class CustomSufaceView extends GLSurfaceView {
    private GLRenderer glRenderer;

    public CustomSufaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
    }

    public void setRenderer(GLRenderer renderer) {
        glRenderer = renderer;
        super.setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                glRenderer.stepSim();
        }
        return true;
    }
}
