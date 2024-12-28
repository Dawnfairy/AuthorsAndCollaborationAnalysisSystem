package com.fnk.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Basit bir bağlı liste (LinkedList) implementasyonu.
 *
 * @param <T> Listenin eleman tipi
 */
public class CustomLinkedList<T> implements Iterable<T>{
    private class Node {
        T data;
        Node next;
        public Node(T data) {
            this.data = data;
        }
    }
    private Node head;
    private Node tail;
    private int size;
    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    public void add(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }
    public void addFirst(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.data;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }
        return arr;
    }
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    public T remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        Node removedNode;

        if (index == 0) { // İlk elemanı kaldırma
            removedNode = head;
            head = head.next;
            if (head == null) { // Liste boşaldıysa tail'i de güncelle
                tail = null;
            }
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) { // Kaldırılacak elemanın bir öncesine git
                current = current.next;
            }
            removedNode = current.next;
            current.next = removedNode.next;
            if (removedNode.next == null) { // Son elemanı kaldırdıysak tail'i güncelle
                tail = current;
            }
        }

        size--;
        return removedNode.data;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node current = head; // head, listenizin ilk düğümünü temsil eder

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        Node current = head;
        while (current != null) {
            sb.append("  ").append(current.data.toString());
            if (current.next != null) {
                sb.append(",");
            }
            sb.append("\n");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
