package com.ben.testapp.common;

/**
 * gl grid
 *     +z
 * +x -|- -x
 *    -z
 * north == 180, south == 0, west == 270, east == 90**/
public enum Direction{
    NORTH(+1,180),SOUTH(-1,0),WEST(+1,270),EAST(-1,90);
    int num;
    int angle;
    Direction(int num,int angle){
        this.num = num;
        this.angle=angle;
    }

    public int getNum() {
        return num;
    }

    public int getAngle() {
        return angle;
    }
}
