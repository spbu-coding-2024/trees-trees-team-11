package libTreeTestsUtils

import libTree.trees.RBTree

class UtilsFunctionsRBTree<K : Comparable<K>, V>(private val tree: RBTree<K, V>) {

    fun checkTree(): Boolean {
        val root = tree.getRoot() ?: return true // Empty tree is trivially correct

        // 1) Root must be black if the tree is not empty
        if (root.color != RBTree.Color.BLACK) return false

        // 2) Check all Red-Black properties
        val blackHeight = checkRBProperties(root)
        if (blackHeight == -1) return false

        // 3) (Optional) Check that it’s a valid BST. Comment out if you allow duplicates or non‐BST usage
        // if (!checkBSTProperty(root, null, null)) return false

        return true
    }

    /**
     * Recursively checks:
     *   - If a node is red, then its children must be black.
     *   - The black‐height (number of black nodes on any path from a node down to a null leaf)
     *     is consistent across the left and right subtrees.
     *
     * @return The black‐height from [node] down to leaves, or -1 if a violation is found.
     */
    private fun checkRBProperties(node: RBTree.RBNode<K, V>?): Int {
        if (node == null) {
            // Null child acts like a black leaf
            return 1
        }
        // If a node is red, children must be black
        if (node.color == RBTree.Color.RED) {
            if (node.left?.color == RBTree.Color.RED || node.right?.color == RBTree.Color.RED) {
                return -1
            }
        }
        // Recursively get the black‐height of each side
        val leftBlackHeight = checkRBProperties(node.left)
        if (leftBlackHeight == -1) return -1

        val rightBlackHeight = checkRBProperties(node.right)
        if (rightBlackHeight == -1) return -1

        // If left and right black‐heights differ, that’s a violation
        if (leftBlackHeight != rightBlackHeight) return -1

        // If this node is black, add 1 to the black‐height
        return leftBlackHeight + if (node.color == RBTree.Color.BLACK) 1 else 0
    }

    /**
     * (Optional) Checks that the tree satisfies the BST property:
     *   For every node, all keys in the left subtree are < node.key,
     *   and all keys in the right subtree are > node.key.
     */
    private fun checkBSTProperty(
        node: RBTree.RBNode<K, V>?,
        min: K?,
        max: K?
    ): Boolean {
        if (node == null) return true

        val key = node.key
        // If 'min' is set, current key must be strictly greater than min
        if (min != null && key <= min) return false
        // If 'max' is set, current key must be strictly less than max
        if (max != null && key >= max) return false

        return checkBSTProperty(node.left, min, key) &&
                checkBSTProperty(node.right, key, max)
    }
}

