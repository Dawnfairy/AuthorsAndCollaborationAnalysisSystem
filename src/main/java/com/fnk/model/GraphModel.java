package com.fnk.model;

import com.fnk.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Grafikteki yazarlar ve kenarları yönetir.
 */
public class GraphModel {

    private CustomMap<Author, CustomMap<Author, Edge>> edges; // Adjacency list: Author -> (Author -> Edge)
    private CustomMap<String, Edge> allEdges; // Tüm kenarlar
    private CustomMap<String, Author> authorsByOrcid; // baş yazarlar
    private CustomMap<Integer, Author> authorsByID; // ID -> Author
    private int authorID; // Yeni yazarlar için ID sayacı

    public GraphModel() {
        this.authorsByOrcid = new CustomMap<>();
        this.edges = new CustomMap<>();
        this.allEdges = new CustomMap<>();
        this.authorsByID = new CustomMap<>();
        this.authorID = 0;
    }

    public Author getOrCreateAuthor(String orcid, String name) {
        Author author;
        System.out.println(orcid);
        System.out.println(name);
        if (orcid != null && !orcid.isEmpty()) {
            if (authorsByOrcid.containsKey(orcid)) {
                author = authorsByOrcid.get(orcid);
                author.addName(name);
            } else {
                authorID++;
                author = new Author(authorID, orcid, name);
                authorsByOrcid.put(orcid, author);
            }
        } else {
            authorID++;
            author = new Author(authorID, "0", name);
            authorsByOrcid.put(name, author);
        }
        return author;
    }
    public void addEdge(Author a1, Author a2) {

        String edgeKey = generateEdgeKey(a1, a2);

        if (allEdges.containsKey(edgeKey)) {
            // Var ise, mevcut kenarın ağırlığını artır
            Edge existingEdge = allEdges.get(edgeKey);
            if (existingEdge != null) {
                existingEdge.incrementWeight();
                System.out.println("Existing edge found. Weight incremented to: " + existingEdge.getWeight());
            }
        } else {
            // Yeni kenarı ekle
            Edge edge = new Edge(a1, a2);

            allEdges.put(edgeKey, edge);
            System.out.println("New edge added with weight: " + edge.getWeight());
            boolean a = allEdges.containsKey(edgeKey);
            System.out.println("true: "+ a);
            // Her iki Author için adjacency haritasına ekle
            addToAdjacencyMap(a1, a2, edge);
            addToAdjacencyMap(a2, a1, edge);
        }
    }
    private String generateEdgeKey(Author a1, Author a2) {
        if (a1.getId() < a2.getId()) {
            return a1.getId() + "-" + a2.getId();
        } else {
            return a2.getId() + "-" + a1.getId();
        }
    }
    private void addToAdjacencyMap(Author source, Author target, Edge edge) {
        if (!edges.containsKey(source)) {
            edges.put(source, new CustomMap<>());
        }
        CustomMap<Author, Edge> adjacency = edges.get(source);
        adjacency.put(target, edge);
    }
    public void synchronizeAuthors(Map<Author, Map<String, List<String>>> coauthorMap, List<Author> mainAuthorList) {

        for (Map.Entry<Author, Map<String, List<String>>> outerEntry : coauthorMap.entrySet()) {
            Author author = outerEntry.getKey();
            Map<String, List<String>> papersMap = outerEntry.getValue();
            System.out.println("coauthorMap");
            System.out.println(mainAuthorList);
            System.out.println(author);
            List<Author> processedCoauthors = new ArrayList<>();

            for (Map.Entry<String, List<String>> paperEntry : papersMap.entrySet()) {
                String paper = paperEntry.getKey();
                List<String> coauthors = paperEntry.getValue();
                System.out.println(paper);
                System.out.println(coauthors);
                //Makalede geçen coauthor isimlerini işliyoruz
                for (String coauthorName : coauthors) {
                    //Main yazarın 'names' setinde yoksa
                    if (!author.getNames().contains(coauthorName)) {
                        System.out.println("girdiii");
                        int flag = 0;
                        //mainAuthorList içerisinde bu ismi taşıyan bir yazar varsa
                        for (Author mainAuthor : mainAuthorList) {
                            if (mainAuthor.getNames().contains(coauthorName)) {
                                System.out.println("ismi aynı olan yazar coauthorName: " +coauthorName +"main author: " + mainAuthor.getNames());
                                addEdge(author, mainAuthor);
                                processedCoauthors.add(mainAuthor);
                                System.out.println("ismi aynı olan yazarla orcid eşleştirildi ");
                                flag = 1;
                            }
                        }
                        //mainAuthorList içinde de bulamadıysak yeni bir yazar oluştur
                        if (flag == 0) {
                            System.out.println("coauthorName: " +coauthorName);
                            Author newAuthor = getOrCreateAuthor(null, coauthorName);
                            newAuthor.addPaper(paper);
                            mainAuthorList.add(newAuthor);
                            addEdge(author, newAuthor);
                            processedCoauthors.add(newAuthor);
                        }
                    }
                }
            }
            /*
            // İşlenen tüm coauthor'lar arasında kenar ekle
            for (int i = 0; i < processedCoauthors.size(); i++) {
                for (int j = i + 1; j < processedCoauthors.size(); j++) {
                    Author coauthor1 = processedCoauthors.get(i);
                    Author coauthor2 = processedCoauthors.get(j);
                    addEdge(coauthor1, coauthor2); // Coauthor'lar arasında kenar ekle
                    System.out.println("Coauthor'lar arasında kenar eklendi i: " + i + " j: "+j);
                }
            }

           */
        }
        //aktarma
        for (String orcid : authorsByOrcid.keySet()) {
            Author author = authorsByOrcid.get(orcid);
            if (author != null) {
                authorsByID.put(author.getId(), author);
            }
        }
    }
    public CustomMap<Author, CustomMap<Author, Edge>> getEdges() {
        return edges;
    }
    public CustomMap<Integer, Author> getAuthors() {
        return authorsByID;
    }
    public Author getAuthorById(Integer id) {
        return authorsByID.get(id);
    }
    public Edge getEdge(Author source, Author target) {
        CustomMap<Author, Edge> sourceMap = edges.get(source);
        if (sourceMap != null) {
            return sourceMap.get(target);
        }
        return null; // Kenar yoksa null döner
    }
    public int getEdgeCount() {
        return allEdges.size();
    }
    public int getAuthorCount() {
        return authorsByID.size();
    }

    @Override
    public String toString() {
        return "GraphModel{" +
                "edges=" + edges.size() +
                ", allEdges=" + allEdges.size() +
                ", authorsByOrcid=" + authorsByOrcid.size() +
                ", authorsByID=" + authorsByID.size() +
                ", authorID=" + authorID +
                '}';
    }
}
