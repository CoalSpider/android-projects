package com.ben.testapp.model;

import android.content.Context;
import android.util.Log;

import com.ben.testapp.common.ObjComponentSize;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.ben.testapp.common.ObjComponentSize.*;

/**
 * Created by Ben on 4/21/2017.
 */

class ObjFileParser {
    private static final String TAG = "ObjFileParser";
    private static final String ASSET_SUB_DIRECTORY = "objFiles" + File.separator;
    private static final String OBJ_FILE_EXTENSION = ".obj";
    private static final String V = "v ";
    private static final String VT = "vt";
    private static final String VN = "vn";
    private static final String F = "f ";

    private static float[] v;
    private static float[] vt;
    private static float[] vn;
    private static short[] fv;
    private static short[] ft;
    private static short[] fn;

    static void parse(Context context, String fileName){
        try {
            String path = getPath(fileName);
            generateArrays(context, path);
            fillArrays(context, path);
        }catch (IOException e){
            Log.e(TAG,"Error reading file: " + fileName);
            e.printStackTrace();
        } finally {
            // were done with the path so clear it
            pathBuilder.delete(0,pathBuilder.length());
        }
    }

    private static StringBuilder pathBuilder = new StringBuilder();
    private static String getPath(String fileName){
        pathBuilder.append(ASSET_SUB_DIRECTORY);
        pathBuilder.append(fileName);
        pathBuilder.append(OBJ_FILE_EXTENSION);
        return pathBuilder.toString();
    }

