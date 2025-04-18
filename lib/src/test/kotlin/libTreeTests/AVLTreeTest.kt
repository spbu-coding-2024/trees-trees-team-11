package libTreeTests

import libTree.trees.AVLTree
import libTreeTestsUtils.UtilsFunctions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.random.Random

open class AVLTreeTest {

    private lateinit var checker: UtilsFunctions
    private lateinit var tree: AVLTree<Int, String>

    @BeforeEach
    fun setupTest() {
        checker = UtilsFunctions()
        tree = AVLTree()
        tree.setRoot(null)
    }

    companion object {
        @JvmStatic
        fun insertTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Balanced insertion (no rotations required)
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                listOf(10, 5, 15)
            ),

            // Test 2: LL imbalance (requires single Right rotation)
            Arguments.of(
                listOf(30 to "A", 20 to "B", 10 to "C"),
                listOf(20, 10, 30)
            ),

            // Test 3: RL imbalance (requires Right-Left double rotation)
            Arguments.of(
                listOf(10 to "A", 30 to "B", 20 to "C"),
                listOf(20, 10, 30)
            ),

            // Test 4: Complex structure with balanced insertions (no rotations)
            Arguments.of(
                listOf(50 to "A", 30 to "B", 70 to "C", 20 to "D", 40 to "E", 60 to "F", 80 to "G"),
                listOf(50, 30, 20, 40, 70, 60, 80)
            ),

            // Test 5: LR imbalance (requires Left-Right double rotation)
            Arguments.of(
                listOf(30 to "A", 10 to "B", 20 to "C"),
                listOf(20, 10, 30)
            ),

