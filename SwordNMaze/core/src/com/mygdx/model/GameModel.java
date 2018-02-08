/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 *
 * @author Ben Norman
 */
public class GameModel extends ModelInstance {

    private final Vector3 center = new Vector3();
    private final Vector3 dimensions = new Vector3();
    public final float radius;
    // only need 1
    private static final BoundingBox bounds = new BoundingBox();

    public GameModel(Model model) {
        super(model);
        model.calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len()/2f;
    }

    public Vector3 getCenter() {
        return center;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }
    
    public float getRadius(){
        return radius;
    }

}
