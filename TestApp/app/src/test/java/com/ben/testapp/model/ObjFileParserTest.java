package com.ben.testapp.model;

import com.ben.testapp.common.ObjComponentSize;

import org.junit.Test;

import static com.ben.testapp.common.ObjComponentSize.VERTEX;
import static org.junit.Assert.*;

/**
 * Created by Ben on 7/31/2017.
 */
public class ObjFileParserTest {


    @Test
    public void vertexParseTest() throws Exception {
        String vertexData = "v 1.000000 -1.000000 -1.000000";
        String vertexDataWithFourthComp = "v 1.000000 -1.000000 -1.000000 0.123456";
        float[] expected = new float[]{1.000000f, -1.000000f, -1.000000f};
        float[] actual1 = new float[3];
        parseFloatData(actual1, 0, vertexData);
        float[] actual2 = new float[3];
        parseFloatData(actual2, 0, vertexDataWithFourthComp);
        for (int i = 0; i < 3; i++) {
            assertTrue(expected[i] == actual1[i]);
            assertTrue(expected[i] == actual2[i]);
        }
    }

    @Test
    public void extraSpacesInVertexData() throws Exception {
        String vertexData = "v  1.000000 -1.000000 -1.000000 ";
        float[] expected = new float[]{1.000000f, -1.000000f, -1.000000f};
        float[] actual1 = new float[3];
        parseFloatData(actual1, 0, vertexData);
        for (int i = 0; i < 3; i++) {
            assertTrue(expected[i] == actual1[i]);
        }
    }

    @Test
    public void throwsNFEForErrorInFile() throws Exception {
        String vertexData = "v  1.000000 abbcd -1.000000 -1.000000 ";
        float[] actual1 = new float[3];
        boolean foundNFE = false;
        try {
            parseFloatData(actual1, 0, vertexData);
        } catch (NumberFormatException e) {
            foundNFE = true;
        }

        assertTrue(foundNFE);

        Float.parseFloat("1.000 ");
    }

    private static void parseFloatData(float[] list, int listIndex, String line) {
        String[] split = line.split(" ");

        int count = 0;
        // skip the identifier for the line (v,vt,vn,etc...)
        for (int i = 1; i < split.length; i++) {

            // skip the w component if the list wont hold it
            if (count >= VERTEX.size())
                continue;

            // skip extra white spaces
            if (split[i].isEmpty())
                continue;

            System.out.println(count);
            list[listIndex + count] = Float.parseFloat(split[i]);
            count++;
        }
    }


    @Test
    public void expandDataTest() {
        float[] data = new float[]{1, 1, 1, 10, 10, 10, 1000, 1000, 1000};
        short[] indexArray = new short[]{1, 2, 3, 1, 2, 3, 1, 2, 3};
        float[] expected = new float[]{1, 1, 1, 10, 10, 10, 1000, 1000, 1000, 1, 1,
                1, 10, 10, 10, 1000, 1000, 1000, 1, 1, 1, 10, 10, 10, 1000, 1000, 1000};
        float[] actual = getExpandedData(data, indexArray, 3);
        assertTrue(expected.length == actual.length);
        for (int i = 0; i < actual.length; i++) {
            assertTrue(expected[i] == actual[i]);
        }
    }

    private static float[] getExpandedData(float[] data, short[] indexArray, int
            dataSize) {
        float[] expandedData = new float[indexArray.length * dataSize];
        for (int i = 0; i < indexArray.length; i++) {
            int indexInData = (indexArray[i] - 1) * dataSize;
            int indexInExpandedData = i * dataSize;
            System.arraycopy(data, indexInData, expandedData, indexInExpandedData,
                    dataSize);
        }
        return expandedData;
    }

    @Test
    public void interleaveDataTest() {
        float[] vertDat = new float[]{0, 0, 0, 1, 0, 0, 1, 1, 0};
        float[] norDat = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        float[] texDat = new float[]{0, 0, 0, 1, 1, 1};
        float[] expected = new float[]{0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0,
                1, 1, 1, 0, 1, 1, 1, 1, 1};
        float[] interleaved = getInterleavedData(
                vertDat, ObjComponentSize.VERTEX,
                norDat, ObjComponentSize.NORMAL,
                texDat, ObjComponentSize.TEXTURE);
        assertTrue(expected.length == interleaved.length);

        for (int i = 0; i < expected.length; i++) {
            assertTrue(expected[i] == interleaved[i]);
        }
    }

