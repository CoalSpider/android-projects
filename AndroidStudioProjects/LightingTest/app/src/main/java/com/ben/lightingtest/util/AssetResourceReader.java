package com.ben.lightingtest.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetResourceReader
{
    /** reads a .glsl file from the assets/shaders/ folder
     * @param fileName the file name of the shader.**/
    public static String readShaderFile(final Context context,
                                        String fileName)
	{
		InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
		try{
            // append subfolder to front
            if(fileName.startsWith("shaders/") == false){
                fileName = "shaders/"+fileName;
            }
            // append extension
            if(fileName.endsWith(".glsl")==false){
                fileName += ".glsl";
            }
			inputStream = context.getAssets().open(fileName);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
		}catch (IOException e){
			Log.e("readShaderFile","error reading shader file");
			e.printStackTrace();
		}
		if(bufferedReader == null){
            Log.e("readShaderFile","null buffered reader");
            return "";
        }

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try
		{
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e)
		{
			return null;
		}

		return body.toString();
	}
}