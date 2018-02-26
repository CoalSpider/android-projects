package com.ben.drawcontrolsprototype.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 4/21/2017.
 */

public class ObjParser {
    private static float[] v;
    private static float[] vt;
    private static float[] vn;
    private static short[] fv;
    private static short[] ft;
    private static short[] fn;

    public static void parse(Context context, int id) throws IOException {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        InputStream inputStream = null;
        BufferedReader br = null;
        try {
            inputStream = context.getResources().openRawResource(id);
            br = new BufferedReader(new InputStreamReader(inputStream));
            int vCount = 0;
            int vtCount = 0;
            int vnCount = 0;
            int fvCount = 0;
            int ftCount = 0;
            int fnCount = 0;
            String currLine;
            while ((currLine = br.readLine()) != null) {
                // get first 2 chars
                if (currLine.startsWith("v ")) {
                    vCount++;
                } else if (currLine.startsWith("vt")) {
                    vtCount++;
                } else if (currLine.startsWith("vn")) {
                    vnCount++;
                } else if (currLine.startsWith("f ")) {
                    fvCount++;
                    ftCount++;
                    fnCount++;
                }
            }
            Log.e("parse", vCount + "," + vtCount + "," + vnCount + "," + fvCount +
                    "," + ftCount + "," + fnCount);
            br.close();
            inputStream.close();
            v = new float[vCount * 3];
            vt = new float[vtCount * 2];
            vn = new float[vnCount * 3];
            fv = new short[fvCount * 3];
            ft = new short[ftCount * 3];
            fn = new short[fnCount * 3];
            int vIndex = 0;
            int vtIndex = 0;
            int vnIndex = 0;
            int fvIndex = 0;
            int ftIndex = 0;
            int fnIndex = 0;
            inputStream = context.getResources().openRawResource(id);
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((currLine = br.readLine()) != null) {
                // get first 2 chars
                if (currLine.startsWith("v ")) {
                    addToList(v, vIndex, currLine);
                    vIndex += 3;
                } else if (currLine.startsWith("vt")) {
                    addToList(vt, vtIndex, currLine);
                    vtIndex += 2;
                } else if (currLine.startsWith("vn")) {
                    addToList(vn, vnIndex, currLine);
                    vnIndex += 3;
                } else if (currLine.startsWith("f ")) {
                    addDrawOrder(fvIndex, ftIndex, fnIndex, currLine);
                    fvIndex+=3;
                    ftIndex+=3;
                    fnIndex+=3;
                }
            }
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < fv.length; i++) {
                b.append(fv[i]);
                b.append(",");
            }
            Log.e("objParse", "FV=" + b.toString());
        } finally {
            if (br != null) {
                br.close();
                inputStream.close();
            }
        }
    }

    private static void addToList(float[] list, int index, String line) {
        String[] split = line.split(" ");
        // skip the identifier for the line (v,vt,vn,etc...)
        for (int i = 1; i < split.length; i++) {
            // if theres a w component skip it
            if (list.equals(v) && i == 4) {
                continue;
            }
            list[index] = Float.parseFloat(split[i]);
            index++;
        }
    }

    private static void addDrawOrder(int fvIndex, int ftIndex, int fnIndex, String
            line) {
        String[] split = line.split(" ");
        // skip the identifier for the line (v,vt,vn,etc...)
        for (int i = 1; i < split.length; i++) {
            // line form is v || v/vt || v/vt/vn || v//vn
            String[] drawOrders = split[i].split("/");
            // fv
            if (drawOrders.length > 0 && drawOrders[0].isEmpty() == false)
                fv[fvIndex+i-1] = Short.parseShort(drawOrders[0]);
            // ft
            if (drawOrders.length > 1 && drawOrders[1].isEmpty() == false)
                ft[ftIndex+i-1] = Short.parseShort(drawOrders[1]);
            // fn
            if (drawOrders.length > 2 && drawOrders[2].isEmpty() == false)
                fn[fnIndex+i-1] = Short.parseShort(drawOrders[2]);
        }
    }

    static float[] getInterleavedVertexColorData() {
        // interleave to be in order: x, y, z, r, g, b, a
        float[] vArr = new float[v.length + (v.length / 3 * 4)];
        int iV = 0;
        for (int i = 0; i < vArr.length; i += 7) {
            vArr[i] = v[iV];
            vArr[i + 1] = v[iV + 1];
            vArr[i + 2] = v[iV + 2];
            iV += 3;
            // generate default color: white with no opacity
            vArr[i + 3] = 1;
            vArr[i + 4] = 1;
            vArr[i + 5] = 1;
            vArr[i + 6] = 1;

        }
        return vArr;
    }

   /* static float[] getVertexData() {
        return v;
    }

    static float[] getTextureData() {
        return vt;
    }

    static float[] getNormalData() {
        return vn;
    }

    static short[] getVertexOrder() {
        return fv;
    }

    static short[] getTextureOrder() {
        return ft;
    }

    static short[] getNormalOrder() {
        return fn;
    } */

    static void clear() {
        v = null;
        vt = null;
        vn = null;
        fv = null;
        ft = null;
        fn = null;
    }

    static float[] getExpandedVertexData(){
        return getExpandedData(v,fv,3);
    }
    static float[] getExpandedTextureData(){
        return getExpandedData(vt,ft,2);
    }
    static float[] getExpandedNormalData(){
        return getExpandedData(vn,fn,3);
    }

    private static float[] getExpandedData(float[] dat, short[] indexs, int datSize) {
        float[] expandedData = new float[indexs.length*datSize];
        for(int i = 0, j=0; i < indexs.length; i++,j+=datSize){
            int z = 0;
            while(z < datSize){
                expandedData[j+z] = dat[indexs[i]+z];
                z++;
            }
        }
        return expandedData;
    }
    private static void printArray(String name, float[] arr){
        StringBuilder b = new StringBuilder();
        for(float f : arr){
            b.append(f);
            b.append(",");
        }
        Log.e("printArray",name+":"+b.toString());
    }
    private static void printArray(String name, short[] arr){
        StringBuilder b = new StringBuilder();
        for(short s : arr){
            b.append(s);
            b.append(",");
        }
        Log.e("printArray",name+":"+b.toString());
    }


    static float[] expandAndInterleaveDataForIndexing() {
        printArray("ver",v);
        printArray("tex",vt);
        printArray("nor",vn);
        printArray("fv",fv);
        printArray("ft",ft);
        printArray("fn",fn);
        List<Vertex> verts = new ArrayList<>();
        for (int i = 0; i < fv.length; i++) {
            int vIndex = fv[i];
            int vtIndex = ft[i];
            int vnIndex = fn[i];
            float[] pos = new float[]{v[vIndex], v[vIndex + 1], v[vIndex + 2]};
            float[] tex = new float[]{vt[vtIndex], vt[vtIndex + 1]};
            float[] nor = new float[]{vn[vnIndex], vn[vnIndex + 1], vn[vnIndex + 2]};
            Vertex temp = new Vertex(pos, tex, nor);
            verts.add(temp);
        }
        int duplicateCount = 0;
       for (Vertex v : verts) {
            for (Vertex v2 : verts) {
                if(v2.equals(v)){
                    continue;
                }
                if (v.sameVertexData(v2)) {
                    // counts both a == b and b == a
                    duplicateCount++;
            //        Log.e("objParse", "duplicate");
           //         Log.e("objParse", v.toString());
           //         Log.e("objParse", v2.toString());
                }
            }
        }
        Log.e("objParse", "duplicates = " + duplicateCount / 2);
        Log.e("objParse","expandedVertexCount="+fv.length*3);
        return null;
    }
}

