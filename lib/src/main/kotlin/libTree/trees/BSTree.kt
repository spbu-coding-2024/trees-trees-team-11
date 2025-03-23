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
    ) : BaseNode<K, V, BSNode<K, V>>(key, value, left, right, 1) {
        var count: Int = 1 // Counter for duplicate keys
    }

    // ======================== Tree Information ========================

    /**
     * Returns the height of the tree using recursive approach
     */
    override fun height(): Int = heightRecursive(root)

    private fun heightRecursive(root: BSNode<K, V>?): Int {
        return if (root == null) {
            0
        } else {
            1 + maxOf(heightRecursive(root.left), heightRecursive(root.right))
        }
    }

    fun isTreeEmpty(): Boolean = root == null


    fun isTreeNotEmpty(): Boolean = root != null

    override fun clean() {
        root = null
    }

    // ======================== Key Methods ========================

    /**
     * Checks if the tree contains a key
     */
    override fun containsKey(key: K): Boolean = getValue(key) != null

    /**
     * Searches for a value by key
     */
    fun getValue(key: K): V? {
        var current = root
        while (current != null) {
            current =
                when {
                    key < current.key -> current.left
                    key > current.key -> current.right
                    else -> return current.value
                }
        }
        return null
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

        when {
            key < node.key -> node.left = insertRecursive(node.left, key, value)
            key > node.key -> node.right = insertRecursive(node.right, key, value)
            else -> {
                node.count++
                node.value = value
            }
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

        when {
            key < node.key -> node.left = deleteRecursive(node.left, key)
            key > node.key -> node.right = deleteRecursive(node.right, key)
            else -> {
                if (node.count > 1) {
                    node.count--
                    return node
                }

                if (node.left == null) {
                    return node.right
                } else if (node.right == null) {
                    return node.left
                }

                val successor = minValueNode(node.right)

                if (successor != null) {
                    node.key = successor.key
                    node.value = successor.value
                    node.count = successor.count
                    node.right = deleteRecursive(node.right, successor.key)
                }
            }
        }

        return node
    }

    // ======================== Traversals ========================

    /**
     * Returns an iterator for traversing the tree in Inorder (left -> root -> right)
     * */
    override fun iterator(): Iterator<Pair<K, V>> = inorder().iterator()

    enum class TraversalType {
        INORDER,
        PREORDER,
        POSTORDER
    }

    /**
     * Performs a Depth-First Search (DFS) traversal of the tree
     *
     * @param type The type of traversal (Inorder, Preorder, Postorder)
     * @return A list of key-value pairs in the specified traversal order
     */
    fun dfs(type: TraversalType): List<Pair<K, V>> =
        when (type) {
            TraversalType.INORDER -> inorder()
            TraversalType.PREORDER -> preorder()
            TraversalType.POSTORDER -> postorder()
        }

    fun inorder(): List<Pair<K, V>> {
        return traverse(root, TraversalType.INORDER)
    }

    fun preorder(): List<Pair<K, V>> {
        return traverse(root, TraversalType.PREORDER)
    }

    fun postorder(): List<Pair<K, V>> {
        return traverse(root, TraversalType.POSTORDER)
    }

    /**
     * Recursive helper function for tree traversal
     *
     * @param node The current node in the recursion
     * @param type The type of traversal: Inorder, Preorder, Postorder
     * @return A list of key-value pairs in the specified traversal order
     */
    private fun traverse(node: BSNode<K, V>?, type: TraversalType): List<Pair<K, V>> {
        if (node == null) return emptyList()

        val left = traverse(node.left, type)
        val right = traverse(node.right, type)

        return when (type) {
            TraversalType.PREORDER -> listOf(node.key to node.value) + left + right
            TraversalType.INORDER -> left + listOf(node.key to node.value) + right
            TraversalType.POSTORDER -> left + right + listOf(node.key to node.value)
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
