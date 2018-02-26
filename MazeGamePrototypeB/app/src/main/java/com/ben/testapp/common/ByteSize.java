package com.ben.testapp.common;

/** Holds the byte size of java primitives
 * boolean is undefined **/
public enum ByteSize {
    DOUBLE(8),
    LONG(8),
    FLOAT(4),
    INT(4),
    SHORT(2),
    CHAR(2),
    BYTE(1);

    private final int size;

    ByteSize(int size){
        this.size = size;
    }

    public int size(){
        return size;
    }
}