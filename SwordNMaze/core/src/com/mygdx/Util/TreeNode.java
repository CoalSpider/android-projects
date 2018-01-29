package com.mygdx.Util;

import java.util.List;

/**
 * Created by Ben Norman on 1/28/2018.
 */

public class TreeNode<T> {
    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }
}
