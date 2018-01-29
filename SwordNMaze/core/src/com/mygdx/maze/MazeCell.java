/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.maze;

/**
 *
 * @author Ben Norman
 */
public class MazeCell {
    // package private so I dont have to write getters everywhere
    private boolean hasRightWall;
    private boolean hasBottomWall;
    private boolean visited;
    private final int row;
    private final int column;
    // used for debugging
    int visitedCount;
    
    /** creates a maze cell at the given row and column with both a right and bottom wall that has not been visited **/
    public MazeCell(int row, int column) {
        this.row = row;
        this.column = column;
        this.hasRightWall = true;
        this.hasBottomWall = true;
        this.visited = false;
        this.visitedCount = 0;
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
}
