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
        swordModel = mb.createBox(0.05f, 1.1f, 0.05f,
                new Material(ColorAttribute.createDiffuse(Color.PINK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        swordInstance = new GameModel(swordModel);
    }

    private final Quaternion rotation = new Quaternion();
    private final Vector3 yAxis = new Vector3(0, 1, 0);

    public void placeInFrontOfCamera(Camera cam) {
        // camera rotation around y axis
        float rotY = cam.view.getRotation(rotation).getAngleAround(yAxis);
        rotY += 0; // shift a little
        float rad = (float) Math.toRadians(rotY);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        // move one unit out in front of camera
        Vector3 pos = cam.position.cpy().add(sin, 0, -cos);

        // set position and negate camera rotation
        swordInstance.transform.set(pos, new Quaternion(yAxis, -rotY));
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
        // get rotation to current mouse location
        Vector3 posA = swordInstance.transform.getTranslation(new Vector3());
        Vector3 posB = cam.unproject(new Vector3(mouseX, mouseY, 1));
        Vector3 dir = posB.sub(posA).nor().scl(1, -1, 1); // flip y axis
        Quaternion rot = new Quaternion().setFromMatrix(new Matrix4().setToRotation(dir, yAxis));
        
        // get the camera rotation around Y
        float rotY = cam
                .view
                .getRotation(rotation)
                .getAngleAround(yAxis);
        
        // translate 1 unit out from the current camera position
        float rad = (float) Math.toRadians(rotY+30); // shift right a little
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        Vector3 newPos = new Vector3(sin, 0, -cos).scl(0.25f).add(cam.position);
        
        // set to point at mouse and translate
        // negate camera rotation on y --> now rotate to point --> 
        // ???
        // --> now translate
        swordInstance.transform.idt().translate(newPos).rotate(rot).translate(0, -0.5f, 0).rotate(yAxis, -rotY);
    }
    
    public GameModel getModelInstance() {
        return this.swordInstance;
    }

    public void onDispose() {
        swordModel.dispose();
    }
}
