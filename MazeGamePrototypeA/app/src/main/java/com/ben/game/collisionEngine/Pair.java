package com.ben.game.collisionEngine;

/**
 * Created by Ben on 6/28/2017.
 */

public class Pair<T,S> {
    T a;
    S b;

    public Pair(T a, S b){
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public S getB() {
        return b;
    }
}
