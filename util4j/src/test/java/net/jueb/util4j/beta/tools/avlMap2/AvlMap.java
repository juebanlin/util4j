package net.jueb.util4j.beta.tools.avlMap2;

//BSTMap.java
//Yu-chi Chang, Allan Wang
//ychang64, awang53
//EN.600.226.01/02
//P3.partB


import java.util.Collection;
import java.util.LinkedList;

/** AVL Map class.
 * @param <K> key
 * @param <V> value
 */
public class AvlMap<K extends Comparable<? super K>, V> extends BSTMap<K, V>  {

    /** Negative 2. */
    private final int n2 = -2;

    /** AvlTree instance. */
    private BSTMap<K, V> avlTree;

    /** AvlTree constructor. */
    public AvlMap() {
        this.avlTree = new BSTMap<K, V>();
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

    /**
     * Get maxium of of two integers. 
     * @param a first integer
     * @param b second integer
     * @return maximum integer
     */
    public int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Get Balance factor of node n.
     * @param n given node
     * @return balance factor
     */
    public int getBalance(BNode n) {
        return this.height(n.left) - this.height(n.right);
    }

    /**
     * Insert into the tree; duplicates are ignored.
     * @param key the key to insert.
     * @param val the value to insert
     * @return true if successful, false if key is null
     */
    public boolean add(K key, V val) {
        if (key != null) {
            this.root = this.insert(key, val, this.root);
            return true;
        }
        return false;
    }

    /**
     * Insert into the tree; duplicates are ignored.
     * If key already exists, update the value of the key.
     * @param key the key to insert
     * @param val the vale to insert
     * @param curr subtree to insert into
     * @return true if successful, false if key is null
     */
    private boolean add(K key, V val, BNode curr) {
        if (key != null) {
            this.root = this.insert(key, val, curr);
            return true;
        }
        return false;
    }

    /**
     * Helper method for add.
     * If key already exists, update the value of the key.
     * @param key the key to insert
     * @param val the vale to insert
     * @param curr root of the subtree to insert into
     * @return inserted node
     */
    private BNode insert(K key, V val, BNode curr) {
        BNode temp = curr;
        if (temp == null) {
            temp = new BNode(key, val);
            this.size++;
            this.isChanged = true;
            //this.state++;
            return temp;
        } else if (temp.key == null) { 
            this.root.key = key;
            this.root.value = val;
            this.size++;
            this.isChanged = true;
            //this.state++; 
            return temp; 
        } else if (key.compareTo(temp.key) < 0) {
            temp.left = this.insert(key, val, temp.left);
            temp = this.balance(temp);
        } else if (key.compareTo(temp.key) > 0) {
            temp.right = this.insert(key, val, temp.right);
            temp = this.balance(temp);
        } else { //update curr's value
            curr.value = val;
            //this.state++; 
            this.isChanged = true; 
        }
        return temp;
    }


    /** Put <key,value> entry into tree.
     *  @param key the key of the entry
     *  @param val the value of the entry
     *  @return the original value associated with the key, or null if not found
     */
    @Override()
    public V put(K key, V val) {
        V old = null;
        if (this.hasKey(key)) {
            old = this.get(key);
        }
        this.root = this.insert(key, val, this.root);
        return old;
    }

    /**
     * AVL balancing.
     * @param curr node to balance
     * @return balanced tree
     */
    private BNode balance(BNode curr) {
        BNode temp = curr;
        int bf = this.getBalance(temp);
        if (bf == 2) {
            int subLeft = this.getBalance(temp.left);
            if (subLeft > 0) {
                temp = this.rotateRight(temp);
            } else {
                temp = this.rotateLeftRight(temp);
            }
        } else if (bf == this.n2) {
            int subRight = this.getBalance(temp.right);
            if (subRight < 0) {
                temp = this.rotateLeft(temp);
            } else {
                temp = this.rotateRightLeft(temp);
            }
        }
        temp.height = this.max(this.height(temp.left),
            this.height(temp.right)) + 1;
        return temp;
    }


    /**
     * Remove a key-value from the tree.
     * @param key the key to remove
     * @return true if removed, false if not found
     */
    public boolean delete(K key) {
        if (this.hasKey(key)) {
            this.root = this.delete(this.root, key);
            this.size--;
            ////this.state++;
            this.isChanged = true;
            return true;
        }
        return false;
    }

    /**
     * Helper method for delete.
     * @param key the value to delete
     * @param curr the root of the subtree to look in
     * @return the new subtree after rebalancing
     */
    private BNode delete(BNode curr, K key) {
        BNode temp = curr;
        if (temp == null) {
            return temp;
        }
        if (key.compareTo(temp.key) < 0) {
            temp.left = this.delete(temp.left, key);
        } else if (key.compareTo(temp.key) > 0) {  // val >= temp
            temp.right = this.delete(temp.right, key);
        } else { //key == temp.key
            if (temp.left == null && temp.right == null) {
                temp = null;
                return temp;
            } else if (temp.right != null && temp.left != null) {
                K tempKey = this.findMin(temp.right).key;
                V tempVal = this.findMin(temp.right).value;
                temp.key = tempKey;
                temp.value = tempVal;
                temp.right = this.delete(temp.right, temp.key);
            } else if (temp.right == null) {
                temp = temp.left;
            } else if (temp.left == null) {
                temp = temp.right;
            }
        }
        temp = this.balance(temp);
        return temp;
    }


    /** Remove entry with specified key from tree.
     *  @param key the key of the entry to remove, if there
     *  @return the value associated with the removed key, or null if not found
     */
    @Override()
    public V remove(K key) {
        V old = null;
        if (this.hasKey(key)) {
            old = get(key);
            this.root = this.delete(this.root, key);
            this.size--;
            //this.state++;
            this.isChanged = true;
        }
        return old;
    }

    /**
     * Find smallest key in the subtree with curr root.
     * @param curr the root of the tree
     * @return the minimum node
     */
    private BNode findMin(BNode curr) {
        BNode temp = curr;
        if (temp == null) {
            return temp;
        }
        while (temp.left != null) {
            temp = temp.left;
        }
        return temp;
    }

    /**
     * Return a pre-ordered list of keys.
     * @return preorder list
     */
    public Collection<K> preOrder() {
        return this.preOrder(this.root);
    }

    /**
     * Return a pre-ordered list of keys.
     * @param curr the root of the tree
     * @return preorder list
     */
    public Collection<K> preOrder(BNode curr) {
        LinkedList<K> set = new LinkedList<K>(); 
        if (curr != null) {
            set.addFirst(curr.key);
            set.addAll(this.preOrder(curr.left));
            set.addAll(this.preOrder(curr.right));
        }
        return set;
    }

    /**
     * Return a post-ordered list of keys.
     * @return postorder list
     */
    public Collection<K> postOrder() {
        return this.postOrder(this.root);
    }

    /**
     * Return a post-ordered list of keys.
     * @param curr the root of the tree
     * @return postorder list
     */
    public Collection<K> postOrder(BNode curr) {
        LinkedList<K> set = new LinkedList<K>(); 
        if (curr != null) {
            set.addAll(this.postOrder(curr.left));
            set.addAll(this.postOrder(curr.right));
            set.addLast(curr.key);
        }
        return set;
    }

    /**
     * Rotate binary tree node with left child.
     * @param k2 node to rotate
     * @return rotated node
     */
    private BNode rotateRight(BNode k2) {
        BNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = this.max(this.height(k2.left), this.height(k2.right)) + 1;
        k1.height = this.max(this.height(k1.left), k2.height) + 1;
        return k1;
    }

    /**
     * Rotate binary tree node with right child.
     * @param k1 node to rotate
     * @return rotated node
     */
    private BNode rotateLeft(BNode k1) {
        BNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = this.max(this.height(k1.left), this.height(k1.right)) + 1;
        k2.height = this.max(this.height(k2.right), k1.height) + 1;
        return k2;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * @param k3 node to rotate
     * @return rotated node
     */
    private BNode rotateLeftRight(BNode k3) {
        k3.left = this.rotateLeft(k3.left);
        return this.rotateRight(k3);
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * @param k1 node to rotate
     * @return rotated node
     */
    private BNode rotateRightLeft(BNode k1) {
        k1.right = this.rotateRight(k1.right);
        return this.rotateLeft(k1);
    }
}