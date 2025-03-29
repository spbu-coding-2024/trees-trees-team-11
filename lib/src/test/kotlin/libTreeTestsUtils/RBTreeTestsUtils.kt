package libTreeTestsUtils

import libTree.trees.RBTree

class UtilsFunctionsRBTree<K : Comparable<K>, V>(private val tree: RBTree<K, V>) {

    fun checkTree(): Boolean {
        val root = tree.getRoot()
        // If the tree is not empty, the root must be black
        if (root != null && root.color != RBTree.Color.BLACK) return false
        return checkNode(root) != -1
    }

    /**
     * Recursively checks the invariants:
     * 1. If a node is red, then its children must be black.
     * 2. On each path from this node to all leaves, the number of black nodes must be the same.
     *
     * @return The number of black nodes from the current node to the leaves, or -1 if the invariant is violated.
     */
    private fun checkNode(node: RBTree.RBNode<K, V>?): Int {
        if (node == null) return 1 // Empty nodes are considered black
        // If a node is red, its children must be black
        if (node.color == RBTree.Color.RED) {
            if (node.left?.color == RBTree.Color.RED || node.right?.color == RBTree.Color.RED) {
                return -1
            }
        }
        val leftBlackHeight = checkNode(node.left)
        val rightBlackHeight = checkNode(node.right)
        if (leftBlackHeight == -1 || rightBlackHeight == -1 || leftBlackHeight != rightBlackHeight) {
            return -1
        }
        return leftBlackHeight + if (node.color == RBTree.Color.BLACK) 1 else 0
    }
}
