/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.google.fpl.liquidfun;

public class DistanceJointDef extends JointDef {
  private transient long swigCPtr;

  protected DistanceJointDef(long cPtr, boolean cMemoryOwn) {
    super(liquidfunJNI.DistanceJointDef_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DistanceJointDef obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        liquidfunJNI.delete_DistanceJointDef(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public void setLocalAnchorA(float x, float y) {
    liquidfunJNI.DistanceJointDef_setLocalAnchorA(swigCPtr, this, x, y);
  }

  public void setLocalAnchorB(float x, float y) {
    liquidfunJNI.DistanceJointDef_setLocalAnchorB(swigCPtr, this, x, y);
  }

  public void setLength(float value) {
    liquidfunJNI.DistanceJointDef_length_set(swigCPtr, this, value);
  }

  public float getLength() {
    return liquidfunJNI.DistanceJointDef_length_get(swigCPtr, this);
  }

  public void setFrequencyHz(float value) {
    liquidfunJNI.DistanceJointDef_frequencyHz_set(swigCPtr, this, value);
  }

  public float getFrequencyHz() {
    return liquidfunJNI.DistanceJointDef_frequencyHz_get(swigCPtr, this);
  }

  public void setDampingRatio(float value) {
    liquidfunJNI.DistanceJointDef_dampingRatio_set(swigCPtr, this, value);
  }

  public float getDampingRatio() {
    return liquidfunJNI.DistanceJointDef_dampingRatio_get(swigCPtr, this);
  }

  public DistanceJointDef() {
    this(liquidfunJNI.new_DistanceJointDef(), true);
  }

}
