package com.ben.glrendererlite.render;

import android.content.Context;

import com.ben.glrendererlite.R;
import com.ben.glrendererlite.util.AttribSize;
import com.ben.glrendererlite.util.Util;
import com.ben.glrendererlite.util.Vec3;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Ben on 8/23/2017.
 */
public class PointRender extends Renderer<Vec3>{
    public PointRender(Context context) {
        super(R.raw.point_vert_shader, R.raw.point_frag_shader, context);
    }

    @Override
    void updateBuffer() {
        System.out.println("updating buffer points");
        float[] vertexData = new float[data.size()* AttribSize.VERTEX.size()];
        float[] colorData = new float[data.size()* AttribSize.COLOR.size()];

        int indexV = 0;
        int indexC = 0;

        for(Vec3 v : data){
            vertexData[indexV] = v.x();
            vertexData[indexV+1] = v.y();
            vertexData[indexV+2] = v.z();
            indexV += 3;

            // red if selected, blue if not selected
            if(v.isSelected()) {
                colorData[indexC] = 1;
                colorData[indexC + 1] = 0;
                colorData[indexC + 2] = 0;
                colorData[indexC + 3] = 1;
            } else {
                colorData[indexC] = 0;
                colorData[indexC + 1] = 0;
                colorData[indexC + 2] = 1;
                colorData[indexC + 3] = 1;
            }
            indexC += 4;
        }

        vertexBuffer.setData(0,vertexData);
        colorBuffer.setData(0,colorData);
    }

    @Override
    void draw() {
        glDrawArrays(GL_POINTS,0,data.size());
    }
}
