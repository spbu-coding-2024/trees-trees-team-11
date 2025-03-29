package libTree.trees

import libTree.interfaceTree.Tree
import kotlin.math.max

/*
 * Implementation of an Red Black Tree, a self-balancing binary search tree.
 *
 * @param K the type of keys maintained by this tree, must be Comparable
 * @param V the type of mapped values
 *

 * Invariants of red black tree:
 *   1) each node is red or black
 *   2) the root and leaves are black
 *   3) each red node has a black parent
 *   4) all paths from a node contain the same number of black nodes
 *   5) a black node can have a black parent
 */

class RBTree<K : Comparable<K>,V> private constructor(
    private var root : RBNode<K,V>? = null,
) : Tree<K,V, RBTree.RBNode<K, V>> {

   
     /**
     * Node class representing each node in the Red Black Tree.
     *
     * @param K the type of keys
     * @param V the type of values
     * @property left - Left child node
     * @property right - Right child node
     * @property height Height of the node in the tree
     * @property parent - Parent of the node
     */

    enum class Color {
        RED,
        BLACK,
    }
    class RBNode<K : Comparable<K>, V> internal constructor (
        key: K,
        value: V,
        override var left: RBNode<K, V>? = null,
        override var right: RBNode<K, V>? = null,
        override var height: Long = 1,
        var color: Color = Color.RED,
        internal var parent: RBNode<K, V>? = null
    ) : BaseNode<K, V, RBNode<K, V>>(key, value, left, right, height)

     /**
     * Secondary constructor initializing an empty Red Black Tree Tree.
     */
    constructor() : this(null)

    /**
     * Calculates the height of the Red Black Tree.
     *
     * @return Height of the tree
     */
    override fun height(): Int {
        return height(root)
    } 

    /*
     * Checks whether the RBTree contains a node with the given key.
     *
     * @param key Key to search for
     * @return True if the tree contains the key, false otherwise
     */
    override fun containsKey(key: K): Boolean {
        return findNode(key) != null
    }
    
     /**
     * Inserts a node into the RBTree with the given key and value.
     *
     * @param key Key for the node
     * @param value Value for the node
     */
    override fun insert(key: K, value: V) {
        val newNode = RBNode(key, value)
        var y: RBNode<K, V>? = null
        var x = root

        while (x != null) { // Looking for a place to insert a new node
            y = x
            if (newNode.key < x.key) {
                x = x.left 
            } else {
                x = x.right 
            }
        }

        newNode.parent = y
        when {
            y == null -> root = newNode // There were no nodes in the tree
            newNode.key < y.key -> y.left = newNode
            else -> y.right = newNode
        }
        fixInsertion(newNode) // Fix colors after insertion
    }

    /*
     * Delets a node into the RBTree with the given key.
     *
     * @param key Key for the node
     */
    override fun erase(key: K) {
        val z = findNode(key) ?: return
        var y = z
        var yOriginalColor = y.color
        var x : RBNode<K,V>? = null

        if (z.left == null) {
            x = z.right 
            transplant(z, z.right )
        } else if (z.right == null) {
            x = z.left 
            transplant(z, z.left )
        } else {
            y = minimum(z.right as RBNode<K,V>)
            yOriginalColor = y.color
            x = y.right 
            if (y.parent == z) {
                if (x != null) {
                    x.parent = y
                }
            } else {
                transplant(y, y.right )
                y.right = z.right
                (y.right )?.parent = y
            }
            transplant(z, y)
            y.left = z.left
            (y.left )?.parent = y
        }
        if (yOriginalColor == Color.BLACK && x != null) {
            fixDeletion(x)
        }
    }

    // --- Private helper methods for RBTree operations ---

    /*
     * Search element by key (for containsKey)
     * 
     * @param key Key to search for
     * @return Node if the tree contains the key, null otherwise
     */
    private fun findNode(key: K): RBNode<K, V>? {
        var current = root
        while (current != null) {
            current = when {
                key < current.key -> current.left 
                key > current.key -> current.right 
                else -> return current
            }
        }
        return null
    }

    /* 
     * Tree balancing after inserting an element
     * 
     * @param current - Node of the RBTree
     */
    private fun fixInsertion(current: RBNode<K, V>) {
        var node = current
        while (node.parent != null && node.parent!!.color == Color.RED) { // Property 3 is violated
            //  using !! is okay, because we have already checked != null
            val parent = node.parent
            val grandparent = parent?.parent ?: break
            if (parent == grandparent.left) { // If the node's parent is on the left
                // From the grandfather of the node
                val uncle = grandparent.right
                if (uncle != null && uncle.color == Color.RED) { // Uncle red => change uncle and parent of node
                    // Colors to black, and grandfather to red
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandparent.color = Color.RED
                    node = grandparent  // Maybe broke invariants => cycle from node's grandfather
                } else {  // Uncle black
                    if (node == parent.right) { // Node right descendant => make left
                        node = parent
                        rotateLeft(node)
                    }
                    parent.color = Color.BLACK
                    grandparent.color = Color.RED
                    rotateRight(grandparent)
                }
            } else { // If the node's parent is to the right of the node's grandparent,
            // do everything similarly
                val uncle = grandparent.left
                if (uncle != null && uncle.color == Color.RED) {
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandparent.color = Color.RED
                    node = grandparent
                } else {
                    if (node == parent.left) {
                        node = parent
                        rotateRight(node)
                    }
                    parent.color = Color.BLACK
                    grandparent.color = Color.RED
                    rotateLeft(grandparent)
                }
            }
        }
        root?.color = Color.BLACK // Make the root black
    }
    
    /* 
     *   Replacing a subtree whose root is u with a subtree v (For erase)
     * 
     *   @param u - Node of the RBTree
     *   @param v - Node of the RBTree
    */
    private fun transplant(u: RBNode<K, V>, v: RBNode<K, V>?) {
        if (u.parent == null) {
            root = v
        } else if (u == u.parent?.left) {
            u.parent?.left = v
        } else {
            u.parent?.right = v
        }
        if (v != null) {
            v.parent = u.parent
        }
    }

    /**
     * Finds the node with the minimum key starting from a given node.
     */
    private fun minimum(node : RBNode<K, V>) : RBNode<K, V> {
        var current = node
        while (current.left != null) {
            current = current.left as RBNode<K,V>
        }
        return current
    }

    /*
     * Performs a left rotation around a given node.
     */
    private fun rotateLeft(x: RBNode<K, V>) {
        val y = x.right  ?: return
        x.right = y.left
        if (y.left != null) {
            (y.left as RBNode<K, V>).parent = x
        }
        y.parent = x.parent
        if (x.parent == null) { // Nothing to turn
            root = y
        } else if (x == x.parent?.left) { // Node glory from parent
            x.parent?.left = y
        } else {
            x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }

    /*
     * Performs a right rotation around a given node.
     */
    private fun rotateRight(x: RBNode<K, V>) {
        val y = x.left  ?: return
        x.left = y.right
        if (y.right != null) {
            (y.right as RBNode<K, V>).parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x == x.parent?.right) {
            x.parent?.right = y
        } else {
            x.parent?.left = y
        }
        y.right = x
        x.parent = y
    }

    /* 
     * Tree balancing after deletion an element
     * 
     * @param current - Node of the RBTree
     */
    private fun fixDeletion(x: RBNode<K, V>) {
        var node = x
        while (node != root && node.color == Color.BLACK) {
            val parent = node.parent ?: break
            if (node == parent.left) {
                var w = parent.right
                if (w != null && w.color == Color.RED) {
                    // Case 1 brother of the node is red
                    w.color = Color.BLACK
                    parent.color = Color.RED
                    rotateLeft(parent)
                    w = parent.right
                }
                if (w == null || ((w.left?.color ?: Color.BLACK) == Color.BLACK &&
                                  (w.right?.color ?: Color.BLACK) == Color.BLACK)) {
                     // Case 2 brother is black, both his descendants are black               
                    w?.color = Color.RED
                    node = parent
                } else {
                     // Case 3 brother black, left descendant red, right black
                    if ((w.right?.color ?: Color.BLACK) == Color.BLACK) {
                        w.left?.color = Color.BLACK
                        w.color = Color.RED
                        rotateRight(w)
                        w = parent.right
                    }
                    // Case 4 brother black, right descendant red
                    w?.color = parent.color
                    parent.color = Color.BLACK
                    w?.right?.color = Color.BLACK
                    rotateLeft(parent)
                    node = root ?: break
                }
            } else {
                var w = parent.left
                // Symmetrical cases
                if (w != null && w.color == Color.RED) {
                    w.color = Color.BLACK
                    parent.color = Color.RED
                    rotateRight(parent)
                    w = parent.left
                }
                if (w == null || ((w.left?.color ?: Color.BLACK) == Color.BLACK &&
                                  (w.right?.color ?: Color.BLACK) == Color.BLACK)) {
                    w?.color = Color.RED
                    node = parent
                } else {
                    if ((w.left?.color ?: Color.BLACK) == Color.BLACK) {
                        w.right?.color = Color.BLACK
                        w.color = Color.RED
                        rotateLeft(w)
                        w = parent.left
                    }
                    
                    w?.color = parent.color
                    parent.color = Color.BLACK
                    w?.left?.color = Color.BLACK
                    rotateRight(parent)
                    node = root ?: break
                }
            }
        }
        node.color = Color.BLACK
    }
    
    /*
     *  Recursive height search
     *
     *  @return Height of the tree
    */
    private fun height(node: RBNode<K, V>?): Int {

        if (node == null) {
            return 0
        } else {
            return (1 + max(height(node.left), height(node.right)))
        }
    }

    /*
     *  Cleaning of a tree
     */
    override fun clean() {
        root = null
    }
    
    /*
     * Returns an iterator to traverse the RBTree.
     *
     * @return Iterator of key-value pairs
     */
    override fun iterator(): Iterator<Pair<K, V>> {
        val list = mutableListOf<Pair<K, V>>()
        fun Order(node: RBNode<K, V>?) {
            if (node == null) return
            list.add(Pair(node.key, node.value))
            Order(node.left)
            Order(node.right)
        }
        Order(root)
        return list.iterator()
    }
}


