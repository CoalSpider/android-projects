package com.ben.game.objects;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.ben.game.collisionEngine.Bounds.Bounds;
import com.ben.game.collisionEngine.Bounds.BoundsType;
import com.ben.game.collisionEngine.Bounds.Rectangle;
import com.ben.game.util.Quaternion;
import com.ben.game.util.Vec2f;
import com.ben.game.util.Vec3f;

/**
 * Created by Ben on 5/13/2017.
 * <p>
 * The base class for all game objects
 * </p>
 * The GameObject is created with a Bounds (at the time of writing a 2d bounds).
 * </p>
 * <p>
 * Then if you want to render the object {@link #loadModel(Context, int, int, int)} needs to
 * be called. The load method will only run once multiple calls will have no effect.
 * </p>
 **/
public class GameObject {
    // modelVBOI holds the vertex buffers for opengl
    private ModelVBOI vboi;
    // model matrix for opengl
    private float[] modelMatrix;

    private Vec3f center3d;
    private Vec2f center2d;

    // the collision bounds for the object
    // the bounds also holds the centroid/centerpoint, and orientation
    private Bounds bounds;

    // flag checking if loadModel has been called
    private boolean loaded;

    private boolean isStatic;

    public GameObject(){
        loaded = false;
        modelMatrix = new float[16];
        this.isStatic = false;
    }

    public void loadModel(Context context, String objectFile, int color, int texture) {
        if(loaded){
            return;
        }
        vboi = (ModelVBOI) new ModelVBOI
                .Builder(context, objectFile)
                .color(color)
                .texture(texture)
                .build();
        loaded = true;
    }

    public Bounds getBounds(){return this.bounds;}

    public void setBounds(Bounds bounds){
        this.bounds = bounds;
    }

    /** @return the model matrix for the object **/
    public float[] getModelMatrix() {
        return modelMatrix;
    }
    public void setModelMatrix(float[] modelMatrix){this.modelMatrix = modelMatrix;}
    /**@return  the object holding render data **/
    public ModelVBOI getVboi() {
        return vboi;
    }
    /** @return the opengl buffer index **/
    public int getInterleavedBuffIndx() {
        return getVboi().getIntlvdBufIndx();
    }
    /** @return the triangle count for the object **/
    public int getTrisCount() {
        return getVboi().getTrisCount();
    }

    /** @return rotation around y axis**/
    public Quaternion getOrientation(){
        return this.bounds.getOrientation();
    }
    /** @return centerpoint in 3d **/
    public Vec3f get3dCenter(){
        if(center3d == null){
            throw new NullPointerException("center3d is null");
        }
        return center3d;
    }
    /** @return the centerpoint in 2d **/
    public Vec2f get2dCenter(){
        return center2d;
    }

    public Vec3f getVelocity3d(){return this.bounds.getVelocity3d();}
    public Vec2f getVelocity2d(){return this.bounds.getVelocity2d();}

    public void setVelocity(Vec3f velocity){
        this.bounds.setVelocity(velocity);
    }
    public void setVelocity(Vec2f velocity){
        this.bounds.setVelocity(velocity);
    }
    public void setCenter(float x, float z){
        setCenter(x,center3d.y(),z);
    }

    public void setCenter(float x, float y,float z){
        center2d = new Vec2f(x,z);
        center3d = new Vec3f(x,y,z);
        float[] center = getVboi().getModelCenter();
        this.bounds.setCenter(x-center[0],y-center[1],z-center[2]);
        resetModelMatrix();
    }

    public void setCenter(Vec3f center){
        setCenter(center.x(),center.y(),center.z());
    }

    public void setCenter(Vec2f center){
        setCenter(center.x(),center.y());
    }

    public void translate(float x, float y, float z){
        Vec3f translation = new Vec3f(x,y,z);
        setCenter(get3dCenter().add(translation));
        resetModelMatrix();
    }

    public void translate(float x, float z){
        Vec2f translation = new Vec2f(x,z);
        setCenter(get2dCenter().add(translation));
        resetModelMatrix();
    }

