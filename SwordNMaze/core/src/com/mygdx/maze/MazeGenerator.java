package com.mygdx.maze;

import com.mygdx.Util.TreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Ben Norman on 1/28/2018.
 *
 */
public class MazeGenerator {
    private MazeCell[][] mazeAsGrid;
    private TreeNode<MazeCell> mazeAsTree;
    private static final Random RANDOM = new Random(1993);
    private final int rows;
    private final int columns;

    MazeGenerator(int rows, int columns) {
        // we need to add a additonal row and column to account for the fact that maze cells are defined by only two walls
        // that means the first row is simply cells with a bottom wall
        // and the first column is simply cells with a right wall
        this.rows = rows + 1;
        this.columns = columns + 1;
        init();
    }

    private void init() {
        mazeAsGrid = new MazeCell[rows][columns];
        initFirstRow();
        initFirstColumn();
        fillGridWithWalls();
    }

    // the ai cant move in these cells so they wont be included in the tree
    private void initFirstRow() {
        for (int i = 0; i < columns; i++) {
            mazeAsGrid[0][i] = new MazeCell(0, i);
            mazeAsGrid[0][i].setVisited(true);
            mazeAsGrid[0][i].setHasRightWall(false);
        }
    }

    // the ai cant move in these cells so they wont be included in the tree
    private void initFirstColumn() {
        for (int i = 0; i < rows; i++) {
            if (mazeAsGrid[i][0] == null) {
                mazeAsGrid[i][0] = new MazeCell(i, 0);
            }
            mazeAsGrid[i][0].setVisited(true);
            mazeAsGrid[i][0].setHasBottomWall(false);
        }
    }

