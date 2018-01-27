package com.ben.drawcontrolsprototype.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ben.drawcontrolsprototype.LessonSevenRenderer;
import com.ben.drawcontrolsprototype.MainActivity;
import com.ben.drawcontrolsprototype.renderer.MyGLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;
    private LessonSevenRenderer lessonSevenRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Create a OpenGL ES 2.0  context
        // DO NOT CHANGE UNLESS MANIFEST.XML HAS CHANGED
        setEGLContextClientVersion(2);

        // set the renderer for drawing on the surface
//        mRenderer = new MyGLRenderer(context);
//        mRenderer = null;
//        lessonSevenRenderer = new LessonSevenRenderer(mainActivity,this);
//        setRenderer(mRenderer);
 //       setRenderer(lessonSevenRenderer);
    }

    public void setRenderer(LessonSevenRenderer renderer){
        lessonSevenRenderer = renderer;
        super.setRenderer(renderer);
    }

    public void setmRenderer(MyGLRenderer renderer){
        mRenderer = renderer;
        super.setRenderer(renderer);
    }


    float oldX = 0;
    float oldY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
 //       Log.e("oldEventXY", oldX + "," + oldY);
  //      Log.e("eventXY", event.getX() + "," + event.getY());
        float dx = oldX - event.getX();
        float dy = oldY - event.getY();
   //     Log.e("deltaXY", dx + "," + dy);
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
  //              Log.d("Tag", "Action was DOWN");
                this.oldX = event.getX();
                this.oldY = event.getY();
                return true;
            case (MotionEvent.ACTION_MOVE):
  //              Log.d("Tag", "Action was Move");
                if (Math.abs(dx) < 100 && Math.abs(dy) < 100) {
                    onMove(event.getX(), event.getY());
                    onMove(oldX, oldY, event.getX(), event.getY());
                }
                this.oldX = event.getX();
                this.oldY = event.getY();
                return true;
            case (MotionEvent.ACTION_UP):
    //            Log.d("Tag", "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
    //            Log.d("Tag", "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
   //             Log.d("Tag", "Movement occurred outside bounds " +
     //                   "of current screen element");
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void onMove(float startX, float startY, float endX, float endY) {
        mRenderer.swapTexture(new float[]{startX,startY,endX,endY});
    }

    private void onMove(float x, float y) {mRenderer.swapTexture(x,y);}

    public void confirmDraw(){
        mRenderer.setConfirmDraw();
    }

    public void clearScreen(){
        mRenderer.setClearScreen();
    }
}