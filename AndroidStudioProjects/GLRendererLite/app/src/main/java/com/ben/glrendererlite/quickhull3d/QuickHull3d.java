package com.ben.glrendererlite.quickhull3d;

import com.ben.glrendererlite.Edge;
import com.ben.glrendererlite.Triangle;
import com.ben.glrendererlite.util.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.ben.glrendererlite.util.Vec3.*;

/**
 * Ben L. Norman on 8/22/2017.
 *
 * Implementation based on :
 *
 * pdf's
 * http://box2d.org/files/GDC2014/DirkGregorius_ImplementingQuickHull.pdf
 * http://dpd.cs.princeton.edu/Papers/BarberDobkinHuhdanpaa.pdf
 *
 * And code by John E. Lloyd
 * https://www.cs.ubc.ca/~lloyd/java/quickhull3d.html
 * and
 * http://thomasdiewald.com/blog/?p=1888
 *
 */

public class QuickHull3d {
    // d == dimension
    //
    // create simplex of d+1 points
    //  for each facet F
    //      for each unassigned point p
    //          if p is above F
    //              assign p to F's outside set
    //  for each facet F with a non-empty outside set
    //      selectFarthest point p of F's outside set
    //      init visible set V to F
    //      for all unvisited neighbors N of facets in V
    //      if p is above N
    //          add N to V
    //      the set of horizon ridges H is the boundary of V
    //      for each ride R in H
    //          create a new facet from R and p
    //          link the new facet to its neighbors
    //      for each new facet F'
    //          for each unassigned point q in an outside set of a facet in V
    //              if q is above F'
    //                  assign q to F''s outside set
    //      delete the facets in V

    private List<Vec3> points;
    private List<Vec3> selectedPoints;
    private List<Triangle> triangles;

