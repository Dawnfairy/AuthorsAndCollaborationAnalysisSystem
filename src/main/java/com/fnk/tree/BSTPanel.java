package com.fnk.tree;

import javax.swing.*;
import java.awt.*;

/**
 * BSTPanel sınıfı, Binary Search Tree'yi grafiksel olarak çizer.
 */
public
class BSTPanel extends JPanel {
    private BSTNode root;
    private int nodeRadius = 20;
    private int verticalSpacing = 50;

    public BSTPanel() {
        this.root = null;
    }

    public void setBSTRoot(BSTNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            drawBST(g, root, getWidth() / 2, 30, getWidth() / 4);
        }
    }

    private void drawBST(Graphics g, BSTNode node, int x, int y, int horizontalSpacing) {
        if (node == null) {
            return;
        }
        if (node.left != null) {
            // Çizgi çiz
            g.drawLine(x, y, x - horizontalSpacing, y + verticalSpacing);
            // Sol alt ağacı çiz
            drawBST(g, node.left, x - horizontalSpacing, y + verticalSpacing, horizontalSpacing / 2);
        }

        if (node.right != null) {
            // Çizgi çiz
            g.drawLine(x, y, x + horizontalSpacing, y + verticalSpacing);
            // Sağ alt ağacı çiz
            drawBST(g, node.right, x + horizontalSpacing, y + verticalSpacing, horizontalSpacing / 2);
        }

        // Düğüm çizer
        g.setColor(Color.CYAN);
        g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        g.setColor(Color.BLACK);
        g.drawOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        // Düğüm bilgilerini yazar
        String text = String.valueOf(node.data.getId());
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textAscent = fm.getAscent();
        g.drawString(text, x - textWidth / 2, y + textAscent / 2);
    }
}
