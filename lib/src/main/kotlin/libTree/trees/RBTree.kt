package libTree.trees

import libTree.interfaceTree.Tree
import kotlin.math.max
/*
инварианты кч:
    1) каждый узел - красный или черный
    2) корень и листья - черные
    3) у каждого красного узла родительский узел - черный
    4) все пути из узла содержат одинаковое количество черных узлов
    5) черный узел может иметь черного родителя
 */
class RBTree<K : Comparable<K>,V> private constructor(
    private var root : RBNode<K,V>? = null,
) : Tree<K,V, RBTree.RBNode<K, V>> {

    enum class Color {
        RED,
        BLACK,
    }
    // узел КЧ дерева
    class RBNode<K : Comparable<K>, V>(
        key: K,
        value: V,
        override var left: RBNode<K, V>? = null,
        override var right: RBNode<K, V>? = null,
        override var height: Long = 1,
        var color: Color = Color.RED,
        internal var parent: RBNode<K, V>? = null
    ) : BaseNode<K, V, RBNode<K, V>>(key, value, left, right, height)

    constructor() : this(null)
    // вставка элемента
    override fun insert(key: K, value: V) {
        val newNode = RBNode(key, value)
        var y: RBNode<K, V>? = null
        var x = root

        while (x != null) { // ищем место для вставки нового узла
            y = x
            if (newNode.key < x.key) {
                x = x.left 
            } else {
                x = x.right 
            }
        }

        newNode.parent = y
        when {
            y == null -> root = newNode // в дереве не было узлов
            newNode.key < y.key -> y.left = newNode
            else -> y.right = newNode
        }
        fixInsertion(newNode) // фиксим цвета после вставки
    }

    // балансировка дерева после вставки элемента
    private fun fixInsertion(current : RBNode<K, V>) {
        var node= current
        while (node.parent?.color == Color.RED) { // нарушено 3 свойство
            if (node.parent == node.parent?.parent?.left) { // если родитель узла находится слева
                // от дедушки узла
                val uncle = node.parent?.parent?.right 
                if (uncle?.color == Color.RED) { // дядя красный => меняем дяде и родителю узла
                    // цвета на черный, а деду - на красный
                    node.parent?.color = Color.BLACK
                    uncle.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node = node.parent?.parent!! // возможно поломали инварианты => цикл от деда узла
                } else { // дядя черный
                    if (node == node.parent?.right) { // узел правый потомок => делаем левым
                        node = node.parent!!
                        rotateLeft(node)
                    }
                    node.parent?.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    rotateRight(node.parent?.parent!!) 
                }
            } else {// если родитель узла находится справа от дедушки узла,
                // делаем все аналогично
                val uncle = node.parent?.parent?.left 
                if (uncle?.color == Color.RED) {
                    node.parent?.color = Color.BLACK
                    uncle.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    node = node.parent?.parent!!
                } else {
                    if (node == node.parent?.left) {
                        node = node.parent!!
                        rotateRight(node)
                    }
                    node.parent?.color = Color.BLACK
                    node.parent?.parent?.color = Color.RED
                    rotateLeft(node.parent?.parent!!)
                }
            }
        }
        root?.color = Color.BLACK // делаем корень черным
    }
    // удаление элемента по ключу
    override fun erase(key: K) {
        val z = findNode(key) ?: return
        var y = z
        var yOriginalColor = y.color
        var x : RBNode<K,V>? = null

        if (z.left == null) {
            x = z.right 
            transplant(z, z.right )
        } else if (z.right == null) {
            x = z.left 
            transplant(z, z.left )
        } else {
            y = minimum(z.right as RBNode<K,V>)
            yOriginalColor = y.color
            x = y.right 
            if (y.parent == z) {
                if (x != null) {
                    x.parent = y
                }
            } else {
                transplant(y, y.right )
                y.right = z.right
                (y.right )?.parent = y
            }
            transplant(z, y)
            y.left = z.left
            (y.left )?.parent = y
        }
        if (yOriginalColor == Color.BLACK && x != null) {
            fixDeletion(x)
        }
    }
    // замена поддерева, корень которого u, поддеревом vя
    private fun transplant(u: RBNode<K, V>, v: RBNode<K, V>?) {
        if (u.parent == null) {
            root = v
        } else if (u == u.parent?.left) {
            u.parent?.left = v
        } else {
            u.parent?.right = v
        }
        if (v != null) {
            v.parent = u.parent
        }
    }
    // для поиска минимального элемента в дереве
    private fun minimum(node : RBNode<K, V>) : RBNode<K, V> {
        var current = node
        while (current.left != null) {
            current = current.left as RBNode<K,V>
        }
        return current
    }
    // поворот влево относительно узла x
    private fun rotateLeft(x: RBNode<K, V>) {
        val y = x.right  ?: return
        x.right = y.left
        if (y.left != null) {
            (y.left as RBNode<K, V>).parent = x
        }
        y.parent = x.parent
        if (x.parent == null) { // нечего поворачивать
            root = y
        } else if (x == x.parent?.left) { // узел слава от родителя
            x.parent?.left = y
        } else {
            x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }
    // поворот вправо относительно узла x, аналогично rotateLeft
    private fun rotateRight(x: RBNode<K, V>) {
        val y = x.left  ?: return
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
    // перекраска узлов после удаления элемента
    private fun fixDeletion(x: RBNode<K, V>) {
        var node = x
        while (node != root && node.color == Color.BLACK) {
            if (node.parent != null && node == node.parent!!.left) {
                var w = node.parent!!.right 
                if (w != null && w.color == Color.RED) {
                    // cлучай 1 брат узла красный
                    w.color = Color.BLACK
                    node.parent!!.color = Color.RED
                    rotateLeft(node.parent!!)
                    w = node.parent!!.right 
                }
                if (w == null ||
                    (((w.left )?.color ?: Color.BLACK) == Color.BLACK &&
                            ((w.right )?.color ?: Color.BLACK) == Color.BLACK)
                ) {
                    // cлучай 2 брат черный, оба его потомка черные
                    w?.color = Color.RED
                    node = node.parent!!
                } else {
                    if (((w.right )?.color ?: Color.BLACK) == Color.BLACK) {
                        // cлучай 3 брат черный, левый потомок красный, правый черный
                        (w.left )?.color = Color.BLACK
                        w.color = Color.RED
                        rotateRight(w)
                        w = node.parent!!.right 
                    }
                    // cлучай 4 брат черный, правый потомок красный
                    w?.color = node.parent!!.color
                    node.parent!!.color = Color.BLACK
                    (w?.right )?.color = Color.BLACK
                    rotateLeft(node.parent!!)
                    node = root!!
                }
            } else if (node.parent != null) {
                var w = node.parent!!.left 
                if (w != null && w.color == Color.RED) {
                    // cимметричные случаи
                    w.color = Color.BLACK
                    node.parent!!.color = Color.RED
                    rotateRight(node.parent!!)
                    w = node.parent!!.left 
                }
                if (w == null ||
                    (((w.left )?.color ?: Color.BLACK) == Color.BLACK &&
                            ((w.right )?.color ?: Color.BLACK) == Color.BLACK)
                ) {

                    w?.color = Color.RED
                    node = node.parent!!
                } else {
                    if (((w.left )?.color ?: Color.BLACK) == Color.BLACK) {

                        (w.right )?.color = Color.BLACK
                        w.color = Color.RED
                        rotateLeft(w)
                        w = node.parent!!.left 
                    }

                    w?.color = node.parent!!.color
                    node.parent!!.color = Color.BLACK
                    (w?.left )?.color = Color.BLACK
                    rotateRight(node.parent!!)
                    node = root!!
                }
            } else {
                break
            }
        }
        node.color = Color.BLACK
    }

    override fun height(): Int = height(root)
   
    
    // рекурсивный поиск высоты
    private fun height(node: RBNode<K, V>?): Int {

        if (node == null) {
            return 0
        } else {
            return (1 + max(height(node.left), height(node.right)))
        }
    }

    // очистка дерева
    override fun clean() {
        root = null
    }
    // поиск элемента по ключу
    private fun findNode(key: K): RBNode<K, V>? {
        var current = root
        while (current != null) {
            current = when {
                key < current.key -> current.left 
                key > current.key -> current.right 
                else -> return current
            }
        }
        return null
    }

    // использует findNode
    override fun containsKey(key: K): Boolean {
        return findNode(key) != null
    }

    //рекурсивный обход дерева
    override fun iterator(): Iterator<Pair<K, V>> {
        val list = mutableListOf<Pair<K, V>>()
        fun Order(node: RBNode<K, V>?) {
            if (node == null) return
            list.add(Pair(node.key, node.value))
            Order(node.left)
            Order(node.right)
        }
        Order(root)
        return list.iterator()
    }
}


