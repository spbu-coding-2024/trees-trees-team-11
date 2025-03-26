package libTree.trees

import libTree.interfaceTree.Tree

/**
 * Implementation of a Binary Search Tree (BST)
 */
class BSTree<K : Comparable<K>, V>(
    private var root: BSNode<K, V>? = null,
) : Tree<K, V, BSTree.BSNode<K, V>> {

    class BSNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        public override var left: BSNode<K, V>? = null,
        public override var right: BSNode<K, V>? = null,
    ) : BaseNode<K, V, BSNode<K, V>>(key, value, left, right, 1)

    // ======================== Tree Methods ========================

    /**
     * Returns the height of the tree using recursive approach
     */
    override fun height(): Int {
        return heightOfTree(root)
    }

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

    // ======================== Key Methods ========================

    /**
     * Checks if the tree contains a key
     */
    override fun containsKey(key: K): Boolean {
        return findNode(key) != null
    }

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

            current =
                if (current.key > key) {
                    current.left
                } else {
                    current.right
                }
        }

        return null
    }

    // ======================== Insertion Logic ========================

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

    // ======================== Deletion Logic ========================

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

            val successor: BSNode<K, V>? = minNode(node, key)
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
    private fun minNode(node: BSNode<K, V>?, key: K): BSNode<K, V>? {
        var current: BSNode<K, V>? = node
        if (current != null) {
            current = current.right
        }

        while (current?.left != null) {
            current = current.left
        }

        return current
    }

    // ======================== Iterator ========================

    /**
     * Returns an iterator for traversing the tree in Inorder (left -> root -> right)
     */
    override fun iterator(): Iterator<Pair<K, V>> {
        return TreeIterator(root)
    }

    /**
     * Inorder iterator implementation for BST
     */
    private class TreeIterator<K : Comparable<K>, V>(root: BSNode<K, V>?) : Iterator<Pair<K, V>> {
        private val stack: MutableList<BSNode<K, V>> = mutableListOf()
        private var current: BSNode<K, V>? = root

        override fun next(): Pair<K, V> {
            while (current != null) {
                val currentNotNull = current
                if (currentNotNull != null) {
                    stack.add(currentNotNull)
                    current = currentNotNull.left
                }
            }

            val node = stack.removeAt(stack.size - 1)
            current = node.right

            return node.key to node.value
        }

        override fun hasNext(): Boolean {
            return (stack.isNotEmpty() || current != null)
        }
    }
}
