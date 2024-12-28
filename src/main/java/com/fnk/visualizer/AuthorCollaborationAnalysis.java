package com.fnk.visualizer;

import com.fnk.model.Author;
import com.fnk.tree.AuthorBST;
import com.fnk.tree.BSTPanel;
import com.fnk.util.*;
import com.fnk.model.Edge;
import com.fnk.model.GraphModel;
import com.fnk.parsers.ExcelParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class AuthorCollaborationAnalysis extends JFrame {
    private GraphModel graphModel;
    private GraphVisualizer graphVisualizer;
    private CustomLinkedList<Author> path;
    private AuthorBST authorBST = new AuthorBST();
    private BSTPanel bstPanel;
    private double averagePapers;
    private JPanel rightPanel;
    private JPanel leftPanel;
    private JPanel centerCardPanel;
    private JList<String> requirementList;
    private JList<String> queueList;
    private DefaultListModel<String> listModel;
    private DefaultListModel<String> queueListModel;
    private JTextArea detailTextArea;
    private JScrollPane detailScrollPane;
    private JScrollPane bstScrollPane;
    private JProgressBar progressBar;
    private CardLayout cardLayout;


    public AuthorCollaborationAnalysis() {
        graphModel = new GraphModel();

        parseExcel(graphModel);
        calculateAveragePapers();

        graphVisualizer = new GraphVisualizer(graphModel, averagePapers);

        initializeUI();

        setTitle("Yazarlar ve İşbirliği Analizi");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Tam ekran modunda başlat
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // UI Bileşenlerini Başlatma
    private void initializeUI() {

        leftPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        leftPanel.add(progressBar, BorderLayout.NORTH);

        JPanel queuePanel = new JPanel(new BorderLayout());
        queuePanel.setBorder(BorderFactory.createTitledBorder("Kuyruk Durumu"));

        queueListModel = new DefaultListModel<>();
        queueList = new JList<>(queueListModel);
        queueList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane queueScrollPane = new JScrollPane(queueList);
        queuePanel.add(queueScrollPane, BorderLayout.CENTER);

        leftPanel.add(queuePanel, BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        centerCardPanel = new JPanel(cardLayout);

        detailTextArea = new JTextArea("Sonuçlar");
        detailTextArea.setEditable(false);
        detailTextArea.setLineWrap(true);
        detailTextArea.setWrapStyleWord(true);
        detailScrollPane = new JScrollPane(detailTextArea);
        leftPanel.setPreferredSize(new Dimension(400, 800)); // Genişlik ayarlaması
        centerCardPanel.add(detailScrollPane, "DETAIL");

        bstPanel = new BSTPanel();
        bstPanel.setPreferredSize(new Dimension(400, 400));
        bstScrollPane = new JScrollPane(bstPanel);
        centerCardPanel.add(bstScrollPane, "BST");

        cardLayout.show(centerCardPanel, "DETAIL");

        leftPanel.add(centerCardPanel, BorderLayout.CENTER);

        JPanel graphPanel = graphVisualizer.visualizeGraph();


        rightPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<>();

        listModel.addElement("1. A ile B yazarı arasındaki en kısa yolun bulunması");
        listModel.addElement("2. A yazarının işbirliği yaptığı yazarların sıralanması");
        listModel.addElement("3. Kuyruktaki yazarlardan bir BST oluşturma");
        listModel.addElement("4. A yazarı ve işbirlikçi yazarlar arasındaki kısa yollar");
        listModel.addElement("5. A yazarının işbirliği yaptığı yazar sayısı");
        listModel.addElement("6. En çok işbirliği yapan yazar");
        listModel.addElement("7. A yazarının gidebileceği en uzun yol");

        requirementList = new JList<>(listModel);
        requirementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requirementList.setFixedCellHeight(50);
        requirementList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane listScrollPane = new JScrollPane(requirementList);
        rightPanel.add(listScrollPane, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(400, 800)); // Genişlik ayarlaması

        requirementList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = requirementList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedRequirement = listModel.getElementAt(index);
                        handleRequirement(selectedRequirement, queueListModel);
                    }
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(graphPanel, BorderLayout.CENTER);
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.EAST);
    }

    private void handleRequirement(String requirement, DefaultListModel<String> queueListModel) {
        detailTextArea.setText(""); // Önceki detayları temizle
        progressBar.setValue(0); // Progress bar'ı sıfırla
        if (requirement.startsWith("1.")) {
            cardLayout.show(centerCardPanel, "DETAIL");
            handleRequirement1(queueListModel);
        } else if (requirement.startsWith("2.")) {
            cardLayout.show(centerCardPanel, "DETAIL");
            handleRequirement2(queueListModel);
        } else if (requirement.startsWith("3.")) {
            cardLayout.show(centerCardPanel, "BST");
            handleRequirement3();
        } else if (requirement.startsWith("4.")) {
            cardLayout.show(centerCardPanel, "DETAIL");
            handleRequirement4();
        } else if (requirement.startsWith("5.")) {
            cardLayout.show(centerCardPanel, "DETAIL");
            handleRequirement5();
        } else if (requirement.startsWith("6.")) {
            cardLayout.show(centerCardPanel, "DETAIL");
            handleRequirement6();
        } else if (requirement.startsWith("7.")) {
            cardLayout.show(centerCardPanel, "DETAIL");
            handleRequirement7();
        }
    }
    //7. A yazarının gidebileceği en uzun yol
    private void handleRequirement7() {
        String authorIdStr = JOptionPane.showInputDialog(this, "Başlangıç Yazarının ID'sini giriniz:", "En Uzun Yolun Bulunması", JOptionPane.PLAIN_MESSAGE);

        if (authorIdStr == null) {
            return;
        }

        authorIdStr = authorIdStr.trim();

        if (authorIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen yazarın ID'sini giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int authorId = Integer.parseInt(authorIdStr);
            Author startAuthor = graphModel.getAuthorById(authorId);

            if (startAuthor == null) {
                JOptionPane.showMessageDialog(this, "Girilen ID'ye sahip yazar bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // En uzun yolu bulma
            CustomLinkedList<Author> longestPath = findLongestPath(startAuthor);

            if (longestPath.size() == 0) {
                JOptionPane.showMessageDialog(this, "Belirtilen yazarın herhangi bir işbirliği yolu bulunmamaktadır.", "Sonuç", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Başlangıç Yazarından Gidebileceği En Uzun Yol:\n\n Düğüm sayısı: " + longestPath.size() + "\n\n");

            for (int i = 0; i < longestPath.size(); i++) {
                Author author = longestPath.get(i);
                resultBuilder.append(author.getNames()).append(" (ID: ").append(author.getId()).append(")");
                if (i < longestPath.size() - 1) {
                    resultBuilder.append(" -> ");
                }
            }
            resultBuilder.append("\n");

            detailTextArea.setText(resultBuilder.toString());
            graphVisualizer.resetGraphStyles();
            graphVisualizer.highlightPath(longestPath);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    //6. En çok işbirliği yapan yazar
    private void handleRequirement6() {

        Author topCollaborator = getTopCollaborator();

        if (topCollaborator == null) {
            JOptionPane.showMessageDialog(this, "Veritabanında hiç yazar bulunmamaktadır.", "Sonuç", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CustomSet<Author> collaborators = getCollaborators(topCollaborator);
        int collaboratorCount = collaborators.size();

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("En Çok İşbirliği Yapan Yazar:\n\n");
        resultBuilder.append("Yazar: ").append(topCollaborator.getNames())
                .append(" (ID: ").append(topCollaborator.getId()).append(")\n");
        resultBuilder.append("İşbirliği Yaptığı Yazar Sayısı: ").append(collaboratorCount).append("\n\n");

        if (collaboratorCount > 0) {
            resultBuilder.append("İşbirliği Yaptığı Yazarlar:\n");
            for (int i = 0; i < collaborators.size(); i++) {
                Author collaborator = collaborators.get(i);
                resultBuilder.append("- ").append(collaborator.getNames())
                        .append(" (ID: ").append(collaborator.getId()).append(")\n");
            }
        }

        detailTextArea.setText(resultBuilder.toString());
        graphVisualizer.highlightTopCollaborator(topCollaborator, collaborators);
    }
    //5. A yazarının işbirliği yaptığı yazar sayısı
    private void handleRequirement5() {

        String authorAIdStr = JOptionPane.showInputDialog(this, "A Yazarının ID'sini giriniz:", "İşbirliği Yaptığı Yazar Sayısını Hesaplama", JOptionPane.PLAIN_MESSAGE);

        if (authorAIdStr == null) {
            return;
        }

        authorAIdStr = authorAIdStr.trim();

        if (authorAIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen A yazarının ID'sini giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int authorAId = Integer.parseInt(authorAIdStr);
            Author authorA = graphModel.getAuthorById(authorAId);

            if (authorA == null) {
                JOptionPane.showMessageDialog(this, "Girilen ID'ye sahip yazar bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // İşbirliği yaptığı yazarları bulma
            CustomSet<Author> collaborators = getCollaborators(authorA);
            int collaboratorCount = collaborators.size();

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Yazar: ").append(authorA.getNames())
                    .append(" (ID: ").append(authorA.getId()).append(")\n");
            resultBuilder.append("İşbirliği Yaptığı Yazar Sayısı: ").append(collaboratorCount).append("\n\n");

            if (collaboratorCount > 0) {
                resultBuilder.append("İşbirliği Yaptığı Yazarlar:\n");
                for (int i = 0; i < collaborators.size(); i++) {
                    Author collaborator = collaborators.get(i);
                    resultBuilder.append("- ").append(collaborator.getNames())
                            .append(" (ID: ").append(collaborator.getId()).append(")\n");
                }
            }

            detailTextArea.setText(resultBuilder.toString());
            graphVisualizer.highlightCollaborators(authorA, collaborators);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    //4. A yazarı ve işbirlikçi yazarlar arasındaki kısa yollar
    private void handleRequirement4() {
        String authorAIdStr = JOptionPane.showInputDialog(this, "A Yazarının ID'sini giriniz:", "Kısa Yolları Hesaplama", JOptionPane.PLAIN_MESSAGE);

        if (authorAIdStr == null) {
            return;
        }

        authorAIdStr = authorAIdStr.trim();

        if (authorAIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen A yazarının ID'sini giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int authorAId = Integer.parseInt(authorAIdStr);
            Author authorA = graphModel.getAuthorById(authorAId);

            if (authorA == null) {
                JOptionPane.showMessageDialog(this, "Girilen ID'ye sahip yazar bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // En kısa yolları bulma
            CustomMap<Author, List<Author>> shortestPaths = findShortestPaths(authorA);

            if (shortestPaths.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A yazarından diğer yazarlarla herhangi bir işbirliği yolu bulunmamaktadır.", "Sonuç", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("A Yazarından Diğer Yazarlarla Olan En Kısa Yollar:\n\n");

            graphVisualizer.resetGraphStyles();

            for (Author author : shortestPaths.keySet()) {
                List<Author> path = shortestPaths.get(author);


                CustomLinkedList<Author> customPath = new CustomLinkedList<>();
                for (Author authorPath : path) {
                    customPath.add(authorPath);
                }

                graphVisualizer.highlightPath(customPath);

                resultBuilder.append("A Yazarından ").append(author.getNames()).append(" (ID: ").append(author.getId()).append(") Yazarına Olan Yol:\n");

                for (int j = 0; j < path.size(); j++) {
                    Author step = path.get(j);
                    resultBuilder.append(step.getNames()).append(" (ID: ").append(step.getId()).append(")");
                    if (j < path.size() - 1) {
                        resultBuilder.append(" -> ");
                    }
                }
                resultBuilder.append("\n\n");
            }

            detailTextArea.setText(resultBuilder.toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    //3. Kuyruktaki yazarlardan bir BST oluşturma
    private void handleRequirement3() {

        authorBST = new AuthorBST();

        if (path == null) {
            JOptionPane.showMessageDialog(this, "Kuyruk boş.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;

        }
        // Kuyruktaki yazarları al ve BST'ye ekle
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getId());
            Author author = path.get(i);
            authorBST.insert(author);
        }

        bstPanel.setBSTRoot(authorBST.getRoot());
        leftPanel.revalidate();
        leftPanel.repaint();

        String authorToDeleteStr = JOptionPane.showInputDialog(this, "BST'den silmek istediğiniz yazarın ID'sini giriniz:", "BST'den Yazar Silme", JOptionPane.PLAIN_MESSAGE);

        if (authorToDeleteStr == null) {
            return;
        }

        authorToDeleteStr = authorToDeleteStr.trim();

        if (authorToDeleteStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir yazar ID'si giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int authorToDeleteId = Integer.parseInt(authorToDeleteStr);
            System.out.println("girilen id: " + authorToDeleteId);
            Author authorToDelete = authorBST.searchById(authorToDeleteId);

            if (authorToDelete == null) {
                JOptionPane.showMessageDialog(this, "Girilen ID'ye sahip yazar BST'de bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean deleted = authorBST.delete(authorToDeleteId);

            if (deleted) {
                JOptionPane.showMessageDialog(this, "Yazar BST'den başarıyla silindi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Yazar BST'den silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }

            bstPanel.setBSTRoot(authorBST.getRoot());
            leftPanel.revalidate();
            leftPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    //2. A yazarının işbirliği yaptığı yazarların sıralanması
    private void handleRequirement2(DefaultListModel<String> queueListModel) {

        String authorAIdStr = JOptionPane.showInputDialog(this, "A Yazarının ID'sini giriniz:", "Kuyruk Oluşturma", JOptionPane.PLAIN_MESSAGE);

        if (authorAIdStr == null) {
            return;
        }

        authorAIdStr = authorAIdStr.trim();

        if (authorAIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen A yazarının ID'sini giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int authorAId = Integer.parseInt(authorAIdStr);
            Author authorA = graphModel.getAuthorById(authorAId);

            if (authorA == null) {
                JOptionPane.showMessageDialog(this, "Girilen ID'ye sahip yazar bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CustomSet<Author> collaborators = new CustomSet<>();
            CustomMap<Author, CustomMap<Author, Edge>> edgesMap = graphModel.getEdges();

            for (Author sourceAuthor : edgesMap.keySet()) {
                CustomMap<Author, Edge> adjacencyMap = edgesMap.get(sourceAuthor);
                if (adjacencyMap == null) continue;

                for (Author targetAuthor : adjacencyMap.keySet()) {
                    Edge edge = adjacencyMap.get(targetAuthor);
                    if (edge == null) {
                        continue;
                    }

                    if (edge.getAuthor1().equals(authorA)) {
                        Author target = edge.getAuthor2();
                        if (!collaborators.contains(target)) {
                            collaborators.add(target);
                        }
                    } else if (edge.getAuthor2().equals(authorA)) {
                        Author source = edge.getAuthor1();
                        if (!collaborators.contains(source)) {
                            collaborators.add(source);
                        }
                    }
                }
            }


            if (collaborators.size() == 0) {
                JOptionPane.showMessageDialog(this, "A yazarının işbirliği yaptığı yazar bulunmamaktadır.", "Sonuç", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            CustomPriorityQueue<AuthorDistancePair> queue = new CustomPriorityQueue<>();

            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    SwingUtilities.invokeLater(() -> progressBar.setValue(0));
                    publish("Sıralanmamış Liste: " + formatCustomSet(collaborators) + "\n");
                    publish("Sıralı Kuyruk: " + formatQueue(queue) + "\n\n");


                    int total = collaborators.size();
                    int count = 0;

                    for (int i = 0; i < collaborators.size(); i++) {

                        Author collaborator = collaborators.get(i);
                        int maxIndex = i;

                        for (int j = i + 1; j < collaborators.size(); j++) {
                            if (collaborators.get(j).getPaperCount() > collaborator.getPaperCount()) {
                                collaborator = collaborators.get(j);
                                maxIndex = j;
                            }
                        }


                        // En büyük yazarın listede yer değiştirmesi (swap)
                        if (maxIndex != i) {
                            collaborators.swap(i, maxIndex);
                            publish(String.format("Swapped: %s (ID: %d) with %s (ID: %d)\n",
                                    collaborators.get(maxIndex).getNames(), collaborators.get(maxIndex).getId(),
                                    collaborators.get(i).getNames(), collaborators.get(i).getId()));
                            publish("\n");
                            publish("Sıralanmamış Liste: " + formatCustomSet(collaborators) + "\n");
                            publish("Sıralı Kuyruk : " + formatQueue(queue) + "\n\n");
                            Thread.sleep(1000); // 500 ms gecikme
                        }

                        // Enqueue işlemi
                        AuthorDistancePair pair = new AuthorDistancePair(collaborator, collaborator.getPaperCount());
                        queue.enqueue(pair);

                        SwingUtilities.invokeLater(() -> {
                            queueListModel.clear();
                            Object[] queueArray = queue.toArray();
                            for (Object obj : queueArray) {
                                AuthorDistancePair pair1 = (AuthorDistancePair) obj;
                                String line = pair1.getAuthor().getNames() + " (" + pair1.getDistance() + ")";
                                queueListModel.addElement(line);
                            }
                        });
                        publish(String.format("Enqueue: %s (ID: %d, Makale Sayısı: %d)\n",
                                collaborator.getNames().toString(), collaborator.getId(), collaborator.getPaperCount()));
                        publish("\n");
                        publish("Sıralanmamış Liste: " + formatCustomSet(collaborators) + "\n");
                        publish("Sıralı Kuyruk : " + formatQueue(queue) + "\n\n");

                        count++;
                        setProgress((int) ((count / (double) total) * 100));

                        Thread.sleep(500);
                    }
                    publish("Kuyruk İşlemleri Tamamlandı.\n");
                    setProgress(100);
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    for (String text : chunks) {
                        detailTextArea.append(text);
                    }
                }

                @Override
                protected void done() {
                    progressBar.setValue(100);
                }
            };

            worker.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer) evt.getNewValue());
                }
            });

            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    //1. A ile B yazarı arasındaki en kısa yolun bulunması
    private void handleRequirement1(DefaultListModel<String> queueListModel) {

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenarlık ekleme

        panel.add(new JLabel("A Yazarının ID'si:"));
        JTextField authorAField = new JTextField();
        panel.add(authorAField);
        panel.add(new JLabel("B Yazarının ID'si:"));
        JTextField authorBField = new JTextField();
        panel.add(authorBField);

        int result = JOptionPane.showConfirmDialog(this, panel, "En Kısa Yol Hesaplama",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String authorAIdStr = authorAField.getText().trim();
            String authorBIdStr = authorBField.getText().trim();

            if (authorAIdStr.isEmpty() || authorBIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen her iki yazarın ID'sini de giriniz.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int authorAId = Integer.parseInt(authorAIdStr);
                int authorBId = Integer.parseInt(authorBIdStr);

                Author authorA = graphModel.getAuthorById(authorAId);
                Author authorB = graphModel.getAuthorById(authorBId);

                if (authorA == null || authorB == null) {
                    JOptionPane.showMessageDialog(this, "Girilen ID'lere sahip yazar bulunamadı.",
                            "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                System.out.println("Author A ID: " + authorA.getId());
                System.out.println("Author B ID: " + authorB.getId());

                detailTextArea.setText("");
                queueListModel.clear();

                StringBuilder stepDetails = new StringBuilder();
                stepDetails.append(String.format("En kısa yol hesaplanıyor: %s (ID: %d, ORCID: %s) -> %s (ID: %d, ORCID: %s)\n\n",
                        authorA.getNames(), authorA.getId(), authorA.getOrcid(),
                        authorB.getNames(), authorB.getId(), authorB.getOrcid()));

                CustomLinkedList<AuthorDistancePair> distances = new CustomLinkedList<>();
                CustomLinkedList<AuthorPreviousPair> previous = new CustomLinkedList<>();
                CustomPriorityQueue<AuthorDistancePair> queue = new CustomPriorityQueue<>();

                CustomMap<Integer, Author> authorsMap = graphModel.getAuthors();

                // Başlangıç düğümünü kuyruğa ekle ve mesafesini 0 yap
                distances.add(new AuthorDistancePair(authorA, 0));
                queue.enqueue(new AuthorDistancePair(authorA, 0));
                previous.add(new AuthorPreviousPair(authorA, null));

                // İlk kuyruk durumunu ekle
                stepDetails.append("Başlangıç Kuyruğu Durumu:\n");
                appendQueueState(stepDetails, queue);
                stepDetails.append("\n----------------------------------------\n");

                while (!queue.isEmpty()) {
                    AuthorDistancePair currentPair = queue.dequeue();
                    Author currentAuthor = currentPair.getAuthor();
                    int currentDistance = currentPair.getDistance();

                    // Eğer bu yazarın mesafesi artık güncel değilse atla
                    int recordedDistance = getDistance(distances, currentAuthor);
                    if (currentDistance > recordedDistance) {
                        continue;
                    }

                    // İşlenen yazar hedef düğümse, algoritmayı durdur
                    if (currentAuthor.equals(authorB)) {
                        stepDetails.append(String.format("Hedef yazar (%s, ID: %d) işlendi. Algoritma durduruluyor.\n",
                                currentAuthor.getNames(), currentAuthor.getId()));
                        appendQueueState(stepDetails, queue);
                        stepDetails.append("\n----------------------------------------\n");
                        break;
                    }
                    // Adım bilgilerini ekleme
                    stepDetails.append(String.format("İşlenen Yazar: %s (ID: %d), Mesafe: %d\n",
                            currentAuthor.getNames(), currentAuthor.getId(), currentDistance));

                    CustomMap<Author, Edge> adjacencyMap = graphModel.getEdges().get(currentAuthor);
                    System.out.println("Processing author: " + currentAuthor.getId());

                    // Mevcut yazarın komşularını incele
                    if (adjacencyMap != null) {
                        for (Author neighbor : adjacencyMap.keySet()) {
                            Edge edge = adjacencyMap.get(neighbor);

                            if (neighbor == null || edge == null) {
                                System.out.println("Komşu veya kenar bilgisi null.");
                                continue;
                            }

                            int edgeWeight = edge.getWeight();

                            int newDist = currentDistance + edgeWeight;
                            int neighborDist = getDistance(distances, neighbor);

                            if (newDist < neighborDist) {
                                // Mesafeyi güncelle
                                updateDistance(distances, neighbor, newDist);

                                // Önceki yazar bilgisini güncelle
                                updatePrevious(previous, neighbor, currentAuthor);

                                // Kuyruğa yeniden ekle
                                queue.enqueue(new AuthorDistancePair(neighbor, newDist));

                                stepDetails.append(String.format("  Komşu: %s (ID: %d), Yeni Mesafe: %d\n",
                                        neighbor.getNames(), neighbor.getId(), newDist));

                                stepDetails.append("  Güncellenmiş Kuyruk Durumu:\n");
                                appendQueueState(stepDetails, queue);
                                stepDetails.append("\n----------------------------------------\n");

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    JOptionPane.showMessageDialog(this, "İşlem kesildi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }
                            }
                        }
                    }
                    stepDetails.append("Güncellenmiş Kuyruk Durumu:\n");
                    appendQueueState(stepDetails, queue);
                    stepDetails.append("\n----------------------------------------\n");
                }

                int endDistance = getDistance(distances, authorB);
                if (endDistance == Integer.MAX_VALUE) {
                    stepDetails.append("Hedef yazar arasında yol bulunamadı.\n");
                    detailTextArea.setText(stepDetails.toString());
                    JOptionPane.showMessageDialog(this, "Hedef yazar arasında yol bulunamadı.", "Sonuç", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Yolun geri izlenmesi
                path = new CustomLinkedList<>();
                Author step = authorB;
                while (step != null) {
                    path.addFirst(step);
                    step = getPrevious(previous, step);
                }

                System.out.println("En kısa yol bulundu");
                System.out.println(Arrays.toString(path.toArray()));

                graphVisualizer.resetGraphStyles();
                graphVisualizer.highlightPath(path);

                stepDetails.append("En Kısa Yol Bulundu:\n");
                for (int i = 0; i < path.size(); i++) {
                    Author author = path.get(i);
                    stepDetails.append(String.format("  %d. %s (ID: %d)\n", i + 1, author.getNames(), author.getId()));
                }

                detailTextArea.setText(stepDetails.toString());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    //BFS algoritması kullanarak başlangıç yazarından tüm diğer yazarlara olan en kısa yolları bulma
    public CustomMap<Author, List<Author>> findShortestPaths(Author start) {
        CustomMap<Author, List<Author>> shortestPaths = new CustomMap<>();
        CustomQueue<Author> queue = new CustomQueue<>();
        CustomSet<Author> visited = new CustomSet<>();
        CustomMap<Author, Author> predecessor = new CustomMap<>();

        queue.enqueue(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Author current = queue.dequeue();

            CustomMap<Author, Edge> neighborsMap = graphModel.getEdges().get(current);
            if (neighborsMap != null) {
                for (Author neighbor : neighborsMap.keySet()) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        predecessor.put(neighbor, current);
                        queue.enqueue(neighbor);
                    }
                }
            }
        }

        // En kısa yolları oluşturma
        for (int i = 0; i < visited.size(); i++) {
            Author author = visited.get(i);
            if (!author.equals(start)) {
                List<Author> path = new ArrayList<>();
                Author step = author;
                while (step != null && !step.equals(start)) {
                    path.add(step);
                    step = predecessor.get(step);
                }
                if (step != null) { // Başlangıç yazarına ulaşıldıysa
                    path.add(start);
                    Collections.reverse(path);
                    shortestPaths.put(author, path);
                }
            }
        }

        return shortestPaths;
    }
    //Verilen yazarın işbirliği yaptığı yazarları bulma
    public CustomSet<Author> getCollaborators(Author author) {
        CustomSet<Author> collaborators = new CustomSet<>();
        CustomMap<Author, Edge> adjacencyMap = graphModel.getEdges().get(author);
        if (adjacencyMap != null) {
            for (Author collaborator : adjacencyMap.keySet()) {
                collaborators.add(collaborator);
            }
        }
        return collaborators;
    }
    //En çok işbirliği yapan yazarı belirler.
    public Author getTopCollaborator() {
        Author topAuthor = null;
        int maxCollaborations = -1;

        for (Author currentAuthor : graphModel.getEdges().keySet()) {
            CustomMap<Author, Edge> adjacencyMap = graphModel.getEdges().get(currentAuthor);
            int collaborationCount = adjacencyMap.size();

            if (collaborationCount > maxCollaborations) {
                maxCollaborations = collaborationCount;
                topAuthor = currentAuthor;
            }
        }

        return topAuthor;
    }
    //Verilen yazardan itibaren en uzun yolu bulma
    public CustomLinkedList<Author> findLongestPath(Author start) {
        CustomLinkedList<Author> longestPath = new CustomLinkedList<>();
        CustomLinkedList<Author> currentPath = new CustomLinkedList<>();
        CustomSet<Author> visited = new CustomSet<>();

        dfs(start, visited, currentPath, longestPath);

        return longestPath;
    }
    //DFS algoritması kullanarak en uzun yolu bulma
    private void dfs(Author current, CustomSet<Author> visited, CustomLinkedList<Author> currentPath, CustomLinkedList<Author> longestPath) {
        visited.add(current);
        currentPath.add(current);

        // Eğer şu anki yol uzunluğu en uzun yoldan daha uzunsa, güncelle
        if (currentPath.size() > longestPath.size()) {
            longestPath.clear();
            for (Author author : currentPath) {
                longestPath.add(author);
            }
        }

        CustomSet<Author> collaborators = getCollaborators(current);
        for (int i = 0; i < collaborators.size(); i++) {
            Author collaborator = collaborators.get(i);
            if (!visited.contains(collaborator)) {
                dfs(collaborator, visited, currentPath, longestPath);
            }
        }

        // Geri dönme adımı
        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }
    // Kuyruğun mevcut durumunu UI'de güncelleyen yöntem
    private void appendQueueState(StringBuilder stepDetails, CustomPriorityQueue<AuthorDistancePair> queue) {

        CustomLinkedList<AuthorDistancePair> tempList = new CustomLinkedList<>();
        while (!queue.isEmpty()) {
            tempList.add(queue.dequeue());
        }

        for (AuthorDistancePair pair : tempList) {
            queue.enqueue(pair);
        }

        int count = 0;
        int maxElementsToShow = 30;
        for (AuthorDistancePair pair : tempList) {
            if (count >= maxElementsToShow) {
                stepDetails.append("    ...\n");
                break;
            }
            stepDetails.append(String.format("    Yazar: %s (ID: %d) - Mesafe: %d\n",
                    pair.getAuthor().getNames(),
                    pair.getAuthor().getId(),
                    pair.getDistance()));
            count++;
        }

        if (queue.size() > maxElementsToShow) {
            stepDetails.append("    Kuyrukta daha fazla yazar var...\n");
        }
    }
    //Mesafe listesinden belirli bir yazarın mesafesini döndürür.
    private int getDistance(CustomLinkedList<AuthorDistancePair> distances, Author author) {
        for (int i = 0; i < distances.size(); i++) {
            AuthorDistancePair pair = distances.get(i);
            if (pair.getAuthor().equals(author)) {
                return pair.getDistance();
            }
        }
        return Integer.MAX_VALUE;
    }
    //Mesafe listesindeki belirli bir yazarın mesafesini günceller.
    private void updateDistance(CustomLinkedList<AuthorDistancePair> distances, Author author, int newDist) {
        for (int i = 0; i < distances.size(); i++) {
            AuthorDistancePair pair = distances.get(i);
            if (pair.getAuthor().equals(author)) {
                pair.setDistance(newDist);
                return;
            }
        }
        distances.add(new AuthorDistancePair(author, newDist));
    }
    //Önceki listesinden belirli bir yazarın öncülünü döndürür.
    private Author getPrevious(CustomLinkedList<AuthorPreviousPair> previous, Author author) {
        for (int i = 0; i < previous.size(); i++) {
            AuthorPreviousPair pair = previous.get(i);
            if (pair.getCurrent().equals(author)) {
                return pair.getPrevious();
            }
        }
        return null;
    }
    //Önceki listesindeki belirli bir yazarın öncülünü günceller.
    private void updatePrevious(CustomLinkedList<AuthorPreviousPair> previous, Author author, Author prev) {
        for (int i = 0; i < previous.size(); i++) {
            AuthorPreviousPair pair = previous.get(i);
            if (pair.getCurrent().equals(author)) {
                pair.setPrevious(prev);
                return;
            }
        }
        previous.add(new AuthorPreviousPair(author, prev));
    }
    private String formatQueue(CustomPriorityQueue<AuthorDistancePair> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        Object[] queueArray = queue.toArray();
        for (int i = 0; i < queueArray.length; i++) {
            AuthorDistancePair pair = (AuthorDistancePair) queueArray[i];
            sb.append("  ")
                    .append(pair.getAuthor().getNames())
                    .append(" (")
                    .append(pair.getDistance())
                    .append(")");
            if (i < queueArray.length - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    private String formatCustomSet(CustomSet<Author> set) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < set.size(); i++) {
            Author author = set.get(i);
            sb.append(String.format("  %s (ID: %d, Makale Sayısı: %d)\n",
                    author.getNames(), author.getId(), author.getPaperCount()));
        }
        sb.append("]\n");
        return sb.toString();
    }
    private void parseExcel(GraphModel graphModel) {
        ExcelParser.parseExcel("DATASET.xlsx", graphModel);
    }
    private void calculateAveragePapers() {
        int totalPapers = 0;
        System.out.println("graphModel.getAuthorCount(): " + graphModel.getAuthorCount());
        CustomMap<Integer, Author>.KeySet keySet = graphModel.getAuthors().keySet();
        for (Integer id : keySet) {
            Author author = graphModel.getAuthors().get(id);
            if (author != null) {
                totalPapers += author.getPaperCount();
            }
        }
        averagePapers = (double) totalPapers / graphModel.getAuthors().size();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthorCollaborationAnalysis frame = new AuthorCollaborationAnalysis();
            frame.setVisible(true);
        });
    }
}
