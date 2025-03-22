package libTreeTestsUtils
import libTree.trees.AVLTree
import kotlin.math.abs
import kotlin.math.max

class UtilsFunctions {

    fun<K : Comparable<K>,V : Any> is_balanced(root: AVLTree.AVLNode<K, V>?): Boolean {

        if(root == null) return true

        val lhs = height(root.left)
        val rhs = height(root.right)

        if (abs(rhs - lhs) > 1) return false

        return is_balanced(root.left) && is_balanced(root.right)
    }

    fun<K:Comparable<K>, V> nodesInTreeCounter(tree: AVLTree<K, V>?): Int {
        if(tree == null) return 0
        val listOfNodes = mutableListOf<K>()
        for ((key, _) in tree) {
            listOfNodes.add(key)
        }
        val nodesInTree = listOfNodes.size
        return nodesInTree
    }

    fun <K: Comparable<K>, V> showKeyValue(root: AVLTree.AVLNode<K, V>?, key: K): AVLTree.AVLNode<K, V> {
        return when {
            root == null -> throw NoSuchElementException("Key $key not found")
            key < root.key -> showKeyValue(root.left, key)
            key > root.key -> showKeyValue(root.right, key)
            else -> root
        }
    }

    private fun<K : Comparable<K>,V> height(node: AVLTree.AVLNode<K, V>?) : Int {
        if(node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }
}
