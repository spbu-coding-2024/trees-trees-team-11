package libTree.trees

open class BaseNode<K: Comparable<K>,V, TreeNode> (
    var key: K,
    var value: V,
    internal open var left: TreeNode? = null,
    internal open var right: TreeNode? = null,
    internal open val height: Long,
)
