package LibTree.trees

import LibTree.interfaceTree.Tree
import kotlin.math.max

class RBTree<K : Comparable<K>,V>(
    private var root : RBNode<K,V>? = null,
) : Tree<K,V, RBTree<K, V>> {

    enum class Color {
        RED,
        BLACK,
    }

    class RBNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        override var left: BaseNode<K, V>? = null,
        override var right: BaseNode<K, V>? = null,
        override var height: Long = 1,
        var color: Color = Color.RED,
        var parent: RBNode<K, V>? = null
    ) : BaseNode<K, V, RBNode<K, V>>(key, value, left, right, height)

    // операции вставки и удаления, которым необходимы повороты и фикса вставки
    override fun insert(key: K, value: V) {
        val newNode = RBNode(key, value)
        var y: RBNode<K, V>? = null
        var x = root

        while (x != null) {
            y = x
            x = if (newNode.key < x.key) x.left as? RBNode<K, V> else x.right as? RBNode<K, V>
        }

        newNode.parent = y
        when {
            y == null -> root = newNode
            newNode.key < y.key -> y.left = newNode
            else -> y.right = newNode
        }
        fixInsertion(newNode)
    }

    override fun erase(key: K) {
        TODO("слишком сложна")
    }
    private fun transplant(u: RBNode<K, V>, v: RBNode<K, V>?) {
        TODO("для erase")
    }
    private fun minimum(node : RBNode<K, V>) : RBNode<K, V> {
        TODO("для erase ")
    }
    private fun rotateLeft(x: RBNode<K, V>) {
        val y = x.right as? RBNode<K, V> ?: return
        x.right = y.left
        if (y.left != null) {
            (y.left as RBNode<K, V>).parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x == x.parent?.left) {
            x.parent?.left = y
        } else {
            x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rotateRight(x: RBNode<K, V>) {
        val y = x.left as? RBNode<K, V> ?: return
        x.left = y.right
        if (y.right != null) {
            (y.right as RBNode<K, V>).parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x == x.parent?.right) {
            x.parent?.right = y
        } else {
            x.parent?.left = y
        }
        y.right = x
        x.parent = y
    }

    public fun fixInsertion(current : RBNode<K, V>) {
        var node = current
        while (node.parent?.color == Color.RED) {
            if (node.parent == node.parent?.parent?.left) {
                val y = node.parent?.parent?.right
                if (y?.color == Color.RED) {
                    node.parent?.color = Color.BLACK
                    y.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node = node.parent?.parent!! // todo
                } else {
                    if (node == node.parent?.right) {
                        node = node.parent!!
                        rotateLeft(node)
                    }
                    node.parent?.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    rotateRight(node.parent?.parent!!)
                }
            } else {
                val y = node.parent?.parent?.left
                if (y?.color == Color.RED) {
                    node.parent?.color = Color.BLACK
                    y.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node = node.parent?.parent!!
                } else {
                    if (node == node.parent?.left) {
                        node = node.parent!!
                        rotateLeft(node)
                    }
                    node.parent?.color = BLACK
                    node.parent?.parent?.color = Color.RED
                    rotateLeft(node.parent?.parent!!)
                }
            }
        }
        root?.color = Color.BLACK
    }

    override fun height(): Long = height(root)

    private fun height(node: BaseNode<K, V>?): Long =
        if (node == null) {
            return 0
        } else {
            return (1 + max(height(node.left), height(node.right)))
        }

    override fun clean() {
        root = null
    }
    private fun findNode(key: K): RBNode<K, V>? {
        var current = root
        while (current != null) {
            current = when {
                key < current.key -> current.left as? RBNode<K, V>
                key > current.key -> current.right as? RBNode<K, V>
                else -> return current
            }
        }
        return null
    }
    override fun containsKey(key: K): Boolean {
        return findNode(key) != null
    }

    override fun iterator(): Iterator<Pair<K, V>> {
        val list = mutableListOf<Pair<K, V>>()
        fun Order(node: BaseNode<K, V>?) {
            if (node == null) return
            list.add(Pair(node.key, node.value))
            Order(node.left)
            Order(node.right)
        }
        Order(root)
        return list.iterator()
    }
}

