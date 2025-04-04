package libTreeTestsUtils

import libTree.trees.BSTree

class BSTreeUtilsFunctions<K : Comparable<K>, V>(private val tree: BSTree<K, V>) {

    fun checkTree(): Boolean {
        return isBST(tree.getRoot())
    }

    private fun isBST(node: BSTree.BSNode<K, V>?): Boolean {
        return isBSTUtil(node, min = null, max = null)
    }

    private fun isBSTUtil(node: BSTree.BSNode<K, V>?, min: K?, max: K?): Boolean {
        node ?: return true

        if (min != null && node.key < min || max != null && node.key > max) {
            return false
        }

        return isBSTUtil(node.left, min, node.key) &&
                isBSTUtil(node.right, node.key, max)
    }

    fun countNodes(): Int {
        return getCountNode(tree.getRoot())
    }

    private fun getCountNode(node: BSTree.BSNode<K, V>?): Int {
        node ?: return 0
        return 1 + getCountNode(node.left) + getCountNode(node.right);
    }

    fun toKeyList(): List<K> {
        return tree.iterator().asSequence().map { it.first }.toList()
    }
}