    private static void generateArrays(Context context, String path) throws
            IOException {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = context.getAssets().open(path);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            initArrays(bufferedReader);
        } finally {
            if(bufferedReader != null)
                bufferedReader.close();
            if(inputStreamReader != null)
                inputStreamReader.close();
            if(inputStream != null)
                inputStream.close();
        }
    }

    private static void initArrays(BufferedReader bufferedReader) throws IOException{
        int vCount = 0;
        int vtCount = 0;
        int vnCount = 0;
        int fCount = 0;

        String currentLine = bufferedReader.readLine();
        while (currentLine != null) {
            if (currentLine.startsWith(V)) {
                vCount++;
            } else if (currentLine.startsWith(VT)) {
                vtCount++;
            } else if (currentLine.startsWith(VN)) {
                vnCount++;
            } else if (currentLine.startsWith(F)) {
                fCount++;
            }
            currentLine = bufferedReader.readLine();
        }

        v = new float[vCount * VERTEX.size()];
        vt = new float[vtCount * TEXTURE.size()];
        vn = new float[vnCount * NORMAL.size()];
        fv = new short[fCount * FACE.size()];
        ft = new short[fCount * FACE.size()];
        fn = new short[fCount * FACE.size()];
    }

    private static void fillArrays(Context context, String path) throws
            IOException {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = context.getAssets().open(path);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            // read in the obj data
            readObjData(bufferedReader);
        } finally {
            if(bufferedReader != null)
                bufferedReader.close();
            if(inputStreamReader != null)
                inputStreamReader.close();
            if(inputStream != null)
                inputStream.close();
        }
    }

    private static void readObjData(BufferedReader bufferedReader) throws IOException{
        int vertexIndex = 0;
        int textureIndex = 0;
        int normalIndex = 0;
        int faceIndex = 0;

        String currLine = bufferedReader.readLine();
        while (currLine != null) {
            if (currLine.startsWith(V)) {
                parseFloatData(v, vertexIndex, currLine);
                vertexIndex += VERTEX.size();
            } else if (currLine.startsWith(VT)) {
                parseFloatData(vt, textureIndex, currLine);
                textureIndex += TEXTURE.size();
            } else if (currLine.startsWith(VN)) {
                parseFloatData(vn, normalIndex, currLine);
                normalIndex += NORMAL.size();
            } else if (currLine.startsWith(F)) {
                parseDrawOrder(faceIndex, currLine);
                faceIndex += FACE.size();
            }
            currLine = bufferedReader.readLine();
        }
    }

    private static void parseFloatData(float[] list, int listIndex, String line) throws NumberFormatException{
        String[] split = line.split(" ");

        int count = 0;
        // skip the identifier for the line (v,vt,vn,etc...)
        for (int i = 1; i < split.length; i++) {

            // skip the w component if the list wont hold it
            if (list.equals(v) && count >= VERTEX.size())
                continue;

            // skip extra white spaces such as a extra space at the end of a line
            if(split[i].equals(""))
                continue;

            list[listIndex+count] = Float.parseFloat(split[i]);
            count++;
        }
    }

    private static void parseDrawOrder(int fIndex, String line) throws NumberFormatException{
        String[] split = line.split(" ");
        // skip the identifier for the line (v,vt,vn,etc...)
        for (int i = 1; i < split.length; i++) {
            // line form is v || v/vt || v/vt/vn || v//vn
            String[] drawOrders = split[i].split("/");
            // fv
            if (drawOrders.length > 0 && drawOrders[0].isEmpty() == false)
                fv[fIndex + i - 1] = Short.parseShort(drawOrders[0]);
            // ft
            if (drawOrders.length > 1 && drawOrders[1].isEmpty() == false)
                ft[fIndex + i - 1] = Short.parseShort(drawOrders[1]);
            // fn
            if (drawOrders.length > 2 && drawOrders[2].isEmpty() == false)
                fn[fIndex + i - 1] = Short.parseShort(drawOrders[2]);
        }
    }

    static float[] getExpandedVertexData() {
        return getExpandedData(v, fv, VERTEX);
    }

    static float[] getExpandedTextureData() {
        return getExpandedData(vt, ft, TEXTURE);
    }

    static float[] getExpandedNormalData() {
        return getExpandedData(vn, fn, NORMAL);
    }

    static float[] getVertexNormalTextureInterleavedData(){
        return getInterleavedData(
                getExpandedVertexData(),VERTEX,
                getExpandedNormalData(),NORMAL,
                getExpandedTextureData(),TEXTURE);
    }

    private static float[] getExpandedData(float[] data, short[] indexArray,
                                           ObjComponentSize componentSize) {
        int dataSize = componentSize.size();
        float[] expandedData = new float[indexArray.length*dataSize];
        for (int i = 0; i < indexArray.length; i++) {
          // obj face index start count at 1 not zero
            int indexInData = (indexArray[i]-1)*dataSize;
            int indexInExpandedData = i*dataSize;
            System.arraycopy(data,indexInData,expandedData,indexInExpandedData,dataSize);
        }
        return expandedData;
    }

    private static float[] getInterleavedData(
            float[] expandedVertexData, ObjComponentSize vertCompSize,
            float[] expandedNormalData, ObjComponentSize norCompSize,
            float[] expandedTextureData, ObjComponentSize texCompSize){

        if(vertCompSize!=ObjComponentSize.VERTEX)
            throw new RuntimeException("vertCompSize does not equal vertex");

        if(norCompSize!=ObjComponentSize.NORMAL)
            throw new RuntimeException("norCompSize does not equal normal");

        if(texCompSize!=ObjComponentSize.TEXTURE)
            throw new RuntimeException("texCompSize does not equal texture");

        if(expandedVertexData.length!=expandedNormalData.length)
            throw new RuntimeException("vertex length != normal length");

        float sum = 0;
        for(int i = 0; i < expandedNormalData.length; i++){
            sum+=expandedNormalData[i];
        }
        int normalCount = expandedNormalData.length/norCompSize.size();
        if((int)(sum/normalCount)!=normalCount)
            throw new RuntimeException("normals are not normalized");

        if(expandedVertexData.length==expandedTextureData.length)
            throw new RuntimeException("wrong argument for texture or vertex data");

        if((expandedVertexData.length/3)*2 != expandedTextureData.length)
            throw new RuntimeException("somethings wrong with texture data arg");

        int stride = vertCompSize.size()+norCompSize.size()+texCompSize.size();
        int size = expandedVertexData.length+expandedNormalData.length+expandedTextureData.length;

        float[] interleaved = new float[size];

        int vertDatIndx = 0;
        int norDatIndx = 0;
        int texDatIndx = 0;
        for(int i = 0; i < size; i+=stride){
            interleaved[i] = expandedVertexData[vertDatIndx];
            interleaved[i+1] = expandedVertexData[vertDatIndx+1];
            interleaved[i+2] = expandedVertexData[vertDatIndx+2];
            vertDatIndx += vertCompSize.size();
            interleaved[i+3] = expandedNormalData[norDatIndx];
            interleaved[i+4] = expandedNormalData[norDatIndx+1];
            interleaved[i+5] = expandedNormalData[norDatIndx+2];
            norDatIndx += norCompSize.size();
            interleaved[i+6] = expandedTextureData[texDatIndx];
            interleaved[i+7] = expandedTextureData[texDatIndx+1];
            texDatIndx += texCompSize.size();
        }

        return interleaved;
    }

    /**
     * Since the arrays are static they need a explicit nulling for garbage collection
     **/
    static void nullStaticModelArrays() {
        v = null;
        vn = null;
        vt = null;
        fv = null;
        ft = null;
        fn = null;
    }
}