package com.ben.liquidfuntutorials;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Ben on 11/22/2017.
 */

public class CustomSurfaceView extends GLSurfaceView {
    private GLRenderer glRenderer;

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(GLRenderer renderer) {
        this.glRenderer = renderer;
        super.setRenderer(renderer);
    }
}