    public void translate(Vec3f translation){
        setCenter(get3dCenter().add(translation));
        resetModelMatrix();
    }

    public void translate(Vec2f translation){
        setCenter(get2dCenter().add(translation));
        resetModelMatrix();
    }

    public void setOrientation(Quaternion orientation){
        this.bounds.setOrientation(orientation);
        resetModelMatrix();
    }
    public void setOrientation(float x, float y, float z, float angleInRadians) {
        this.bounds.setOrientation(x,y,z,angleInRadians);
        resetModelMatrix();
    }

    public void rotate(Quaternion quaternion) {
        this.bounds.rotate(quaternion);
        resetModelMatrix();
    }

    public void rotate(float x, float y, float z, float angleInRadians) {
        this.rotate(x,y,z,angleInRadians);
        resetModelMatrix();
    }

    /** used to keep the model matrix updated when orientation/translation changes
     *
     * flips y and z for opengl**/
    protected void resetModelMatrix(){
        Matrix.setIdentityM(modelMatrix,0);
        float[] modelMatrixCopy = new float[modelMatrix.length];
        System.arraycopy(modelMatrix,0,modelMatrixCopy,0,modelMatrix.length);
        Vec3f center = get3dCenter();
        // fudging in the event that the model is not centered at 0,0,0 in the obj file
        // so that the collision bounds matches the rendered object
        Matrix.translateM(modelMatrixCopy,0,center.x(),center.y(),center.z());
        float[] rotationMatrix = getOrientation().unit().toRotationMatrixCM();
        Matrix.multiplyMM(modelMatrix,0,modelMatrixCopy,0,rotationMatrix,0);

        //    Quaternion aa = orientation.toAxisAngle();
        //    float degs = (float)Math.toDegrees(aa.w());
        //    Matrix.rotateM(modelMatrix,0,modelMatrix,0,degs,aa.x(),aa.y(),aa.z());

     //   v  2.000000 -2.000000 -0.250000
     //   v  2.000000 -2.000000  0.250000
     //   v -2.000000 -2.000000  0.250000
    //    v -2.000000 -2.000000 -0.250000
    //    v  2.000000  2.000000 -0.250000
    //    v  2.000000  2.000000  0.250000
    //    v -2.000000  2.000000  0.250000
    //    v -2.000000  2.000000 -0.250000

      /*  if(getBounds().getBoundsType().ordinal()==BoundsType.RECTANGLE.ordinal()){
            Log.d("resetModelMatrix","Printing bounds verts");
            Rectangle r = (Rectangle)getBounds();
            System.out.println(r.get3dCenter()+" VS " + get3dCenter());
            System.out.println(r.get2dCenter()+" VS " + get2dCenter());

            Vec2f[] verts = r.getVerts();
            for(Vec2f v : verts){
              //  Log.d("resetModelMatrix",v.sub(r.get2dCenter()).toString());
                Log.d("resetModelMatrix",v.toString());
            }

            Log.e("resetModelMatrix","Printing obj rectangle verts");
            float x=100f;
            float x2=2f;
            float y=2f;
            float z=0.5f;
            float[][] verts2 = new float[][]{
                    new float[]{x,-y,-z,1},
                    new float[]{x,-y,z,1},
                    new float[]{-x2,-y,z,1},
                    new float[]{-x2,-y,-z,1},
                    new float[]{x,x2,-z,1},
                    new float[]{x,x2,z,1},
                    new float[]{-x2,y,z,1},
                    new float[]{-x2,y,-z,1},
            };
            for(float[] f : verts2){
                float[] fNew = new float[]{0,0,0,0};
                Matrix.multiplyMV(fNew,0,modelMatrix,0,f,0);
              //  fNew[0] -= center.x();
             //   fNew[1] -= center.y();
             //  fNew[2] -= center.z();
                Log.d("resetModelMatrix",fNew[0]+","+fNew[1]+","+fNew[2]+","+fNew[3]);
            }
            Log.d("resetModelMatrix","done");
        }

    */
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }
}