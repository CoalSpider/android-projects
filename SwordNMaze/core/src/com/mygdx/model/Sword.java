/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.util2.RotationComposer;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Ben Norman
 *
 *
 * The players main weapon
 */
public class Sword {

    private Model swordModel;
    private GameModel swordInstance;

    public void initModel() {
        ModelBuilder mb = new ModelBuilder();
        swordModel = mb.createBox(0.1f, 1.1f, 0.1f,
                new Material(ColorAttribute.createDiffuse(Color.PINK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        swordInstance = new GameModel(swordModel);
    }

    private final Quaternion rotation = new Quaternion();
    private final Vector3 yAxis = new Vector3(0, 1, 0);

    public void placeInFrontOfCamera(Camera cam) {
        // camera location
        Vector3 pos = cam.position.cpy();
        // camera rotation // for now we assume we are only about to rotate around y axis
        float rotY = cam.view.getRotation(rotation).getAngleAround(yAxis);
        float rotationShiftDegs = 0;
        float rad = (float) Math.toRadians(rotY + rotationShiftDegs); // shift a little to right
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        // move one unit out in front of camera
        pos.add(sin, 0, -cos);
        // set position and negate camera rotation
        //swordInstance.transform.set(pos, new Quaternion(yAxis, -rotY));
        swordInstance.transform.set(pos, new Quaternion(yAxis, -rotY));
        // rotate 45 degrees around local y axis (minus a shift)
        //swordInstance.transform.rotate(yAxis, 45-rotationShiftDegs);

    }

    private Quaternion start = new RotationComposer().rX(-90).rZ(75).rX(45).getComposition();
    private Quaternion end = new RotationComposer().rX(-90).rZ(-75).rX(-45).getComposition();
    private float percent;

    public void advanceTestAnim() {
        percent += 0.01;
        swordInstance.transform
                .rotate(start.cpy().slerp(end, percent))
                .translate(0, 0.2f, 0)
                .rotate(new Quaternion(new Vector3(0, 1, 0), percent * 360));
        if (percent >= 1) {
            percent = 0;
            Quaternion tmp = start.cpy();
            start.set(end);
            end.set(tmp);
        }
    }

    public void pointAtTest(Camera cam, float mouseX, float mouseY) {
        Vector3 posA = swordInstance.transform.getTranslation(new Vector3());
        Vector3 posB = cam.unproject(new Vector3(mouseX,mouseY,1));
        Vector3 dir = posB.sub(posA).nor().scl(1, -1, 1); // flip y axos
        // get rotation matrix
        Matrix4 rotmat = new Matrix4().setToRotation(dir, yAxis);
        
        float rotY = cam.view.getRotation(rotation).getAngleAround(yAxis);
        rotY += 0; // shift a little
        float rad = (float) Math.toRadians(rotY);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        
        // move one unit out in front of camera
        Vector3 newPos = new Vector3(sin,0,-cos).add(cam.position);
        Quaternion newRot = new Quaternion().setFromMatrix(rotmat);
        //swordInstance.transform.set(newPos, newRot);
        swordInstance.transform.idt().translate(newPos).rotate(newRot).rotate(yAxis, -rotY);
    }
    
    public GameModel getModelInstance() {
        return this.swordInstance;
    }

    public void onDispose() {
        swordModel.dispose();
    }
}
