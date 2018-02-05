/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.model;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;

/**
 *
 * @author Ben Norman
 */
public class MergedCubes {

    private Model cube;

    public ModelInstance get() {
        // make new model builder
        ModelBuilder modelBuilder = new ModelBuilder();
        //start
        modelBuilder.begin();
        // make new meshPartBuilder
        MeshPartBuilder meshBuilder;
        // init part1
        meshBuilder = modelBuilder.part("part1", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material());
        // build cone shape
        ConeShapeBuilder.build(meshBuilder,5, 5, 5, 10);
        // create new node
        Node node = modelBuilder.node();
        // translate node
        node.translation.set(10, 0, 0);
        /// create part2
        meshBuilder = modelBuilder.part("part2", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material());
        // build shphere
        SphereShapeBuilder.build(meshBuilder,5, 5, 5, 10, 10);
        // end model builder
        cube = modelBuilder.end();
        // return new
        return new ModelInstance(cube);
    }
    
    public void dispose(){
        cube.dispose();
    }
}

// OBJ FILE
/**
 * # Blender v2.79 (sub 0) OBJ File: '' # www.blender.org o Cube v 0.050000
 * -0.500000 -0.500000 v 0.050000 -0.500000 0.500000 v -0.050000 -0.500000
 * 0.500000 v -0.050000 -0.500000 -0.500000 v 0.050000 0.500000 -0.500000 v
 * 0.050000 0.500000 0.500000 v -0.050000 0.500000 0.500000 v -0.050000 0.500000
 * -0.500000 vt 0.050000 0.250000 vt -0.950000 1.250000 vt 0.050000 1.250000 vt
 * 0.050000 0.250000 vt 1.050000 -0.750000 vt 1.050000 0.250000 vt 0.250000
 * 0.250000 vt -0.750000 -0.750000 vt 0.250000 -0.750000 vt 0.050000 0.250000 vt
 * -0.950000 -0.750000 vt 0.050000 -0.750000 vt 0.250000 0.250000 vt 1.250000
 * 1.250000 vt 1.250000 0.250000 vt 0.050000 0.250000 vt -0.950000 1.250000 vt
 * 0.050000 1.250000 vt -0.950000 0.250000 vt 0.050000 -0.750000 vt -0.750000
 * 0.250000 vt -0.950000 0.250000 vt 0.250000 1.250000 vt -0.950000 0.250000 vn
 * 0.0000 -1.0000 0.0000 vn 0.0000 1.0000 0.0000 vn 1.0000 -0.0000 0.0000 vn
 * 0.0000 -0.0000 1.0000 vn -1.0000 -0.0000 -0.0000 vn 0.0000 0.0000 -1.0000 s
 * off f 2/1/1 4/2/1 1/3/1 f 8/4/2 6/5/2 5/6/2 f 5/7/3 2/8/3 1/9/3 f 6/10/4
 * 3/11/4 2/12/4 f 3/13/5 8/14/5 4/15/5 f 1/16/6 8/17/6 5/18/6 f 2/1/1 3/19/1
 * 4/2/1 f 8/4/2 7/20/2 6/5/2 f 5/7/3 6/21/3 2/8/3 f 6/10/4 7/22/4 3/11/4 f
 * 3/13/5 7/23/5 8/14/5 f 1/16/6 4/24/6 8/17/6
 */
