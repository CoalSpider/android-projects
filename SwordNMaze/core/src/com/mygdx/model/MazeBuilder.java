/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.mygdx.maze.MazeCell;
import com.mygdx.maze.MazeGenerator;
import java.util.ArrayList;
import java.util.List;

import static com.mygdx.util2.Settings.*;

/**
 *
 * @author Ben Norman
 *
 * Your one stop shop for all game models
 */
public class MazeBuilder {

    private static final float WALL_THICKNESS = 0.1f * MAZE_SCALE;
    private static final float HEIGHT_SHIFT = 1.0f * MAZE_SCALE * 0.5f;
    private static final float SHIFT_X = MAZE_SCALE * 0.5f - WALL_THICKNESS * 0.5f;
    private static final float SHIFT_Z = MAZE_SCALE * 0.5f - WALL_THICKNESS * 0.5f;

    private Model bottomWall;
    private Model rightWall;
    private Model cornerWall;

    private final List<GameModel> mazeParts = new ArrayList<GameModel>();

    public void loadMazeModels(MazeGenerator generator) {
        initModels();
        loadMergedInstances(generator.getMazeAsGrid());
    }

    private void initModels() {
        ModelLoader loader = new ObjLoader();
        bottomWall = loader.loadModel(Gdx.files.local("bottomWall.obj"));
        rightWall = loader.loadModel(Gdx.files.local("rightWall.obj"));
        cornerWall = loader.loadModel(Gdx.files.local("cornerWall.obj"));
    }

    public void loadMergedInstances(MazeCell[][] grid) {
        this.loadMergedInstances(grid, 1);
    }

    // chunks maze into equal size bits
    private void loadMergedInstances(MazeCell[][] grid, int size) {
        if (grid.length % size != 0 || grid[0].length % size != 0) {
            throw new IllegalArgumentException("grid len x or len y not divisible by " + size);
        }

        int startI = 0;
        int startJ = 0;
        while (startI < grid.length && startJ < grid[0].length) {
            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            for (int i = startI; i < startI + size; i++) {
                for (int j = startJ; j < startJ + size; j++) {
                    buildCell(modelBuilder, grid, i, j);
                }
            }
            mazeParts.add(new GameModel(modelBuilder.end()));
            if (startI == grid.length - size) {
                startJ += size;
                startI = 0;
            } else {
                startI += size;
            }
        }
        System.out.println("mergedPartsSize == " + mazeParts.size());
    }

    // TODO: need to manually set the translation of each cell manually or it wont be culled properly
    private void buildCell(ModelBuilder modelBuilder, MazeCell[][] grid, int row, int column) {
        float x = row * MAZE_SCALE;
        float z = column * MAZE_SCALE;
        float y = HEIGHT_SHIFT;
        MazeCell cell = grid[row][column];
        MeshPartBuilder meshBuilder;
        Material mat = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        if (cell.hasBottomWall()) {
            meshBuilder = modelBuilder.part(cell.getRow() + "" + cell.getColumn() + "Bottom", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, mat);
            BoundingBox box = new BoundingBox();
            new ModelInstance(bottomWall).calculateBoundingBox(box);
            BoxShapeBuilder.build(meshBuilder, x + SHIFT_X, y, z, box.getWidth(), box.getHeight(), box.getDepth());
        }
        if (cell.hasRightWall()) {
            meshBuilder = modelBuilder.part(cell.getRow() + "" + cell.getColumn() + "Right", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, mat);
            BoundingBox box = new BoundingBox();
            rightWall.calculateBoundingBox(box);
            BoxShapeBuilder.build(meshBuilder, x, y, z + SHIFT_Z, box.getWidth(), box.getHeight(), box.getDepth());
        }
        if (!cell.hasBottomWall() && !cell.hasRightWall()) {
            meshBuilder = modelBuilder.part(cell.getRow() + "" + cell.getColumn() + "Corner", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, mat);
            BoundingBox box = new BoundingBox();
            cornerWall.calculateBoundingBox(box);
            BoxShapeBuilder.build(meshBuilder, x + SHIFT_X, y, z + SHIFT_Z, box.getWidth(), box.getHeight(), box.getDepth());
        }
    }

    public void dispose() {
        bottomWall.dispose();
        rightWall.dispose();
        cornerWall.dispose();
    }

    public List<GameModel> getMazeParts() {
        return mazeParts;
    }
}
