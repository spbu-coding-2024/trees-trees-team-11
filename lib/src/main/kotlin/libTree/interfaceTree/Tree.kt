package libTree.interfaceTree

interface Tree<K : Comparable<K>,V, root> : Iterable<Pair<K,V>> {
    fun height(): Int
    fun containsKey(key: K): Boolean
    fun erase(key: K)
    fun insert(key: K, value: V)
    fun clean()
}
