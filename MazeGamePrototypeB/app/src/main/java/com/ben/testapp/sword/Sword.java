package com.ben.testapp.sword;

import android.opengl.Matrix;

import com.ben.testapp.model.SceneModel;
import com.ben.testapp.userInterface.Observer;
import com.ben.testapp.userInterface.Point;
import com.ben.testapp.userInterface.Subject;
import com.ben.testapp.userInterface.SwordPlane;
import com.ben.testapp.userInterface.UserInterface;
import com.ben.testapp.util.Interpolator;
import com.ben.testapp.util.Quaternion;
import com.ben.testapp.util.Vector3;

import java.util.ArrayList;
import java.util.List;

import static com.ben.testapp.util.Quaternion.*;

/**
 * Created by Ben on 8/9/2017.
 */

/**
 * The movements of the object
 * -z is forward
 * +z is backwards
 * +x is right
 * -x is left
 * +y is counterclockwise
 * -y is clockwise
 **/
public class Sword extends SceneModel implements Observer{
    private static final float[] restPosQuat = {0, 0, 0, 1};
    private float[] currentQuat = {0, 0, 0, 1};

    private float[] playerOrient = fromAxisAngle(0, 1, 0, toRad(0));

    Animation currentAnim;

    private RotationBuilder rotationBuilder = new RotationBuilder();

    public Sword(float[] center, float size) {
        super(center, size);

        intitAnimatations();
    }

    private void intitAnimatations() {
        curveCutRightToHorizontalLeft();
    //    chopAnim = animCCRHL;

        curveCutLeftToHorizontalRight();
    //    chopAnim = animCCLHR;

        chopDown();
       // chopAnim = animCD;

        curveCutLefToDiagDownRight();
    //    chopAnim = animCCLDR;

        curveCutRightToDiagDownLeft();
    //    chopAnim = animCCRDL;
    }

    Animation animCCRHL = new Animation();
    private void curveCutRightToHorizontalLeft(){
        animCCRHL.addFrame(new Keyframe(restPosQuat));

        rotationBuilder.clearCurrentRotation();

        float[] curveCut =
                rotationBuilder.rotY(180).rotZ(-90).rotY(-25).getComposition();
        animCCRHL.addFrame(new Keyframe(curveCut));

        float[] horizontalSlice = rotationBuilder.rotY(135).getComposition();
        animCCRHL.addFrame(new Keyframe(horizontalSlice));
    }

    Animation animCCLHR = new Animation();
    private void curveCutLeftToHorizontalRight(){
        animCCLHR.addFrame(new Keyframe(restPosQuat));

        rotationBuilder.clearCurrentRotation();

        float[] curveCut =
                rotationBuilder.rotY(-180).rotZ(-90).rotY(25).getComposition();
        animCCLHR.addFrame(new Keyframe(curveCut));

        float[] horizontalSlice = rotationBuilder.rotY(-135).getComposition();
        animCCLHR.addFrame(new Keyframe(horizontalSlice));
    }

    Animation animCD = new Animation();
    private void chopDown(){
        animCD.addFrame(new Keyframe(restPosQuat));

        rotationBuilder.clearCurrentRotation();
        float[] prepEdgeAlign = rotationBuilder.rotY(90).getComposition();
        animCD.addFrame(new Keyframe(prepEdgeAlign));

        float[] verticalChop = rotationBuilder.rotZ(-135).getComposition();
        animCD.addFrame(new Keyframe(verticalChop));
    }

    Animation animCCLDR = new Animation();
    private void curveCutLefToDiagDownRight(){
        animCCLDR.addFrame(new Keyframe(restPosQuat));

        rotationBuilder.clearCurrentRotation();
        float[] curveCut =
                rotationBuilder.rotY(-180).rotZ(-55).rotY(45).getComposition();
        animCCLDR.addFrame(new Keyframe(curveCut));

        rotationBuilder.clearCurrentRotation();
        float[] diagonalSlice =
                rotationBuilder.rotY(-180).rotZ(-125).rotY(-90).getComposition();
        animCCLDR.addFrame(new Keyframe(diagonalSlice));
    }

    Animation animCCLUR = new Animation();
    private void curveCutLefToDiagUpRight(){
        // TODO: dont know if want u shape cut
    }

    Animation animCCRDL = new Animation();
    private void curveCutRightToDiagDownLeft(){
        animCCRDL.addFrame(new Keyframe(restPosQuat));

        rotationBuilder.clearCurrentRotation();
        float[] curveCut =
                rotationBuilder.rotY(180).rotZ(-55).rotY(-45).getComposition();
        animCCRDL.addFrame(new Keyframe(curveCut));

        rotationBuilder.clearCurrentRotation();
        float[] diagonalSlice =
                rotationBuilder.rotY(180).rotZ(-125).rotY(90).getComposition();
        animCCRDL.addFrame(new Keyframe(diagonalSlice));
    }

    Animation animCCRUL = new Animation();
    private void curveCutRightToDiagUpLeft(){
        // TODO: dont know if want u shape cut
    }