    @Test
    public void interleaveDataRTETest() {
        float[] vertDat = new float[]{0, 0, 0, 1, 0, 0, 1, 1, 0};
        float[] norDat = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        float[] texDat = new float[]{0, 0, 0, 1, 1, 1};
        float[] expected = new float[]{0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0,
                1, 1, 1, 0, 1, 1, 1, 1, 1};

        boolean throwsRTE = false;
        try {
            throwsRTE = false;
            float[] interleaved = getInterleavedData(
                    norDat, ObjComponentSize.VERTEX,
                    vertDat, ObjComponentSize.NORMAL,
                    texDat, ObjComponentSize.TEXTURE);
        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
        try {
            throwsRTE = false;
            float[] interleaved = getInterleavedData(
                    vertDat, ObjComponentSize.VERTEX,
                    vertDat, ObjComponentSize.NORMAL,
                    vertDat, ObjComponentSize.TEXTURE);
        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
        try {
            throwsRTE = false;
            float[] interleaved = getInterleavedData(
                    texDat, ObjComponentSize.VERTEX,
                    texDat, ObjComponentSize.NORMAL,
                    texDat, ObjComponentSize.TEXTURE);
        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
        try {
            throwsRTE = false;
            float[] interleaved = getInterleavedData(
                    norDat, ObjComponentSize.VERTEX,
                    norDat, ObjComponentSize.NORMAL,
                    norDat, ObjComponentSize.TEXTURE);
        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
        try {
            throwsRTE =false;
            float[] interleaved = getInterleavedData(
                    vertDat, ObjComponentSize.NORMAL,
                    norDat, ObjComponentSize.NORMAL,
                    texDat, ObjComponentSize.TEXTURE);

        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
        try {
            throwsRTE =false;
            float[] interleaved = getInterleavedData(
                    vertDat, ObjComponentSize.VERTEX,
                    norDat, ObjComponentSize.VERTEX,
                    texDat, ObjComponentSize.TEXTURE);

        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
        try {
            throwsRTE =false;
            float[] interleaved = getInterleavedData(
                    vertDat, ObjComponentSize.VERTEX,
                    norDat, ObjComponentSize.NORMAL,
                    texDat, ObjComponentSize.VERTEX);

        } catch (RuntimeException e) {
            throwsRTE = true;
        }
        assertTrue(throwsRTE);
    }

    private static float[] getInterleavedData(
            float[] expandedVertexData, ObjComponentSize vertCompSize,
            float[] expandedNormalData, ObjComponentSize norCompSize,
            float[] expandedTextureData, ObjComponentSize texCompSize) {

        if (vertCompSize != ObjComponentSize.VERTEX)
            throw new RuntimeException("vertCompSize does not equal vertex");

        if (norCompSize != ObjComponentSize.NORMAL)
            throw new RuntimeException("norCompSize does not equal normal");

        if (texCompSize != ObjComponentSize.TEXTURE)
            throw new RuntimeException("texCompSize does not equal texture");

        if (expandedVertexData.length != expandedNormalData.length)
            throw new RuntimeException("vertex length != normal length");

        float sum = 0;
        for (int i = 0; i < expandedNormalData.length; i++)
            sum += expandedNormalData[i];

        int normalCount = expandedNormalData.length / norCompSize.size();
        if ((int) (sum / normalCount) != normalCount)
            throw new RuntimeException("normals are not normalized");

        if (expandedVertexData.length == expandedTextureData.length)
            throw new RuntimeException("wrong argument for texture or vertex data");

        if ((expandedVertexData.length / 3) * 2 != expandedTextureData.length)
            throw new RuntimeException("somethings wrong with texture data arg");

        int stride = vertCompSize.size() + norCompSize.size() + texCompSize.size();
        int size = expandedVertexData.length + expandedNormalData.length +
                expandedTextureData.length;

        float[] interleaved = new float[size];

        int vertDatIndx = 0;
        int norDatIndx = 0;
        int texDatIndx = 0;
        for (int i = 0; i < size; i += stride) {
            interleaved[i] = expandedVertexData[vertDatIndx];
            interleaved[i + 1] = expandedVertexData[vertDatIndx + 1];
            interleaved[i + 2] = expandedVertexData[vertDatIndx + 2];
            vertDatIndx += vertCompSize.size();
            interleaved[i + 3] = expandedNormalData[norDatIndx];
            interleaved[i + 4] = expandedNormalData[norDatIndx + 1];
            interleaved[i + 5] = expandedNormalData[norDatIndx + 2];
            norDatIndx += norCompSize.size();
            interleaved[i + 6] = expandedTextureData[texDatIndx];
            interleaved[i + 7] = expandedTextureData[texDatIndx + 1];
            texDatIndx += texCompSize.size();
        }

        return interleaved;
    }
}