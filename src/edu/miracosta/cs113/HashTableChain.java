package edu.miracosta.cs113;

import java.util.*;

public class HashTableChain<K, V> implements Map<K, V> {

    private LinkedList<Entry<K, V>>[] table;
    private int numKeys;
    private static final int CAPACITY = 101;
    private static final double LOAD_THRESHOLD = 3.0;
    // Constructor
    public HashTableChain() {
        table = new LinkedList[CAPACITY];
    }

    /** Contains key‐value pairs for a hash table. */
    private static class Entry<K, V> {

        private final K key;
        private V value;

        /** Creates a new key‐value pair.
         * @param key The key
         * @param value The value
         */
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        /** Retrieves the key.
         * @return The key
         */
        public K getKey() {
            return key;
        }
        /** Retrieves the value.
         * @return The value
         */
        public V getValue() {
            return value;
        }
        /** Sets the value.
         @param val The new value
         @return The old value
         */
        public V setValue(V val) {
            V oldVal = value;
            value = val;
            return oldVal;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Hashtable)) {
            return false;
        }
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null) {
                for(Entry<K, V> element : table[i]) {
                    if(!((Hashtable) o).containsKey(element.getKey()) ||
                            !((Hashtable) o).get(element.getKey()).equals(element.getValue())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashVal = 0;
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null) {
                for(Entry<K, V> element : table[i]) {
                    hashVal += (element.getKey().hashCode() ^ element.getValue().hashCode());
                }
            }
        }
        return hashVal;
    }

    @Override
    public int size() {
        return numKeys;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if(get(key) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null) {
                for(Entry<K, V> element : table[i]) {
                    if(element.getValue().equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        int index = key.hashCode() % table.length;
        if(index < 0) {
            index += table.length;
        }
        if(table[index] == null) {
            return null;
        }
        for(Entry<K, V> element : table[index]) {
            if(element.getKey().equals(key)) {
                return element.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int index = key.hashCode() % table.length;
        if(index < 0) {
            index += table.length;
        }
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        for(Entry<K, V> element : table[index]) {
            if(element.getKey().equals(key)) {
                return element.setValue(value);
            }
        }
        table[index].addFirst(new Entry<>(key, value));   // add the entry to the front of the linkedlist
        numKeys++;
        if(numKeys > (LOAD_THRESHOLD * table.length)) {
            rehash();
        }
        return null;
    }

    public void rehash() {
        LinkedList<Entry<K, V>>[] oldTable = table;
        table = new LinkedList[(oldTable.length * 2) + 1];
        numKeys = 0;
        for(int i = 0; i < oldTable.length; i++) {
            if(oldTable[i] != null) {
                for(Entry<K, V> element : oldTable[i]) {
                    put(element.getKey(), element.getValue());
                }
            }
        }
    }

    @Override
    public V remove(Object key) {
        int index = key.hashCode() % table.length;
        if(index < 0) {
            index += table.length;
        }
        if(table[index] == null) {
            return null;
        }
        for(Entry<K, V> element : table[index]) {
            if(element.getKey().equals(key)) {
                table[index].remove(element);
                numKeys--;
                if(table[index].isEmpty()) {
                    table[index] = null;
                }
                return element.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    @Override
    public void clear() {
        numKeys = 0;
        table = new LinkedList[CAPACITY];
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet<>();
    }

    private class KeySet<K> extends EntrySet<K, V> {
        @Override
        public String toString() {
            String objString = "[";
            for(Entry<K, V> element : setTable) {
                objString += element.getKey() + ", ";
            }
            return objString.substring(0,objString.length()-2) + "]";
        }
    }

    private class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {

        LinkedList<Entry<K, V>> setTable;

        EntrySet() {
            setTable = new LinkedList<>();
            int index = 0;
            for(int i = 0; i < table.length; i++) {
                if(table[i] != null) {
                    for(Entry element : table[i]) {
                        setTable.add(element);
                    }
                }
            }
        }

        @Override
        public String toString() {
            String objString = "[";
            for(Entry<K, V> element : setTable) {
                objString += element.getKey() + "=" + element.getValue() + ", ";
            }
            return objString.substring(0,objString.length()-2) + "]";
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new SetIterator<>();
        }

        @Override
        public int size() {
            return numKeys;
        }

        private class SetIterator<Entry> implements Iterator {

            private int index;
            private int lastItemReturned;

            public SetIterator() {
                index = 0;
                lastItemReturned = -1;
            }

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public Entry next() {
                if(hasNext()) {
                    lastItemReturned = index;
                    return (Entry) setTable.get(index++);
                }
                return null;
            }

            @Override
            public void remove() {
                if(lastItemReturned != -1) {
                    setTable.remove(lastItemReturned);
                    lastItemReturned = -1;
                }
            }
        }
    }

}

