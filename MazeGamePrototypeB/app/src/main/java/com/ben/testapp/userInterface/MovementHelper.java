package com.ben.testapp.userInterface;

/**
 * Created by Ben on 8/4/2017.
 */

import android.os.SystemClock;

/**
 * Converts joystick input to something we can use to move the
 * {@link com.ben.testapp.util.PlayerCamera}
 *
 * Joystick mapping for the angle is......
 *            -90
 *             |
 * -180,180 ---|---- -0,0
 *             |
 *            90
 **/
public class MovementHelper {
    private static float degreesRotationAroundY;
    private static final float[] translationXYZ = new float[3];

    private static final float moveSpeedPerSec = 5.0f;
    private static final float rotationPerSecInDegrees = 360.0f;

    private Joystick joystick;
    MovementHelper(Joystick joystick) {
        this.joystick = joystick;
    }

    public void update() {
        float elapsedTimeInSec = getElapsedSecondsSinceLastCall();
        if(joystick.getInnerDotDistanceFromCenter()/joystick.getOuterRadius() < 0.1){
            return;
        }

        float joystickAngle = (float)Math.toDegrees(joystick.getInnerDotAngleFromCenter());
        float rotateAmount = rotationPerSecInDegrees*elapsedTimeInSec;
        updateRotation(joystickAngle,rotateAmount);

        float joystickPercentForward =
                joystick.getInnerDotDistanceFromCenter()/joystick.getOuterRadius();
        float moveAmount = moveSpeedPerSec*elapsedTimeInSec*joystickPercentForward;
        updateTranslation(joystickAngle,moveAmount);

    }

    private void updateRotation(float angleInDegs, float rotationSpeed){
        degreesRotationAroundY += getRotationPercent(angleInDegs)*rotationSpeed;
    }

    private float getRotationPercent(float angleInDegs){
        float sign = getRotationSign(angleInDegs);
        float percent = ((float)(Math.cos(2* Math.toRadians(angleInDegs)))+1)/2f;
        return sign*percent;
    }

    // returns +1 for right (clockwise) , -1 for left (counterclockwise)
    private float getRotationSign(float angInDegs){
        if(angInDegs == 90 || angInDegs == -90) return 0;
        // should map -90 through 90 to rotate right
        return (angInDegs < 90 && angInDegs > -90) ? 1 : -1;
    }

    private void updateTranslation(float angle, float moveSpeed){
        float translationPercent = getTranslationPercent(angle)*moveSpeed;
        double angleInRad = Math.toRadians(degreesRotationAroundY);
        translationXYZ[0] += (float)Math.cos(angleInRad)*translationPercent;
        translationXYZ[2] += (float)Math.sin(angleInRad)*translationPercent;
    }

    private float getTranslationPercent(float angleInDegrees){
        float sign = getTranslationSign(angleInDegrees);
        float percent = 1 - ((float)(Math.cos(2* Math.toRadians(angleInDegrees)))+1)/2f;
        return percent*sign;
    }

    // returns +1 for up (forwards), -1 for down (backwards)
    private float getTranslationSign(float angleInDegrees){
        if(angleInDegrees == 0 || angleInDegrees == 180 || angleInDegrees == -180) {
            return 0;
        }
        return (angleInDegrees > 0) ? -1 : 1;
    }

    private long oldTime;
    private final float MILLISEC_PER_SEC = 1000f;
    private float getElapsedSecondsSinceLastCall() {
        if (oldTime == 0) {oldTime = SystemClock.currentThreadTimeMillis();}
        float delta =
                (SystemClock.currentThreadTimeMillis() - oldTime) / MILLISEC_PER_SEC;
        oldTime = SystemClock.currentThreadTimeMillis();
        return delta;
    }

    /** @return rotation around y in degrees**/
    public float getCurrentRotation() {
        return degreesRotationAroundY;
    }

    public float[] getTranslationXYZ() {
        return translationXYZ;
    }

    public void setTranslationXYZ(float x, float y, float z) {
        MovementHelper.translationXYZ[0] = x;
        MovementHelper.translationXYZ[1] = y;
        MovementHelper.translationXYZ[2] = z;
    }

    public void setDegreesRotationAroundY(float degreesRotationAroundY) {
        MovementHelper.degreesRotationAroundY = degreesRotationAroundY;
    }
}
