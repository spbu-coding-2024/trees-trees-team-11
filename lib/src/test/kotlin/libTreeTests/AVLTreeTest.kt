package libTreeTests

import org.junit.jupiter.api.Test;
import libTree.trees.AVLTree
import libTreeTestsUtils.UtilsFunctions
import org.junit.Assert.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

class AVLTreeTest<K : Comparable<K>,V> {
    private lateinit var balanceChecker: UtilsFunctions
    @BeforeEach
    fun setupTest() {
        balanceChecker = UtilsFunctions()
    }

    @Test
    fun avlTreeTest_with_null_root_insert() {
        val tree = AVLTree(1,1)
        tree.erase(1)
        Assertions.assertTrue(balanceChecker.is_balanced(tree.getRoot()))
    }

    @Test
    fun avlTreeTest_with_only_inserting_nodes() {
        val tree = AVLTree(1,1)
        repeat(30) {
            tree.insert(Random.nextInt(0,999),Random.nextInt(0,999))
        }
        Assertions.assertTrue(balanceChecker.is_balanced(tree.getRoot()))
    }

    @Test
    fun avlTreeTest_checking_for_containsKey() {
        val tree = AVLTree(1,1)
        repeat(100) {
            tree.insert(Random.nextInt(-999,999),Random.nextInt(0,999))
        }
        tree.insert(51,123)
        Assertions.assertTrue(tree.containsKey(51))
    }

    @Test
    fun avlTreeTest_checking_for_clean() {
        val tree = AVLTree(1,1)
        repeat(100) {
            tree.insert(Random.nextInt(-999,999),Random.nextInt(0,999))
        }
        tree.clean()
        val list = mutableListOf<Int>()
        for((key) in tree) {
            list.add(key)
        }
        Assertions.assertEquals(0, list.size)
    }


    @Test
    fun AVLTreeTest_with_only_one_node() {

    }

    @Test
    fun AVLTreeTest_insert() {
        TODO("Not yet implemented")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun TestSetup(): Unit {

        }
    }
}