package com.ben.testapp.collision;

/**
 * Created by Ben on 8/18/2017.
 */
class SupportCube extends SupportFunction {
    @Override
    Vec3 supportA(PointCloud p, Vec3 direction) {
        return supportForCube(p,direction);
    }

    @Override
    Vec3 supportB(PointCloud p, Vec3 direction) {
        return supportForCube(p,direction);
    }

    private Vec3 supportForCube(PointCloud p, Vec3 dir){
        Vec3 maxVec = null;
        float maxDot = -Float.MAX_VALUE;
        for(Vec3 pnt : p.getPoints()){
            float dot = Vec3.dot(pnt,dir);
            if(dot > maxDot){
                maxDot = dot;
                maxVec = pnt;
            }
        }
         return maxVec;
    }
}
