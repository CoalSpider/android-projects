package com.ben.testapp.maze;

import android.opengl.Matrix;

import com.ben.testapp.AI.Scout;
import com.ben.testapp.common.Direction;
import com.ben.testapp.util.Vector3;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Ben on 8/7/2017.
 */
public class ScoutTest {
    private int columns = 5;
    private int rows = 5;
    private RecursiveSubdivision recursiveSubdivision;
    // expected 5x5 maze with random seed 1993
    private String mazeString =
            "  _ _ _ _ _ \n" +
            "   |  _ _  |\n" +
            " |_ _|  _| |\n" +
            " |  _ _ _ _|\n" +
            " | |  _  |  \n" +
            " |_ _|_ _ _|\n";


    private Maze maze;
    private Tree<MazeCell> connGraph;
    private MazeCell[][] grid;
    private Random random;

    @Before
    public void load() {
        recursiveSubdivision = new RecursiveSubdivision(columns, rows);
        recursiveSubdivision.run();
        maze = new Maze();
        connGraph = recursiveSubdivision.getConnectionGraph();
        grid = recursiveSubdivision.getGrid();
        maze.setGrid(grid);
        random = new Random(0);
    }

    @Test
    public void getDirectionTest() {
        Node<MazeCell> one = new Node<>(new MazeCell(1, 1));
        Node<MazeCell> north = new Node<>(new MazeCell(1, 2));
        Node<MazeCell> south = new Node<>(new MazeCell(1, 0));
        Node<MazeCell> west = new Node<>(new MazeCell(0, 1));
        Node<MazeCell> east = new Node<>(new MazeCell(2, 1));
        Node<MazeCell> invalid = new Node<>(new MazeCell(2, 2));
        assertTrue(getDirection(one, north) == Direction.NORTH);
        assertTrue(getDirection(one, south) == Direction.SOUTH);
        assertTrue(getDirection(one, west) == Direction.WEST);
        assertTrue(getDirection(one, east) == Direction.EAST);
        try {
            getDirection(one, invalid);
            fail("exception not caught");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    private Direction getDirection(Node<MazeCell> current, Node<MazeCell> next) {
        float x1 = current.getData().getGridX();
        float y1 = current.getData().getGridY();
        float x2 = next.getData().getGridX();
        float y2 = next.getData().getGridY();
        float dx = x2 - x1;
        float dy = y2 - y1;
        if (dx > 1 || dy > 1) {
            throw new IllegalArgumentException("cells not adjacent");
        }
        if (dx >= 1 && dy >= 1) {
            throw new IllegalArgumentException("cells not adjacent");
        }
        if (dy > 0) return Direction.NORTH;
        if (dy < 0) return Direction.SOUTH;
        if (dx < 0) return Direction.WEST;
        if (dx > 0) return Direction.EAST;
        throw new RuntimeException("unknown angle " + current.getData() + " vs " +
                next.getData());
    }

    @Test
    public void moveTest(){
        Node<MazeCell> current = new Node<>(new MazeCell(1,1));
        Node<MazeCell> southTarget = new Node<>(new MazeCell(1,0));

        float[] currentLoc = maze.getCellCenter(current.getData());
        float[] southLoc = maze.getCellCenter(southTarget.getData());
        float dx = southLoc[0] - currentLoc[0];
        float dy = southLoc[1] - currentLoc[1];
        float[] moveVector = new float[]{dx,0,dy};
        float[] curr = new float[3];
        for(int i = 0; i <= 100; i+=10){
            Vector3.multiplyByScalar(curr,moveVector,i/100f);
            Vector3.addVV(curr,curr,new float[]{currentLoc[0],0,currentLoc[1]});
            System.out.printf("start=(%f,%f) vec=(%f,%f,%f) curr=(%f,%f)\n",
                    currentLoc[0],currentLoc[1],
                    moveVector[0],moveVector[1],moveVector[2],
                    curr[0],curr[2]);
        }
        assertTrue(curr[2]==26f);
    }

    @Test
    public void validCells(){

    }
    private boolean isValidCell(Direction currentDir, Node<MazeCell> current, Node<MazeCell> target){
        return false;
    }
}