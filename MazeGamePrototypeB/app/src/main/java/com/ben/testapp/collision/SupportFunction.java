package com.ben.testapp.collision;

/**
 * Created by Ben on 8/18/2017.
 */
abstract class SupportFunction {
    public SupportPoint support(PointCloud a, PointCloud b, Vec3 direction) {
        Vec3 supportA = supportA(a, direction);
        Vec3 supportB = supportB(b, Vec3.negate(direction));
        Vec3 minkoskiSupport = Vec3.sub(supportA,supportB);
        return new SupportPoint(supportA,supportB,minkoskiSupport);
    }

    abstract Vec3 supportA(PointCloud p, Vec3 direction);

    abstract Vec3 supportB(PointCloud p, Vec3 direction);
}
