package com.ben.lightingtest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {
    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Create a OpenGL ES 2.0  context
        // DO NOT CHANGE UNLESS MANIFEST.XML HAS CHANGED
        setEGLContextClientVersion(2);
        // perserve on pause
     //   setPreserveEGLContextOnPause(true);
    }

    public void setRenderer(MyGLRenderer renderer) {
        super.setRenderer(renderer);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}