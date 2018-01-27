package com.ben.glrendererlite.render;

import android.content.Context;
import android.opengl.GLES20;

import com.ben.glrendererlite.util.AttribSize;
import com.ben.glrendererlite.util.DynamicFloatBuffer;
import com.ben.glrendererlite.util.RenderProgram;

import static android.opengl.GLES20.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 8/23/2017.
 */

public abstract class Renderer<T> {
    private int programHandle;
    int matrixHandle;
    int vertexHandle;
    int colorHandle;

    private float[] mvpMatrix;

    private RenderProgram program;

    List<T> data;
    DynamicFloatBuffer vertexBuffer;
    DynamicFloatBuffer colorBuffer;

    boolean buffersNeedUpdating;

    Renderer(int vertexShader, int fragmentShader, Context context){
        program = new RenderProgram(vertexShader, fragmentShader, context);
        programHandle = program.getProgram();
        glUseProgram(program.getProgram());
        matrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        vertexHandle = glGetAttribLocation(programHandle, "a_Vertex");
        colorHandle = glGetAttribLocation(programHandle, "a_Color");

        checkHandle("matrixHandle",matrixHandle);
        checkHandle("vertexHandle",vertexHandle);
        checkHandle("colorHandle",colorHandle);

        data = new ArrayList<>();
        buffersNeedUpdating = true;

        vertexBuffer = new DynamicFloatBuffer();
        colorBuffer = new DynamicFloatBuffer();
    }

    private void checkHandle(String name, int handle){
        if(handle == -1) throw new RuntimeException("could not find " + name);
        System.out.println(name+": "+handle);
    }

    public void addData(T t){
        data.add(t);
        buffersNeedUpdating = true;
    }

    public void removeData(T t){
        data.remove(t);
        buffersNeedUpdating = true;
    }

    public void render(){
        if(buffersNeedUpdating){
            updateBuffer();
            buffersNeedUpdating = false;
        }
        glUseProgram(programHandle);

        glUniformMatrix4fv(matrixHandle,1,false,mvpMatrix,0);

        // vertex data
        passInAttributeData(vertexHandle,vertexBuffer.getBuffer(), AttribSize.VERTEX.size());
        // color data
        passInAttributeData(colorHandle,colorBuffer.getBuffer(), AttribSize.COLOR.size());

        draw();
    }

    void passInAttributeData(int handle, FloatBuffer data, int size){
        // reset
        data.position(0);
        // pass to graphics card
        GLES20.glVertexAttribPointer(handle,size,GL_FLOAT,false,0,data);
        // enable
        GLES20.glEnableVertexAttribArray(handle);
    }

    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }

    public List<T> getData() {
        return data;
    }

    abstract void updateBuffer();

    abstract void draw();

}

