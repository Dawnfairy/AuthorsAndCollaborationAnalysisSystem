package com.fnk.util;

import com.fnk.model.Author;

/**
 * Yazar ve bu yazarın önceki yazarını temsil eder.
 */
public class AuthorPreviousPair {
    private Author current;
    private Author previous;

    public AuthorPreviousPair(Author current, Author previous) {
        this.current = current;
        this.previous = previous;
    }

    public Author getCurrent() {
        return current;
    }

    public Author getPrevious() {
        return previous;
    }

    public void setPrevious(Author previous) {
        this.previous = previous;
    }
}
