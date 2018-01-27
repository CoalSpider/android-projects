package com.ben.drawcontrolsprototype;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ben.drawcontrolsprototype.R;
import com.ben.drawcontrolsprototype.model.ObjParser;
import com.ben.drawcontrolsprototype.model.VBOFactory;
import com.ben.drawcontrolsprototype.renderer.MyGLRenderer;
import com.ben.drawcontrolsprototype.renderer.MyGLSurfaceView;


public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     VBOFactory vboFactory = new VBOFactory(getApplicationContext());

        setContentView(R.layout.activity_main);

        mGLView = (MyGLSurfaceView) findViewById(R.id.GLSurface);
        mGLView.setRenderer(new LessonSevenRenderer(this,mGLView));
     //   mGLView.setRenderer(new MyGLRenderer(getApplicationContext()));
        final Button clearButton = (Button) findViewById(R.id.ClearScreen);
        final Button endButton = (Button) findViewById(R.id.EndDraw);

        final ActivityManager activityManager = (ActivityManager) getSystemService
                (Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;


        Log.e("HeapSize", (Debug.getNativeHeapSize()/Math.pow(2,20))+"");
        Runtime runtime = Runtime.getRuntime();
        Log.e("MaxMem", (runtime.maxMemory()/Math.pow(2,20))+"");
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.e("MemoryClass", (memoryClass)+"");
        Log.e("LargeMemoryClass",am.getLargeMemoryClass()+"");

        if (supportsEs2) {
            if (mGLView != null) {
                return;
            }
            // request OpenGL ES 2.0 compatible context
            mGLView.setEGLContextClientVersion(2);
        } else {
            /* Does not support OpenGL ES 1.x compatible version */
            return;
        }
    }

    // called by clear button
    void clearScreen(View view) {
        mGLView.clearScreen();
    }

    // called by end draw button
    void endDraw(View view) {
        mGLView.confirmDraw();
    }

    @Override
    protected void onResume() {
        // activity must call GL surface views onResume() on activity onResume()
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        // activity must call the GL surface views onPause() on activity onPause()
        super.onPause();
        mGLView.onPause();
    }
}



