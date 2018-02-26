package com.ben.game.objects;

import android.opengl.Matrix;

import com.ben.game.mazeEngine.MazeGenerator3d;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 5/13/2017.
 */

public class Player extends GameObject{
    public Player() {

    }

    public int getGridX(){
        float x = get2dCenter().x() - MazeGenerator3d.getStartX();
        return (int)(x/MazeGenerator3d.getWallLen());
    }
    public int getGridY(){
        float y = get2dCenter().y() - MazeGenerator3d.getStartY();
        return (int)(y/MazeGenerator3d.getWallLen());
    }
}
