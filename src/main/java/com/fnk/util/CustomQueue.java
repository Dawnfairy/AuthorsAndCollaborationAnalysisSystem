package com.fnk.util;

public class CustomQueue<T> {
    private CustomSet<T> list;
    public CustomQueue() {
        list = new CustomSet<>();
    }
    public void enqueue(T element) {
        list.add(element);
    }
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T element = list.get(0);
        list.remove(element);
        return element;
    }
    public boolean isEmpty() {
        return list.size() == 0;
    }
    public int size() {
        return list.size();
    }
}
