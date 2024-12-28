package com.fnk.util;

/**
 * Basit bir dizi tabanlı liste (List) implementasyonu.
 *
 * @param <T> Listeye eklenecek öğelerin tipi
 */
public class CustomSet<T> {
    private Object[] elements;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public CustomSet() {
        elements = new Object[INITIAL_CAPACITY];
        size = 0;
    }
    public void add(T element) {
        if (!contains(element)) {
            ensureCapacity();
            elements[size++] = element;
        }
    }
    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return true;
            }
        }
        return false;
    }
    public int size() {
        return size;
    }
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        return (T) elements[index];
    }
    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                // Elemanı kaldırmak için son elemanı bu konuma taşı
                elements[i] = elements[size - 1];
                elements[size - 1] = null;
                size--;
                return true;
            }
        }
        return false;
    }
    public void set(int index, T element) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        elements[index] = element;
    }
    public void swap(int i, int j) {
        if (i < 0 || j < 0 || i >= size() || j >= size()) {
            return;
        }
        T temp = get(i);
        set(i, get(j));
        set(j, temp);
    }
    private void ensureCapacity() {
        if (size >= elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < size; i++) {
            sb.append("  ").append(elements[i].toString());
            if (i < size - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
