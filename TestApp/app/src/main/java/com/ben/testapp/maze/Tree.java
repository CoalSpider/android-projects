package com.ben.testapp.maze;

import java.util.Set;

/**
 * Created by Ben on 7/29/2017.
 */

public class Tree<T> {
    public Node<T> rootNode;

    public Tree() {
        this.rootNode = new Node<>(null);
    }

    public void setRootNode(Node<T> root) {
        this.rootNode = root;
    }

    public Node<T> getRootNode() {
        return rootNode;
    }

    public boolean containsNode(Node<T> node) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * deletes the given node and all children nodes
     **/
    public void deleteSubtree(Node<T> subtreeRoot) {
        if (subtreeRoot.isRoot())
            throw new RuntimeException("cannot delete root node");

        subtreeRoot.getParent().removeChild(subtreeRoot);
    }

    /**
     * deletes the given node and places all children of that node into the parent of
     * that node
     **/
    public void deleteNode(Node<T> node) {
        if (node.isRoot())
            throw new RuntimeException("cannot delete root node");

        Set<Node<T>> children = node.getChildren();
        Node<T> parent = node.getParent();
        parent.removeChild(node);
        parent.addChildren(children);
    }

    public void moveSubtree(Node<T> branchStart, Node<T> newParentNode) {
        if (branchStart == rootNode)
            throw new RuntimeException("cannot move root node");

        if (branchStart.getParent().equals(newParentNode))
            return;

        branchStart.getParent().removeChild(branchStart);
        newParentNode.addChild(branchStart);
    }

    /**
     * moves branch start to newParentNode, the descendants of branchStart are then
     * made descendants of newParentNode
     **/
    public void moveNode(Node<T> branchStart, Node<T> newParentNode) {
        if (branchStart == rootNode)
            throw new RuntimeException("cannot move root node");

        if (branchStart.getParent().equals(newParentNode))
            return;

        branchStart.getParent().removeChild(branchStart);
        newParentNode.addChild(branchStart);
    }
}
