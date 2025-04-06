import libTree.trees.BSTree

fun main() {
    //create BSTree object
    val tree = BSTree<Int,Int>()

    //inserting node in BSTree
    tree.insert(4,4)

    //erasing node from BSTree
    tree.erase(4)

    //clean tree
    tree.clean()


    tree.insert(9,9)
    tree.insert(10,9)
    tree.insert(11,9)
    tree.insert(12,9)
    //checking height of BSTree
    tree.height()

    //checking for contains key in BSTree
    tree.containsKey(11)
    tree.containsKey(0)

    //iterating in InOrder
    for(node in tree) {
        print("${node}\n")
    }
}
