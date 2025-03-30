package libTree.trees

import libTree.interfaceTree.Tree

/**
 * Implementation of a Binary Search Tree (BST).
 *
 * @param K The type of the node's key, which must be Comparable.
 * @param V The type of mapped values.
 */
class BSTree<K : Comparable<K>, V> private constructor(
    private var root: BSNode<K, V>? = null,
) : Tree<K, V, BSTree.BSNode<K, V>> {
    /**
     * Represents a node in the BS Tree.
     *
     * @param K The type of key.
     * @param V The type of value.
     * @property left The left child of the node.
     * @property right The right child of the node.
     * @property height Height of the node in the tree.
     */
    class BSNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        override var left: BSNode<K, V>? = null,
        override var right: BSNode<K, V>? = null,
        override var height: Long = 1,
    ) : BaseNode<K, V, BSNode<K, V>>(key, value, left, right, height)

    /**
     * Secondary constructor initializing an empty BS Tree.
     */
    constructor() : this(null)

    /**
     * Returns the root node of the BS tree.
     *
     * @return The root node.
     */
    fun getRoot() : BSNode<K, V>? {
        return root
    }

    /**
     * Sets the root node of the BS tree.
     *
     * @param node The new root node.
     */
    fun setRoot(node: BSNode<K, V>?) {
        root = node
    }

    /**
     * Calculates the height of the BS Tree.
     *
     * @return Height of the tree.
     */
    override fun height(): Int {
        return heightOfTree(root)
    }

    /**
     * Checks if the tree contains a node with the given key.
     *
     * @param key The key to search for.
     * @return True if the key contains in the tree, otherwise false.
     */
    override fun containsKey(key: K): Boolean {
        return findNode(key) != null
    }

    /**
     * Removes a node with the key from the tree.
     *
     * @param key The key of the node to be removed.
     */
    override fun erase(key: K) {
        root = deleteNode(root, key)
    }

    /**
     * Inserts a node into the tree using given key and value.
     *
     * @param key Key for the node.
     * @param value Value for the node.
     */
    override fun insert(key: K, value: V) {
        root = insertNode(root, key, value)
    }

    /**
     * Clears the tree by removing all nodes.
     */
    override fun clean() {
        root = null
    }

    /**
     * Returns an iterator for traversing the tree in Inorder (left -> root -> right).
     *
     * @return Iterator of key-value pairs.
     */
    override fun iterator(): Iterator<Pair<K, V>> {
        val list = mutableListOf<Pair<K, V>>()
        fun treeIterator(node: BSNode<K, V>?) {
            node ?: return

            treeIterator(node.left)
            list.add(node.key to node.value)
            treeIterator(node.right)
        }

        treeIterator(root)
        return list.iterator()
    }

    // ================ Private helper methods for BS Tree operations ================

    /**
     * The helper function which returns the height of the tree using recursive approach.
     */
    private fun heightOfTree(root: BSNode<K, V>?): Int {
        return if (root == null) {
            0
        } else {
            1 + maxOf(heightOfTree(root.left), heightOfTree(root.right))
        }
    }

    /**
     * The helper function which finds and returns the node with the key using iterative search.
     */
    private fun findNode(key: K): BSNode<K, V>? {
        var current: BSNode<K, V>? = root
        while (current != null) {
            current = when {
                current.key == key -> return current
                current.key > key -> current.left
                else -> current.right
            }
        }

        return null
    }

    /**
     * Recursive helper function for deleting a node with the key.
     */
    private fun deleteNode(node: BSNode<K, V>?, key: K): BSNode<K, V>? {
        node ?: return null

        if (node.key > key) {
            node.left = deleteNode(node.left, key)
        } else if (node.key < key) {
            node.right = deleteNode(node.right, key)
        } else {
            node.left ?: return node.right
            node.right ?: return node.left

            val successor: BSNode<K, V>? = minValueNode(node)
            if (successor != null) {
                node.key = successor.key
                node.right = deleteNode(node.right, successor.key)
            }
        }

        return node
    }

    /**
     * Returns the node with the minimum key in the given subtree.
     */
    private fun minValueNode(node: BSNode<K, V>?): BSNode<K, V>? {
        var current: BSNode<K, V>? = node
        while (current?.left != null) {
            current = current.left
        }

        return current
    }

    /**
     * Recursive helper function for inserting a node into the tree.
     */
    private fun insertNode(node: BSNode<K, V>?, key: K, value: V): BSNode<K, V> {
        node ?: return BSNode(key, value)

        when {
            key < node.key -> node.left = insertNode(node.left, key, value)
            key > node.key -> node.right = insertNode(node.right, key, value)
            else -> node.value = value
        }
        return node
    }

}
