package com.ben.testapp.collision;

import android.opengl.Matrix;

import com.ben.testapp.model.SceneModel;
import com.ben.testapp.util.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 9/9/2017.
 */

public class Collision {
    public List<CollisionData> getCollisions(List<SceneModel> models){
        List<CollisionData> colList = new ArrayList<>();
        for(int i = 0; i < models.size(); i++){
            SceneModel a = models.get(i);
            for(int j = i+1; j < models.size(); j++){
                SceneModel b = models.get(j);
                // prevent self collision
                if(a.equals(b)){continue;}
                CollisionData dat = collides(a,b);
                if(dat!=null){colList.add(dat);}
            }
        }
        return colList;
    }

    private CollisionData collides(SceneModel a, SceneModel b){
        AxisBox aB = a.getAxisBox();
        AxisBox bB = b.getAxisBox();
        float[] aM = a.getModelMatrix();
        float[] bM = b.getModelMatrix();
        List<Vec3> vertsA = aB.getVerts().getPoints();
        List<Vec3> vertsB = bB.getVerts().getPoints();
        List<float[]> modifiedVA = new ArrayList<>();
        List<float[]> modifiedVB = new ArrayList<>();
        for(Vec3 v : vertsA){
            float[] result = new float[4];
            Matrix.multiplyMV(result,0,aM,0,new float[]{v.x(),v.y(),v.z(),1.0f},0);
            modifiedVA.add(new float[] {result[0],result[1],result[2]});
        }
        for(Vec3 v : vertsB){
            float[] result = new float[4];
            Matrix.multiplyMV(result,0,bM,0,new float[]{v.x(),v.y(),v.z(),1.0f},0);
            modifiedVB.add(new float[]{result[0],result[1],result[2]});
        }

        float minXA,minYA,minZA,maxXA,maxYA,maxZA;
        minXA = Float.MAX_VALUE;
        minYA = Float.MAX_VALUE;
        minZA = Float.MAX_VALUE;
        maxXA = -Float.MAX_VALUE;
        maxYA = -Float.MAX_VALUE;
        maxZA = -Float.MAX_VALUE;
        for(float[] f : modifiedVA){
            minXA = (f[0] < minXA) ? f[0] : minXA;
            minYA = (f[1] < minYA) ? f[1] : minYA;
            minZA = (f[2] < minZA) ? f[2] : minZA;
            maxXA = (f[0] > maxXA) ? f[0] : maxXA;
            maxYA = (f[1] > maxYA) ? f[1] : maxYA;
            maxZA = (f[2] > maxZA) ? f[2] : maxZA;
        }
        float minXB,minYB,minZB,maxXB,maxYB,maxZB;
        minXB = Float.MAX_VALUE;
        minYB = Float.MAX_VALUE;
        minZB = Float.MAX_VALUE;
        maxXB = -Float.MAX_VALUE;
        maxYB = -Float.MAX_VALUE;
        maxZB = -Float.MAX_VALUE;
        for(float[] f : modifiedVB){
            minXB = (f[0] < minXB) ? f[0] : minXB;
            minYB = (f[1] < minYB) ? f[1] : minYB;
            minZB = (f[2] < minZB) ? f[2] : minZB;
            maxXB = (f[0] > maxXB) ? f[0] : maxXB;
            maxYB = (f[1] > maxYB) ? f[1] : maxYB;
            maxZB = (f[2] > maxZB) ? f[2] : maxZB;
        }
        CollisionData cd = null;
        // in 2d if x is min y is normal
        // if y is min x is normal

        // assuming y is up
        // in 3d x is min z is normal
        // if z is min x is normal
        // if y is min y is normal
        if(lineOverlap(minXA,minXB,maxXA,maxXB)){
            float penX = calcOverlap(minXA,minXB,maxXA,maxXB);
            // if a center is less than b center normal points to b
            // else normal points to a
            float x = (minXA < minXB) ? -1 :1;
            cd = new CollisionData(a,b,new float[]{x,0,0},penX);
            if(lineOverlap(minYA,minYB,maxYB,maxYB)){
                float penY = calcOverlap(minYA,minYB,maxYB,maxYB);
                if(penY < cd.getPenetration()){
                    float y = (minYA < minYB) ? -1 : 1;
                    cd.setCollisionNormal(new float[]{0,y,0});
                    cd.setPenetration(penY);
                }
                if(lineOverlap(minZA,minZB,maxZA,maxZB)){
                    float penZ = calcOverlap(minYA,minYB,maxYB,maxYB);
                    if(penZ < cd.getPenetration()){
                        float z = (minZA < minZB) ? -1 : 1;
                        cd.setCollisionNormal(new float[]{0,0,z});
                        cd.setPenetration(penZ);
                    }
                }
            }
        }
        return cd;
    }

    private boolean lineOverlap(float minA,float minB,float maxA, float maxB){
        if(minB <= maxA && minB >= minA){
           return true;
        }
        if(minA <= maxB && minA >= maxA){
            return true;
        }
        return false;
    }

    private float calcOverlap(float minA, float minB, float maxA, float maxB){
        if(minA < minB){
            return maxA-minB;
        } else {
            return maxB-minA;
        }
    }
}
