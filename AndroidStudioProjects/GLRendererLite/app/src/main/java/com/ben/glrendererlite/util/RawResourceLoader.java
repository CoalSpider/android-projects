package com.ben.glrendererlite.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ben on 7/23/2017.
 */

public class RawResourceLoader {

    public static String loadShaderFile(Context context, int shaderID){
        String fileAsString = null;
        try{
            fileAsString = loadFileAsStringFromRaw(context,shaderID);
        }catch (IOException e){
            Log.e("RawResourceLoader","error loading shader file");
            e.printStackTrace();
        }
        return fileAsString;
    }

    private static String loadFileAsStringFromRaw(Context context, int intID) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();

        Resources resources = context.getResources();
        InputStream rawResourceStream = resources.openRawResource(intID);
        InputStreamReader inputStreamReader = new InputStreamReader(rawResourceStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String currentLine = bufferedReader.readLine();
        while (currentLine != null) {
            stringBuilder.append(currentLine);
            stringBuilder.append("\n");
            currentLine = bufferedReader.readLine();
        }
        // remove trailing "\n"
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        // explicitly close everything
        bufferedReader.close();
        inputStreamReader.close();
        rawResourceStream.close();

        return stringBuilder.toString();
    }
}