class Vertex {
    private float[] position;
    private float[] texture;
    private float[] normal;

    Vertex(float[] position, float[] texture, float[] normal) {
        this.position = position;
        this.texture = texture;
        this.normal = normal;
    }

    /**
     * @return true of all vertex attribs are equal
     **/
    public boolean sameVertexData(Vertex ver) {
        float x = position[0];
        float y = position[0];
        float z = position[1];
        float u = texture[0];
        float v = texture[1];
        float nx = normal[0];
        float ny = normal[1];
        float nz = normal[2];
        float x2 = ver.getPosition()[0];
        float y2 = ver.getPosition()[0];
        float z2 = ver.getPosition()[1];
        float u2 = ver.getTexture()[0];
        float v2 = ver.getTexture()[1];
        float nx2 = ver.getNormal()[0];
        float ny2 = ver.getNormal()[1];
        float nz2 = ver.getNormal()[2];

        if (x == x2 && y == y2 && z == z2) {
            if (u == u2 && v == v2) {
                if (nx == nx2 && ny == ny2 && nz == nz2) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "P:" + position[0] + "," + position[1] + "," + position[2] +
                " T:" + texture[0] + "," + texture[1] +
                " N:" + normal[0] + "," + normal[1] + "," + normal[2];

    }

    public float[] getPosition() {
        return position;
    }

    public float[] getNormal() {
        return normal;
    }

    public float[] getTexture() {
        return texture;
    }

    public void setNormal(float[] normal) {
        this.normal = normal;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public void setTexture(float[] texture) {
        this.texture = texture;
    }
}