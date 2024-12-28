package com.fnk.model;

import java.util.Objects;

/**
 * Yazarlar arasındaki işbirliği ilişkisini temsil eder.
 */
public class Edge {
    private Author author1;
    private Author author2;
    private int weight;
    public Edge(Author author1, Author author2) {
        // Her zaman daha küçük hashCode'a sahip yazar ilk yazar olur
        if (author1.hashCode() <= author2.hashCode()) {
            this.author1 = author1;
            this.author2 = author2;
        } else {
            this.author1 = author2;
            this.author2 = author1;
        }
        this.weight = 1;
    }

    public Author getAuthor1() {
        return author1;
    }
    public Author getAuthor2() {
        return author2;
    }
    public int getWeight() {
        return weight;
    }
    public void incrementWeight() {
        this.weight++;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;
        // Yönsüzlük sağlamak için iki Author karşılaştırılır
        return (author1.equals(edge.author1) && author2.equals(edge.author2)) ||
                (author1.equals(edge.author2) && author2.equals(edge.author1));
    }
    @Override
    public int hashCode() {
        return Objects.hash(author1, author2);
    }
    @Override
    public String toString() {
        return "Edge{" +
                "author1=" + author1 +
                ", author2=" + author2 +
                ", weight=" + weight +
                '}';
    }
}