            // Test 6: RR imbalance (requires single Left rotation)
            Arguments.of(
                listOf(10 to "A", 20 to "B", 30 to "C"),
                listOf(20, 10, 30)
            )

        )
        @JvmStatic
        fun eraseTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Root deletion in RR-imbalanced tree (requires left rotation after deletion)
            // Tests removal of root node with right-right imbalance
            Arguments.of(
                listOf(10 to "A", 20 to "B", 30 to "C"),
                listOf(20),
                listOf(30, 10)
            ),

            // Test 2: Sequential deletions with multiple rotations
            // Removes left child (30), then its replacement (40), then root (50)
            // Tests LR case with multiple rebalancing operations
            Arguments.of(
                listOf(50 to "A", 30 to "B", 70 to "C", 10 to "D", 40 to "E"),
                listOf(30, 40, 50),
                listOf(70,10)
            ),

            // Test 3: Complex deletion with two children and subtree reorganization
            // Removes node (80) with both left and right subtrees
            // Tests case with successor node (90) replacement and multiple rebalancing
            Arguments.of(
                listOf(50 to "A",40 to "K", 80 to "B", 20 to "C", 30 to "D", 60 to "E", 100 to "F", 55 to "G", 70 to "H", 90 to "J", 110 to "I"),
                listOf(80),
                listOf(50, 30, 20, 40, 90, 60, 55, 70, 100, 110)
            ),

            // Test 4: Single child node deletion with height update
            // Removes node (80) with only right child (90)
            // Tests simple promotion of right child and AVL height correction
            Arguments.of(
                listOf(50 to "A",40 to "K", 80 to "B", 90 to "C"),
                listOf(80),
                listOf(50,40,90)
            ),

        )
        @JvmStatic
        fun cleanTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of(listOf(10 to "A", 20 to "B", 30 to "C")),
            Arguments.of(listOf(50 to "X", 25 to "Y"))
        )
        @JvmStatic
        fun containsTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                listOf(5, 10, 20),
                listOf(true, true, false)
            ),
            Arguments.of(
                listOf(30 to "A", 20 to "B", 40 to "C"),
                listOf(20, 25, 40),
                listOf(true, false, true)
            )
        )
        @JvmStatic
        fun heightTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                2
            ),
            Arguments.of(
                listOf(30 to "A", 20 to "B", 10 to "C"),
                2
            ),
            Arguments.of(
                listOf(10 to "A", 20 to "B", 30 to "C", 40 to "D"),
                3
            )
        )
    }

    @ParameterizedTest
    @MethodSource("insertTestCases")
    @Tag("insert")
    fun `Test for function insert in AVLTree`(inserts: List<Pair<Int,String>>, resultNodes: List<Int>) {
        inserts.forEach { (key, value) -> tree.insert(key, value) }
        val treeListKeys = mutableListOf<Int>()
        val treeListPairs = mutableListOf<Pair<Int,String>>()
        for ((key, value) in tree) {
            treeListKeys.add(key)
            treeListPairs.add(key to value)
        }
        assertEquals(resultNodes, treeListKeys)
        assertTrue(checker.is_balanced(tree.getRoot()))
        assertEquals(inserts.size, checker.nodesInTreeCounter(tree))
        val insertsMap = inserts.toMap()
        treeListPairs.forEach { (k,v) ->
            assertEquals(insertsMap[k], v)
        }
    }

    @ParameterizedTest
    @MethodSource("eraseTestCases")
    @Tag("erase")
    fun `Tests for erasing only contains nodes in AVLTree`(inserts: List<Pair<Int,String>>, erases: List<Int>, resultNodes: List<Int>) {
        val treeListKeys = mutableListOf<Int>()
        val treeListPairs = mutableListOf<Pair<Int,String>>()
        inserts.forEach { (key, value) -> tree.insert(key, value) }
        erases.forEach { key ->
            tree.erase(key)
            assertTrue(checker.is_balanced(tree.getRoot()))
        }
        for ((key, value) in tree) {
            treeListKeys.add(key)
            treeListPairs.add(key to value)
        }
        assertEquals(resultNodes, treeListKeys)
        assertEquals(inserts.size - erases.size, checker.nodesInTreeCounter(tree))
        val insertsMap = inserts.toMap()
        treeListPairs.forEach { (k,v) ->
            assertEquals(insertsMap[k], v)
        }
    }


    @ParameterizedTest
    @MethodSource("cleanTestCases")
    @Tag("clean")
    fun `Tests for cleaning AVLTree`(inserts: List<Pair<Int,String>>) {
        inserts.forEach { (key, value) -> tree.insert(key, value) }
        tree.clean()
        assertTrue(checker.is_balanced(tree.getRoot()))
        assertEquals(null, tree.getRoot())
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @Tag("contains")
    fun `Tests for contains keys in AVLTree`(inserts: List<Pair<Int,String>>, contains: List<Int>, flags: List<Boolean>) {
        inserts.forEach { (key, value) -> tree.insert(key, value) }
        contains.zip(flags).forEach {(key,flag) ->
            assertEquals(flag, tree.containsKey(key))
        }
        assertTrue(checker.is_balanced(tree.getRoot()))
    }

    @ParameterizedTest
    @MethodSource("heightTestCases")
    @Tag("height")
    fun `Tests for counting height of AVLTree`(inserts: List<Pair<Int,String>>, correctHeightOfTree: Int) {
        inserts.forEach { (key, value) -> tree.insert(key, value) }
        assertTrue(checker.is_balanced(tree.getRoot()))
        assertEquals(correctHeightOfTree, tree.height())
    }

    @Test
    @Tag("corner-case")
    fun `Test erase non-existing key does not modify tree`() {

        tree.insert(12,"A")
        tree.erase(0)
        println(checker.nodesInTreeCounter(tree))
        assertEquals(1, checker.nodesInTreeCounter(tree))
        assertTrue(checker.is_balanced(tree.getRoot()))
    }

    @Test
    @Tag("corner-case")
    fun `Test for erasing key from empty AVLTree`() {
        tree.erase(0)
        assertEquals(0, checker.nodesInTreeCounter(tree))
        assertTrue(checker.is_balanced(tree.getRoot()))
    }

    @Test
    @Tag("corner-case")
    fun `Test for double insert key in AVLTree`() {
        tree.insert(101, "10")
        tree.insert(101, "12")
        println(checker.nodesInTreeCounter(tree))
        assertEquals(1, checker.nodesInTreeCounter(tree))
        assertEquals("12", checker.showKeyValue(tree.getRoot(), 101).value)
        assertTrue(checker.is_balanced(tree.getRoot()))
    }

    @Test
    @Tag("corner-case")
    fun `Test for double clean in AVLTree`() {
        repeat(10) {
            tree.insert(Random.nextInt(10,100), Random.nextInt(0,100).toString())
        }
        tree.clean()
        tree.clean()
        assertEquals(null, tree.getRoot())
        assertTrue(checker.is_balanced(tree.getRoot()))
    }
    @Test
    @Tag("corner-case")
    fun `Test for checking height of Tree for empty tree`() {
        assertEquals(0, tree.height())
    }

    @Test
    @Tag("slow")
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun `AVLTree inserting to many nodes with random values (stress test)`() {
        repeat(100000) {
            tree.insert(Random.nextInt(0,999), Random.nextInt(0,999).toString())
        }
        repeat(3000) {
            tree.erase(Random.nextInt(0,999))
        }
        assertTrue(checker.is_balanced(tree.getRoot()))
    }
}
