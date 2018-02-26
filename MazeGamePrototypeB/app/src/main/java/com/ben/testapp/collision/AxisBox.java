package com.ben.testapp.collision;

/**
 * Created by Ben on 9/9/2017.
 */

public class AxisBox {
    private Vec3 center;
    private float extentX;
    private float extentY;
    private float extentZ;
    private PointCloud verts;

    private AxisBox(Vec3 center, float extentX, float extentY, float extentZ){
        this.center = center;
        this.extentX = extentX;
        this.extentY = extentY;
        this.extentZ = extentZ;
        calculateVerts();
    }

    public static AxisBox fromVertexData(float[] vertexData){
        float minX,minY,minZ,maxX,maxY,maxZ;
        minX=Float.MAX_VALUE;
        minY=Float.MAX_VALUE;
        minZ=Float.MAX_VALUE;
        maxX=-Float.MAX_VALUE;
        maxY=-Float.MAX_VALUE;
        maxZ=-Float.MAX_VALUE;
        for(int i = 0; i < vertexData.length-2; i+=3){
            float x = vertexData[i];
            float y = vertexData[i+1];
            float z = vertexData[i+2];
            minX = (x < minX) ? x : minX;
            minY = (y < minY) ? y : minY;
            minZ = (z < minZ) ? z : minZ;
            maxX = (x > maxX) ? x : maxX;
            maxY = (y > maxY) ? y : maxY;
            maxZ = (z > maxZ) ? z : maxZ;
        }
        float extentX = (maxX-minX)/2f;
        float extentY = (maxX-minX)/2f;
        float extentZ = (maxZ-minZ)/2f;
        Vec3 center = new Vec3(minX+extentX,minY+extentY,minZ+extentZ);
        return new AxisBox(center,extentX,extentY,extentZ);
    }

    /** point cloud no specific ordering **/
    private void calculateVerts(){
        Vec3 v1 = new Vec3(center.x()-extentX,center.y()-extentY,center.z()-extentZ);
        Vec3 v2 = new Vec3(center.x()+extentX,center.y()-extentY,center.z()-extentZ);
        Vec3 v3 = new Vec3(center.x()-extentX,center.y()+extentY,center.z()-extentZ);
        Vec3 v4 = new Vec3(center.x()-extentX,center.y()-extentY,center.z()+extentZ);
        Vec3 v5 = new Vec3(center.x()+extentX,center.y()+extentY,center.z()-extentZ);
        Vec3 v6 = new Vec3(center.x()-extentX,center.y()+extentY,center.z()+extentZ);
        Vec3 v7 = new Vec3(center.x()+extentX,center.y()-extentY,center.z()+extentZ);
        Vec3 v8 = new Vec3(center.x()+extentX,center.y()+extentY,center.z()+extentZ);
        verts = new PointCloud(v1,v2,v3,v4,v5,v6,v7,v8);
    }

    public PointCloud getVerts() {
        return verts;
    }
}
