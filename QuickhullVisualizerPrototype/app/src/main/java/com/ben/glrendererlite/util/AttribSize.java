package com.ben.glrendererlite.util;

/**
 * Created by Ben on 8/23/2017.
 */
public enum AttribSize{
    VERTEX(3), COLOR(4);

    private final int size;

    AttribSize(int size){
        this.size = size;
    }

    public int size() {
        return size;
    }
}
