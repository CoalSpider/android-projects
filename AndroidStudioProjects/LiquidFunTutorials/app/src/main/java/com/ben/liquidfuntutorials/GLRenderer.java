package com.ben.liquidfuntutorials;

import static android.opengl.GLES20.*;
import android.opengl.GLSurfaceView;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.JointDef;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.World;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ben on 11/22/2017.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private MainActivity activity;

    private ShaderProgram shaderProgram;

    GLRenderer(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0,0,0,1);
        shaderProgram = new ShaderProgram(R.raw.point_vertex_shader,R.raw.point_vertex_shader,this.activity);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // empty
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);



        // glDrawArrays(GL_POINTS,0,world.getParticleCount);

    }
}
/* wrapper to manage some missing functionality of the library I found*/
class WorldManager{
    private List<Body> bodyList = new ArrayList<>();
    private List<Joint> jointList = new ArrayList<>();
    private World w;

    public WorldManager(World w){
        this.w = w;
    }

    public Body createBody(BodyDef bDef){
        Body body = w.createBody(bDef);
        bodyList.add(body);
        return body;
    }

    public void destroyBody(Body body){
        bodyList.remove(body);
        w.destroyBody(body);
    }

    public List<Body> getBodyList() {
        return bodyList;
    }

    public Joint createJoint(JointDef jDef){
        Joint joint = w.createJoint(jDef);
        jointList.add(joint);
        return joint;
    }

    public void destroyJoint(Joint joint){
        jointList.remove(joint);
        w.destroyJoint(joint);
    }

    public List<Joint> getJointList() {
        return jointList;
    }
}
class BufferGetter{
    public static final int BYTES_PER_COLOR = 16;
    public static final int BYTES_PER_POSITION = 8;
    static FloatBuffer getParticleColorBuffer(ParticleSystem ps){
        ByteBuffer colorBuffer = ByteBuffer.allocateDirect(ps.getParticleCount()*BYTES_PER_COLOR);
        ps.copyColorBuffer(0,ps.getParticleCount(),colorBuffer);
        return colorBuffer.asFloatBuffer();
    }
    static FloatBuffer getParticlePositionBuffer(ParticleSystem ps){
        ByteBuffer positionBuffer = ByteBuffer.allocateDirect(ps.getParticleCount()*BYTES_PER_POSITION);
        ps.copyPositionBuffer(0,ps.getParticleCount(),positionBuffer);
        return positionBuffer.asFloatBuffer();
    }

    static FloatBuffer getBodyPositionBuffer(World w){
        return null;
    }
}