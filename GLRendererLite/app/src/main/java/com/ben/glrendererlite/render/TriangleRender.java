package com.ben.glrendererlite.render;

import android.content.Context;
import android.opengl.GLES20;

import com.ben.glrendererlite.R;
import com.ben.glrendererlite.Triangle;
import com.ben.glrendererlite.util.AttribSize;
import com.ben.glrendererlite.util.Util;

/**
 * Created by Ben on 8/23/2017.
 */

public class TriangleRender extends Renderer<Triangle> {
    public TriangleRender(Context context) {
        super(R.raw.vert_shader, R.raw.frag_shader, context);
    }

    @Override
    void updateBuffer() {
        float[] vertexData = new float[data.size()* AttribSize.VERTEX.size()*3];
        float[] colorData = new float[data.size()* AttribSize.COLOR.size()*3];
        int vertexIndx = 0;
        int colorIndx = 0;
        for(Triangle t : data){
            vertexData[vertexIndx] = t.a().x();
            vertexData[vertexIndx+1] = t.a().y();
            vertexData[vertexIndx+2] = t.a().z();

            vertexData[vertexIndx+3] = t.b().x();
            vertexData[vertexIndx+4] = t.b().y();
            vertexData[vertexIndx+5] = t.b().z();

            vertexData[vertexIndx+6] = t.c().x();
            vertexData[vertexIndx+7] = t.c().y();
            vertexData[vertexIndx+8] = t.c().z();

            vertexIndx+=9;

            for(int i = 0; i < 12; i+=4){
                colorData[colorIndx+i] = 1;
                colorData[colorIndx+i+1] = 1;
                colorData[colorIndx+i+2] = 0;
                colorData[colorIndx+i+3] = 0.5f;
            }
            colorIndx += 12;
        }

        vertexBuffer.setData(0,vertexData);
        colorBuffer.setData(0,colorData);
    }

    @Override
    void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,data.size()*3);
    }
}