    // because were using a recursive backtracking algorithm we want to fill all the cells with walls before we start
    private void fillGridWithWalls() {
        // skip first column and first row
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < columns; j++) {
                mazeAsGrid[i][j] = new MazeCell(i, j);
            }
        }
    }

    /**
     * generate a maze using the recursive backtracking algorithm
     */
    public void generate() {
        Stack<MazeCell> mazeStack = new Stack<MazeCell>();
        // pick a random cell in the maze
        MazeCell startCell = getRandomMazeCell();
        // add to the stack
        startCell.setVisited(true);
        mazeStack.push(startCell);
        int depth = 0;
        
        // setup root node of the maze tree
        mazeAsTree = new TreeNode<MazeCell>(mazeStack.peek());
        TreeNode<MazeCell> currentNode = mazeAsTree;
        
        MazeCell currentCell;
        while (mazeStack.isEmpty() == false) {
            // set curent cell to top cell in stack
            currentCell = mazeStack.peek();
            List<MazeCell> unvisited = getUnvisitedNearbyCells(currentCell);
            // if all nearby cells have been visisted backtrack
            if (unvisited.isEmpty()) {
                // set current node as parent (move up tree)
                currentNode = currentNode.getParent();
                // backtrack
                mazeStack.pop();
                continue;
            }
            // otherwise carve a passge to a random unvisited nearby cell
            MazeCell nextCell = unvisited.get(RANDOM.nextInt(unvisited.size()));
            nextCell.setVisited(true);
            nextCell.visitedCount = depth++;
            // add next cell as child (ie they connect)
            TreeNode<MazeCell> next = new TreeNode<MazeCell>(nextCell);
            currentNode.addChild(next);
            currentNode = next;
            // psuh to stack
            carvePassage(currentCell, nextCell);
            mazeStack.push(nextCell);
        }
    }

    /**
     * gets a random maze cell to ignores the first row and column*
     */
    private MazeCell getRandomMazeCell() {
        int randomRow = getRandomIntInRange(1, rows - 1);
        int randomColumn = getRandomIntInRange(1, columns - 1);
        return mazeAsGrid[randomRow][randomColumn];
    }

    // return int in range min,max inclusive
    private int getRandomIntInRange(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * @return a nearby unvisited maze cell or null *
     */
    private List<MazeCell> getUnvisitedNearbyCells(MazeCell cell) {
        List<MazeCell> unvisited = new ArrayList<MazeCell>();
        int row = cell.getRow();
        int column = cell.getColumn();
        // if "above" row is not the first row
        if (row - 1 > 0) {
            MazeCell c = mazeAsGrid[row - 1][column];
            if (c.isVisited() == false) {
                unvisited.add(c);
            }
        }
        // if "below" is within the maze
        if (row + 1 < rows) {
            MazeCell c = mazeAsGrid[row + 1][column];
            if (c.isVisited() == false) {
                unvisited.add(c);
            }
        }
        // if "left" is not the first column
        if (column - 1 > 0) {
            MazeCell c = mazeAsGrid[row][column - 1];
            if (c.isVisited() == false) {
                unvisited.add(c);
            }
        }
        // if "right" is within the maze
        if (column + 1 < columns) {
            MazeCell c = mazeAsGrid[row][column + 1];
            if (c.isVisited() == false) {
                unvisited.add(c);
            }
        }

        return unvisited;
    }

    private void carvePassage(MazeCell current, MazeCell next) {
        int currentRow = current.getRow();
        int currentColumn = current.getColumn();
        int nextRow = next.getRow();
        int nextColumn = next.getColumn();
        // check if same cell loc
        if (nextRow == currentRow && nextColumn == currentColumn) {
            throw new IllegalArgumentException("current and next have same location");
        }
        // check if cells are adjacemt
        if (Math.abs(nextRow - currentRow) > 1 || Math.abs(nextColumn - currentColumn) > 1) {
            throw new IllegalArgumentException("current and next cell are not ajacent");
        }

        if (nextRow == currentRow) {
            if (nextColumn > currentColumn) {
                // carve "right"
                current.setHasRightWall(false);
            } else {
                // carve "left"
                next.setHasRightWall(false);
            }
        } else {
            if (nextRow > currentRow) {
                // carve "down"
                current.setHasBottomWall(false);
            } else {
                // carve "up"
                next.setHasBottomWall(false);
            }
        }
    }

    public MazeCell[][] getMazeAsGrid() {
        return mazeAsGrid;
    }

    public TreeNode<MazeCell> getMazeAsTree() {
        return mazeAsTree;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    
    /* 
    * =========================================================================
    * TEST CODE
    * =========================================================================
     */
    private void printMaze() {
        StringBuilder maze = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                MazeCell c = mazeAsGrid[i][j];
                maze.append(c.hasBottomWall() ? "_" : " ");
                maze.append(c.hasRightWall() ? "|" : " ");
            }
            maze.append("\n");
        }
        System.out.println(maze.toString());
    }
    private void printMaze2() {
        StringBuilder maze = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                MazeCell c = mazeAsGrid[i][j];
                maze.append((c.visitedCount<10)? "0"+c.visitedCount+" ":c.visitedCount+" ");
            }
            maze.append("\n");
        }
        System.out.println(maze.toString());
    }
    private void printTree(TreeNode<MazeCell> node, String tabs){
        System.out.println(tabs+node.getData());
        if(node.isLeaf()){
            return;
        }
        for(int i = 0; i < node.getChildren().size(); i++){
            printTree(node.getChildren().get(i),tabs+"\t");
        }
    }
    
    private void printTree(TreeNode<MazeCell> node, int depth){
        System.out.println(depth+" "+node);
        if(node.isLeaf()){
            return;
        }
        for(int i = 0; i < node.getChildren().size(); i++){
            printTree(node.getChildren().get(i),depth+1);
        }
    }

    public static void main(String[] args) {
        MazeGenerator mg = new MazeGenerator(5, 5);
        mg.printMaze();
        mg.generate();
        mg.printMaze();
        mg.printMaze2();
        mg.printTree(mg.mazeAsTree,"");
        mg.printTree(mg.mazeAsTree,0);
    }
}
