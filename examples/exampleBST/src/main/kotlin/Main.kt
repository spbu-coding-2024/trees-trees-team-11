import libTree.trees.BSTree

fun main() {
    //create AVLTree object
    val tree = BSTree<Int,Int>()

    //inserting node in AVLTree
    tree.insert(4,4)

    //erasing node from AVLTree
    tree.erase(4)

    //clean tree
    tree.clean()


    tree.insert(9,9)
    tree.insert(10,9)
    tree.insert(11,9)
    tree.insert(12,9)
    //checking height of AVLTree
    tree.height()

    //checking for contains key in AVLTree
    tree.containsKey(11)
    tree.containsKey(0)

    //iterating in InOrder
    for(node in tree) {
        print("${node}\n")
    }
}
