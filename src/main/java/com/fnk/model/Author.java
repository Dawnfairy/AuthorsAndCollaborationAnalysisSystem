package com.fnk.model;

import com.fnk.util.CustomSet;

import java.util.ArrayList;
import java.util.List;

public class Author{
    private int id;
    private String orcid;
    private List<String> names;
    private int paperCount;
    private CustomSet<String> papers;
    public Author(int id , String orcid, String name) {
        this.id = id;
        this.orcid = orcid;
        this.names = new ArrayList<>();
        this.names.add(name);
        this.paperCount = 0;
        this.papers = new CustomSet<>();
    }
    public int getId() {
        return id;
    }
    public String getOrcid() {
        return orcid;
    }
    public List<String> getNames() {
        return names;
    }
    public void addName(String name) {
        if (!names.contains(name)) {
            this.names.add(name);
        }
    }
    public int getPaperCount() {
        return paperCount;
    }
    public void addPaper(String paperTitle) {
        papers.add(paperTitle);
        paperCount++;
    }
    public CustomSet<String> getPapers() {
        return papers;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id == author.id;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", orcid='" + orcid + '\'' +
                ", names=" + stringSet() +
                '}';
    }
    private String stringSet() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0;i<names.size();i++) {
            sb.append(names.get(i));
            if(i < names.size()-1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