    public void setAnimationBasedOnLine(float startX, float startY, float endX, float endY){
        if(startX<0||startY<0||endX<0||endY<0){
            return;
        }
        // startX is close to endX then vertical
        // startY is close to endY then horizontal
        // if both are close discard it as theres not enough data

        // otherwise we want a slope between start and end
        // we check for the sign of dx,dy
        // if both signs are the same the slope is a top left to bottom right diagonal
        // if the signs are different the slope is a top right to bottom left diagonal

        // TODO: animation lock or restart animation
        if(currentAnim!=null&&currentAnim.isAnimationFinished()==false){
            return;
        }

        float dx = endX - startX;
        float dy = endY - startY;
        System.out.println("diffX = " + dx+" diffY = "+dy);
        System.out.println("Start = ("+(int)startX+","+(int)startY+") End = ("+endX+","+endY+")");
        if(Math.abs(dy) < 100 && Math.abs(dx) < 100){
            return;
        }
        if(Math.abs(dy) < 100){
            if(dx > 0) {
                // left to right
                currentAnim = animCCLHR;
            } else {
                // right to left
                currentAnim = animCCRHL;
            }
        } else if(Math.abs(dx) < 100){
            if(dy > 0){
                // down
                currentAnim = animCD;
            } else {
                // up
                //TODO: currentAnim = animCU;
                return;
            }
        } else {
            if((dx < 0 && dy < 0) || (dx > 0 && dy > 0)){
                // same sign
                // diag top left to bottom right
                currentAnim = animCCLDR;
            } else {
                // diff sign
                // diag top right to bottom left
                currentAnim = animCCRDL;
            }
        }
        currentAnim.start();
    }

    private float toRad(float degs) {
        return (float) Math.toRadians(degs);
    }

    private float percent = 0;

    public void playAnim(float elapsedTime) {
        if(currentAnim == null || currentAnim.isAnimationFinished()) {
            return;
        }

        if (percent >= 1) {
            currentAnim.incrementFrame();
            percent = 0;
        }

        if (currentAnim.keyframes.size() < 2) return;
        float[] quat1 = currentAnim.getCurrentFrame().getQuaternion();
        float[] quat2 = currentAnim.getNextFrame().getQuaternion();

        currentQuat = Interpolator.nLerpQuat(quat1, quat2, percent);

        float[] result = new float[4];
        // move current to align to the player
        // another way of thinkng is were at orient Q1 we want to rotate Q1 to
        // be in line with the player orientation P1 so Q1 * P1
        Quaternion.multiplyQQ(result, currentQuat, playerOrient);

        setNewRotation(toRotationMatrixCM(result));

        /** TODO: frame rate independent animation time**/
        percent += 36f / 1000f;
    }

    // places on camera
    public void setXYZ(float[] xyz) {
        // do nothing
        setTranslation(xyz);
    }

    public void setOrient(float rotY) {
        float fudgeX = (float) Math.cos(rotY);
        float fudgeZ = (float) Math.sin(rotY);
        float[] newTranslate = new float[3];
        Vector3.addVV(newTranslate, getTranslation(), new float[]{fudgeX, 0, fudgeZ});
        setTranslation(newTranslate);
        /** TODO: remove magic number 1.25 **/
        playerOrient = fromAxisAngle(0, 1, 0, -rotY);
    }

    public boolean currentAnimFinished(){
        if(currentAnim == null){return true;}
        return currentAnim.isAnimationFinished();
    }

    @Override
    public void update(Subject o) {
        if(o instanceof UserInterface){
            SwordPlane so = ((UserInterface) o).getSwordPlane();
            float startX = so.getStartX();
            float startY = so.getStartY();
            float endX = so.getEndX();
            float endY = so.getEndY();
            setAnimationBasedOnLine(startX,startY,endX,endY);
        }
    }
}

class Animation {
    List<Keyframe> keyframes;
    private int frameNumber;
    private boolean animationFinished = false;

    Animation() {
        keyframes = new ArrayList<>();
        frameNumber = 0;
    }

    void addFrame(Keyframe keyframe) {
        keyframes.add(keyframe);
    }

    Keyframe getCurrentFrame() {
        if (frameNumber > keyframes.size()-1) {
            frameNumber = 0;
            System.out.println(animationFinished);
            animationFinished = true;
        }
        return keyframes.get(frameNumber);
    }

    Keyframe getNextFrame() {
        if (frameNumber + 1 > keyframes.size() - 1) {
            return keyframes.get(0);
        } else {
            return keyframes.get(frameNumber + 1);
        }
    }

    void incrementFrame() {
        frameNumber += 1;
    }

    void start(){
        frameNumber = 0;
        animationFinished = false;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public boolean isAnimationFinished(){
        return animationFinished;
    }
}

class Keyframe {
    private final float[] quaternion;

    Keyframe(float[] quaternion) {
        this.quaternion = quaternion;
    }

    float[] getQuaternion() {
        return quaternion;
    }
}