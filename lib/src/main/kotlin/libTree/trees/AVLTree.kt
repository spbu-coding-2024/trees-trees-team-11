package libTree.trees

import libTree.interfaceTree.Tree
import kotlin.math.max

class AVLTree<K : Comparable<K>, V> private constructor (
    private var root: AVLNode<K, V>? = null,
) : Tree<K, V, AVLTree.AVLNode<K, V>> {

    class AVLNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        public override var left: AVLNode<K, V>? = null,
        public override var right: AVLNode<K, V>? = null,
        public override var height: Long = 1
    ) : BaseNode<K, V, AVLNode<K, V>>(key, value, left, right, height) {
    }

    fun getRoot() : AVLNode<K, V>? = root
    fun setRoot(node: AVLNode<K,V>?) {
        root = node
    }

    constructor() : this(null)


    override fun height(): Int {
        return heightOfTree(root)
    }

    override fun erase(key: K) {
        root = eraseNode(root, key)
    }

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

    override fun insert(key: K, value: V) {
        root = insertNode(root, key, value)
    }

    override fun clean() {
        root = null
    }

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


    private fun heightNode(node: AVLNode<K, V>?): Long {
        if (node == null) return 0
        return node.height
    }


    private fun rightRotate(node: AVLNode<K, V>?): AVLNode<K, V>? {
        val newNode = node?.left
        val tempNode = newNode?.right

        newNode?.right = node
        node?.left = tempNode

        node?.height = 1 + max(heightNode(node?.left), heightNode(node?.right))
        newNode?.height = 1 + max(heightNode(newNode?.left), heightNode(newNode?.right))

        return newNode
    }

    private fun leftRotate(node: AVLNode<K, V>?): AVLNode<K, V>? {
        val newNode = node?.right
        val tempNode = newNode?.left

        newNode?.left = node
        node?.right = tempNode

        node?.height = 1 + max(heightNode(node?.left), heightNode(node?.right))
        newNode?.height = 1 + max(heightNode(newNode?.left), heightNode(newNode?.right))

        return newNode
    }

    private fun getBalance(node: AVLNode<K, V>?): Long {
        if (node == null) {
            return 0
        }
        return heightNode(node.left) - heightNode(node.right)
    }

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

    private fun heightOfTree(node: AVLNode<K, V>?): Int {
        if (node == null) return 0
        return 1 + max(heightOfTree(node.left), heightOfTree(node.right))
    }

    private fun minValueNode(node: AVLNode<K, V>?): AVLNode<K, V>? {
        if (node?.left == null)
            return node
        return minValueNode(node.left)
    }
}
