package com.ben.testapp.userInterface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Ben on 8/3/2017.
 */

class HealthBar {
    /** TODO: border image **/
    private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    // units are in dp so make sure to convert
    private static final float LEFT = UserInterface.convertPxToDp(10);
    private static final float TOP = UserInterface.convertPxToDp(10);
    private static final float WIDTH = UserInterface.convertPxToDp(300);
    private static final float HEIGHT = UserInterface.convertPxToDp(50);
    private static final float RIGHT = LEFT+WIDTH;
    private static final float BOTTOM = TOP+HEIGHT;

    static{
        PAINT.setColor(Color.GREEN);
        PAINT.setStyle(Paint.Style.FILL);
    }

    private HealthBar(){}

    static void draw(Canvas canvas){
        canvas.drawRect(LEFT,TOP,RIGHT,BOTTOM,PAINT);
    }
}
