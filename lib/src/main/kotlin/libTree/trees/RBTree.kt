package libTree.trees

import libTree.interfaceTree.Tree
import kotlin.math.max

/**
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

    /**
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
        // Standard BST insert
        val newNode = RBNode(key, value)
        var parent: RBNode<K, V>? = null
        var current = root

        while (current != null) {
            parent = current
            current = if (key < current.key) {
                current.left
            } else {
                current.right
            }
        }

        newNode.parent = parent
        if (parent == null) {
            // The tree was empty
            root = newNode
        } else if (key < parent.key) {
            parent.left = newNode
        } else {
            parent.right = newNode
        }

        // Fix any violations
        fixInsertion(newNode)
    }

    /**
     * Delets a node into the RBTree with the given key.
     *
     * @param key Key for the node
     */
    override fun erase(key: K) {
        val z = findNode(key) ?: return  // Key not found, do nothing
        var y = z
        val yOriginalColor = y.color
        var x: RBNode<K, V>? = null

        when {
            // Node z has no left child
            z.left == null -> {
                x = z.right
                transplant(z, z.right)
            }
            // Node z has no right child
            z.right == null -> {
                x = z.left
                transplant(z, z.left)
            }
            // Node z has two children
            else -> {
                y = minimum(z.right!!)
                val tmpColor = y.color
                x = y.right
                if (y.parent == z) {
                    // If y is z's direct child
                    x?.parent = y
                } else {
                    // Move y up
                    transplant(y, y.right)
                    y.right = z.right
                    y.right?.parent = y
                }
                // Now replace z with y
                transplant(z, y)
                y.left = z.left
                y.left?.parent = y
                y.color = z.color
                // fixDeletion needs to know if we removed a black node
                if (tmpColor == Color.BLACK) {
                    fixDeletion(x)
                }
                return
            }
        }

        // If a black node was physically removed, fix the tree
        if (yOriginalColor == Color.BLACK) {
            fixDeletion(x)
        }
    }

    // --- Private helper methods for RBTree operations ---

    /**
    * Help function for fixDeletion and fixInsertion
    *
    * @param node RBNode whose color we want to find out
    * @return Color
    */
    private fun colorOf(n: RBNode<K, V>?): Color {
        // null children are treated as BLACK
        return n?.color ?: Color.BLACK
    }

    /**
    * Help function for fixDeletion and fixInsertion
    *
    * @param node RBNode
    * @return RBNode parent
    */
    private fun parentOf(n: RBNode<K, V>?): RBNode<K, V>? {
        return n?.parent
    }

    /**
    * Help function for fixDeletion and fixInsertion
    *
    * @param node RBNode for setting color
    */
    private fun setColor(n: RBNode<K, V>?, c: Color) {
        if (n != null) n.color = c
    }

    /**
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

    /**
     * Tree balancing after inserting an element
     *
     * @param current - Node of the RBTree
     */
    private fun fixInsertion(node: RBNode<K, V>) {
        var z = node
        while (colorOf(parentOf(z)) == Color.RED) {
            val gp = parentOf(parentOf(z)) ?: break// grandparent

            if (parentOf(z) == gp.left) {
                val uncle = gp.right
                if (colorOf(uncle) == Color.RED) {
                    // Case 1: Uncle is red
                    setColor(parentOf(z), Color.BLACK)
                    setColor(uncle, Color.BLACK)
                    setColor(gp, Color.RED)
                    z = gp
                } else {
                    // Case 2 or 3: Uncle is black
                    if (z == parentOf(z)?.right) {
                        // Case 2: z is right child => left rotate parent
                        z = parentOf(z) ?: break
                        rotateLeft(z)
                    }
                    // Case 3
                    setColor(parentOf(z), Color.BLACK)
                    setColor(gp, Color.RED)
                    rotateRight(gp)
                }
            } else {
                // Mirror
                val uncle = gp.left
                if (colorOf(uncle) == Color.RED) {
                    // Case 1: Uncle is red
                    setColor(parentOf(z), Color.BLACK)
                    setColor(uncle, Color.BLACK)
                    setColor(gp, Color.RED)
                    z = gp
                } else {
                    if (z == parentOf(z)?.left) {
                        z = parentOf(z) ?: break
                        rotateRight(z)
                    }
                    setColor(parentOf(z), Color.BLACK)
                    setColor(gp, Color.RED)
                    rotateLeft(gp)
                }
            }
        }
        // Root is always black
        root?.color = Color.BLACK
    }

    /**
    * Tree balancing after deletion an element
    *
    * @param current - Node of the RBTree
    */
    private fun fixDeletion(x: RBNode<K, V>?) {
        var node = x
        while (node != root && colorOf(node) == Color.BLACK) {
            val p = parentOf(node) ?: break
            if (node == p.left) {
                var w = p.right
                if (colorOf(w) == Color.RED) {
                    // Case 1: brother is red
                    setColor(w, Color.BLACK)
                    setColor(p, Color.RED)
                    rotateLeft(p)
                    w = p.right
                }
                if (colorOf(w?.left) == Color.BLACK && colorOf(w?.right) == Color.BLACK) {
                    // Case 2: brother black, both children black
                    setColor(w, Color.RED)
                    node = p
                } else {
                    // Case 3 or 4
                    if (colorOf(w?.right) == Color.BLACK) {
                        // Case 3: brother black, left child red, right child black
                        setColor(w?.left, Color.BLACK)
                        setColor(w, Color.RED)
                        if (w != null) rotateRight(w)
                        w = p.right
                    }
                    // Case 4: brother black, right child red
                    setColor(w, colorOf(p))
                    setColor(p, Color.BLACK)
                    setColor(w?.right, Color.BLACK)
                    rotateLeft(p)
                    node = root
                }
            } else {
                // Mirror
                var w = p.left
                if (colorOf(w) == Color.RED) {
                    setColor(w, Color.BLACK)
                    setColor(p, Color.RED)
                    rotateRight(p)
                    w = p.left
                }
                if (colorOf(w?.left) == Color.BLACK && colorOf(w?.right) == Color.BLACK) {
                    setColor(w, Color.RED)
                    node = p
                } else {
                    if (colorOf(w?.left) == Color.BLACK) {
                        setColor(w?.right, Color.BLACK)
                        setColor(w, Color.RED)
                        if (w != null) rotateLeft(w)
                        w = p.left
                    }
                    setColor(w, colorOf(p))
                    setColor(p, Color.BLACK)
                    setColor(w?.left, Color.BLACK)
                    rotateRight(p)
                    node = root
                }
            }
        }
        // Make sure node is black at the end
        setColor(node, Color.BLACK)
    }

    /**
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

    /**
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

    /**
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

    /**
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

    /**
     *  Cleaning of a tree
     */
    override fun clean() {
        root = null
    }

    /**
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