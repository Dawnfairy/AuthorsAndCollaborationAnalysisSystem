package com.fnk.tree;

import com.fnk.model.Author;

public class AuthorBST {
    private BSTNode root;
    public AuthorBST() {
        this.root = null;
    }
    public void insert(Author author) {
        root = insertRec(root, author);
    }
    private BSTNode insertRec(BSTNode root, Author author) {
        if (root == null) {
            root = new BSTNode(author);
            return root;
        }

        if (author.getId() < root.data.getId()) {
            root.left = insertRec(root.left, author);
        } else if (author.getId() > root.data.getId()) {
            root.right = insertRec(root.right, author);
        }

        return root;
    }
    public boolean delete(int authorId) {
        if (searchById(authorId) == null) {
            return false; // Yazar bulunamadı.
        }
        root = deleteRec(root, authorId);
        return true;
    }
    private BSTNode deleteRec(BSTNode root, int authorId) {
        if (root == null) {
            return root;
        }

        if (authorId < root.data.getId()) {
            root.left = deleteRec(root.left, authorId);
        } else if (authorId > root.data.getId()) {
            root.right = deleteRec(root.right, authorId);
        } else {
            // Bu düğüm silinecek.

            // Düğümlerin biri ya da hiç çocuğu yoksa.
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            // Düğümün iki çocuğu varsa, in-order successor'ı (küçük olan sağ alt ağacın en sol düğümü) bul ve değiştir.
            root.data = minValue(root.right);

            // Sağ alt ağacın in-order successor'ını sil.
            root.right = deleteRec(root.right, root.data.getId());
        }

        return root;
    }
    private Author minValue(BSTNode root) {
        Author minv = root.data;
        while (root.left != null) {
            minv = root.left.data;
            root = root.left;
        }
        return minv;
    }
    public Author searchById(int authorId) {
        return searchRec(root, authorId);
    }
    private Author searchRec(BSTNode root, int authorId) {
        if (root == null) {
            return null;
        }

        if (authorId == root.data.getId()) {
            return root.data;
        }

        if (authorId < root.data.getId()) {
            return searchRec(root.left, authorId);
        } else {
            return searchRec(root.right, authorId);
        }
    }
    public BSTNode getRoot() {
        return this.root;
    }
}