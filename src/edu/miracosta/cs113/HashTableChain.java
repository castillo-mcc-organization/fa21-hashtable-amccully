package edu.miracosta.cs113;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class HashTableChain<T, T1> implements Map<String, Integer> {

    private LinkedList<Entry<String, Integer>>[] table;
    private int numKeys;
    private static final int CAPACITY = 101;
    private static final double LOAD_THRESHOLD = 3.0;
    // Constructor
    public HashTableChain() {
        table = new LinkedList[CAPACITY];
    }

    /** Contains key‐value pairs for a hash table. */
    private static class Entry<String, Integer> {

        private final String key;
        private Integer value;

        /** Creates a new key‐value pair.
         * @param key The key
         * @param value The value
         */
        public Entry(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
        /** Retrieves the key.
         * @return The key
         */
        public String getKey() {
            return key;
        }
        /** Retrieves the value.
         * @return The value
         */
        public Integer getValue() {
            return value;
        }
        /** Sets the value.
         @param val The new value
         @return The old value
         */
        public Integer setValue(Integer val) {
            Integer oldVal = value;
            value = val;
            return oldVal;
        }
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
    public boolean containsValue(Object o) {
        return false;
    }

    @Override
    public Integer get(Object key) {
        int index = key.hashCode() % table.length;
        if(index < 0) {
            index += table.length;
        }
        if(table[index] == null) {
            return null;
        }
        for(Entry element : table[index]) {
            if(element.getKey().equals(key)) {
                return (Integer) element.getValue();
            }
        }
        return null;
    }

    @Override
    public Integer put(String key, Integer value) {
        int index = key.hashCode() % table.length;
        if(index < 0) {
            index += table.length;
        }
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        for(Entry element : table[index]) {
            if(element.getKey().equals(key)) {
                return (Integer) element.setValue(value);
            }
        }
        table[index].addFirst(new Entry(key, value));   // add the entry to the front of the linkedlist
        numKeys++;
        if(numKeys > (LOAD_THRESHOLD * table.length)) {
            rehash();
        }
        return null;
    }

    public void rehash() {
        LinkedList<Entry<String, Integer>>[] oldTable = table;
        table = new LinkedList[(oldTable.length * 2) + 1];
        numKeys = 0;
        for(int i = 0; i < oldTable.length; i++) {
            if(oldTable[i] != null) {
                for(Entry element : oldTable[i]) {
                    put((String) element.getKey(), (Integer) element.getValue());
                }
            }
        }
    }

    @Override
    public Integer remove(Object key) {
        int index = key.hashCode() % table.length;
        if(index < 0) {
            index += table.length;
        }
        if(table[index] == null) {
            return null;
        }
        for(Entry element : table[index]) {
            if(element.getKey().equals(key)) {
                table[index].remove(element);
                numKeys--;
                if(table[index].isEmpty()) {
                    table[index] = null;
                }
                return (Integer) element.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> map) {

    }

    @Override
    public void clear() {
        numKeys = 0;
        table = new LinkedList[CAPACITY];
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Integer> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<String, Integer>> entrySet() {
        return null;
    }
}

