package com.ben.testapp.maze;

import com.ben.testapp.common.Direction;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ben on 8/8/2017.
 */
public class RecursiveSubdivisionTest {
    private int columns;
    private int rows;
    private RecursiveSubdivision rs2;
    private MazeCell[][] grid;
    String coordMaze =
            "  _ _ _ _ _ \n" +
            " |    _|   |\n" +
            "  _|_ _ _| |\n" +
            " |  _   _ _|\n" +
            " | |_ _|   |\n" +
            " |_ _ _ _|_ \n";
    // coord system
    //    +z
    // +x -|- -x
    //    -z

    // padding needed on top and left edges (gridLength)

    @Before
    public void load(){
        columns = 5;
        rows = 5;
        rs2 = new RecursiveSubdivision(columns,rows);
        rs2.run();
        grid = rs2.getGrid();
    }

    @Test
    public void run() {
        String actualMaze = "";
        for (int j = grid.length-1; j >= 0; j--) {
            for (int i = grid[j].length-1; i >= 0; i--) {
                actualMaze += (grid[j][i].hasBottomWall()) ? "_" : " ";
                actualMaze += (grid[j][i].hasRightWall()) ? "|" : " ";
            }
            actualMaze += "\n";
        }

        assertEquals(coordMaze, actualMaze);
    }

    @Test
    public void carvePathBetweenCells(){
        MazeCell current = new MazeCell(0,0);
        MazeCell above = new MazeCell(0,1);
        MazeCell below = new MazeCell(0,-1);
        MazeCell right = new MazeCell(1,0);
        MazeCell left = new MazeCell(-1,0);
        carvePathBetweenCurrentAndNew(current,above);
        assertTrue(above.hasBottomWall()==false);
        carvePathBetweenCurrentAndNew(current,below);
        assertTrue(current.hasBottomWall()==false);
        current = new MazeCell(0,0);
        carvePathBetweenCurrentAndNew(current,right);
        assertTrue(right.hasRightWall()==false);
        carvePathBetweenCurrentAndNew(current,left);
        assertTrue(current.hasRightWall()==false);
    }

    private void carvePathBetweenCurrentAndNew(MazeCell currentCell, MazeCell newCell) {
        int currX = currentCell.getGridX();
        int currY = currentCell.getGridY();
        int newX = newCell.getGridX();
        int newY = newCell.getGridY();
        if(newY > currY){
            newCell.removeBottomWall();
        } else if(newY < currY){
            currentCell.removeBottomWall();
        }else if(newX > currX){
            newCell.removeRightWall();
        }else if(newX < currX){
            currentCell.removeRightWall();
        }
    }

    @Test
    public void oppositeDirection(){
        Node<MazeCell> fiveFour = new Node<>(new MazeCell(5,4));
        Node<MazeCell> fiveFive = new Node<>(new MazeCell(5,5));
        Node<MazeCell> fourFive = new Node<>(new MazeCell(4,5));
        Node<MazeCell> threeFive = new Node<>(new MazeCell(3,5));
        Node<MazeCell> fourFour = new Node<>(new MazeCell(4,4));
        Node<MazeCell> threeFour = new Node<>(new MazeCell(3,4));
        //  _ _ _
        // |    _|
        //  _|_ _

        // test 5,4 -> 5,5
        assertTrue(isInOppositDirection(Direction.SOUTH,fiveFour,fiveFive));
        assertFalse(isInOppositDirection(Direction.NORTH,fiveFour,fiveFive));
        assertFalse(isInOppositDirection(Direction.WEST,fiveFour,fiveFive));
        assertFalse(isInOppositDirection(Direction.EAST,fiveFour,fiveFive));
        // test 5,5 -> 4,5
        assertTrue(isInOppositDirection(Direction.WEST,fiveFive,fourFive));
        assertFalse(isInOppositDirection(Direction.NORTH,fiveFive,fourFive));
        assertFalse(isInOppositDirection(Direction.SOUTH,fiveFive,fourFive));
        assertFalse(isInOppositDirection(Direction.EAST,fiveFive,fourFive));
        // test 4,5 -> 4,4
        assertTrue(isInOppositDirection(Direction.NORTH,fourFive,fourFour));
        assertFalse(isInOppositDirection(Direction.SOUTH,fourFive,fourFour));
        assertFalse(isInOppositDirection(Direction.WEST,fourFive,fourFour));
        assertFalse(isInOppositDirection(Direction.EAST,fourFive,fourFour));
    }
    private boolean isInOppositDirection(Direction currDir, Node<MazeCell> current, Node<MazeCell> next) {
        Direction dir = getDirection(current, next);
        switch (dir) {
            case NORTH:
                return (currDir == Direction.SOUTH);
            case SOUTH:
                return (currDir == Direction.NORTH);
            case WEST:
                return (currDir == Direction.EAST);
            case EAST:
                return (currDir == Direction.WEST);
        }
        return false;
    }

    @Test
    public void getDirection(){
        Node<MazeCell> current = new Node<>(new MazeCell(0,0));
        Node<MazeCell> north = new Node<>(new MazeCell(0,1));
        Node<MazeCell> south = new Node<>(new MazeCell(0,-1));
        Node<MazeCell> west = new Node<>(new MazeCell(1,0));
        Node<MazeCell> east = new Node<>(new MazeCell(-1,0));
        Node<MazeCell> invalid = new Node<>(new MazeCell(0,0));
        assertTrue(getDirection(current,north)==Direction.NORTH);
        assertTrue(getDirection(current,south)==Direction.SOUTH);
        assertTrue(getDirection(current,west)==Direction.WEST);
        assertTrue(getDirection(current,east)==Direction.EAST);
        try {
            getDirection(current,invalid);
            fail("failed to catch exception");
        }catch (RuntimeException e){
            assertTrue(true);
        }
        Node<MazeCell> three4 = new Node<>(new MazeCell(3,4));
        Node<MazeCell> four4 = new Node<>(new MazeCell(4,4));
        assertTrue(getDirection(three4,four4)==Direction.WEST);
        Node<MazeCell> two4 = new Node<>(new MazeCell(2,4));
        assertTrue(getDirection(three4,two4)==Direction.EAST);
    }
    private Direction getDirection(Node<MazeCell> current, Node<MazeCell> next) {
        float x1 = current.getData().getGridX();
        float y1 = current.getData().getGridY();
        float x2 = next.getData().getGridX();
        float y2 = next.getData().getGridY();
        if (y2 > y1) return Direction.NORTH;
        if (y2 < y1) return Direction.SOUTH;
        if (x2 > x1) return Direction.WEST;
        if (x2 < x1) return Direction.EAST;
        throw new RuntimeException("unknown angle " + current.getData() + " vs " +
                next.getData());
    }
}