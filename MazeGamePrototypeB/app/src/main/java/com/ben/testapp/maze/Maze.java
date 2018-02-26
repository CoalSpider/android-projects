package com.ben.testapp.maze;

import android.content.Context;

import com.ben.testapp.model.SceneModel;
import com.ben.testapp.util.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 7/30/2017.
 */

public class Maze {
    // cells with both walls
    //      add a corner wall obj
    // cell with bottom wall where cell to the left is empty
    // and cell below and to right has only a right wall
    //      lengthen bottom wall
    //      If scaled to lengthen translation is needed
    private static final int SCALE = 2;
    private static final int GRID_CELL_SIZE = SCALE * 2;

    private MazeCell[][] grid;
    private Context context;
    private List<SceneModel> walls = new ArrayList<>();
    private RecursiveSubdivision recursiveSubdivision;

    public void loadMaze(Context context, int rows, int columns) {
        recursiveSubdivision = new RecursiveSubdivision(rows, columns);
        this.context = context;
        recursiveSubdivision.run();
        grid = recursiveSubdivision.getGrid();
        convertMazeCellGridToModelGrid();
    }


    private void convertMazeCellGridToModelGrid() {
        // the grid is flipped on x and y to make it look like the above image in opengl
        for (int j = 0; j < grid.length; j++) {
            for (int i = 0; i < grid[j].length; i++) {
                MazeCell cell = grid[j][i];
                float[] xz = getXZ(cell.getGridX(), cell.getGridY());
                if (cell.hasBottomWall() && cell.hasRightWall()) {
                    addCornerWall(xz[0], xz[1]);
                } else if (cell.hasBottomWall()) {
                    addBottomWall(xz[0], xz[1]);
                } else if (cell.hasRightWall()) {
                    addRightWall(xz[0], xz[1]);
                }

                /** TODO: long bottom wall, corner wall with long bottom **/
                /** TODO: insert corner object instead? **/
            }
        }
    }

    private float[] getXZ(float gridX, float gridY) {
        float[] glXZ = new float[2];
        glXZ[0] = gridX*GRID_CELL_SIZE;
        glXZ[1] = gridY*GRID_CELL_SIZE;
        return glXZ;
    }

    private void addCornerWall(float x, float z) {
        addWall("corner_wall", new float[]{1, 0, 0, 1}, x, z);
    }

    private void addRightWall(float x, float z) {
        addWall("right_wall", new float[]{0, 1, 0, 1}, x, z);
    }

    private void addBottomWall(float x, float z) {
        addWall("bottom_wall", new float[]{0, 0, 1, 1}, x, z);
    }

    private void addLongBottomWall(float x, float z) {
        throw new UnsupportedOperationException("not implemented");
    }

    private void addCornerWallLongBottom(float x, float z) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * TODO: remove magic number
     **/
    private static final float[] translation = new float[]{0, -4.9f, 0};

    private void addWall(String objName, float[] color, float x, float z) {
        float[] pos = new float[]{x, 0, z};
        Vector3.addVV(pos, pos, translation);
        SceneModel wall = new SceneModel(pos, SCALE);
        wall.loadModel(context, objName, color, null);
        walls.add(wall);
    }

    public List<SceneModel> getWalls() {
        return walls;
    }

    public float[] getMazeStart() {
        float[] start = recursiveSubdivision.getStartCell();
        float[] xz = getXZ(start[0], start[1]);
        return new float[]{xz[0], 0, xz[1]};
    }

    public float[] getMazeExit() {
        float[] exit = recursiveSubdivision.getEndCell();
        float[] xz = getXZ(exit[0], exit[1]);
        return new float[]{xz[0], 0, xz[1]};
    }

    public float[] getLocationOfCell(MazeCell mazeCell) {
        return getXZ(mazeCell.getGridX(), mazeCell.getGridY());
    }

    public float[] getCellCenter(MazeCell mazeCell) {
        float[] xyShift = getXZ(mazeCell.getGridX(), mazeCell.getGridY());

        return xyShift;
    }

    public Tree<MazeCell> getConnectionGraph() {
        return recursiveSubdivision.getConnectionGraph();
    }

    void setGrid(MazeCell[][] grid) {
        this.grid = grid;
    }
}
