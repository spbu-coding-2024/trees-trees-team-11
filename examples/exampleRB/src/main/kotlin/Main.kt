import libTree.trees.RBTree

fun main() {
    //create RBTree object
    val tree = RBTree<Int,Int>()

    //inserting node in RBTree
    tree.insert(4,4)

    //erasing node from RBTree
    tree.erase(4)

    //clean tree
    tree.clean()


    tree.insert(9,9)
    tree.insert(10,9)
    tree.insert(11,9)
    tree.insert(12,9)
    //checking height of RBTree
    tree.height()

    //checking for contains key in RBTree
    tree.containsKey(11)
    tree.containsKey(0)
    //iterating in preOrder
    for(node in tree) {
        print("${node}\n")
    }
}
