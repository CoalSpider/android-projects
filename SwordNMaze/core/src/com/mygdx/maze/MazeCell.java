/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.maze;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.util2.Settings;

/**
 *
 * @author Ben Norman
 * 
 *<pre>
 *MazeCell are defined as a grid cell with right and bottom walls
 *This is to allow for ease of use with the Recursive Backtracking (maze generation) algorithm
 *</pre>
 */
public class MazeCell {
    private boolean hasRightWall;
    private boolean hasBottomWall;
    private boolean visited;
    private final int row;
    private final int column;
    // used for debugging
    int visitedCount;
    
    private final Vector2 position;
    
    /** creates a maze cell at the given row and column with both a right and bottom wall that has not been visited **/
    public MazeCell(int row, int column) {
        this.row = row;
        this.column = column;
        this.hasRightWall = true;
        this.hasBottomWall = true;
        this.visited = false;
        this.visitedCount = 0;
        this.position = new Vector2(this.column,this.row).scl(Settings.MAZE_SCALE);
    }

    public boolean hasRightWall() {
        return hasRightWall;
    }

    public void setHasRightWall(boolean hasRightWall) {
        this.hasRightWall = hasRightWall;
    }

    public boolean hasBottomWall() {
        return hasBottomWall;
    }

    public void setHasBottomWall(boolean hasBottomWall) {
        this.hasBottomWall = hasBottomWall;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    
    public Vector2 asVector(){
        return position;
    }
}
