package com.fnk.visualizer;

import com.fnk.model.Author;
import com.fnk.model.Edge;
import com.fnk.model.GraphModel;
import com.fnk.util.CustomLinkedList;
import com.fnk.util.CustomSet;
import com.fnk.util.CustomMap;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class GraphVisualizer {
    private GraphModel graphModel;
    private mxGraph mxGraph;
    private Object parent;
    private CustomMap<Author, Object> vertexMap;
    private double averagePapers;
    int count = 0;
    public GraphVisualizer(GraphModel graphModel, double averagePapers) {
        this.graphModel = graphModel;
        this.averagePapers = averagePapers;
        this.mxGraph = new mxGraph();
        this.parent = mxGraph.getDefaultParent();
        this.vertexMap = new CustomMap<>();
    }


    // Grafiği görselleştirme
    public JPanel visualizeGraph() {
        JPanel graphPanel = new JPanel(new BorderLayout());

        mxGraph.getModel().beginUpdate();
        try {
            // Düğümleri ekle
            for (Author author : graphModel.getAuthors().values()) {
                int paperCount = author.getPaperCount();
                boolean isAboveAverage = paperCount > (averagePapers * 1.2);

                Color color = isAboveAverage ? new Color(205, 97, 85) : new Color(217, 136, 128); // Opaklığı azaltılmış renk
                int size = isAboveAverage ? 70 : 40;

                String style = String.format(
                        "shape=ellipse;fillColor=%s;strokeColor=#000000;opacity=100;perimeter=ellipsePerimeter;",
                        colorToHex(color)
                );
                Object v1 = mxGraph.insertVertex(parent, null, author.getId(), 0, 0, size, size, style);
                vertexMap.put(author, v1);
                System.out.println("Düğüm eklendi: " + v1.toString() + " (Author: " + author.getId() + ")");

            }
            CustomSet<Edge> addedEdges = new CustomSet<>();
            CustomMap<Author, CustomMap<Author, Edge>> edgesMap = graphModel.getEdges();
            // Kenarları ekle
            for (Author source : edgesMap.keySet()) {
                CustomMap<Author, Edge> adjacencyMap = edgesMap.get(source);

                for (Author target : adjacencyMap.keySet()) {
                    Edge edge = adjacencyMap.get(target);

                    if (addedEdges.contains(edge)) {
                        continue;
                    }
                    addedEdges.add(edge);

                    Object sourceCell = vertexMap.get(source);
                    Object targetCell = vertexMap.get(target);

                    if (sourceCell != null && targetCell != null) {
                        int weight = edge.getWeight();

                        String edgeColor = determineEdgeColor(weight);

                        String edgeStyle = String.format("endArrow=none;strokeColor=%s;strokeWidth=1;", edgeColor);

                        mxGraph.insertEdge(parent, null, String.valueOf(weight), sourceCell, targetCell, edgeStyle);
                        count++;
                        System.out.println("Kenar eklendi: " + edge.hashCode() + " (Ağırlık: " + weight + ") Count: " + count);
                    }
                }
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        mxFastOrganicLayout layout = new mxFastOrganicLayout(mxGraph);
        layout.setForceConstant(200);
        layout.setMinDistanceLimit(50);
        layout.execute(parent);

        mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);
        graphComponent.setConnectable(false);
        graphComponent.setDragEnabled(true);
        graphComponent.setToolTips(true);
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                if (cell instanceof mxCell && ((mxCell) cell).isVertex()) {
                    Integer authorId = (Integer) ((mxCell) cell).getValue();
                    Author author = graphModel.getAuthorById(authorId);
                    if (author != null) {
                        showAuthorDetails(author);
                    }
                }
            }
        });
        graphComponent.getGraphControl().addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        graphComponent.zoomIn();
                    } else {
                        graphComponent.zoomOut();
                    }
                    e.consume();
                }
            }
        });

        graphComponent.zoomTo(0.2, true);
        graphComponent.setPanning(true);
        graphComponent.setToolTips(true);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        JButton resetZoomButton = new JButton("Reset Zoom");

        zoomInButton.addActionListener(e -> graphComponent.zoomIn());
        zoomOutButton.addActionListener(e -> graphComponent.zoomOut());
        resetZoomButton.addActionListener(e -> graphComponent.zoomTo(1.0, true));

        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(resetZoomButton);

        graphPanel.add(controlPanel, BorderLayout.NORTH);

        graphPanel.add(graphComponent, BorderLayout.CENTER);
        return graphPanel;
    }
    public void resetGraphStyles() {
        CustomSet<Author> authors = new CustomSet<>();
        CustomSet<Edge> edges = new CustomSet<>();
        CustomMap<Integer, Author> authorsMap = graphModel.getAuthors();
        for (int key : authorsMap.keySet()) {
            Author author = authorsMap.get(key);
            authors.add(author);
        }

        CustomMap<Author, CustomMap<Author, Edge>> edgesMap = graphModel.getEdges();
        for (Author author : edgesMap.keySet()) {
            CustomMap<Author, Edge> adjacencyMap = edgesMap.get(author);
            for (Author author1 : adjacencyMap.keySet()) {
                Edge e = adjacencyMap.get(author1);
                edges.add(e);
            }
        }

        // Düğümleri geri yükleme
        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            Object cell = vertexMap.get(author);
            if (cell != null) {
                int paperCount = author.getPaperCount();
                boolean isAboveAverage = paperCount > (averagePapers * 1.2);

                Color color = isAboveAverage ? new Color(205, 97, 85) : new Color(217, 136, 128); // Opaklığı azaltılmış renk
                int size = isAboveAverage ? 70 : 40;

                String style = String.format(
                        "shape=ellipse;fillColor=%s;strokeColor=#000000;opacity=100;perimeter=ellipsePerimeter;",
                        colorToHex(color)
                );
                mxGraph.getModel().beginUpdate();
                try {
                    mxGraph.setCellStyle(style, new Object[]{cell});
                    mxGeometry geometry = mxGraph.getCellGeometry(cell);
                    if (geometry != null) {
                        geometry = (mxGeometry) geometry.clone();
                        geometry.setWidth(size);
                        geometry.setHeight(size);
                        mxGraph.getModel().setGeometry(cell, geometry);
                    }
                } finally {
                    System.out.println("güncellendi3");
                    mxGraph.getModel().endUpdate();
                }
            }
        }
        // Kenarları geri yükleme
        CustomSet<Edge> addedEdges = new CustomSet<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);

            boolean alreadyAdded = false;
            for (int k = 0; k < addedEdges.size(); k++) {
                if (addedEdges.get(k).equals(edge)) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (alreadyAdded) {
                continue;
            }
            addedEdges.add(edge);
            Author source = edge.getAuthor1();
            Author target = edge.getAuthor2();

            Object sourceCell = vertexMap.get(source);
            Object targetCell = vertexMap.get(target);

            if (sourceCell != null && targetCell != null) {
                String edgeColor = determineEdgeColor(edge.getWeight());
                String edgeStyle = String.format("endArrow=none;strokeColor=%s;strokeWidth=1;", edgeColor);
                mxGraph.insertEdge(parent, null, String.valueOf(edge.getWeight()), sourceCell, targetCell, edgeStyle);
            }
        }
        mxGraph.refresh();
    }
    // Yolun Grafikte Vurgulanması
    public void highlightPath(CustomLinkedList<Author> path) {
        Color randomColor = new Color((int)(Math.random() * 0x1000000));
        String hexColor = String.format("#%06X", (0xFFFFFF & randomColor.getRGB()));

        for (int i = 0; i < path.size(); i++) {
            Author author = path.get(i);
            Object cell = vertexMap.get(author);
            if (cell != null) {
                mxGraph.getModel().beginUpdate();
                try {
                    mxGraph.setCellStyle("shape=ellipse;fillColor=" + hexColor + ";strokeColor=#000000;opacity=100;", new Object[]{cell});
                } finally {
                    System.out.println("Düğüm güncellendi: " + author.getNames());
                    mxGraph.getModel().endUpdate();
                }
            }
            // Eğer yol devam ediyorsa, kenarı vurgula
            if (i < path.size() - 1) {
                Author nextAuthor = path.get(i + 1);
                Edge edge = graphModel.getEdge(author, nextAuthor);
                if (edge != null) {
                    Object edgeCell = getEdgeCell(edge);
                    if (edgeCell != null) {
                        mxGraph.getModel().beginUpdate();
                        try {
                            mxGraph.setCellStyle("endArrow=none;strokeColor=" + hexColor + ";strokeWidth=3;", new Object[]{edgeCell});
                        } finally {
                            System.out.println("güncellendi2");
                            mxGraph.getModel().endUpdate();
                        }
                    }
                }
            }
        }

        mxGraph.refresh();
    }
    //Verilen yazarın işbirliği yaptığı yazarları grafikte vurgular.
    public void highlightCollaborators(Author author, CustomSet<Author> collaborators) {
        resetGraphStyles();

        for (int i = 0; i < collaborators.size(); i++) {
            Author collaborator = collaborators.get(i);

            Object cell = vertexMap.get(collaborator);
            if (cell != null) {
                mxGraph.getModel().beginUpdate();
                try {
                    mxGraph.setCellStyle("shape=ellipse;fillColor=#FF0000;strokeColor=#FF0000;opacity=100;", new Object[]{cell});
                } finally {
                    System.out.println("Vurgulandı: " + collaborator.getNames());
                    mxGraph.getModel().endUpdate();
                }
            }

            // Kenarın vurgulanması
            Edge edge = graphModel.getEdge(author, collaborator);
            if (edge != null) {
                Object edgeCell = getEdgeCell(edge);
                if (edgeCell != null) {
                    mxGraph.getModel().beginUpdate();
                    try {
                        mxGraph.setCellStyle("endArrow=none;strokeColor=#FF0000;strokeWidth=3;", new Object[]{edgeCell});
                    } finally {
                        System.out.println("Kenar Vurgulandı: " + author.getNames() + " - " + collaborator.getNames());
                        mxGraph.getModel().endUpdate();
                    }
                }
            }
        }
        mxGraph.refresh();
    }
    // En çok işbirliği yapan yazarı vurgulama
    public void highlightTopCollaborator(Author author, CustomSet<Author> collaborators) {
        resetGraphStyles();

        Object cell = vertexMap.get(author);
        if (cell != null) {
            mxGraph.getModel().beginUpdate();
            try {
                mxGraph.setCellStyle("shape=ellipse;fillColor=#00FF00;strokeColor=#00FF00;opacity=100;", new Object[]{cell});
            } finally {
                System.out.println("En Çok İşbirliği Yapan Yazar Vurgulandı: " + author.getNames());
                mxGraph.getModel().endUpdate();
            }
        }

        for (int i = 0; i < collaborators.size(); i++) {
            Author collaborator = collaborators.get(i);

            Object collaboratorCell = vertexMap.get(collaborator);
            if (collaboratorCell != null) {
                mxGraph.getModel().beginUpdate();
                try {
                    mxGraph.setCellStyle("shape=ellipse;fillColor=#0000FF;strokeColor=#0000FF;opacity=100;", new Object[]{collaboratorCell});
                } finally {
                    System.out.println("İşbirliği Yapılan Yazar Vurgulandı: " + collaborator.getNames());
                    mxGraph.getModel().endUpdate();
                }
            }
            Edge edge = graphModel.getEdge(author, collaborator);
            if (edge != null) {
                Object edgeCell = getEdgeCell(edge);
                if (edgeCell != null) {
                    mxGraph.getModel().beginUpdate();
                    try {
                        mxGraph.setCellStyle("endArrow=none;strokeColor=#0000FF;strokeWidth=3;", new Object[]{edgeCell});
                    } finally {
                        System.out.println("Kenar Vurgulandı: " + author.getNames() + " - " + collaborator.getNames());
                        mxGraph.getModel().endUpdate();
                    }
                }
            }
        }
        mxGraph.refresh();
    }
    // Kenar Hücresini Bulma
    private Object getEdgeCell(Edge edge) {
        // JGraphX'de kenar hücresini bulmak için tüm kenar hücrelerini dolaşma
        Object[] edges = mxGraph.getChildEdges(parent);
        for (Object edgeCell : edges) {
            String label = (String) mxGraph.getModel().getValue(edgeCell);
            if (label.equals(String.valueOf(edge.getWeight()))) {
                // Kenarın ağırlığına göre eşleştirme
                Author source = edge.getAuthor1();
                Author target = edge.getAuthor2();
                Object sourceCell = vertexMap.get(source);
                Object targetCell = vertexMap.get(target);
                if (mxGraph.getModel().getTerminal(edgeCell, true).equals(sourceCell) &&
                        mxGraph.getModel().getTerminal(edgeCell, false).equals(targetCell)) {
                    return edgeCell;
                }
            }
        }
        return null;
    }
    // Yazar detaylarını gösterme
    private void showAuthorDetails(Author author) {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(author.getId()).append("\n");
        sb.append("Orcid: ").append(author.getOrcid()).append("\n");
        sb.append("Yazar: ").append(author.getNames().toString()).append("\n");
        sb.append("Makale Sayısı: ").append(author.getPaperCount()).append("\n");
        sb.append("Makale Başlıkları:\n");

        CustomSet<String> papers = author.getPapers();
        for (int i = 0; i < papers.size(); i++) {
            sb.append("- ").append(papers.get(i)).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "Yazar Detayları", JOptionPane.INFORMATION_MESSAGE);
    }
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    private String determineEdgeColor(int weight) {
        // Ağırlığa göre renk belirleme
        return switch (weight) {
            case 1 -> "#e5e8e8"; // Hafif
            case 2 -> "#ccd1d1"; // Orta
            case 3 -> "#b2babb";
            case 4 -> "#99a3a4";
            case 5 -> "#7f8c8d";
            default -> "#707b7c"; // Ağır
        };
    }
}
