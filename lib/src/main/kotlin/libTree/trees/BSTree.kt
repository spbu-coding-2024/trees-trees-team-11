package libTree.trees

import libTree.interfaceTree.Tree

/**
 * Implementation of a Binary Search Tree (BST)
 */
class BSTree<K : Comparable<K>, V> private constructor(
    private var root: BSNode<K, V>? = null,
) : Tree<K, V, BSTree.BSNode<K, V>> {

    class BSNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        public override var left: BSNode<K, V>? = null,
        public override var right: BSNode<K, V>? = null,
        public override var height: Long = 1,
    ) : BaseNode<K, V, BSNode<K, V>>(key, value, left, right, height)

    constructor() : this(null)

    /**
     * Returns the height of the tree using recursive approach
     */
    override fun height(): Int = heightOfTree(root)

    private fun heightOfTree(root: BSNode<K, V>?): Int {
        if (root == null) {
            return 0
        } else {
            return 1 + maxOf(heightOfTree(root.left), heightOfTree(root.right))
        }
    }

    /**
     * Clears the tree by removing all nodes
     */
    override fun clean() {
        root = null
    }

    /**
     * Checks if the tree contains a key
     */
    override fun containsKey(key: K): Boolean = findNode(key) != null

    /**
     * Finds and returns the node with the specified key using iterative search
     *
     * @return the node containing the key, or null if not found
     */
    private fun findNode(key: K): BSNode<K, V>? {
        var current: BSNode<K, V>? = root
        while (current != null) {
            if (current.key == key) {
                return current
            }

            current = if (current.key > key) {
                current.left
            } else {
                current.right
            }
        }

        return null
    }
    
    /**
     * Inserts a key-value pair into the tree
     */
    override fun insert(key: K, value: V) {
        root = insertNode(root, key, value)
    }

    /**
     * Recursive helper function for inserting a key-value pair into the tree
     *
     * @return The updated node after insertion
     */
    private fun insertNode(node: BSNode<K, V>?, key: K, value: V): BSNode<K, V> {
        if (node == null) {
            return BSNode(key, value)
        }

        if (key < node.key) {
            node.left = insertNode(node.left, key, value)
        } else if (key > node.key) {
            node.right = insertNode(node.right, key, value)
        } else {
            node.value = value
        }

        return node
    }

    /**
     * Removes a key from the tree
     */
    override fun erase(key: K) {
        root = deleteNode(root, key)
    }

    /**
     * Recursive helper function for deleting a node with the specified key
     *
     * @return The updated node after deletion
     */
    private fun deleteNode(node: BSNode<K, V>?, key: K): BSNode<K, V>? {
        if (node == null) {
            return null
        }

        if (node.key > key) {
            node.left = deleteNode(node.left, key)
        } else if (node.key < key) {
            node.right = deleteNode(node.right, key)
        } else {
            if (node.left == null) {
                return node.right
            }

            if (node.right == null) {
                return node.left
            }

            val successor: BSNode<K, V>? = minValueNode(node)
            if (successor != null) {
                node.key = successor.key
                node.right = deleteNode(node.right, successor.key)
            }
        }

        return node
    }

    /**
     * Returns the node with the minimum key in the given subtree
     */
    private fun minValueNode(node: BSNode<K, V>?): BSNode<K, V>? {
        var current: BSNode<K, V>? = node
        while (current?.left != null) {
            current = current.left
        }

        return current
    }

    /**
     * Returns an iterator for traversing the tree in Inorder (left -> root -> right)
     */
    override fun iterator(): Iterator<Pair<K, V>> {
        val list = mutableListOf<Pair<K, V>>()
        fun treeIterator(node: BSNode<K, V>?) {
            if (node == null) {
                return
            }

            treeIterator(node.left)
            list.add(node.key to node.value)
            treeIterator(node.right)
        }

        treeIterator(root)
        return list.iterator()
    }
}
