package com.ben.game.util;

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

    public static void parse(Context context, String fileName) throws IOException {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        InputStream inputStream = null;
        BufferedReader br = null;
        try {
            fileName = "gameObjects/"+fileName;
            if(fileName.endsWith(".obj")==false){
                fileName += ".obj";
            }
            inputStream = context.getAssets().open(fileName);
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
          //  Log.e("parse", vCount + "," + vtCount + "," + vnCount + "," + fvCount +
           //         "," + ftCount + "," + fnCount);
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
            inputStream = context.getAssets().open(fileName);
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
                    fvIndex += 3;
                    ftIndex += 3;
                    fnIndex += 3;
                }
            }
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
                fv[fvIndex + i - 1] = Short.parseShort(drawOrders[0]);
            // ft
            if (drawOrders.length > 1 && drawOrders[1].isEmpty() == false)
                ft[ftIndex + i - 1] = Short.parseShort(drawOrders[1]);
            // fn
            if (drawOrders.length > 2 && drawOrders[2].isEmpty() == false)
                fn[fnIndex + i - 1] = Short.parseShort(drawOrders[2]);
        }
    }

    static void clear() {
        v = null;
        vt = null;
        vn = null;
        fv = null;
        ft = null;
        fn = null;
    }

    public static float[] getExpandedVertexData() {
        return getExpandedData(v, fv, 3);
    }

    public static float[] getExpandedTextureData() {
        return getExpandedData(vt, ft, 2);
    }

    public static float[] getExpandedNormalData() {
        return getExpandedData(vn, fn, 3);
    }

    private static float[] getExpandedData(float[] dat, short[] indexs, int datSize) {
        float[] expandedData = new float[indexs.length * datSize];
        for (int i = 0; i < indexs.length; i++) {
            // index list for obj files start count at 1
            int indexInDat = (indexs[i]-1)*datSize;
            for (int j = 0; j < datSize; j++) {
                float data = dat[indexInDat+j];
                expandedData[i*datSize+j] = data;
            }
        }
        return expandedData;
    }
}