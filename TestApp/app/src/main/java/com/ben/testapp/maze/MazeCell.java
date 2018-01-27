package com.ben.testapp.maze;

/**
 * Created by Ben on 7/30/2017.
 */

public class MazeCell {
    private int gridX;
    private int gridY;
    private int depth = 0;
    private boolean rightWall = true;
    private boolean bottomWall = true;
    private boolean visited = false;

    MazeCell(int gridX, int gridY){
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    boolean hasRightWall(){
        return rightWall;
    }

    boolean hasBottomWall(){
        return bottomWall;
    }

    int getDepth() {
        return depth;
    }

    boolean hasBeenVisited(){
        return visited;
    }

    void removeRightWall(){
        rightWall = false;
    }

    void removeBottomWall(){
        bottomWall = false;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString(){
        return gridX+","+gridY+","+depth;
    }
}
