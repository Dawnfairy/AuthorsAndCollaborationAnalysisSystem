package com.fnk.util;

import java.util.NoSuchElementException;

/**
 * Basit bir bağlı liste tabanlı öncelik kuyruğu (CustomPriorityQueue) implementasyonu.
 *
 * @param <T> Kuyrukta saklanacak öğenin tipi
 */
public class CustomPriorityQueue<T extends Comparable<T>> {
    private CustomLinkedList<T> heap;
    public CustomPriorityQueue() {
        this.heap = new CustomLinkedList<>();
    }

    /**
     * Eleman ekleme: Heap mantığında son pozisyona ekle, sonra yukarı taşı (sift-up).
     */
    public void enqueue(T item) {
        // Basit yaklaşım: elemanı ekleyip, sonra eklenen elemanı uygun konuma yerleştirmek için insertion sort benzeri bir yaklaşım.
        // 1) Geçici bir liste oluşturup, item'ı doğru yerine ekleyelim
        CustomLinkedList<T> tempList = new CustomLinkedList<>();
        boolean inserted = false;

        for (int i = 0; i < heap.size(); i++) {
            T current = heap.get(i);
            if (!inserted && item.compareTo(current) < 0) {
                tempList.add(item);
                inserted = true;
            }
            tempList.add(current);
        }
        if (!inserted) {
            // listenin sonuna ekle
            tempList.add(item);
        }
        // 2) tempList'i heap'e geri kopyala
        heap = tempList;
    }
    /**
     * Kuyruktan en küçük elemanı çekip döndürme.
     */
    public T dequeue() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        // Min-Heap'te en öndeki eleman en küçük olandır
        T min = heap.get(0);

        // heap’in ilk elemanını silmek için yeni bir listeye kopyalayalım
        CustomLinkedList<T> tempList = new CustomLinkedList<>();
        for (int i = 1; i < heap.size(); i++) {
            tempList.add(heap.get(i));
        }
        heap = tempList;
        return min;
    }
    public int size() {
        return heap.size();
    }
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    public Object[] toArray() {
        return heap.toArray();
    }
    @Override
    public String toString() {
        if (heap.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < heap.size(); i++) {
            sb.append("  ").append(heap.get(i).toString());
            if (i < heap.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}