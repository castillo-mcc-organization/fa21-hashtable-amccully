package edu.miracosta.cs113;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * HashTableChain.java : A Hashtable class which uses the method of chaining to store key, value pairings
 *
 * @author          Aaron McCully <amccully2001@gmail.com>
 * @version         1.0
 *
 * @param <K, V>    K is the key, V is the value that corresponds with the key
 */
public class HashTableChain<K, V> implements Map<K, V> {
    // data fields
    private LinkedList<Entry<K, V>>[] table;
    private int numKeys;
    private static final int CAPACITY = 101;
    private static final double LOAD_THRESHOLD = 3.0;
    /**
     *  Default constructor that creates an empty table (an array of LinkedLists) with size capacity
     */
    public HashTableChain() {
        table = new LinkedList[CAPACITY];
    }
    /**
     * An inner class for creating entries that contain key-value pairs for the hashtable
     * @param <K, V>    K is the key, V is the value that corresponds with the key
     */
    private static class Entry<K, V> {
        // data fields
        private final K key;
        private V value;
        /**
         * Creates a new key‐value pair.
         * @param key The key
         * @param value The value
         */
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        /**
         * Retrieves the key.
         * @return The key
         */
        public K getKey() {
            return key;
        }
        /**
         * Retrieves the value.
         * @return The value
         */
        public V getValue() {
            return value;
        }
        /**
         * Sets the value.
         * @param val The new value
         * @return The old value
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
        return get(key) != null;
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

    /**
     * A method for rehashing the Hashtable. A new table that is double the size, plus 1, will replace the old table
     */
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

    /**
     * An inner class that extends the EntrySet class, used to display a set of all the keys in the Hashtable.
     * @param <K, V>    K is the key, V is the value that corresponds with the key
     */
    private class KeySet<K> extends EntrySet<K, V> {
        // can override next() from EntrySet to only return the key value of an entry
        @Override
        public String toString() {
            String objString = "[";
            for(Entry element : setTable) {
                objString += element.getKey() + ", ";
            }
            return objString.substring(0,objString.length()-2) + "]";
        }
    }

    /**
     * An inner class used to display a set of all the entries in the Hashtable.
     * @param <K, V>    K is the key, V is the value that corresponds with the key
     */
    private class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
        // data fields
        LinkedList<Entry<K, V>> setTable;

        /**
         * Default constructor: copies entries from the Hashtable and makes a Set of them using a single LinkedList
         */
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
            for(Entry element : setTable) {
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

        /**
         * An inner class of EntrySet that can iterate over the entries in the Set.
         * @param <Entry>   The data that is stored in a Set, consisting of key, value pairs
         */
        private class SetIterator<Entry> implements Iterator {
            // data fields
            private int index;
            private int lastItemReturned;

            /**
             * Default constructor: starts iterator at index 0, with no item having been returned yet
             */
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
                    HashTableChain.this.remove(setTable.get(lastItemReturned).getKey());
                    setTable.remove(lastItemReturned);
                    lastItemReturned = -1;
                }
            }
        }
    }
}

