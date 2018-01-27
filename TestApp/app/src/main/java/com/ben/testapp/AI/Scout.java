package com.ben.testapp.AI;

import android.opengl.Matrix;

import com.ben.testapp.common.Direction;
import com.ben.testapp.maze.Maze;
import com.ben.testapp.maze.MazeCell;
import com.ben.testapp.maze.Node;
import com.ben.testapp.maze.Tree;
import com.ben.testapp.model.SceneModel;
import com.ben.testapp.util.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ben on 8/7/2017.
 */

public class Scout extends SceneModel {
    private static final Random random = new Random(0);
    private static final float moveSpeedPerSec = 1;
    private static final float rotationSpeedPerSec = 1;

    /**
     * TODO: hook valid default so scout doesnt get stuck on its spawnpoint
     **/
    private Direction currentDirection = Direction.EAST;
    private Direction targetDirection = null;

    private Node<MazeCell> current;
    private Node<MazeCell> target;
    private Tree<MazeCell> connectionGraph;
    private Maze maze;

    private float[] startPos;
    private float[] targetPos;
    private float[] targetVector;
    private float percentVector;

    private float percentRotation;

    public Scout(float[] center, float size) {
        super(center, size);
    }

    /** since this enemy is a random mover we dont care about shortest path
     * but if we say wanted to get to the player the A* algorithm would need to be implemented**/
    public void update(float elapsedTime) {
        if (connectionGraph == null) {
            throw new RuntimeException("connection graph not initalized");
        }
        if (target == null) {
            setNewTargetCellAndDirection();
        }

        if (percentRotation != 1) {
            rotate(elapsedTime);
        } else if (percentVector != 1) {
            move(elapsedTime);
        } else {
            currentDirection = targetDirection;
            current = target;
            setNewTargetCellAndDirection();
        }
    }

    private void setNewTargetCellAndDirection() {
        if (current.isLeaf()) {
            target = current.getParent();
        } else {
            List<Node<MazeCell>> cells = getValidCells(current);
            if (cells.isEmpty()) {
                target = current.getParent();
            } else {
                target = cells.get(random.nextInt(cells.size()));
            }
        }
        if (target == null) {
            throw new NullPointerException("target not initalized");
        }

        startPos = maze.getCellCenter(current.getData());
        targetPos = maze.getCellCenter(target.getData());
        float dx = targetPos[0] - startPos[0];
        float dy = targetPos[1] - startPos[1];
        targetVector = new float[]{dx, 0, dy};
        percentVector = 0;

        targetDirection = getDirection(current, target);
        percentRotation = 0;
    }

    private List<Node<MazeCell>> getValidCells(Node<MazeCell> current) {
        List<Node<MazeCell>> validCells = new ArrayList<>();
        for (Node<MazeCell> cell : current.getChildren()) {
            if (isInOppositDirection(current, cell) == false || targetDirection == null) {
                validCells.add(cell);
            }
        }
        if(current.getParent() != null) {
            if (isInOppositDirection(current, current.getParent()) == false) {
                validCells.add(current.getParent());
            }
        } else if(validCells.size() == 0){
            // if the parent is null and theres no valid cells get the first child
            validCells.add(current.getChildren().iterator().next());
        }
        return validCells;
    }

    private boolean isInOppositDirection(Node<MazeCell> current, Node<MazeCell> next) {
        Direction dir = getDirection(current, next);
        switch (dir) {
            case NORTH:
                return (currentDirection == Direction.SOUTH);
            case SOUTH:
                return (currentDirection == Direction.NORTH);
            case WEST:
                return (currentDirection == Direction.EAST);
            case EAST:
                return (currentDirection == Direction.WEST);
        }
        return false;
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

    private void move(float elapsedTimeInSec) {
        percentVector += 0.05f;
        if (percentVector > 1) {percentVector = 1;}
        float[] position = new float[3];
        Vector3.multiplyByScalar(position, targetVector, percentVector);
        Vector3.addVV(position, position, new float[]{startPos[0], 0, startPos[1]});
        setTranslation(position);
    }

    private void rotate(float elapsedTimeInSec) {
        float currDir = currentDirection.getAngle();
        float tarDir = targetDirection.getAngle();
        if (currentDirection == targetDirection) {
            percentRotation = 1;
        }
        percentRotation += 0.05f;
        if (percentRotation > 1) {percentRotation = 1;}
        float deltaAngle = tarDir - currDir;
        float rotation = currDir + deltaAngle*percentRotation;
        if(tarDir==270 && currDir == 0){
            rotation = currDir - 90*percentRotation;
        }

        float[] rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, rotation, 0, 1, 0);
        setNewRotation(rotationMatrix);
    }


    private void setConnectionGraph(Tree<MazeCell> connectionGraph) {
        this.connectionGraph = connectionGraph;
        /** TODO: convert to spawnpoint **/
        current = connectionGraph.getRootNode();
        float[] spawnPoint = maze.getCellCenter(current.getData());
        startPos = new float[]{spawnPoint[0], -3, spawnPoint[1]};
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        setConnectionGraph(maze.getConnectionGraph());
    }
}