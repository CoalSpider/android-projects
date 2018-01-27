package com.ben.glrendererlite;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.ben.glrendererlite.quickhull3d.QuickHull3d;
import com.ben.glrendererlite.render.EdgeRender;
import com.ben.glrendererlite.render.PointRender;
import com.ben.glrendererlite.render.Renderer;
import com.ben.glrendererlite.render.TriangleRender;
import com.ben.glrendererlite.util.Camera;
import com.ben.glrendererlite.util.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ben on 8/21/2017.
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private MainActivity activity;
    private Camera camera;

    private PointRender pRender;
    private EdgeRender eRender;
    private EdgeRender normalRender;
    private TriangleRender tRender;

    private List<Renderer> renderers;

    private QuickHull3d hull3d;

    private List<Vec3> points;
    private List<Vec3> selectedPoints;
    private List<Triangle> triangles;

    GLRenderer(MainActivity mainActivity) {
        // init cloud with a bunch of random values
        this.activity = mainActivity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        points = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        triangles = new ArrayList<>();
        GLES20.glClearColor(0.25f, 0.25f, 0.25f, 0.25f);
        camera = new Camera();

        pRender = new PointRender(activity);
        eRender = new EdgeRender(activity,Colors.WHITE.getRgba());
        tRender = new TriangleRender(activity);
        normalRender = new EdgeRender(activity,Colors.GREEN.getRgba());


        renderers = new ArrayList<>();
        renderers.add(normalRender);
        renderers.add(eRender);
        renderers.add(pRender);
        renderers.add(tRender);

        hull3d = new QuickHull3d();

        /*hull3d.run();
        triangles = hull3d.getTriangles();
        points = hull3d.getPoints();
        selectedPoints = hull3d.getSelectedPoints();
        resetDrawing(); */
        hull3d.setup();
        triangles = hull3d.getTriangles();
        points = hull3d.getPoints();
        selectedPoints = hull3d.getSelectedPoints();
        resetDrawing();
    }

    boolean stepSim = false;
    public void stepSim(){
        stepSim = true;
    }

    private void resetDrawing(){
        pRender.getData().clear();
        for(Vec3 p : points){
            pRender.addData(p);
        }

        tRender.getData().clear();
        for(Triangle t : triangles){
            tRender.addData(t);
        }

        eRender.getData().clear();
        normalRender.getData().clear();
        for(Triangle t : tRender.getData()){
            eRender.addData(new Edge(t.a(),t.b()));
            eRender.addData(new Edge(t.b(),t.c()));
            eRender.addData(new Edge(t.c(),t.a()));

            addNormalLine(t);
        }

        for(Triangle t : tRender.getData()){
            for(Vec3 v : t.getAssignedPoints()){
                eRender.addData(new Edge(v,t.getCentroid()));
            }
        }
    }

    private void addNormalLine(Triangle t){
        Vec3 v1 = t.getCentroid();
        Vec3 v2 = Vec3.scaleMult(t.getNormal(),0.3f);
        Vec3 v3 = Vec3.add(v1,v2);
        normalRender.addData(new Edge(v1,v3));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        camera.setWidthHeight(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(stepSim){
            hull3d.runNextSimulationStep();
            triangles = hull3d.getTriangles();
            points = hull3d.getPoints();
            selectedPoints = hull3d.getSelectedPoints();
            resetDrawing();
            stepSim = false;
        }
        long elapsedMilliSec = SystemClock.elapsedRealtime();
        long rotationCounter = elapsedMilliSec % 10000L;

        float rotationDegs = (360.0f / 10000.0f) * ((int)rotationCounter);
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.translateM(modelMatrix,0,0,0,-0.5f);
        Matrix.rotateM(modelMatrix,0,rotationDegs,0,1,0);

        for(Renderer r : renderers){
            r.setMvpMatrix(camera.getMVP(modelMatrix));
        }

        // enable face culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // set winding to counter clock wise
        GLES20.glFrontFace(GLES20.GL_CCW);
        // cull the clockwise (back) face
        GLES20.glCullFace(GLES20.GL_BACK);
        // enable depth test otherwise things look weird
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        for(Renderer r : renderers){
            if(r instanceof TriangleRender){
                continue;
            }
            r.render();
        }

        // triangles are transparent so render them last
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
        tRender.render();
    }
}

enum Colors{
    RED(1,0,0),GREEN(0,1,0),BLUE(0,0,1),WHITE(1,1,1), YELLOW(1,1,0);
    private final float[] rgba;
    Colors(float r, float g, float b){
        rgba = new float[]{r,g,b,1};
    }
    Colors(float r, float g, float b, float a){
        rgba = new float[]{r,g,b,a};
    }

    public float[] getRgba() {
        return rgba;
    }
}