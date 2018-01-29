package com.mygdx.maze;

import com.mygdx.Util.TreeNode;

/**
 * Created by Ben Norman on 1/28/2018.
 * 
 * A maze that can be treated like a tree, this is useful for AI movement in the maze
 */
class TreeMaze<T> {
    private final TreeNode<T> root;
    TreeMaze(TreeNode<T> root){
        this.root = root;
    }

    public TreeNode<T> getRoot() {
        return root;
    }
    
}
