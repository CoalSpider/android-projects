package com.ben.testapp.maze;

import com.ben.testapp.common.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static com.ben.testapp.common.Direction.*;

/**
 * Created by Ben on 7/30/2017.
 */

class RecursiveSubdivision {
    private static final int startX = 0;
    private static final int startY = 0;

    private int rows;
    private int columns;

    private int seed = 1993;
    private Random random = new Random(seed);

    private MazeCell[][] grid;
    private Stack<MazeCell> cellStack = new Stack<>();
    private List<MazeCell> validAdjacentCells = new ArrayList<>();
    private MazeCell currentCell;
    private MazeCell exitCell;

    private Tree<MazeCell> connectionGraph;

    RecursiveSubdivision(int columns, int rows) {
        if (columns < 3 || rows < 3) {
            throw new IllegalArgumentException("minimum maze size is 3x3");
        }
        /** TODO: decide on a maximum (for lag purporses) **/

        this.rows = columns + 1;
        this.columns = rows + 1;

        buildGrid();
    }

    private void buildGrid() {
        grid = new MazeCell[rows][columns];
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < columns; i++) {
                grid[j][i] = new MazeCell(i, j);
            }
        }
        removeRightWallsTopRowAndSetVisited();
        removeBotomWallsLeftRowAndSetVisited();
    }

    private void removeRightWallsTopRowAndSetVisited() {
        for (int i = 0; i < columns; i++) {
            getCell(i, grid.length - 1).removeRightWall();
            getCell(i, grid.length - 1).setVisited(true);
        }
    }

    private void removeBotomWallsLeftRowAndSetVisited() {
        for (int i = 0; i < rows; i++) {
            getCell(grid[0].length - 1, i).removeBottomWall();
            getCell(grid[0].length - 1, i).setVisited(true);
        }
    }

    private MazeCell getCell(int column, int row) {
        return grid[row][column];
    }

    void run2(){
        cellR(0,0);
        cellE(0,1);
        cellR(0,2);
        cellR(0,3);
        cellR(0,4);
        cellB(0,5);

        cellB(1,0);
        cellRB(1,1);
        cellE(1,2);
        cellR(1,3);
        cellB(1,4);
        cellB(1,5);

        cellE(2,0);
        cellR(2,1);
        cellRB(2,2);
        cellR(2,3);
        cellRB(2,4);
        cellB(2,5);

        cellB(3,0);
        cellE(3,1);
        cellR(3,2);
        cellR(3,3);
        cellE(3,4);
        cellRB(3,5);

        cellB(4,0);
        cellRB(4,1);
        cellE(4,2);
        cellRB(4,3);
        cellRB(4,4);
        cellB(4,5);

        cellR(5,0);
        cellR(5,1);
        cellR(5,2);
        cellR(5,3);
        cellR(5,4);
        cellRB(5,5);
    }
    private void cellR(int y, int x){
        grid[y][x].removeRightWall();
    }
    private void cellB(int y, int x){
        grid[y][x].removeBottomWall();
    }
    private void cellRB(int y, int x){
        grid[y][x].removeRightWall();
        grid[y][x].removeBottomWall();
    }
    private void cellE(int y, int x){
        // do nothing
    }
    void run() {
        currentCell = getCell(startX, startY);
        currentCell.setVisited(true);
        currentCell.setDepth(0);
        cellStack.push(currentCell);
        // carve entrance to maze
        getCell(0, startY).removeRightWall();

        Node<MazeCell> root = new Node<>(currentCell);
        connectionGraph = new Tree<>();
        connectionGraph.setRootNode(root);
        Node<MazeCell> currentNode = root;

        while (cellStack.size() > 0) {

            validAdjacentCells.clear();
            setValidAdjacentCells();

            if (validAdjacentCells.isEmpty()) {
                cellStack.pop();
                if (cellStack.size() != 0) {
                    currentCell = cellStack.peek();
                    currentNode = currentNode.getParent();
                }
            } else {
                MazeCell newCell = chooseRandomAdjacentCell();
                newCell.setVisited(true);
                newCell.setDepth(currentCell.getDepth() + 1);
                carvePathBetweenCurrentAndNew(newCell);

                Node<MazeCell> newNode = new Node<>(newCell);
                currentNode.addChild(newNode);
                currentNode = newNode;

                currentCell = newCell;
                cellStack.push(currentCell);
            }

            if (exitCell == null) {
                exitCell = currentCell;
            }

            if (currentCell.getDepth() > exitCell.getDepth()) {
                if (isBorderCell(currentCell)) {
                    exitCell = currentCell;
                }
            }
        }

        carveMazeExit();
    }

    private void setValidAdjacentCells() {
        MazeCell left = getLeftCell();
        MazeCell right = getRightCell();
        MazeCell above = getAboveCell();
        MazeCell below = getBelowCell();
        if (left != null) validAdjacentCells.add(left);
        if (right != null) validAdjacentCells.add(right);
        if (above != null) validAdjacentCells.add(above);
        if (below != null) validAdjacentCells.add(below);
    }

    private MazeCell getLeftCell() {
        return getValidCellOrReturnNull(
                currentCell.getGridX() + Direction.WEST.getNum(), currentCell
                        .getGridY());
    }

    private MazeCell getRightCell() {
        return getValidCellOrReturnNull(
                currentCell.getGridX() + Direction.EAST.getNum(), currentCell
                        .getGridY());
    }

    private MazeCell getAboveCell() {
        return getValidCellOrReturnNull(
                currentCell.getGridX(), currentCell.getGridY() + Direction.NORTH
                        .getNum());
    }

    private MazeCell getBelowCell() {
        return getValidCellOrReturnNull(
                currentCell.getGridX(), currentCell.getGridY() + Direction.SOUTH
                        .getNum());
    }

    private MazeCell getValidCellOrReturnNull(int col, int row) {
        MazeCell cell = null;
        if (locationIsValid(col, row)) {
            if (getCell(col, row).hasBeenVisited() == false) {
                cell = getCell(col, row);
            }
        }
        return cell;
    }

    private boolean locationIsValid(int col, int row) {
        return row < rows && row > -1 && col < columns && col > -1;
    }

    private MazeCell chooseRandomAdjacentCell() {
        return validAdjacentCells.get(random.nextInt(validAdjacentCells.size()));
    }

    private boolean isBorderCell(MazeCell mazeCell) {
        int x = mazeCell.getGridX();
        int y = mazeCell.getGridY();
        return x == 1 || x == columns - 1 || y == 1 || y == rows - 1;
    }

    private void carvePathBetweenCurrentAndNew(MazeCell newCell) {
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

    private void carveMazeExit() {
        int col = exitCell.getGridX();
        int row = exitCell.getGridY();
        System.out.println(col+","+row);
        // prefer carving north/south to west/east

        // carve left
        if (col == 1) {
            getCell(grid[0].length-1, row).removeRightWall();
            // carve right
        } else if (col == grid[0].length-1) {
            exitCell.removeRightWall();
            // carve up
        } else if (row == 1) {
            getCell(col, grid.length-1).removeBottomWall();
            // carve down
        } else if (row == grid[0].length-1) {
            exitCell.removeBottomWall();
        } else {
            throw new RuntimeException("exit cells is not border cell");
        }
    }

    MazeCell[][] getGrid() {
        return grid;
    }

    Tree<MazeCell> getConnectionGraph() {
        return connectionGraph;
    }

    public float[] getStartCell() {
        return new float[]{startX, startY};
    }

    public float[] getEndCell() {
        return new float[]{exitCell.getGridX(), exitCell.getGridY()};
    }
}
