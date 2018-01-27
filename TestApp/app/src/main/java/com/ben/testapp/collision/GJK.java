package com.ben.testapp.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Gilbert-Jonson-Kenethri **/
class GJK {
    private List<SupportPoint> simplex = new ArrayList<>();
    private SupportFunction support;
    private PointCloud a;
    private PointCloud b;

    GJK(PointCloud a, PointCloud b){
        this.a = a;
        this.b = b;
        this.support = new SupportCube();
    }

    boolean intersect() {
        SupportPoint S = support(new Vec3(1, 0, 0));
        setSimplex(S);
        Vec3 D = Vec3.negate(S.v());

        while (true) {
            SupportPoint A = support(D);

            // < 0 creates infinite loops which could be handled as a true
            // for touching
            if (Vec3.dot(A.v(), D) <= 0) {
                // treat touching contact (no pentration) as false
                return false;
            }

            simplex.add(A);

            if (doSimplex(D)) {
                return true;
            }
        }
    }

    private SupportPoint support(Vec3 dir) {
        return support.support(a, b, dir);
    }

    private void setSimplex(SupportPoint... points) {
        simplex.clear();
        simplex.addAll(Arrays.asList(points));
    }

    private boolean doSimplex(Vec3 direction) {
        switch (simplex.size()) {
            case 0:
            case 1:
                throw new RuntimeException(
                        "simplex size < 2: size==" + simplex.size());
            case 2:
                return doSimplex2(direction);
            case 3:
                return doSimplex3(direction);
            case 4:
                return doSimplex4(direction);
            default:
                throw new RuntimeException(
                        "simplex size > 4: size==" + simplex.size());
        }
    }


    /** Simplex is a line **/
    private boolean doSimplex2(Vec3 direction) {
        SupportPoint A = simplex.get(1);
        SupportPoint B = simplex.get(0);
        Vec3 AO = Vec3.negate(A.v());
        Vec3 AB = Vec3.sub(B.v(), A.v());

        if (Vec3.dot(AB, AO) > 0) {
            setSimplex(B, A);
            Vec3 newDir = Vec3.tripleProduct(AB, AO, AB);
            if(Vec3.lenSqrd(newDir)<1e-9){
                // line passes through origin, return true;
                /** TODO properly handle this case at some point
                 * TODO: dont complete tris pass to PolytopeExpander as 2 simplex**/
                return true;
            } else {
                direction.set(newDir.x(), newDir.y(), newDir.z());
            }
        } else {
            setSimplex(A);
            direction.set(AO.x(), AO.y(), AO.z());
        }

        return false;
    }

    /** Simplex is a triangle **/
    private boolean doSimplex3(Vec3 direction) {
        SupportPoint A = simplex.get(2);
        SupportPoint B = simplex.get(1);
        SupportPoint C = simplex.get(0);
        // to origin
        Vec3 AO = Vec3.negate(A.v());
        // edges
        Vec3 AB = Vec3.sub(B.v(), A.v());
        Vec3 AC = Vec3.sub(C.v(), A.v());
        // face normal
        Vec3 ABC = Vec3.cross(AB, AC);
        // edge normals
        Vec3 ABNorm = Vec3.cross(AB, ABC);
        Vec3 ACNorm = Vec3.cross(ABC, AC);
        // flip normals that dont face outward
        // not currently called
        //if (dot(ABNorm, AC) > 0) {ABNorm = cross(AB,ABC);System.err.println("called ABNormFlip");}
        //if (dot(ACNorm, AB) > 0) {ACNorm = cross(ABC, AC);System.err.println("called ACNormFlip");}

        // in front of AC
        if (Vec3.dot(ABNorm, AO) > 0) {
            setSimplex(B, A);
            return doSimplex2(ABNorm);
        } else {
            // in front of AC
            if (Vec3.dot(ACNorm, AO) > 0) {
                setSimplex(C, A);
                return doSimplex2(ACNorm);
            } else {
                // are we above or below the triangle
                setSimplex(C,B,A);
                if(Vec3.dot(ABC,AO) >= 0){
                    direction.set(ABC.x(), ABC.y(), ABC.z());
                } else {
                    direction.set(-ABC.x(), -ABC.y(), -ABC.z());
                }
            }
        }
        return false;
    }

    /** Simplex is a tetrahedron **/
    private boolean doSimplex4(Vec3 direction) {
        SupportPoint A = simplex.get(3);
        SupportPoint B = simplex.get(2);
        SupportPoint C = simplex.get(1);
        SupportPoint D = simplex.get(0);

        Vec3 AO = Vec3.negate(A.v());
        Vec3 AB = Vec3.sub(B.v(), A.v());
        Vec3 AC = Vec3.sub(C.v(), A.v());
        Vec3 AD = Vec3.sub(D.v(), A.v());

        Vec3 ABC = Vec3.cross(AB, AC);
        Vec3 ACD = Vec3.cross(AC, AD);
        Vec3 ABD = Vec3.cross(AD, AB);

        // no currently called
        // if(dot(ABC, AD) > 0) {ABC = cross(AC, AB);System.err.println("calledABCAD");}
        // if(dot(ACD, AB) > 0) {ABC = cross(AD, AC);System.err.println("calledACDAB");}
        // if(dot(ABD, AC) > 0) {ABD = cross(AB, AD);System.err.println("calledABDAC");}

        // behind ABC
        if (Vec3.dot(ABC, AO) < 0) {
            // behind ACD
            if (Vec3.dot(ACD, AO) < 0) {
                // behind ABD
                if (Vec3.dot(ABD, AO) < 0) {
                    return true;
                } else {
                    // in front of ABD remove C
                    setSimplex(D, B, A);
                    return doSimplex3(direction);
                }
            } else {
                // in front of ACD remove B
                setSimplex(D, C, A);
                return doSimplex3(direction);
            }
        } else {
            // in front of ABC remove D
            setSimplex(C, B, A);
            return doSimplex3(direction);
        }
    }

    List<SupportPoint> getSimplex() {
        return simplex;
    }

    SupportFunction getSupport() {
        return support;
    }

    PointCloud getA() {
        return a;
    }

    PointCloud getB() {
        return b;
    }
}