    public List<Vec3> getPoints() {
        return points;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public List<Vec3> getSelectedPoints() {
        return selectedPoints;
    }

    private int simulationStep = 0;
    private List<Vec3> pointsFromRemovedTriangles = new ArrayList<>();
    private List<Edge> edgesFromRemovedTriangles = new ArrayList<>();
    public void runNextSimulationStep(){
        if(simulationStep==0){
            runNextSimulationStepA();
            simulationStep = 1;
        } else if(simulationStep==1){
            runNextSimulationStepB(selectedPoints.get(selectedPoints.size()-1));
            simulationStep = 0;
        }
    }
    public void runNextSimulationStepA(){
        System.out.println("sim step = " + simulationStep);
        Vec3 nextPoint = getMaxFromCentroid(triangles);
        if(nextPoint == null){
            System.err.println("no valid point");
            return;
        }

        addToSelected(nextPoint);

        pointsFromRemovedTriangles = new ArrayList<>();
        edgesFromRemovedTriangles = new ArrayList<>();

        Iterator<Triangle> tIter = triangles.iterator();
        while(tIter.hasNext()){
            Triangle t = tIter.next();
            /** TODO: if no triangle contains then check with dot product **/
            if(t.getAssignedPoints().contains(nextPoint)){
                Edge ab = new Edge(t.a(),t.b());
                Edge bc = new Edge(t.b(),t.c());
                Edge ca = new Edge(t.c(),t.a());

                addEdgeTo(edgesFromRemovedTriangles,ab);
                addEdgeTo(edgesFromRemovedTriangles,bc);
                addEdgeTo(edgesFromRemovedTriangles,ca);

                tIter.remove();
                for(Vec3 p : t.getAssignedPoints()){
                    if(pointsFromRemovedTriangles.contains(p)==false){
                        pointsFromRemovedTriangles.add(p);
                    }
                }
            }
        }
    }

    private void runNextSimulationStepB(Vec3 nextPoint){
        for(Edge e : edgesFromRemovedTriangles){
            Vec3 start = e.getStart();
            Vec3 end = e.getEnd();
            Triangle t = new Triangle(start,end,nextPoint);
            triangles.add(t);
        }

        fixTriangleWinding();

        assignPointsToFacets(pointsFromRemovedTriangles,triangles);
    }

    public void setup(){
        points = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        triangles = new ArrayList<>();

        //generateSphericalCloud();
        randomCloud();

        generateStartEdge();
        generateStartTriangle();
        generateStartTetrahedron();

        assignPointsToFacets(points,triangles);
    }

    private void addToSelected(Vec3... pnts){
        for(Vec3 p : pnts){
            p.setSelected(true);
            selectedPoints.add(p);
        }
    }

    public void run(){
        points = new ArrayList<>();
        selectedPoints = new ArrayList<>();
        triangles = new ArrayList<>();

        generateSphericalCloud();
        //generateCubicCloud();
        //randomCloud();

        generateStartEdge();
        generateStartTriangle();
        generateStartTetrahedron();

        assignPointsToFacets(points,triangles);

        for(int i = 0; i < 2; i++){
            System.out.println("gen " + i);
            generateNextPoint();
        }
    }

    private void generateSphericalCloud(){
        for(int i = -180; i < 180; i+=60){
            for(int j = -180; j < 180; j+=60){
                for(int k = -180; k < 180; k+=60){
                    float r1 = (float)Math.toRadians(i);
                    float r2 = (float)Math.toRadians(j);
                    float r3 = (float)Math.toRadians(k);
                    Vec3 v = Vec3.normalize(new Vec3(r1,r2,r3));
                    points.add(v);
                }
            }
        }
    }

    private void generateCubicCloud(){
        for(float i = -0.5f; i < 0.5f; i+=0.1f){
            for(float j = -0.5f; j < 0.5f; j+= 0.1f){
                for(float k = -0.5f; k < 0.5f; k+=0.1f){
                    points.add(new Vec3(i,j,k));
                }
            }
        }
    }

    private void randomCloud(){
        for(int i = 0; i < 1000; i++){
            float rx = (float)Math.random()-0.5f;
            float ry = (float)Math.random()-0.5f;
            float rz = (float)Math.random()-0.5f;
            points.add(new Vec3(rx,ry,rz));
        }
      /*  for(int i = 0; i < 100; i++){
            float rx = (float)Math.random()-0.5f;
            float ry = (float)Math.random()-0.5f;
            float rz = (float)Math.random()-0.5f;
            points.add(normalize(new Vec3(rx,ry,rz)));
        } */
    }

    private void generateStartEdge(){
        Vec3[] pnts = getExtremePoints(points);
        Vec3 p1 = pnts[0];
        Vec3 p2 = pnts[1];
        addToSelected(p1,p2);
    }

    private void generateStartTriangle(){
        Vec3 sel1 = selectedPoints.get(0);
        Vec3 sel2 = selectedPoints.get(1);
        Vec3 pnt = getFarthestPointFromLine(points,new Edge(sel1,sel2));
        addToSelected(pnt);
        triangles.add(new Triangle(sel1,sel2,pnt));
    }

    private void generateStartTetrahedron(){
        Triangle t = triangles.get(0);

        Vec3 pnt = getFarthestPointAlongTrisNormal(points,t);
        addToSelected(pnt);

        Vec3 A = t.a();
        Vec3 B = t.b();
        Vec3 C = t.c();

        triangles.add(new Triangle(pnt,A,B));
        triangles.add(new Triangle(pnt,A,C));
        triangles.add(new Triangle(pnt,B,C));

        fixTriangleWinding();
    }

    private void fixTriangleWinding(){
        Vec3 centroid = computeCentroid(triangles);

        for(Triangle t : triangles){
            t.lookAwayFrom(centroid);
        }
    }

    private void generateNextPoint(){
        Vec3 nextPoint = getMaxFromCentroid(triangles);
        if(nextPoint == null){
            System.err.println("no valid point");
            return;
        }

        addToSelected(nextPoint);

        List<Vec3> pointsFromRemovedTriangles = new ArrayList<>();
        List<Edge> edgesFromRemovedTriangles = new ArrayList<>();

        Iterator<Triangle> tIter = triangles.iterator();
        while(tIter.hasNext()){
            Triangle t = tIter.next();
            /** TODO: if no triangle contains then check with dot product **/
            if(t.getAssignedPoints().contains(nextPoint)){
                Edge ab = new Edge(t.a(),t.b());
                Edge bc = new Edge(t.b(),t.c());
                Edge ca = new Edge(t.c(),t.a());

                addEdgeTo(edgesFromRemovedTriangles,ab);
                addEdgeTo(edgesFromRemovedTriangles,bc);
                addEdgeTo(edgesFromRemovedTriangles,ca);

                tIter.remove();
                for(Vec3 p : t.getAssignedPoints()){
                    if(pointsFromRemovedTriangles.contains(p)==false){
                       pointsFromRemovedTriangles.add(p);
                    }
                }
            }
        }

        for(Edge e : edgesFromRemovedTriangles){
            Vec3 start = e.getStart();
            Vec3 end = e.getEnd();
            Triangle t = new Triangle(start,end,nextPoint);
            triangles.add(t);
        }

        fixTriangleWinding();

        assignPointsToFacets(pointsFromRemovedTriangles,triangles);
    }

    private void addEdgeTo(List<Edge> list, Edge edge){
        boolean contains = false;
        Edge needsRemoving = null;
        for(Edge e : list) {
            if (edgeOppositEdge(e, edge) || edgeEqualsEdge(e,edge)) {
                contains = true;
                needsRemoving = e;
                break;
            }
        }
        if(needsRemoving!=null){
            list.remove(needsRemoving);
        }
        if(contains == false){
            list.add(edge);
        }
    }
    private boolean edgeEqualsEdge(Edge a, Edge b){
        return
                vecEqualsVec(a.getStart(),b.getStart()) &&
                vecEqualsVec(a.getEnd(),b.getEnd());
    }
    private boolean edgeOppositEdge(Edge a, Edge b){
        return
                vecEqualsVec(a.getStart(),b.getEnd()) &&
                vecEqualsVec(a.getEnd(),b.getStart());
    }

    private boolean vecEqualsVec(Vec3 a, Vec3 b){
        // dealing with floating point error
        boolean xEql = Math.abs(a.x()-b.x()) < 1e-9f;
        boolean yEql = Math.abs(a.y()-b.y()) < 1e-9f;
        boolean zEql = Math.abs(a.z()-b.z()) < 1e-9f;
        return xEql && yEql && zEql;
    }

    public Vec3[] getExtremePoints(List<Vec3> inputSet){
        /** TODO: get most extreme pair on each axis instead of just diag **/
        Vec3 dir = new Vec3(1,1,1);
        Vec3 negDir = negate(dir);
        Vec3 p1 = getFarthestPointInDir(inputSet,dir);
        Vec3 p2 = getFarthestPointInDir(inputSet,negDir);
        return new Vec3[]{p1,p2};
    }

    public Vec3 getFarthestPointFromLine(List<Vec3> inputSet, Edge l){
        return getFarthestPointInDir(inputSet,l.getNormal());
    }

    public Vec3 getFarthestPointAlongTrisNormal(List<Vec3> inputSet, Triangle t){
        /** TODO: return max(farthestPosNormal, farthestNegNormal)**/
        return getFarthestPointInDir(inputSet,t.getNormal());
    }

    private Vec3 getFarthestPointInDir(List<Vec3> inputSet, Vec3 direction){
        Vec3 farthest = null;
        float maxDot = -Float.MAX_VALUE;
        for(Vec3 v : inputSet){
            float dot = dot(v,direction);
            if(dot > maxDot){
                maxDot = dot;
                farthest = v;
            }
        }
        return farthest;
    }

    public Vec3 computeCentroid(List<Triangle> triangles){
        float totalX = 0;
        float totalY = 0;
        float totalZ = 0;
        for(Triangle t : triangles){
            Vec3 centroid = t.getCentroid();
            totalX += centroid.x();
            totalY += centroid.y();
            totalZ += centroid.z();
        }
        float vertexCount = triangles.size();
        totalX /= vertexCount;
        totalY /= vertexCount;
        totalZ /= vertexCount;
        return new Vec3(totalX,totalY,totalZ);
    }

    public void assignPointsToFacets(List<Vec3> inputSet, List<Triangle> triangles){
        System.out.println("inputset size = " + inputSet.size() + ", trisSize = " + triangles.size());
        for(Vec3 v : inputSet){
            for(int i = 0; i < triangles.size(); i++){
                Triangle t = triangles.get(i);
                Vec3 cToV = sub(v,t.getCentroid());
                float dot = dot(cToV,t.getNormal());
                if(dot >= 0){
                    t.assignPoint(v);
                }
            }
        }
    }

    public Vec3 getMaxFromCentroid(List<Triangle> triangles){
        Vec3 centroid = computeCentroid(triangles);
        float maxDist = -Float.MAX_VALUE;
        Vec3 maxVec = null;
        for(Triangle t : triangles) {
            Vec3 v = t.getFarthestAssignedPoint();
            if(v != null) {
                float dist = lenSqrd(sub(v, centroid));
                //float dist = Math.abs(dot(sub(v,t.getCentroid()),t.getNormal()));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxVec = v;
                }
            }
        }
        return maxVec;
    }
}