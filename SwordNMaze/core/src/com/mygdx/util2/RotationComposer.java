/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.util2;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Ben Norman
 */
public class RotationComposer {
    private static final Vector3 X_AXIS = new Vector3(1,0,0);
    private static final Vector3 Y_AXIS = new Vector3(0,1,0);
    private static final Vector3 Z_AXIS = new Vector3(0,0,1);
    
    private final Quaternion result = new Quaternion().idt();

    public RotationComposer rX(float degrees) {
        result.mul(new Quaternion(X_AXIS,degrees));
        return this;
    }

    public RotationComposer rY(float degrees) {
        result.mul(new Quaternion(Y_AXIS,degrees));
        return this;
    }

    public RotationComposer rZ(float degrees) {
        result.mul(new Quaternion(Z_AXIS,degrees));
        return this;
    }
    
    public Quaternion getComposition(){
        return result.nor();
    }
}
