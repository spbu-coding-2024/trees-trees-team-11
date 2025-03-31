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
        internal var color: Color = Color.RED,
        internal var parent: RBNode<K, V>? = null
    ) : BaseNode<K, V, RBNode<K, V>>(key, value, left, right, height)

    /**
     * Secondary constructor initializing an empty Red Black Tree Tree.
     */
    constructor() : this(null)

    /**
     * Returns the root node of the RB tree.
     *
     * @return The root node
     */
    fun getRoot() : RBNode<K, V>? = root

    /**
     * Sets the root node of the RB tree.
     *
     * @param node The new root node
     */
    fun setRoot(node: RBNode<K,V>?) {
        root = node
    }

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
        var parent: RBNode<K, V>? = null
        var current = root

        // Standard BST insert
        while (current != null) {
            parent = current
            current = if (newNode.key < current.key) {
                current.left
            } else {
                current.right
            }
        }

        newNode.parent = parent
        if (parent == null) {
            // Tree was empty, new node is root
            root = newNode
        } else if (newNode.key < parent.key) {
            parent.left = newNode
        } else {
            parent.right = newNode
        }

        // Fix any red-black violations
        fixInsertion(newNode)
    }

    /*
     * Delets a node into the RBTree with the given key.
     *
     * @param key Key for the node
     */
    override fun erase(key: K) {
        val z = findNode(key) ?: return
        var y = z
        val originalColor = y.color
        var x: RBNode<K, V>? = null

        when {
            z.left == null -> {
                x = z.right
                transplant(z, z.right)
            }
            z.right == null -> {
                x = z.left
                transplant(z, z.left)
            }
            else -> {
                y = minimum(z.right!!)
                val yOriginalColor = y.color
                x = y.right
                if (y.parent == z) {
                    x?.parent = y
                } else {
                    transplant(y, y.right)
                    y.right = z.right
                    y.right?.parent = y
                }
                transplant(z, y)
                y.left = z.left
                y.left?.parent = y
                y.color = z.color
                // now 'y' is the position in the tree where 'z' was
                if (yOriginalColor == Color.BLACK) {
                    if (x != null) {
                        fixDeletion(x)
                    } else {
                        val parent = y.parent ?: return
                        val missingLeft = (parent.left == null && parent.right != null)
                            .also { /* guess if the missing side is left */ }
                        fixDeletionWhenNull(parent, missingLeft)
                    }
                }
                return
            }
        }

        // if we removed a black node, we need to re-balance
        if (originalColor == Color.BLACK) {
            if (x != null) {
                fixDeletion(x)
            } else {
                // If x is null, use the fix for null children
                val parent = z.parent ?: return
                val isMissingLeft = (parent.left == null && parent.right != null)
                fixDeletionWhenNull(parent, isMissingLeft)
            }
        }
    }

    // --- Private helper methods for RBTree operations ---

    private fun fixDeletionWhenNull(parent: RBNode<K, V>, missingIsLeft: Boolean) {
        var currentParent = parent
        var isLeft = missingIsLeft

        while (currentParent != root) {
            if (isLeft) {
                var w = currentParent.right
                // Case 1: brother is red
                if (w?.color == Color.RED) {
                    w.color = Color.BLACK
                    currentParent.color = Color.RED
                    rotateLeft(currentParent)
                    w = currentParent.right
                }
                val wLeftColor = w?.left?.color ?: Color.BLACK
                val wRightColor = w?.right?.color ?: Color.BLACK
                // Case 2: both children black
                if (w == null || (wLeftColor == Color.BLACK && wRightColor == Color.BLACK)) {
                    w?.color = Color.RED
                    val gp = currentParent.parent ?: break
                    isLeft = (gp.left == currentParent)
                    currentParent = gp
                } else {
                    // Case 3: left child red, right child black
                    if ((w?.right?.color ?: Color.BLACK) == Color.BLACK) {
                        w?.left?.color = Color.BLACK
                        w?.color = Color.RED
                        w?.let { rotateRight(it) }
                        w = currentParent.right
                    }
                    // Case 4: right child red
                    w?.color = currentParent.color
                    currentParent.color = Color.BLACK
                    w?.right?.color = Color.BLACK
                    rotateLeft(currentParent)
                    break
                }
            } else {
                // Mirror for right‐missing
                var w = currentParent.left
                if (w?.color == Color.RED) {
                    w.color = Color.BLACK
                    currentParent.color = Color.RED
                    rotateRight(currentParent)
                    w = currentParent.left
                }
                val wLeftColor = w?.left?.color ?: Color.BLACK
                val wRightColor = w?.right?.color ?: Color.BLACK
                if (w == null || (wLeftColor == Color.BLACK && wRightColor == Color.BLACK)) {
                    w?.color = Color.RED
                    val gp = currentParent.parent ?: break
                    isLeft = (gp.left == currentParent)
                    currentParent = gp
                } else {
                    if ((w?.left?.color ?: Color.BLACK) == Color.BLACK) {
                        w?.right?.color = Color.BLACK
                        w?.color = Color.RED
                        w?.let { rotateLeft(it) }
                        w = currentParent.left
                    }
                    w?.color = currentParent.color
                    currentParent.color = Color.BLACK
                    w?.left?.color = Color.BLACK
                    rotateRight(currentParent)
                    break
                }
            }
        }
    }

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
    private fun fixInsertion(node: RBNode<K, V>) {
        var current = node
        // While the parent is red, we have to fix violations
        while (current.parent?.color == Color.RED) {
            val parent = current.parent ?: break
            val grandparent = parent.parent ?: break

            if (parent == grandparent.left) {
                // Uncle is on the right
                val uncle = grandparent.right
                if (uncle?.color == Color.RED) {
                    // Case 1: uncle is red
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandparent.color = Color.RED
                    current = grandparent
                } else {
                    // Case 2 or 3: uncle is black
                    if (current == parent.right) {
                        // current is right child => left rotate parent
                        current = parent
                        rotateLeft(current)
                    }
                    // Recolor and rotate grandparent
                    current.parent?.color = Color.BLACK
                    current.parent?.parent?.color = Color.RED
                    current.parent?.parent?.let { rotateRight(it) }
                }
            } else {
                // Parent is a right child, do the mirror
                val uncle = grandparent.left
                if (uncle?.color == Color.RED) {
                    // Case 1: uncle is red
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandparent.color = Color.RED
                    current = grandparent
                } else {
                    // Case 2 or 3: uncle is black
                    if (current == parent.left) {
                        current = parent
                        rotateRight(current)
                    }
                    current.parent?.color = Color.BLACK
                    current.parent?.parent?.color = Color.RED
                    current.parent?.parent?.let { rotateLeft(it) }
                }
            }
        }
        root?.color = Color.BLACK
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
                // Case 1: brother is red
                if (w?.color == Color.RED) {
                    w.color = Color.BLACK
                    parent.color = Color.RED
                    rotateLeft(parent)
                    w = parent.right
                }
                // Case 2: brother is black, both children black
                if (w == null ||
                    (w.left?.color ?: Color.BLACK) == Color.BLACK &&
                    (w.right?.color ?: Color.BLACK) == Color.BLACK
                ) {
                    w?.color = Color.RED
                    node = parent
                } else {
                    // Case 3: brother black, left child red, right child black
                    if ((w.right?.color ?: Color.BLACK) == Color.BLACK) {
                        w.left?.color = Color.BLACK
                        w.color = Color.RED
                        rotateRight(w)
                        w = parent.right
                    }
                    // Case 4: brother black, right child red
                    w?.color = parent.color
                    parent.color = Color.BLACK
                    w?.right?.color = Color.BLACK
                    rotateLeft(parent)
                    node = root ?: break
                }
            } else {
                // Mirror cases
                var w = parent.left
                if (w?.color == Color.RED) {
                    w.color = Color.BLACK
                    parent.color = Color.RED
                    rotateRight(parent)
                    w = parent.left
                }
                if (w == null ||
                    (w.left?.color ?: Color.BLACK) == Color.BLACK &&
                    (w.right?.color ?: Color.BLACK) == Color.BLACK
                ) {
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