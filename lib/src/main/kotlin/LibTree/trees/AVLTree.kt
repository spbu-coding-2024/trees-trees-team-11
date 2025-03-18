package LibTree.trees

import LibTree.interfaceTree.Tree
import kotlin.math.max

class AVLTree<K : Comparable<K>, V>(
    private var root: AVLNode<K, V>? = null,
) : Tree<K, V, AVLTree.AVLNode<K, V>> {

    class AVLNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        public override var left: AVLNode<K,V>? = null,
        public override var right: AVLNode<K,V>? = null,
        public override var height: Long = 1
    ) : BaseNode<K, V, AVLNode<K,V>>(key, value, left, right, height) {
        val balanceFactor: Int
            get() = (left?.height ?: 0).toInt() - (right?.height ?: 0).toInt()
    }

    override fun height(): Int {
        TODO("Not yet implemented")
    }

    override fun erase(key: K) {
        TODO("Not yet implemented")
    }

    override fun containsKey(key: K): Boolean {
        for ((k, _) in this) {
            if (k == key) {
                return true
            }
        }
        return false
    }

    override fun insert(key: K, value: V) {
        root = insertNode(root, key, value)
    }

    private fun balanceNode(node: AVLNode<K, V>?) : AVLNode<K, V> {
       TODO()
    }

    private fun insertNode(node: AVLNode<K,V>?, key: K, value: V) : AVLNode<K, V> {
        if(node == null) {
            return AVLNode(key, value)
        }
        when {
                key < node.key -> node.left = insertNode(node.left, key, value)
                key > node.key -> node.right = insertNode(node.right, key, value)
                else -> node.value = value
        }

        node.height = 1 + max(node.left?.height ?: 0, node.right?.height ?: 0)

        return balanceNode(node)
    }

    override fun clean() {
        root = null
    }

    override fun iterator(): Iterator<Pair<K, V>> {
        val result = mutableListOf<Pair<K, V>>()
        fun nodeIteration(node: AVLNode<K,V>?) {
            if(node == null) return
            result.add(node.key to node.value)
            nodeIteration(node.left)
            nodeIteration(node.right)
        }
        nodeIteration(root)
        return result.iterator()
    }

}

