package libTree.trees

import libTree.interfaceTree.Tree
import kotlin.math.max

/**
 * Implementation of an AVL Tree, a self-balancing binary search tree.
 *
 * @param K the type of keys maintained by this tree, must be Comparable
 * @param V the type of mapped values
 */
class AVLTree<K : Comparable<K>, V> private constructor (
    private var root: AVLNode<K, V>? = null,
) : Tree<K, V, AVLTree.AVLNode<K, V>> {
    /**
     * Node class representing each node in the AVL Tree.
     *
     * @param K the type of keys
     * @param V the type of values
     * @property left Left child node
     * @property right Right child node
     * @property height Height of the node in the tree
     */
    class AVLNode<K : Comparable<K>, V> internal constructor (
        key: K,
        value: V,
        override var left: AVLNode<K, V>? = null,
        override var right: AVLNode<K, V>? = null,
        override var height: Long = 1
    ) : BaseNode<K, V, AVLNode<K, V>>(key, value, left, right, height)

    /**
     * Secondary constructor initializing an empty AVL Tree.
     */
    constructor() : this(null)

    /**
     * Returns the root node of the AVL tree.
     *
     * @return The root node
     */
    fun getRoot() : AVLNode<K, V>? = root

    /**
     * Sets the root node of the AVL tree.
     *
     * @param node The new root node
     */
    fun setRoot(node: AVLNode<K,V>?) {
        root = node
    }

    /**
     * Calculates the height of the AVL Tree.
     *
     * @return Height of the tree
     */
    override fun height(): Int {
        return root?.height?.toInt() ?: 0
    }

    /**
     * Removes a node with the specified key from the AVL Tree.
     *
     * @param key Key of the node to be removed
     */
    override fun erase(key: K) {
        root = eraseNode(root, key)
    }

    /**
     * Checks whether the AVL Tree contains a node with the given key.
     *
     * @param key Key to search for
     * @return True if the tree contains the key, false otherwise
     */
    override fun containsKey(key: K): Boolean {
        var localRoot = root
        localRoot ?: return false
        while (localRoot != null) {
            localRoot = when {
                key < localRoot.key -> localRoot.left
                key > localRoot.key -> localRoot.right
                else -> return true
            }
        }
        return false
    }

    /**
     * Inserts a node into the AVL Tree with the given key and value.
     *
     * @param key Key for the node
     * @param value Value for the node
     */
    override fun insert(key: K, value: V) {
        root = insertNode(root, key, value)
    }

    /**
     * Removes all nodes from the AVL Tree.
     */
    override fun clean() {
        root = null
    }

    /**
     * Returns an iterator to traverse the AVL Tree.
     *
     * @return Iterator of key-value pairs
     */
    override fun iterator(): Iterator<Pair<K, V>> {
        val result = mutableListOf<Pair<K, V>>()
        fun nodeIteration(node: AVLNode<K, V>?) {
            if (node == null) return
            result.add(node.key to node.value)
            nodeIteration(node.left)
            nodeIteration(node.right)
        }
        nodeIteration(root)
        return result.iterator()
    }

    // --- Private helper methods for AVL Tree operations ---

    /**
     * Calculates the height of a given node.
     */
    private fun heightNode(node: AVLNode<K, V>?): Long {
        if (node == null) return 0
        return node.height
    }


    /**
     * Updated height of current node
     */
    private fun updateHeight(node: AVLNode<K, V>?) : Long {
        return 1 + max(heightNode(node?.left), heightNode(node?.right))
    }

    /**
     * Performs a right rotation around a given node.
     */
    private fun rightRotate(node: AVLNode<K, V>?): AVLNode<K, V>? {
        val newNode = node?.left
        val tempNode = newNode?.right

        newNode?.right = node
        node?.left = tempNode

        node?.height = updateHeight(node)
        newNode?.height = updateHeight(newNode)

        return newNode
    }

    /**
     * Performs a left rotation around a given node.
     */
    private fun leftRotate(node: AVLNode<K, V>?): AVLNode<K, V>? {
        val newNode = node?.right
        val tempNode = newNode?.left

        newNode?.left = node
        node?.right = tempNode

        node?.height = updateHeight(node)
        newNode?.height = updateHeight(newNode)

        return newNode
    }

    /**
     * Calculates the balance factor of a given node.
     */
    private fun getBalance(node: AVLNode<K, V>?): Long {
        if (node == null) {
            return 0
        }
        return heightNode(node.left) - heightNode(node.right)
    }

    /**
     * Inserts a node into the AVL tree and maintains balance.
     */
    private fun insertNode(node: AVLNode<K, V>?, key: K, value: V): AVLNode<K, V>? {
        if (node == null)
            return AVLNode(key, value)
        when {
            key < node.key -> node.left = insertNode(node.left, key, value)
            key > node.key -> node.right = insertNode(node.right, key, value)
            else -> {
                node.value = value
                return node
            }
        }

        node.height = 1 + max(heightNode(node.left), heightNode(node.right))
        val leftNode = node.left
        val rightNode = node.right

        val balance = getBalance(node)

        if(balance < -1 && rightNode != null) {
            if(key > rightNode.key)
                return leftRotate(node)
            else {
                node.right = rightRotate(node.right)
                return leftRotate(node)
            }
        }

        if(balance > 1 && leftNode != null) {
            if(key < leftNode.key)
                return rightRotate(node)
            else {
                node.left = leftRotate(node.left)
                return rightRotate(node)
            }
        }

        return node
    }

    /**
     * Erases a node with the specified key and maintains AVL balance.
     */
    private fun eraseNode(node: AVLNode<K, V>?, key: K): AVLNode<K, V>? {
        var localNode = node
        localNode ?: return null
        when {
            key < localNode.key -> localNode.left = eraseNode(localNode.left, key)
            key > localNode.key -> localNode.right = eraseNode(localNode.right, key)
            else -> {
                if ((localNode.left == null) || (localNode.right == null)) {
                    val tempNode = if (localNode.left != null) localNode.left else localNode.right
                    localNode = tempNode
                } else {
                    val tempNode = minValueNode(localNode.right)
                    if (tempNode != null) {
                        localNode.key = tempNode.key
                        localNode.value = tempNode.value
                        localNode.right = eraseNode(localNode.right, tempNode.key)
                    }

                }
            }
        }

        localNode ?: return null

        localNode.height = 1 + max(heightNode(localNode.left), heightNode(localNode.right))

        val leftNode = localNode.left
        val rightNode = localNode.right

        val balance = getBalance(localNode)

        if(balance > 1) {
            if (getBalance(leftNode) >= 0)
                return rightRotate(localNode)
            if (getBalance(leftNode) < 0) {
                localNode.left = leftRotate(leftNode)
                return rightRotate(localNode)
            }
        }
        if (balance < -1) {
            if (getBalance(rightNode) <= 0)
                return leftRotate(localNode)
            if (getBalance(rightNode) > 0) {
                localNode.right = rightRotate(rightNode)
                return leftRotate(localNode)
            }
        }

        return localNode
    }

    /**
     * Finds the node with the minimum key starting from a given node.
     */
    private fun minValueNode(node: AVLNode<K, V>?): AVLNode<K, V>? {
        if (node?.left == null)
            return node
        return minValueNode(node.left)
    }
}
