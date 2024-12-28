package com.fnk.util;

import com.fnk.model.Author;

public class AuthorDistancePair implements Comparable<AuthorDistancePair> {
    private Author author;
    private int distance;

    public AuthorDistancePair(Author author, int distance) {
        this.author = author;
        this.distance = distance;
    }

    public Author getAuthor() {
        return author;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(AuthorDistancePair o) {
        return Integer.compare(this.distance, o.distance);
    }
}
