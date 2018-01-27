package com.ben.glrendererlite.render;

import android.content.Context;

import com.ben.glrendererlite.Edge;
import com.ben.glrendererlite.R;
import com.ben.glrendererlite.util.AttribSize;
import com.ben.glrendererlite.util.Util;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glLineWidth;

/**
 * Created by Ben on 8/23/2017.
 */
public class EdgeRender extends Renderer<Edge>{
    private float[] color;
    public EdgeRender(Context context, float[] color) {
        super(R.raw.vert_shader, R.raw.frag_shader, context);
        this.color = color;
    }

    @Override
    void updateBuffer() {
        System.out.println("updating buffer lines");
        float[] vertexData = new float[data.size()* AttribSize.VERTEX.size()*2];
        float[] colorData = new float[data.size()* AttribSize.COLOR.size()*2];

        int indexV = 0;
        int indexC = 0;

        for(Edge l : data){
            vertexData[indexV] = l.getStart().x();
            vertexData[indexV+1] = l.getStart().y();
            vertexData[indexV+2] = l.getStart().z();
            vertexData[indexV+3] = l.getEnd().x();
            vertexData[indexV+4] = l.getEnd().y();
            vertexData[indexV+5] = l.getEnd().z();
            indexV += AttribSize.VERTEX.size()*2;

            colorData[indexC] = color[0];
            colorData[indexC+1] = color[1];
            colorData[indexC+2] = color[2];
            colorData[indexC+3] = color[3];
            colorData[indexC+4] = color[0];
            colorData[indexC+5] = color[1];
            colorData[indexC+6] = color[2];
            colorData[indexC+7] = color[3];
            indexC += AttribSize.COLOR.size()*2;
        }

        vertexBuffer.setData(0,vertexData);
        colorBuffer.setData(0,colorData);
    }

    @Override
    void draw() {
        glLineWidth(1);
        glDrawArrays(GL_LINES,0,data.size()*2);
    }
}
