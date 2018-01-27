package com.ben.testapp.model;

/**
 * Created by Ben on 7/26/2017.
 */

import android.content.Context;
import android.util.Log;

import com.ben.testapp.util.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** The goal of this class is to load models in a way that prevents duplicated data
 * ie: 2 of the same cubes share buffers
 * or if one cube is a different color then only that buffer is different
 *
 * This class intended to load obj models that do not have any animation data
 * such as a skeletal system
 *
 * Warning: the float[] color is converted to a Hex string to differentiate color
 * variants in the loader**/
class VAOLoader {
    private static final String TAG = "VAOLoader";
    private static Map<String,VAO> objModelVAOPairs = new HashMap<>();

    static VAO loadVAO(Context context, String objName, float[] color, Texture texture){
        String genName = generateNameForMap(objName,color);
        if(isAlreadyLoaded(genName)){
            Log.e(TAG,"creating duplicate");
            return objModelVAOPairs.get(genName);
        }
        VAO modelWithSameObj = getFirstInstanceOfName(objName);
        if(modelWithSameObj != null){
            Log.e(TAG,"creating color variant");
            VAO colorVariant = new VAO(modelWithSameObj,color, texture);
            addVAOToMap(genName,colorVariant);
            return colorVariant;
        }
        Log.e(TAG,"creating new instance");
        VAO newInstance = new VAO(context,objName,color,texture);
        addVAOToMap(genName,newInstance);
        return newInstance;
    }

    private static VAO getFirstInstanceOfName(String objName){
        Set<String> keys = objModelVAOPairs.keySet();
        for(String s : keys){
            if(s.contains(objName)){
                return objModelVAOPairs.get(s);
            }
        }
        return null;
    }

    private static void addVAOToMap(String name, VAO vao){
        objModelVAOPairs.put(name,vao);
    }

    private static boolean isAlreadyLoaded(String name){
        return objModelVAOPairs.containsKey(name);
    }

    /** FORMAT == objFile + "#" + rgbaAsEightBitHexString **/
    private static String generateNameForMap(String objFile, float[] color){
        if(color == null){
            return objFile+"#"+toEightBitHex(new float[]{0,0,0,0});
        }
        String name = objFile+"#"+toEightBitHex(color);
        return name;
    }

    /** last two numbers range from 00 (fully transparent) to ff (fully opaque)**/
    private static String toEightBitHex(float[] rgba){
        return toEightBitHex(rgba[0],rgba[1],rgba[2],rgba[3]);
    }
    /** last two numbers range from 00 (fully transparent) to ff (fully opaque)**/
    private static String toEightBitHex(float r, float g, float b, float a){
        int rgbInt = floatRGBToInt(r,g,b);
        String rgbString = Integer.toHexString(rgbInt);
        int alphaInt = (int)(a*255)&0xFF;
        String alphaString = Integer.toHexString(alphaInt);
        return rgbString + alphaString;
    }

    private static int floatRGBToInt(float r, float g, float b){
        int intR = (int)(r*255) & 0xFF;
        int intG = (int)(g*255) & 0xFF;
        int intB = (int)(b*255) & 0xFF;
        // + can be used instead of bit wise OR the result is the same
        return (intR << 16) | (intG << 8) | (intB);
    }

    private int convolutedRGBToIntConverter(float r, float g, float b){
        return (((int)(r*255)&0xFF)<<16)|(((int)(g*255)&0xFF)<<8)|((int)(b*255)&0xFF);
    }
    // heh its almost like lisp
    private int convolutedRGBToIntConverter(int r, int g, int b){
        return (((r*255)&0xFF)<<16)|(((g*255)&0xFF)<<8)|((b*255)&0xFF);
    }
}