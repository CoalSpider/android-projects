package com.ben.game.mazeEngine;

import android.content.Context;
import android.util.Log;

import com.ben.game.util.Vec2f;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RecursiveBacktracking {
    private List<List<Cell>> cells;
    private Stack<Cell> visitedCells;
    private Cell startCell;
    private Cell endCell;
    private int columns;
    private int rows;
    // params only valid for square maze
    private int wallIndexMin;
    private int wallIndexMax;
    // count for cells
    private int count = 0;
    // randomizer for maze gen, and a seed value so we can reload a maze
    private long seed;
    private Random random;

    public RecursiveBacktracking(int columns, int rows) {
        // get the seed if it exists
        seed = new Random().nextLong();
        random = new Random(seed);

        this.columns = columns;
        this.rows = rows;
        this.wallIndexMax = this.columns - 1;
        this.wallIndexMin = 1;
    }

    /** generates a new seed for the maze, the seed is used to reload the current maze **/
    void setNewSeed(){
        seed = new Random().nextLong();
        random = new Random(seed);
    }

    void setSeed(long seed) {
        this.seed = seed;
        random = new Random(seed);
    }

    long getSeed() {
        return seed;
    }

    private void loadGrid(){
        visitedCells = new Stack<>();
        cells = new ArrayList<>(rows);

        // add all the cells to the maze
        for (int i = 0; i < rows; i++) {
            cells.add(new ArrayList<Cell>(columns));
            for (int j = 0; j < columns; j++) {
                cells.get(i).add(new Cell(-1, false));
            }
        }
        // remove top row right walls
        for (int i = 0; i < columns; i++) {
            cells.get(0).get(i).setRight(false);
        }
        // remove bottom row left walls
        for (int i = 0; i < rows; i++) {
            cells.get(i).get(0).setBottom(false);
        }
        // remove left wall from top right corner
        cells.get(0).get(0).setRight(false);
    }

    /** TODO: if we can save the seed of the randomizer we can recreate the maze exactly
     * this would be a problem if the walls would be moved/removed after generation**/

    /**
     * method called to generate the maze
     **/
    void run() {
        loadGrid();
        int startX = 1;
        int startY = 1;
        startCell = getCell(startX, startY);
        startCell.setVisited(true);
        startCell.setNum(count);
        startCell.setDepth(0);
        count++;
        visitedCells.add(startCell);
        carvePassages(startX, startY);
        openEnds();
        // add any remaining cells without parents to the start cell
        // this is done to allow ease of seralization/deserialization
        for (List<Cell> list : cells) {
            for (Cell c : list) {
                if (c.getParent() == null) {
                    startCell.addChild(c);
                }
            }
        }
    }

    /**
     * @return if the given cell is part of the maze border
     **/
    private boolean isBorderCell(Cell c) {
        int[] index = getIndex2(c);
        int x = index[0];
        int y = index[1];
        return x == wallIndexMin || x == wallIndexMax || y == wallIndexMin || y ==
                wallIndexMax;
    }

    /**
     * Helper method to remove the correct walls of a cell that is part of the border
     * of the maze
     **/
    private void removeWalls(Cell... cells) {
        for (Cell c : cells) {
            int[] index1 = getIndex2(c);
            int x = index1[0];
            int y = index1[1];
            if (x == wallIndexMin) {
                getCell(0, y).setRight(false);
            } else if (x == wallIndexMax) {
                c.setRight(false);
            } else if (y == wallIndexMin) {
                getCell(x, 0).setBottom(false);
            } else if (y == wallIndexMax) {
                c.setBottom(false);
            }
        }
    }

    /**
     * <p>
     * Creates a exit for the maze by removing a wall of a border cell.
     * </p>
     * <p>
     * The method also creates a maze entrance if and only if the startCell for the
     * algorithm is someone on the border of the maze
     * </p>
     **/
    private void openEnds() {
        // with recursive subdivision there is only one path from the start cell
        // so now we just need the deepest wall from that point
        Cell deepestWall = null;
        int currentDepth = 0;
        for (List<Cell> list : cells) {
            for (Cell c : list) {
                // skip the startCell
                if (c.equals(startCell)) continue;
                // we only want to make a exit in the maze's border
                if (isBorderCell(c)) {
                    int depth = c.getDepth();
                    if (deepestWall == null) {
                        deepestWall = c;
                        currentDepth = depth;
                    } else if (c.getDepth() > currentDepth) {
                        deepestWall = c;
                        currentDepth = depth;
                    }
                }
            }
        }
        System.out.println("deepestCell = " + deepestWall);
        endCell = deepestWall;
        if (isBorderCell(startCell)) {
            removeWalls(startCell, deepestWall);
        } else {
            removeWalls(deepestWall);
        }
    }

    private void carvePassages(int x, int y) {
        if (visitedCells.isEmpty()) {
            throw new RuntimeException("fist cell not added to visited cells");
        }
        while (visitedCells.isEmpty() == false) {
            List<Cell> toVisit = new ArrayList<>();
            // add adjacent cells to a temp list
            if (x + 1 < columns) toVisit.add(getCell(x + 1, y));
            if (x - 1 >= 1) toVisit.add(getCell(x - 1, y));
            if (y + 1 < rows) toVisit.add(getCell(x, y + 1));
            if (y - 1 >= 1) toVisit.add(getCell(x, y - 1));
            // iterate through the cells and remove cells that have already been
            // visited
            Iterator<Cell> iter = toVisit.iterator();
            while (iter.hasNext()) {
                if (iter.next().hasBeenVisited()) {
                    iter.remove();
                }
            }
            int newX = 0;
            int newY = 0;
            // if there are no valid cells go back to the most recently visited cell
            if (toVisit.isEmpty()) {
                Cell c = visitedCells.pop();
                // get xy of previous visited cell
                int[] xy = getIndex2(c);
                newX = xy[0];
                newY = xy[1];
                // else move to a random (and valid) adjacent cell
            } else {
                Cell newCell = toVisit.get(random.nextInt(toVisit.size()));
                newCell.setVisited(true);
                newCell.setNum(count);
                count++;
                // adds the new cell as a child of the previous cell
                visitedCells.peek().addChild(newCell);
                // add the new cell to the visited stack
                visitedCells.push(newCell);
                // get xy of the new cell
                int[] xy = getIndex2(newCell);
                newX = xy[0];
                newY = xy[1];
                // carve passages between the old cell and the new cell
                Cell oldCell = getCell(x, y);
                int dx = x - newX;
                int dy = y - newY;
                // tested for screen space
                if (dx < 0) {
                    oldCell.setRight(false);
                } else if (dx > 0) {
                    newCell.setRight(false);
                } else if (dy < 0) {
                    oldCell.setBottom(false);
                } else if (dy > 0) {
                    newCell.setBottom(false);
                }
            }
            // set the new cell position to the current position
            x = newX;
            y = newY;
        }
    }

    /**
     * helper method to flip x and y
     * it makes more sense to me to say getCell(x,y) instead of getCell(y,x)
     **/
    private Cell getCell(int x, int y) {
        return cells.get(y).get(x);
    }

    /**
     * @return the index of the given cell in a int[]
     * int[0] == x coord int[1] == y coord
     **/
    private int[] getIndex2(Cell c) {
        for (List<Cell> list : cells) {
            int index = list.indexOf(c);
            if (index != -1) {
                return new int[]{index, cells.indexOf(list)};
            }
        }
        return null;
    }

    public List<List<Cell>> getCells() {
        return cells;
    }

    public Vec2f getStartCellPos() {
        int[] t = getIndex2(startCell);
        return new Vec2f(t[0], t[1]);
    }

    public Vec2f getEndCellPos() {
        int[] t = getIndex2(endCell);
        return new Vec2f(t[0], t[1]);
    }
}