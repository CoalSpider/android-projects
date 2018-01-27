package com.ben.game.mazeEngine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.ben.game.R;
import com.ben.game.collisionEngine.Bounds.Rectangle;
import com.ben.game.objects.GameObject;
import com.ben.game.util.Vec2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/10/2017.
 */

public class MazeGenerator3d {
    private int columns;
    private int rows;
    private MazeType mazeType;
    // should be the same as cell size
    private static final int wallLen = 4;
    private static List<GameObject>[][] wallGrid;
    private List<List<Cell>> cells;
    private static Vec2f startCellPos;
    private static Vec2f endCellPos;
    private static float startX;
    private static float startY;
    private RecursiveBacktracking rb;

    public MazeGenerator3d(MazeType mazeType, int columns, int rows) {
        this.mazeType = mazeType;
        this.columns = columns+1;
        this.rows = rows+1;
        rb = new RecursiveBacktracking(columns,rows);
    }

    public List<GameObject> loadNewMaze(Activity activity) {
        // set a new seed
        rb.setNewSeed();
        Log.i("MazeGen3d","load new maze start");
        Context context = activity.getApplicationContext();
        SharedPreferences sharedPref = activity.getPreferences(Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.i("MazeGen3d","put seed " + rb.getSeed());
        editor.putLong(context.getString(R.string.MazeSeed), rb.getSeed());
        editor.apply();
        Log.i("MazeGen3d","applied changes");

        return generate(context);
    }

    public List<GameObject> loadOldMaze(Activity activity) {
        Log.i("MazeGen3d","loading old maze");
        Context context = activity.getApplicationContext();
        SharedPreferences sharPref = activity.getPreferences(Context
                .MODE_PRIVATE);
        long seed = sharPref.getLong(context.getString(R.string.MazeSeed), -1);
        Log.i("MazeGen3d","seed is " + seed);
        if (seed != -1) {
            Log.i("MazeGen3d","loading old seed");
            rb.setSeed(seed);
            return generate(context);
        }
        Log.i("MazeGen3d","old seed not found loading new maze");
        return loadNewMaze(activity);
    }

    private List<GameObject> generate(Context context) {
        cells = null;
        wallGrid = new List[columns][rows];
        startCellPos = null;
        endCellPos = null;
        if (mazeType.equals(MazeType.RECURSIVE_BACKTRACKING)) {
            System.out.println("start of gen");
            rb.run();
            Vec2f scp = rb.getStartCellPos();
            Vec2f ecp = rb.getEndCellPos();
            cells = rb.getCells();
            this.startX = -wallLen * cells.size() / 2f;
            this.startY = -wallLen * cells.size() / 2f;
            startCellPos = new Vec2f(scp.x(), scp.y());
            endCellPos = new Vec2f(ecp.x(), ecp.y());
        }
        if (cells == null) {
            throw new NullPointerException("maze did not get generated");
        }
        return walls(context, cells);
    }

    private List<GameObject> walls(Context context, List<List<Cell>> cells) {
        List<GameObject> walls = new ArrayList<>();
        float cellStartX;
        float cellStartY = this.startY;
        float cellSize = wallLen;
        float halfLength = cellSize / 2f;
        float wallHalfThickness = 0.5f;
        for (int i = 0; i < cells.size(); i++) {
            cellStartX = this.startX;
            for (int j = 0; j < cells.get(i).size(); j++) {
                Cell c = cells.get(i).get(j);
                if (c.getRight() || c.getBottom()) {
                    if (wallGrid[j][i] == null) {wallGrid[j][i] = new ArrayList<>();}
                } else {
                    cellStartX += cellSize;
                    continue;
                }
                int rightX = Math.min(j + 1, cells.get(i).size() - 1);
                int belowY = Math.min(cells.size() - 1, i + 1);
                int leftX = Math.max(j - 1, 0);
                int aboveY = Math.max(0, i - 1);
                Cell left = cells.get(i).get(leftX);
                Cell diag = cells.get(aboveY).get(leftX);
                boolean shortBottom = c.getRight();
                boolean longBottom = !left.getRight() && !left.getBottom();
                boolean shiftedBottom = shortBottom && longBottom;
                boolean shortRight = c.getBottom();
                // bias to chop  off bottom
                // this will be kept until I decide what to do about poor tiling
                if (shortBottom && shortRight) {
                    shortRight = false;
                }
                if (diag.equals(c)) {
                    shiftedBottom = true;
                }
                float y = halfLength;
                if (c.getBottom()) {
                    float x = cellStartX + halfLength;
                    float z = cellStartY + cellSize - wallHalfThickness;
                    if (shortBottom) {
                        x -= wallHalfThickness * 2;
                        if (shiftedBottom) {
                            // shifted bottom
                            // same as normal but shifted leftward by wall thickness
                            addBottomWall(context, walls, x, y, z);
                        } else {
                            // short bottom
                            addShortBottomWall(context, walls, x, y, z);
                        }
                    } else if (longBottom) {
                        // long bottom
                        addLongBottomWall(context, walls, x, y, z);
                    } else {
                        // normal bottom
                        addBottomWall(context, walls, x, y, z);
                    }
                    wallGrid[j][i].add(walls.get(walls.size() - 1));
                }
                if (c.getRight()) {
                    float x = cellStartX + cellSize - wallHalfThickness;
                    float z = cellStartY + halfLength;
                    if (shortRight) {
                        z -= wallHalfThickness * 2;
                        /** TODO this is currently never called
                         * if the tiling is unacceptable then we will shorten both
                         * sides and add a corner
                         * **/
                        // short right
                        addShortRightWall(context, walls, x, y, z);
                    } else {
                        // normal right
                        addRightWall(context, walls, x, y, z);
                    }
                    wallGrid[j][i].add(walls.get(walls.size() - 1));
                }

                cellStartX += cellSize;
            }
            cellStartY += cellSize;
        }
        return walls;
    }

    private void addRightWall(String objFile, int color,
                              Context context, List<GameObject> walls,
                              float cX, float cY, float cZ) {
        int texture = R.drawable.wall_texture;
        GameObject w = new GameObject();
        w.setStatic(true);
        w.loadModel(context, objFile, color, texture);
        Rectangle r = Rectangle.fromMinMax(w.getVboi().getMinMax(), (float) Math
                .toRadians(90));
        w.setBounds(r);
        w.setCenter(cX, cY, cZ);
        walls.add(w);
    }

    private void addRightWall(Context context, List<GameObject> walls, float cX,
                              float cY, float cZ) {
        int color = Color.argb(255, 0, 0, 255);
        addRightWall("wall2", color, context, walls, cX, cY, cZ);
    }

    private void addShortRightWall(Context context, List<GameObject> walls, float cX,
                                   float cY, float cZ) {
        int color = Color.argb(255, 0, 0, 255);
        addRightWall("wall_short", color, context, walls, cX, cY, cZ);
    }

    private void addBottomWall(String objFile, int color, Context context,
                               List<GameObject> walls, float cX, float cY, float cZ) {
        int texture = R.drawable.wall_texture;
        GameObject w = new GameObject();
        w.setStatic(true);
        w.loadModel(context, objFile, color, texture);
        Rectangle r = Rectangle.fromMinMax(w.getVboi().getMinMax());
        w.setBounds(r);
        w.setCenter(cX, cY, cZ);
        walls.add(w);
    }

    private void addBottomWall(Context context, List<GameObject> walls, float cX,
                               float cY, float cZ) {
        int color = Color.argb(255, 255, 0, 0);
        addBottomWall("wall2", color, context, walls, cX, cY, cZ);
    }

    private void addShortBottomWall(Context context, List<GameObject> walls, float
            cX, float cY, float cZ) {
        int color = Color.argb(255, 255, 0, 0);
        addBottomWall("wall_short", color, context, walls, cX, cY, cZ);
    }

    private void addLongBottomWall(Context context, List<GameObject> walls, float
            cX, float cY, float cZ) {
        int color = Color.argb(255, 255, 255, 255);
        addBottomWall("wall_long", color, context, walls, cX, cY, cZ);
    }
    // shaping of the maze, the cell size is the longest side of a cell
    // the walls are only on the bottom or right of a cell to the inside of the cell
    // for example:
    // cell 0,4         4,4


    //      0,0         4,0

    // the walls would be centered at...
    //      bottom = cellMinX + halfLength, cellMinY + cellWidth, cellHalfHeight
    //      right = cellMaxX - halfLength, cellMinY + cellHalfLength, cellHalfHeight
    /** TODO: Cases where changes need to happen....
     * <p>
     *     Case 1: Cell has both walls
     *          1a: Cell to right has bottom, shorten right
     *          1b: Cell below has right, shorten bottom
     *          1c: 1a == false && 1b == false shorten both, insert corner wall
     *              <- note -> only needed if walls would not tile properly
     *              otherwise we would only shorten one
     *          1d: 1a == true && 1b == true leave alone (cant be seen)
     *     Case 2: Cell has bottom
     *          If cell to right is clear and cell to right+below has right
     *          extend the bottom wall
     * </p>**/
    /** TODO: short wall **/
    /**
     * TODO: corner wall
     **/

    public static List<GameObject>[][] getWallGrid() {
        return wallGrid;
    }

    public static int getWallLen() {
        return wallLen;
    }

    public static float getStartX() {
        return startX;
    }

    public static float getStartY() {
        return startY;
    }

    /** @return the angle in degrees from the start cell into the maze **/
    public static float getStartDirection(){
        // start cell is always left side of maze corner
        // we want to face to the right
        return 0;
    }
    public static float getStartCellX() {
        // fudge is -1/2 cell width
        return (startCellPos.x() - 1) * wallLen + startX + wallLen / 2f;
    }

    public static float getStartCellY() {
        return startCellPos.y() * wallLen + startY + wallLen / 2f;
    }

    /** TODO: have endCellPos already be shifted **/
    public static float getEndCellX() {
        float x = endCellPos.x();
        // left side
        if(endCellPos.x() == 0){
            x -= 1;
        } else if(endCellPos.x() >= 9){
            x += 1;
        }
        return x * wallLen + startX + wallLen / 2f;
    }

    /** TODO: have endCellPos already be shifted **/
    public static float getEndCellY() {
        float y = endCellPos.y();
        if(endCellPos.y() == 0){
            y -= 1;
        } else if(endCellPos.y() >= 9){
            y += 1;
        }
        return y * wallLen + startY + wallLen / 2f;
    }
}
