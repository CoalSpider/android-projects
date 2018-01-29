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
class MazeCell {
    // package private so I dont have to write getters everywhere
    boolean hasRightWall;
    boolean hasBottomWall;
    boolean visited;
    final int row;
    final int column;
    
    /** creates a maze cell at the given row and column with both a right and bottom wall that has not been visited **/
    public MazeCell(int row, int column) {
        this.row = row;
        this.column = column;
        this.hasRightWall = true;
        this.hasBottomWall = true;
        this.visited = false;
    }
}
