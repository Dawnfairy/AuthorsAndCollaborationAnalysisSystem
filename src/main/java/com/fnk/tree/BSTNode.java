package com.fnk.tree;

import com.fnk.model.Author;

public class BSTNode {
    Author data;
    BSTNode left;
    BSTNode right;

    public BSTNode(Author data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
