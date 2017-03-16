package net.jueb.util4j.beta.tools.avlMap2;

//BSTMap.java
//Yu-chi Chang, Allan Wang
//ychang64, awang53
//EN.600.226.01/02
//P3 (for partB)


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

/** Binary Search Tree Map implementation with inner Node class.
 *  @param <K> the base type of the keys in the entries
 *  @param <V> the base type of the values
 */
public class BSTMap<K extends Comparable<? super K>, V>
    implements MapJHU<K, V>, Iterable<Map.Entry<K, V>> {

    /** Inner node class.  Do not make this static because you want
        the K to be the same K as in the BSTMap header.
    */
    protected class BNode implements Map.Entry<K, V> {

        /** The key of the entry (null if sentinel node). */
        protected K key;
        /** The value of the entry (null if sentinel node). */
        protected V value;
        /** The left child of this node. */
        protected BNode left;
        /** The right child of this node. */
        protected BNode right;
        /** The height of this node. */
        protected int height; 

        /** Create a new node with a particular key and value.
         *  @param k the key for the new node
         *  @param v the value for the new node
         */
        BNode(K k, V v) {
            this.key = k;
            this.value = v;
            this.left = null;
            this.right = null;
            this.height = 1;
        }

        /** Check whether this node is a leaf sentinel, based on key.
         *  @return true if leaf, false otherwise
         */
        public boolean isLeaf() {
            return this.key == null;  // this is a sentinel-based implementation
        }

        /** set the height of the node.
        * @param h height
        */
        public void setHeight(int h) {
            this.height = h;
        }

        /** 
        * get the height of this node.
        * @return height
        */
        public int getHeight() {
            return this.height;
        }

        /** 
         * Dummy setValue method.
         *  @param val Value
         *  @return null
         */
        public V setValue(V val) {
            return null;
        }

        /** 
         * Dummy setValue method.
         *  @return node's value
         */
        public V getValue() {
            return this.value;
        }

        /** Dummy setValue method.
         *  @return key
         */
        public K getKey() {
            return this.key;
        }

        /** 
        * Dummy setKey method.
        * @param k key
        * @return null
        */
        public K setKey(K k) {
            return null;
        }

        /** 
         * Dummy setValue method.
         *  @param o Some object
         *  @return False
         */
        public boolean equals(Object o) {
            return false;
        }

        /** 
         * Dummy hashCode method.
         *  @return 0
         */
        public int hashCode() {
            return 0;
        }

        /** 
        * find the root of the subtree containing the specified key.
        * @param k the search key 
        * @return node w/ the key, null if not found
        */
        public BNode find(K k) {
            if (k == null || this == null || this.key == null) {
                throw new java.lang.NullPointerException();
            }
            int diff = this.key.compareTo(k);
            if (diff == 0) {
                return this;
            }
            if (diff > 0) {
                if (this.left != null) {
                    return this.left.find(k);
                } else {
                    return null;
                }
            } else {
                if (this.right != null) {
                    return this.right.find(k);
                } else {
                    return null;
                }
            }
        }

        /** 
        * insert key-value in to the tree.
        * @param k the key to insert
        * @param val the value associated w/ the inserted key
        * @return original value, null if not exists
        */
        public V insert(K k, V val) {
            if (k == null || this == null || this.key == null) {
                throw new java.lang.NullPointerException();
            }
            int diff = this.key.compareTo(k); //this.key !=null
            if (diff == 0) {
                V temp = this.value;
                this.value = val; //update value
                //val may be the same as this.value already
                return temp;
            }
            if (diff > 0) {  // look left
                //key not exists in current tree, add new node
                if (this.left == null) { 
                    this.left = new BNode(k, val); 
                    return null;
                } else {
                    this.left.insert(k, val);
                }
            } else { // look right
                //key not exists in current tree, add new node
                if (this.right == null) { 
                    this.right = new BNode(k, val);
                    return null;
                } else {
                    this.right.insert(k, val);
                }
            }
            return null;
        }

        /**
         * Remove the specified key.
         * @param k the key to remove
         * @return BNode as the root (updated tree)
         */
        public BNode remove(K k) {
            //when calling this method, BNode and key != null
            int diff = this.key.compareTo(k); 
            if (diff == 0) {
                if (this.left == null && this.right == null) {
                    return null;
                }
                if (this.left == null && this.right != null) {
                    return this.right;
                }
                if (this.left != null && this.right == null) {
                    return this.left;
                }
                BNode temp = this;
                BNode minNode = this.right;
                while (minNode.left != null) {
                    minNode = minNode.left; // get min from right subtree
                }
                this.key = minNode.getKey();
                this.value = minNode.getValue();            
                this.right = this.right.remove(this.key);
                return this;
            }
            if (diff > 0) {
                this.left = this.left.remove(k);
            } else {
                this.right = this.right.remove(k);
            }
            return this;
        }
        /**
        * String representation of the key and value.
        * @return key and value
        */
        public String toString() {
            String s = "[";
            s += this.key.toString() + ", " + this.value.toString() + "]";
            return s;
        }

    } //end inner class 

    /** The root of this tree. */
    protected BNode root;
    /** The number of entries in this map (== non-sentinel nodes). */
    protected int size;
    /** Check if map status is changed. */
    protected boolean isChanged;

    /** Create an empty tree with a sentinel root node.
     */
    public BSTMap() {
        // empty tree is a sentinel for the root
        this.root = new BNode(null, null); //root is NOT null 
        this.size = 0;
        this.isChanged = false; 
    }

    /** Gets root node of tree.
     *  @return Root node of tree.
     */
    public BNode getRoot() {
        return this.root;
    }

    /** Sets root node of tree.
     *  @param node New root node.
     *  @return Root node of tree.
     */
    public BNode setRoot(BNode node) {
        this.root = node;
        return node;
    }

    /** Checks if tree is changed.
     *  @return isChanged boolean.
     */
    public boolean getIsChanged() {
        return this.isChanged;
    }

    /** Returns size of tree.
     *  @return Size of tree.
     */
    @Override()
    public int size() {
        return this.size;
    }

    /** Clears tree. */
    @Override()
    public void clear() {
        this.root = new BNode(null, null);
        //this.root = null;
        this.size = 0;
        this.isChanged = true;
    }

    /** Checks if tree is empty.
     *  @return Boolean of size == 0.
     */
    @Override()
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /** Checks if given key is in the tree.
     *  @param key Key to find.
     *  @return If key is in tree.
     */
    @Override()
    public boolean hasKey(K key) {
        if (!this.isEmpty()) {
            return this.get(key) != null;
        } else {
            return false;
        }
    }

    /** Checks if a value is in the tree.
     *  @param value Value to find.
     *  @return If value is in tree.
     */
    @Override()
    public boolean hasValue(V value) {
        if (!this.isEmpty()) { //loop through the inOrderedList of values
            Iterator<Map.Entry<K, V>> itr = this.inOrder().iterator();
            while (itr.hasNext()) {
                if (itr.next().getValue().equals(value)) {
                    return true;
                }
            } //end while 
        } //end if-else
        return false;
    }

    /** Checks if value is in subtree.
     *  @param val Value to find.
     *  @param curr Root of subtree to search.
     *  @return Node from the subtree with given value.
     */
    public BNode hasValue(V val, BNode curr) {
        if (!this.isEmpty()) { //loop through the inOrderedList of values
            Iterator<Map.Entry<K, V>> itr = this.inOrder(curr).iterator();
            while (itr.hasNext()) {
                Map.Entry<K, V> temp = itr.next(); //???
                if (temp.getValue().equals(val)) {
                    BNode tempNode = new BNode(temp.getKey(), temp.getValue());
                    return tempNode;
                } 
            } //end while 
        } //end if-else
        return null;
    } 

    /** Find value associated with the given key in tree.
     *  @param key Key to find in tree.
     *  @return Associated value in the tree.
     */
    @Override()
    public V get(K key) {
        return this.get(key, this.root);
    }

    /** Get the value associated with key from subtree with given root node.
     *  @param key the key of the entry
     *  @param curr the root of the subtree from which to get the entry
     *  @return the value associated with the key, or null if not found
     */
    public V get(K key, BNode curr) {
        if (curr == null) {
            throw new java.lang.NullPointerException();
        } else {
            BNode node = curr.find(key);
            if (node == null) { // key not found
                return null;
            } else {
                return node.getValue();
            }
        } 
    }

    /** Put <key,value> entry into tree.
     *  @param key the key of the entry
     *  @param val the value of the entry
     *  @return the original value associated with the key, or null if not found
     */
    @Override()
    public V put(K key, V val) {
        return this.put(key, val, this.root);
    }

    /** Put <key,value> entry into subtree with given root node.
     *  @param key the key of the entry
     *  @param val the value of the entry
     *  @param curr the root of the subtree into which to put the entry
     *  @return the original value associated with the key, or null if not found
     */
    private V put(K key, V val, BNode curr) {
        if (key == null) {
            throw new java.lang.NullPointerException();
        }
        if (curr == null) {
            this.root = new BNode(key, val);
            this.isChanged = true;
            this.size++; //or this.size = 1;
            return null;
        } 
        if (curr.key == null) { 
            //intiial root has null key null value, but root is not null
            this.root.key = key;
            this.root.value = val;
            this.size++;
            this.isChanged = true;
            return null; 
        } else { //curr !=null , curr.key != null
            this.isChanged = true; 
            if (curr.insert(key, val) == null) { 
                this.size++;
                return null;
            } else { //key already exists 
                return curr.insert(key, val);
            }
        }
    }

    /** Remove entry with specified key from tree.
     *  @param key the key of the entry to remove, if there
     *  @return the value associated with the removed key, or null if not found
     */
    @Override()
    public V remove(K key) { 
        return this.remove(key, this.root);
    }

    /** Remove entry with specified key from subtree with given root node.
     *  @param key the key of the entry to remove, if there
     *  @param curr the root of the subtree from which to remove the entry
     *  @return the value associated with the removed key, or null if not found
     */
    private V remove(K key, BNode curr) {
        if (curr == null || curr.key == null || key == null) {
            throw new java.lang.NullPointerException();
        }
        int diff = curr.key.compareTo(key);
        V removedValue = this.get(key, curr);
        if (removedValue != null) { //key-value exists
            curr = curr.remove(key); //returns updated tree
            if (this.root.key.compareTo(key) == 0) {
                this.root = curr;
            }
            this.size--;
            this.isChanged = true;
            return removedValue;
        }
        return null;
    }
    
    /** Returns a set of all entries.
     *  @return HashSet of tree entries.
     */
    @Override()
    public Set<Map.Entry<K, V>> entries() {
        Set<Map.Entry<K, V>> setEntries = new HashSet<Map.Entry<K, V>>(); 
        for (Map.Entry<K, V> entry : this.inOrder()) { 
            setEntries.add(entry); //not ever null??
        } // for
        return setEntries;
    }

    /** Returns a set of all keys.
     *  @return HashSet of tree keys.
     */
    @Override()
    public Set<K> keys() {
        Set<K> setKeys = new HashSet<K>();   
        for (Map.Entry<K, V> entry : this.inOrder()) {
            setKeys.add(entry.getKey()); //never null
        }
        return setKeys;
    }

    /** Returns a set of all values.
     *  @return ArrayList of tree values.
     */
    @Override()
    public Collection<V> values() {
        Collection<V> setValues = new ArrayList<V>(); 
        for (Map.Entry<K, V> entry : this.inOrder()) {
            setValues.add(entry.getValue());
        }
        return setValues;
    }

    /* -----   BSTMap-specific functions   ----- */


    /**
    * Get the smallest key in the tree.
    * @return the first key
    */
    public K firstKey() {
        return this.firstKey(this.root);
    }

    /** Get the smallest key in a subtree.
     *  @param curr the root of the subtree to search
     *  @return the min key
     */
    public K firstKey(BNode curr) {
    // Fill in
        if (curr != null) {
            BNode temp = curr; 
            while (temp.left != null) {
                temp = temp.left;
            }
            return temp.key; 
        }
        return null;
    }

    /**
    * Get the largest key in the tree.
    * @return the max key
    */
    public K lastKey() {
        return this.lastKey(this.root);
    }

    /** Get the largest key in a subtree.
     *  @param curr the root of the subtree to search
     *  @return the max key
     */
    public K lastKey(BNode curr) {
    // Fill in
        if (curr != null) {
            BNode temp = curr; 
            while (temp.right != null) {
                temp = temp.right;
            }
            return temp.key; 
        }
        return null;
    }

    /**
    * Get Balance factor of node n.
    * @param n parent of the left & right child.
    * @return balance factor
    */
    public int getBalance(BNode n) {
        if (n == null || n.key == null) {
            throw new java.lang.NullPointerException();
        } 
        return this.height(n.left) - this.height(n.right);
    }

    /** 
    * Get the height of the tree.
    * @return the height of the tree
    */
    public int height() {
        return this.height(this.root);
    }

    /**
    * Get the height of the tree.
    * @param n the root of the subtree
    * @return the height of the tree
    */
    private int height(BNode n) {
        if (n == null) {
            return 0;
        }
        return n.height;
    }

    /** Inorder traversal that produces an iterator over key-value pairs.
     *  @return an iterable list of entries ordered by keys
     */
    public Iterable<Map.Entry<K, V>> inOrder() {
        return this.inOrder(this.root);
    }
    
    /** Inorder traversal produces an iterator over entries in a subtree.
     *  @param curr the root of the subtree to iterate over
     *  @return an iterable list of entries ordered by keys
     */
    private Collection<Map.Entry<K, V>> inOrder(BNode curr) {
        LinkedList<Map.Entry<K, V>> ordered = new LinkedList<Map.Entry<K, V>>();
        if (curr != null) {
            ordered.addAll(this.inOrder(curr.left));
            //ordered.addLast(curr);
            ordered.add(curr);
            ordered.addAll(this.inOrder(curr.right));
        }
        this.isChanged = false;
        return ordered;
    }

    /** Returns a copy of the portion of this map whose keys are in a range.
     *  @param fromKey the starting key of the range, inclusive if found
     *  @param toKey the ending key of the range, inclusive if found
     *  @return the resulting submap
     */
    public BSTMap<K, V> subMap(K fromKey, K toKey) {
        if (fromKey == null || toKey == null) {
            throw new java.lang.NullPointerException();
        }
        BSTMap<K, V> sMap = new BSTMap<K, V>();
        return this.subMap(this.root, sMap, fromKey, toKey);
    }
    /**
    * create a map whose keys are in a range.
    * @param n the root of the tree
    * @param sMap the map whose keys are in a range
    *  @param fromKey the starting key of the range, inclusive if found
    *  @param toKey the ending key of the range, inclusive if found
    *  @return sMap submap of nodes in key range
    */
    public BSTMap<K, V> subMap(BNode n, BSTMap<K, V> sMap, K fromKey, K toKey) {
        if (n.left == null && n.right == null) {
            return sMap;
        }
        if (n.left == null) {
            this.subMap(n.right, sMap, fromKey, toKey);
        }
        if (n.right == null) {
            this.subMap(n.left, sMap, fromKey, toKey);
        }
        int lower = n.key.compareTo(fromKey);
        int upper = n.key.compareTo(toKey);
        if (lower < 0) { 
            this.subMap(n.right, sMap, fromKey, toKey);
        }
        if (lower >= 0  && upper <= 0) {
            sMap.put(n.key, n.value);
        }
        if (upper > 0) {
            this.subMap(n.left, sMap, fromKey, toKey);
        }
        return sMap;
    }

    /* ---------- from Iterable ---------- */

    /** Iterator constructor for the BSTMap.
     *  @return the iterator
     */
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new BSTMapIterator();
    }

    /** Not implemented. */
    @Override
    public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        // you do not have to implement this
    }

    /** Not implemented.
     *  @return null
     */
    @Override
    public Spliterator<Map.Entry<K, V>> spliterator() {
        // you do not have to implement this
        return null;
    }

    /* -----  insert the BSTMapIterator inner class here ----- */

    /** Inner class of BSTMap. 
    */
    private class BSTMapIterator implements Iterator<Map.Entry<K, V>> {
        /** Initialize iterator variable. */
        private Iterator<Map.Entry<K, V>> inOrderIter; 
        /** Counter for iterator. */
        //private int current; 
        
        /** Constructor. */
        BSTMapIterator() {
            //this.current = 0;
            this.inOrderIter = BSTMap.this.inOrder().iterator();
        }
       /**
       * Check if there's still elements left to traverse. 
       * @return false if at the end of the underlying array
       */
        public boolean hasNext() {
            if (BSTMap.this.isChanged) {
                throw new java.util.ConcurrentModificationException();
            }
            //return (this.current < BSTMap.this.size());
            return this.inOrderIter.hasNext();
        } 
    
        /** Remove function for BSTMap iterator. */
        public void remove() {
            // optional to implement
        }
    
        /** Next function for BSTMap iterator.
         *  @return Next map entry in the map.
         */
        public Map.Entry<K, V> next() { //or BNode ??
            if (BSTMap.this.isChanged) {
                throw new java.util.ConcurrentModificationException();
            }
            return this.inOrderIter.next();
            //if (this.hasNext()) {
            //    this.current++;
            //    return this.inOrderIter.next();
            //}
            //return null;
        }
    }



}
