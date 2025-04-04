package libTreeTestsUtils

import libTree.trees.RBTree

class UtilsFunctionsRBTree<K : Comparable<K>, V>(private val tree: RBTree<K, V>) {

    fun checkTree(): Boolean {
        val root = tree.getRoot() ?: return true // empty tree is correct

        // 1) Root must be black if the tree is not empty
        if (root.color != RBTree.Color.BLACK) return false

        // 2) Check all Red-Black properties
        val blackHeight = checkRBProperties(root)
        if (blackHeight == -1) return false

        return true
    }

    /**
     * Recursively checks:
     *     If a node is red, then its children must be black.
     *     The black‐height (number of black nodes on any path from a node down to a null leaf)
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

        // If left and right black‐heights differ
        if (leftBlackHeight != rightBlackHeight) return -1

        // If this node is black, add 1 to the black‐height
        return leftBlackHeight + if (node.color == RBTree.Color.BLACK) 1 else 0
    }
    // function for getting height
    fun getHeight(): Int {
        return getHeightRecursive(tree.getRoot())
    }

    private fun getHeightRecursive(node: RBTree.RBNode<K, V>?): Int {
        if (node == null) return 0
        val leftHeight = getHeightRecursive(node.left)
        val rightHeight = getHeightRecursive(node.right)
        return 1 + maxOf(leftHeight, rightHeight)
    }


}

