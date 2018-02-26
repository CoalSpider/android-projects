package com.ben.game.mazeEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/10/2017.
 */

public class Cell{
    private List<Cell> children;
    private Cell parent;
    private int num;
    private boolean visited;
    private boolean right;
    private boolean bottom;
    private int depth;

    Cell(int num, boolean visited){
        this.num = num;
        this.visited = visited;
        this.right = true;
        this.bottom = true;
        this.children = new ArrayList<>();
        this.depth = -1;
    }

    /** this method does no checking for duplicates or if c is the parent of this**/
    public void addChild(Cell c){
        c.setParent(this);
        children.add(c);
        c.setDepth(this.getDepth()+1);
    }

    public int getNum() {
        return num;
    }

    public boolean getRight(){
        return right;
    }

    public boolean getBottom(){
        return bottom;
    }

    public boolean hasBeenVisited(){
        return visited;
    }

    public List<Cell> getChildren() {
        return children;
    }

    public Cell getParent() {
        return parent;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public int getDepth(){
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    @Override
    public String toString(){
        return "Cell: n"+num+", d"+getDepth()+", r"+right+",b"+bottom;
    }
}