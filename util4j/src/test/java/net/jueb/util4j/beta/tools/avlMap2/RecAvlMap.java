package net.jueb.util4j.beta.tools.avlMap2;

//AVLMap.java
//Yu-chi Chang, Allan Wang
//ychang64, awang53
//EN.600.226.01/02
//P3

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;



/**
 *  AVL Map class.
 *  @param <K> key
 *  @param <V> value
 */
public class RecAvlMap<K extends Comparable<? super K>, V> extends BSTMap<K, V>  {

    /*
    private class AvlNode extends BNode {
        private int height = 0; 
<<<<<<< HEAD
=======

>>>>>>> 4c09e0deeaf31ccbd48d08744b25a1ddf37dc9af
        AvlNode(K k, V v) {
            super(k, v);
            this.height = 1;
        }
        public void setHeight(int h) {
            this.height = h;
        }
        public int getHeight() {
            return this.height;
        }        
<<<<<<< HEAD
=======

>>>>>>> 4c09e0deeaf31ccbd48d08744b25a1ddf37dc9af
    } //end inner class 
*/
    /** AvlTree variable. */
    private BSTMap<K, V> avlTree;

    /** AvlTree constructor. */
    public RecAvlMap() {
        this.root = new BNode(null, null);
        this.size = 0;
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
        return n.height;
    }

    /**
    * Get maximum of of two integers. 
    * @param a first integer argument
    * @param b second integer argument
    * @return maximum of two integers
    */
    public int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    /**
    * Get balance factor of node n.
    * @param n given node
    * @return balance of node
    */
    public int getBalance(BNode n) {
        return this.height(n.left) - this.height(n.right);
    }

    /**
    * Perform right rotation.
    * @param r the root of the subtree
    * @return rotated node
    */ 
    // y <--> r;   x <--> n;
    public BNode rightRotate(BNode r) {
        // Save children
        BNode n = r.left;
        BNode temp = n.right;
 
        // Perform rotation
        n.right = r;
        r.left = temp;
 
        // Update heights
        r.height = this.max(this.height(r.left), this.height(r.right)) + 1;
        n.height = this.max(this.height(n.left), this.height(n.right)) + 1;
 
        // Return new root
        return n;
    }

    /**
    * Perform left rotation.
    * @param r the root of the subtree
    * @return rotated node
    */ 
    // x <--> r;   y <--> n;
    public BNode leftRotate(BNode r) {
        //Save children
        BNode n = r.right;
        BNode temp = n.left;
 
        // Perform rotation
        n.left = r;
        r.right = temp;
 
        //  Update heights
        r.height = this.max(this.height(r.left), this.height(r.right)) + 1;
        n.height = this.max(this.height(n.left), this.height(n.right)) + 1;
 
        // Return new root
        return n;
    }

    /**
    * Insert into the tree; duplicates are ignored.
    * @param n the node to insert.
    * @return value of inserted node
    */
    public V insert(BNode n) {
        return this.insert(n.key, n.value, this.root);
    }

    public V insert(K key, V val) {
        return this.insert(key, val, this.root);
    }


    /**
    * Recursively insert into the subtree.
    * @param key the key to insert.
    * @param val the value to insert
    * @param curr the root of the subtree
    * @return inserted node
    */
    private BNode recInsert(K key, V val, BNode curr) {
        BNode n = new BNode(key, val);
        if (key == null) {
            throw new NullPointerException();
        }
        if (curr == null) {
            curr = new BNode(key, val);
        } else if (curr.key == null) {
            this.root = new BNode(key, val);
            this.size++;
            return null;
        } else if (key.compareTo(curr.left.key) < 0) {
            curr.left = this.recInsert(key, val, curr.left);
            if (this.getBalance(curr) == 2) {
                if (key.compareTo(curr.left.key) < 0) {
                    curr = this.rightRotate(curr);
                } else {
                    curr.left = this.leftRotate(curr.left);
                    curr = this.rightRotate(curr);
                }
                curr.height = this.max(this.height(curr.left), this.height(curr.right)) + 1;
            }
        } else if (key.compareTo(curr.left.key) > 0) {
            curr.right = this.recInsert(key, val, curr.right);
            if (this.getBalance(curr) == -2) {
                if (key.compareTo(curr.right.key) > 0) {
                    curr = this.leftRotate(curr);
                } else {
                    curr.right = this.rightRotate(curr.right);
                    curr = this.leftRotate(curr);
                }
                curr.height = this.max(this.height(curr.left), this.height(curr.right)) + 1;
            }
        }
        this.size++;
        return curr;
    }

