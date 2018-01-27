package com.ben.testapp.collision;

import com.ben.testapp.collision.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 8/18/2017.
 */
class PointCloud {
    private List<Vec3> points;
    PointCloud(Vec3... vec3s){
        points = new ArrayList<>();
        for(Vec3 v : vec3s){
            points.add(v);
        }
    }
    List<Vec3> getPoints() {
        return points;
    }
}
