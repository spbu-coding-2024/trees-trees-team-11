package libTree.trees

import libTree.interfaceTree.Tree
import kotlin.collections.ArrayDeque

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

    // ======================== Tree Information ========================

    /**
     * Returns the height of the tree using recursive approach
     */
    override fun height(): Int {
        return heightRecursive(root)
    }

    private fun heightRecursive(root: BSNode<K, V>?): Int {
        if (root == null) {
            return 0
        } else {
            return 1 + maxOf(heightRecursive(root.left), heightRecursive(root.right))
        }
    }

    fun isTreeEmpty(): Boolean {
        return root == null
    }

    fun isTreeNotEmpty(): Boolean {
        return root != null
    }

    override fun clean() {
        root = null
    }

    // ======================== Key Methods ========================

    /**
     * Checks if the tree contains a key
     */
    override fun containsKey(key: K): Boolean {
        return search(key) != null
    }

    /**
     * Searches for the value associated with a given key
     */
    private fun search(key: K): V? {
        var current = root

        while (current != null) {
            if (current.key == key) {
                return current.value
            }

            current = if (current.key  > key) {
                current.left
            } else {
                current.right
            }
        }

        return null
    }

    /**
     * Searches for the node containing the given key
     */
    fun getNode(current: BSNode<K, V>?, key: K): BSNode<K, V>? {
        if (current == null || current.key == key) {
            return current
        };

        if (current.key < key) {
            return getNode(current.right, key)
        }

        return getNode(current.left, key)
    }

    // ======================== Insertion ========================

    /**
     * Inserts a key-value pair into the tree
     *
     * If the key already exists in the tree, the duplicate counter is incremented If the key is not
     * found, a new node is created and added to the tree
     */
    override fun insert(key: K, value: V) {
        root = insertRecursive(root, key, value)
    }

    /**
     * Recursive helper function for inserting a key-value pair into the tree
     * @return The updated node after insertion
     */
    private fun insertRecursive(node: BSNode<K, V>?, key: K, value: V): BSNode<K, V> {
        if (node == null) {
            return BSNode(key, value)
        }

        if (key < node.key) {
            node.left = insertRecursive(node.left, key, value)
        } else if (key > node.key) {
            node.right = insertRecursive(node.right, key, value)
        } else {
                node.value = value
        }

        return node
    }

    // ======================== Deletion Logic ========================

    /**
     * Removes a key from the tree
     *
     * If the key has duplicates, the duplicate count is decremented If the key has no duplicates,
     * the node is removed from the tree
     */
    override fun erase(key: K) {
        root = deleteRecursive(root, key)
    }

    /**
     * Recursive helper function for deleting a node with the specified key
     * @return The updated node after deletion
     */
    private fun deleteRecursive(node: BSNode<K, V>?, key: K): BSNode<K, V>? {
        if (node == null) {
            return null
        }

        if (node.key > key) {
            node.left = deleteRecursive(node.left, key)
        } else if (node.key < key) {
            node.right = deleteRecursive(node.right, key)
        } else {
            if (node.left == null) {
                return node.right
            }

            if (node.right == null) {
                return node.left
            }

            val successor: BSNode<K, V>? = getSuccessor(node, key)

            if (successor != null) {
                node.key = successor.key
            }
            if (successor != null) {
                node.right = deleteRecursive(node.right, successor.key)
            }
        }
        return node
    }

    /**
     *  Finds the inorder successor of a given node
     */
    private fun getSuccessor(node: BSNode<K, V>?, key: K): BSNode<K, V>? {
        var current: BSNode<K, V>? = node

        if (current != null) {
            current = current.right
        }

        while (current?.left != null) {
            current = current.left
        }

        return current
    }


    // ======================== Traversals ========================

    /**
     * Returns an iterator for traversing the tree in Inorder (left -> root -> right)
     * */
    override fun iterator(): Iterator<Pair<K, V>> {
        return InOrderIterator.iterator(root)
    }

    /**
     * Inorder iterator for the Binary Search Tree (BST).
     */
    private class InOrderIterator<K : Comparable<K>, V>(root: BSNode<K, V>?) : Iterator<Pair<K, V>> {
        private val stack = ArrayDeque<BSNode<K, V>>()
        private var current: BSNode<K, V>? = root

        init {
            current = root
        }

        override fun next(): Pair<K, V> {
            while (current != null) {
                stack.push(current)
                current = current?.left
            }

            val node =  stack.pop()
            current = node?.right

            return Pair(node.key, node.value)
        }

        override fun hasNext(): Boolean {
            return (stack.isNotEmpty() || current != null)
        }

        companion object {
            fun <K : Comparable<K>, V> iterator(root: BSNode<K, V>?): Iterator<Pair<K, V>> {
                return iterator(root)
            }
        }
    }

    // ======================== Supporting Methods ========================

    /**
     * Returns the node with the smallest key in the given subtree
     */
    private fun minValueNode(node: BSNode<K, V>?): BSNode<K, V>? {
        var current = node

        while (current?.left != null) {
            current = current.left
        }

        return current
    }

    /**
     * Returns the node with the largest key in the given subtree
     */
    private fun maxValueNode(node: BSNode<K, V>?): BSNode<K, V>? {
        var current = node

        while (current?.right != null) {
            current = current.right
        }

        return current
    }
}