    /**
    * Insert into the subtree.
    * @param key the key to insert.
    * @param val the value to insert
    * @param curr the root of the subtree
    * @return value of inserted node
    */
    public V insert(K key, V val, BNode curr) {
        return this.recInsert(key, val, curr).value;
    }

    /**
    * Delete node from the subtree.
    * @param key the key to delete
    * @param curr the root of the subtree
    * @return deleted node
    */
    private BNode recDelete(K key, BNode curr) {
        if (curr == null || key == null) {
            throw new NullPointerException();
        }
        if (key.compareTo(curr.key) < 0) {
            curr.left = this.recDelete(key, curr.left);
        } else if (key.compareTo(curr.key) > 0) {
            curr.right = this.recDelete(key, curr.right);
        } else {
            // node with only one child or no child
            if ((curr.left == null) || (curr.right == null)) {
                BNode temp = null;
                if (temp == curr.left) {
                    temp = curr.right;
                } else {
                    temp = curr.left;
                }
                // No child case
                if (temp == null) {
                    temp = root;
                    root = null;
                } else { // One child case
                    curr = temp; // Copy non-empty child
                }
            } else { //Two children case
                BNode temp = curr.right; 
                while (temp.left != null) {
                    temp = temp.left;
                }
                // Copy the inorder successor's data to this node
                curr.key = temp.key;
                curr.value = temp.value;
                // Delete the inorder successor
                curr.right = this.recDelete(temp.key, curr.right);
            }
        }
        // If the tree had only one node then return
        if (curr == null) {
            return curr;
        }
        // Update height
        root.height = this.max(this.height(curr.left), this.height(curr.right)) + 1;
        // Get balance factor
        int balance = this.getBalance(curr);
        // If this node becomes unbalanced, then there are 4 cases
        // Left Left Case
        if (balance > 1 && this.getBalance(curr.left) >= 0) {
            return this.rightRotate(curr);
        }
        // Left Right Case
        if (balance > 1 && this.getBalance(curr.left) < 0) {
            curr.left = this.leftRotate(curr.left);
            return this.rightRotate(curr);
        }
        // Right Right Case
        if (balance < -1 && this.getBalance(curr.right) <= 0) {
            return this.leftRotate(curr);
        }
        // Right Left Case
        if (balance < -1 && this.getBalance(curr.right) > 0) {
            curr.right = this.rightRotate(curr.right);
            return this.leftRotate(curr);
        }
        return curr;
    }

    /**
    * Delete node from the subtree.
    * @param key the key to delete
    * @param curr the root of the subtree
    * @return value of deleted node
    */
    public V delete(K key, BNode curr) {
        return this.recDelete(key, curr).value;
    }

    public Collection<K> preOrder() {
        return this.preOrder(this.root);
    }

    public Collection<K> preOrder(BNode curr) {
        LinkedList<K> set = new LinkedList<K>(); 
        if (curr == null) {
        } else {
            set.addLast(curr.key);
            set.addAll(this.preOrder(curr.left));
            set.addAll(this.preOrder(curr.right));
        }
        return set;
    }

    public Collection<K> postOrder() {
        return this.postOrder(this.root);
    }


    public Collection<K> postOrder(BNode curr) {
        LinkedList<K> set = new LinkedList<K>(); 
        if (curr == null) {
        } else {
            set.addAll(this.postOrder(curr.left));
            set.addAll(this.postOrder(curr.right));
            set.addLast(curr.key);
        }
        return set;
    }
}
