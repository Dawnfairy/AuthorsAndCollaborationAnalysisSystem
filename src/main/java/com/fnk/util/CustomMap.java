package com.fnk.util;

import java.util.*;

/**
 * Basit bir key-value map (CustomMap) implementasyonu.
 *
 * @param <K> Anahtar tipi
 * @param <V> Değer tipi
 */
public class CustomMap<K, V> {

    private class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public Entry<K, V> getNext() {
            return next;
        }
    }

    private Entry<K, V>[] entries;
    private int size = 0;
    private int INITIAL_CAPACITY = 16;
    private float loadFactor = 0.75f; // Yük faktörü

    @SuppressWarnings("unchecked")
    public CustomMap() {
        entries = new Entry[INITIAL_CAPACITY];
    }
    private int getEntriesIndex(K key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % INITIAL_CAPACITY;
    }
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Anahtar null olamaz.");
        }

        int bucketIndex = getEntriesIndex(key);
        Entry<K, V> existing = entries[bucketIndex];

        if (existing == null) {
            // Bucket boşsa, yeni bir Entry ekle
            entries[bucketIndex] = new Entry<>(key, value);
            size++;
        } else {
            // Bucket doluysa, anahtarın var olup olmadığını kontrol et
            Entry<K, V> current = existing;
            while (current != null) {
                if (current.key.equals(key)) {
                    // Anahtar bulundu, değeri güncelle
                    current.value = value;
                    return;
                }
                if (current.next == null) {
                    break;
                }
                current = current.next;
            }
            // Anahtar bulunamadı, yeni bir Entry ekle
            current.next = new Entry<>(key, value);
            size++;
        }

        // Yük faktörünü kontrol et ve gerekirse yeniden boyutlandır
        if ((1.0 * size) / INITIAL_CAPACITY >= loadFactor) {
            resize();
        }
    }
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Anahtar null olamaz.");
        }

        int bucketIndex = getEntriesIndex(key);
        Entry<K, V> current = entries[bucketIndex];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Anahtar null olamaz.");
        }

        int bucketIndex = getEntriesIndex(key);
        Entry<K, V> current = entries[bucketIndex];

        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public KeySet keySet() {
        return new KeySet(this.entries);
    }
    public Values values() {
        return new Values(this.entries);
    }
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = INITIAL_CAPACITY * 2;
        Entry<K, V>[] newBuckets = new Entry[newCapacity];

        // Mevcut tüm elemanları yeni bucketa yeniden ekle
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            Entry<K, V> current = entries[i];
            while (current != null) {
                Entry<K, V> next = current.next;
                int newIndex = Math.abs(current.key.hashCode()) % newCapacity;

                current.next = newBuckets[newIndex];
                newBuckets[newIndex] = current;

                current = next;
            }
        }

        // Yeni bucketa geçiş yap
        entries = newBuckets;
        INITIAL_CAPACITY = newCapacity;

    }

    public class KeySet implements Iterable<K> {
        private Entry<K, V>[] bucketArray;

        public KeySet(Entry<K, V>[] bucketArray) {
            this.bucketArray = bucketArray;
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        private class KeyIterator implements Iterator<K> {
            private int bucketIndex;
            private Entry<K, V> currentEntry;

            public KeyIterator() {
                this.bucketIndex = 0;
                this.currentEntry = null;
                advanceToNextEntry();
            }

            // İteratörün mevcut ve sonraki elemanı bulmasını sağlar
            private void advanceToNextEntry() {
                while (bucketIndex < bucketArray.length) {
                    if (currentEntry == null) {
                        currentEntry = bucketArray[bucketIndex];
                    } else {
                        currentEntry = currentEntry.next;
                    }

                    if (currentEntry != null) {
                        break;
                    } else {
                        bucketIndex++;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return currentEntry != null;
            }

            @Override
            public K next() {
                if (currentEntry == null) {
                    throw new java.util.NoSuchElementException();
                }

                K key = currentEntry.key;
                advanceToNextEntry();
                return key;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() metodu desteklenmiyor.");
            }
        }
    }
    public class Values implements Iterable<V> {
        private Entry<K, V>[] bucketArray;

        public Values(Entry<K, V>[] bucketArray) {
            this.bucketArray = bucketArray;
        }

        @Override
        public Iterator<V> iterator() {
            return new ValuesIterator();
        }

        // Iterator İç Sınıfı
        private class ValuesIterator implements Iterator<V> {
            private int bucketIndex;
            private Entry<K, V> currentEntry;

            public ValuesIterator() {
                this.bucketIndex = 0;
                this.currentEntry = null;
                advanceToNextEntry();
            }

            // İteratörün mevcut ve sonraki elemanı bulmasını sağlar
            private void advanceToNextEntry() {
                while (bucketIndex < bucketArray.length) {
                    if (currentEntry == null) {
                        currentEntry = bucketArray[bucketIndex];
                    } else {
                        currentEntry = currentEntry.next;
                    }

                    if (currentEntry != null) {
                        break;
                    } else {
                        bucketIndex++;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return currentEntry != null;
            }

            @Override
            public V next() {
                if (currentEntry == null) {
                    throw new NoSuchElementException();
                }

                V value = currentEntry.value;
                advanceToNextEntry();
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() metodu desteklenmiyor.");
            }
        }
    }
}

