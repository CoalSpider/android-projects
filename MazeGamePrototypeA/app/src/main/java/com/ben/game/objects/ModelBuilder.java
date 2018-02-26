package com.ben.game.objects;

import android.content.Context;

abstract class ModelBuilder{
    // required params
    private Context context;
    private String objFile;
    // optional params
    private int color = -1;
    private int texture = -1;
    public ModelBuilder(Context context, String objFile){
        this.context = context;
        this.objFile = objFile;
    }
    public ModelBuilder color(int color){
        this.color = color;
        return this;
    }

    public ModelBuilder texture(int texture){
        this.texture = texture;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public String getObjFile() {
        return objFile;
    }

    public int getColor() {
        return color;
    }

    public int getTexture() {
        return texture;
    }

    abstract ModelBase build();
}