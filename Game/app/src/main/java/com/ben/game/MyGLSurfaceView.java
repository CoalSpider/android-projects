package com.ben.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Create a OpenGL ES 2.0  context
        // DO NOT CHANGE UNLESS MANIFEST.XML HAS CHANGED
        setEGLContextClientVersion(2);
        // perserve on pause
        setPreserveEGLContextOnPause(true);
    }

    public void setRenderer(MyGLRenderer renderer) {
        mRenderer = renderer;
        super.setRenderer(renderer);
    }

    float oldX = 0;
    float oldY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        float dx = oldX - event.getX();
        float dy = oldY - event.getY();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d("Tag", "Action was DOWN");
                this.oldX = event.getX();
                this.oldY = event.getY();
                return true;
            case (MotionEvent.ACTION_MOVE):
                if (Math.abs(dx) < 100 && Math.abs(dy) < 100) {
                    onMove(oldX, oldY, event.getX(), event.getY());
                }
                this.oldX = event.getX();
                this.oldY = event.getY();
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d("Tag", "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d("Tag", "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d("Tag", "Movement occurred outside bounds of current screen " +
                        "element");
                return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void onMove(float startX, float startY, float endX, float endY) {
        mRenderer.swapTexture(new float[]{startX, startY, endX, endY});
    }

    void clearScreen() {
        Log.e("GLSurf", "clear screen");
        mRenderer.clearScreen();
    }

    void endDraw() {
        Log.e("GLSurf", "end draw");
        mRenderer.endDraw();
    }

    void startDraw() {
        Log.e("GLSurf", "start draw");
        mRenderer.startDraw();
    }
}