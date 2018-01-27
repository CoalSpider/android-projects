package com.ben.testapp.common;

/**
 * Created by Ben on 7/28/2017.
 */

public enum ObjComponentSize {
    VERTEX(3),
    COLOR(4),
    NORMAL(3),
    TEXTURE(2),
    FACE(3);

    private final int size;

    ObjComponentSize(int size){
        this.size = size;
    }

    public int size(){
        return size;
    }
}


