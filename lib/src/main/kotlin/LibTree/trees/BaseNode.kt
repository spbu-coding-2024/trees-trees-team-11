package LibTree.trees


open class BaseNode<K: Comparable<K>,V, TreeNode> (
    var key: K,
    var value: V,
    protected open var left: TreeNode? = null,
    protected open var right: TreeNode? = null,
    protected open val height: Long,
)