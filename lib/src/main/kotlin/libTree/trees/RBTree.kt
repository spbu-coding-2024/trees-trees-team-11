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

class RBTree<K : Comparable<K>, V> private constructor(
    private var root: RBNode<K, V>? = null,
) : Tree<K, V, RBTree.RBNode<K, V>> {


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

    class RBNode<K : Comparable<K>, V> internal constructor(
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
    fun getRoot(): RBNode<K, V>? = root

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
        val current = findNode(key) ?: return  // Key not found, do nothing
        var nodeToRemove = current
        val originalColor = nodeToRemove.color
        var replacementNode: RBNode<K, V>? = null

        when {
            // Node current has no left child
            current.left == null -> {
                replacementNode = current.right
                transplant(current, current.right)
            }
            // Node current has no right child
            current.right == null -> {
                replacementNode = current.left
                transplant(current, current.left)
            }
            // Node current has two children
            else -> {
                nodeToRemove = minimum(current?.right ?: return)
                val tmpColor = nodeToRemove.color
                replacementNode = nodeToRemove.right
                if (nodeToRemove.parent == current) {
                    // If nodeToRemove is current's direct child
                    replacementNode?.parent = nodeToRemove
                } else {
                    // Move nodeToRemove up
                    transplant(nodeToRemove, nodeToRemove.right)
                    nodeToRemove.right = current.right
                    nodeToRemove.right?.parent = nodeToRemove
                }
                // Now replace current with nodeToRemove
                transplant(current, nodeToRemove)
                nodeToRemove.left = current.left
                nodeToRemove.left?.parent = nodeToRemove
                nodeToRemove.color = current.color
                // fixDeletion needs to know if we removed a black node
                if (tmpColor == Color.BLACK) {
                    fixDeletion(replacementNode)
                }
                return
            }
        }

        // If a black node was physically removed, fix the tree
        if (originalColor == Color.BLACK) {
            fixDeletion(replacementNode)
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
        n?.color = c
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
        var current = node
        while (colorOf(parentOf(current)) == Color.RED) {
            val gp = parentOf(parentOf(current)) ?: break// grandparent

            if (parentOf(current) == gp.left) {
                val uncle = gp.right
                if (colorOf(uncle) == Color.RED) {
                    // Case 1: Uncle is red
                    setColor(parentOf(current), Color.BLACK)
                    setColor(uncle, Color.BLACK)
                    setColor(gp, Color.RED)
                    current = gp
                } else {
                    // Case 2 or 3: Uncle is black
                    if (current == parentOf(current)?.right) {
                        // Case 2: z is right child => left rotate parent
                        current = parentOf(current) ?: break
                        rotateLeft(current)
                    }
                    // Case 3
                    setColor(parentOf(current), Color.BLACK)
                    setColor(gp, Color.RED)
                    rotateRight(gp)
                }
            } else {
                // Mirror
                val uncle = gp.left
                if (colorOf(uncle) == Color.RED) {
                    setColor(parentOf(current), Color.BLACK)
                    setColor(uncle, Color.BLACK)
                    setColor(gp, Color.RED)
                    current = gp
                } else {
                    if (current == parentOf(current)?.left) {
                        current = parentOf(current) ?: break
                        rotateRight(current)
                    }
                    setColor(parentOf(current), Color.BLACK)
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
                var sibling = p.right
                if (colorOf(sibling) == Color.RED) {
                    // Case 1: brother is red
                    setColor(sibling, Color.BLACK)
                    setColor(p, Color.RED)
                    rotateLeft(p)
                    sibling = p.right
                }
                if (colorOf(sibling?.left) == Color.BLACK && colorOf(sibling?.right) == Color.BLACK) {
                    // Case 2: brother black, both children black
                    setColor(sibling, Color.RED)
                    node = p
                } else {
                    // Case 3 or 4
                    if (colorOf(sibling?.right) == Color.BLACK) {
                        // Case 3: brother black, left child red, right child black
                        setColor(sibling?.left, Color.BLACK)
                        setColor(sibling, Color.RED)
                        if (sibling != null) rotateRight(sibling)
                        sibling = p.right
                    }
                    // Case 4: brother black, right child red
                    setColor(sibling, colorOf(p))
                    setColor(p, Color.BLACK)
                    setColor(sibling?.right, Color.BLACK)
                    rotateLeft(p)
                    node = root
                }
            } else {
                // Mirror
                var sibling = p.left
                if (colorOf(sibling) == Color.RED) {
                    setColor(sibling, Color.BLACK)
                    setColor(p, Color.RED)
                    rotateRight(p)
                    sibling = p.left
                }
                if (colorOf(sibling?.left) == Color.BLACK && colorOf(sibling?.right) == Color.BLACK) {
                    setColor(sibling, Color.RED)
                    node = p
                } else {
                    if (colorOf(sibling?.left) == Color.BLACK) {
                        setColor(sibling?.right, Color.BLACK)
                        setColor(sibling, Color.RED)
                        if (sibling != null) rotateLeft(sibling)
                        sibling = p.left
                    }
                    setColor(sibling, colorOf(p))
                    setColor(p, Color.BLACK)
                    setColor(sibling?.left, Color.BLACK)
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
    private fun transplant(nodeToReplace: RBNode<K, V>, replacement: RBNode<K, V>?) {
        if (nodeToReplace.parent == null) {
            root = replacement
        } else if (nodeToReplace == nodeToReplace.parent?.left) {
            nodeToReplace.parent?.left = replacement
        } else {
            nodeToReplace.parent?.right = replacement
        }
        if (replacement != null) {
            replacement.parent = nodeToReplace.parent
        }
    }

    /**
     * Finds the node with the minimum key starting from given node.
     */
    private fun minimum(node: RBNode<K, V>): RBNode<K, V> {
        var current = node
        while (current.left != null) {
            current = current.left as RBNode<K, V>
        }
        return current
    }

    /**
     * Performs a left rotation around a given node.
     */
    private fun rotateLeft(node: RBNode<K, V>) {
        val rightChild = node.right ?: return
        node.right = rightChild.left
        rightChild.left?.parent = node
        rightChild.parent = node.parent
        if (node.parent == null) { // Nothing to turn
            root = rightChild
        } else if (node == node.parent?.left) { // Node glorrightChild from parent
            node.parent?.left = rightChild
        } else {
            node.parent?.right = rightChild
        }
        rightChild.left = node
        node.parent = rightChild
    }

    /**
     * Performs a right rotation around a given node.
     */
    private fun rotateRight(node: RBNode<K, V>) {
        val leftChild = node.left ?: return
        node.left = leftChild.right
        leftChild.left?.parent = node
        leftChild.parent = node.parent
        if (node.parent == null) {
            root = leftChild
        } else if (node == node.parent?.right) {
            node.parent?.right = leftChild
        } else {
            node.parent?.left = leftChild
        }
        leftChild.right = node
        node.parent = leftChild
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
