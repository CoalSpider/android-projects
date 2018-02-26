package com.ben.testapp.util;

import android.util.Log;

public class FPSCounter {
    private static final String TAG = "FPSCounter";
    long startTime = System.nanoTime();
    int frames = 0;

    StringBuilder builder = new StringBuilder();

    public void logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            builder.append("fps : ");
            builder.append(frames);
            Log.i(TAG, builder.toString());
            frames = 0;
            startTime = System.nanoTime();
            builder.delete(0,builder.length());
        }
    }
}