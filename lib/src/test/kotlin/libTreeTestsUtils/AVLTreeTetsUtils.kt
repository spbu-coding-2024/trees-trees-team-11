package libTreeTestsUtils
import libTree.trees.AVLTree
import kotlin.math.abs
import kotlin.math.max

class UtilsFunctions {

    fun<K : Comparable<K>,V : Any> is_balanced(root: AVLTree.AVLNode<K, V>?): Boolean {

        if(root == null) return false

        val lhs = height(root.left)
        val rhs = height(root.right)

        if (abs(rhs - lhs) > 2) return false

        return is_balanced(root.left) && is_balanced(root.right)
    }

    private fun<K : Comparable<K>,V> height(node: AVLTree.AVLNode<K, V>?) : Int {
        if(node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }





}