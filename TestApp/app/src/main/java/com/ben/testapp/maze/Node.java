package com.ben.testapp.maze;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ben on 7/29/2017.
 */

/** Node class for maze generation, nodes cannot contain duplicate children**/
public class Node<T> {
    private T data;
    private Node<T> parent;
    private Set<Node<T>> children;

    public Node(T data){
        if(parent != null && parent.equals(this))
            throw new RuntimeException("node cannot be a parent of itself");

        this.data = data;
    }

    public void addChild(Node<T> child){
        if(child.equals(this))
            throw new RuntimeException("node cannot be a child of itself");

        if(children == null)
            children = new HashSet<>();

        children.add(child);
        child.parent = this;
    }

    public void addChildren(Set<Node<T>> nodes){
        for(Node<T> node : nodes)
            addChild(node);
    }

    public boolean containsChild(Node<T> child){
        return children.contains(child);
    }

    public void removeChild(Node<T> child){
        if(children != null)
            children.remove(child);
    }

    public Set<Node<T>> getChildren() {
        return children;
    }

    public Node<T> getParent() {
        return parent;
    }

    public T getData(){
        return data;
    }

    public boolean isRoot(){
        return parent == null;
    }

    public boolean isLeaf() {
        if (children == null)
            return true;

        if (children.size() == 0)
            return true;

        return false;
    }
}
