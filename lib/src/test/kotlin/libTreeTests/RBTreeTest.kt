package libTreeTests

import libTree.trees.RBTree
import libTreeTestsUtils.UtilsFunctionsRBTree
import org.junit.jupiter.api.* // main annotations
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.random.Random

open class RBTreeTest {

    private lateinit var checker : UtilsFunctionsRBTree<Int, String>
    private lateinit var tree : RBTree<Int, String>

    @BeforeEach
    fun setupTest() {
        tree = RBTree()
        checker = UtilsFunctionsRBTree(tree)
    }

    companion object {
        @JvmStatic
        fun insertTestCases() : Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                listOf(10, 5, 15)
            ),
            Arguments.of(
                listOf(30 to "A", 20 to "B", 10 to "C"),
                listOf(20, 10, 30)
            ),
            Arguments.of(
                listOf(10 to "A", 30 to "B", 20 to "C"),
                listOf(20, 10, 30)
            ),
            Arguments.of(
                listOf(50 to "A", 30 to "B", 70 to "C", 20 to "D", 40 to "E", 60 to "F", 80 to "G"),
                listOf(50, 30, 20, 40, 70, 60, 80)
            ),
            Arguments.of(
                listOf(30 to "A", 10 to "B", 20 to "C"),
                listOf(20, 10, 30)
            ),
            Arguments.of(
                listOf(10 to "A", 20 to "B", 30 to "C"),
                listOf(20, 10, 30)
            )
        )

        @JvmStatic
        fun deleteTestCases() : Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                5,
                listOf(10, 15)
            ),
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                10,
                listOf(15, 5)
            ),
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C", 12 to "D"),
                15,
                listOf(10, 5, 12)
            ),
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                100,
                listOf(10, 5, 15)
            )
        )
    }

    @ParameterizedTest
    @MethodSource("insertTestCases")
    fun testInsert(insertions : List<Pair<Int, String>>, expected : List<Int>) {
        for ((key, value) in insertions) {
            tree.insert(key, value)

            assertTrue(checker.checkTree(), "RBTree invariant violated after inserting $key")
        }
        val result = tree.iterator().asSequence().map {it.first}.toList()
        assertEquals(expected, result, "Preorder traversal does not match expected order")
    }

    @ParameterizedTest
    @MethodSource("deleteTestCases")
    fun testDelete(insertions : List<Pair<Int, String>>, deleteKey : Int, expected : List<Int>) {
        for ((key, value) in insertions) {
            tree.insert(key, value)

            assertTrue(checker.checkTree(), "RBTree invariant violated after inserting $key")
        }
        tree.erase(deleteKey)
        assertTrue(checker.checkTree(), "RBTree invariant violated after deleting $deleteKey")
        val result = tree.iterator().asSequence().map { it.first}.toList()
        assertEquals(expected, result, "Preorder traversal after deletion does not match expected order")
    }

    @Test
    fun testContainsKey() {
        assertFalse(tree.containsKey(10), "Tree should not contain key 10 initially")
        tree.insert(10, "A")
        assertTrue(tree.containsKey(10), "Tree should contain key 10 after insertion")
        tree.erase(10)
        assertFalse(tree.containsKey(10), "Tree should not contain key 10 after deletion")
    }

    @Test
    fun testClean() {
        tree.insert(10, "A")
        tree.insert(5, "B")
        tree.insert(15, "C")
        tree.clean()
        assertFalse(tree.iterator().hasNext(), "Tree should be empty after cleaning")
    }

    @Test
    fun testDublicateInsertion() {
        tree.insert(10, "A")
        tree.insert(10, "B")
        val keys = tree.iterator().asSequence().map { it.first}.toList()
        // double keys are permitted in this implementation of RBTree
        assertEquals(listOf(10, 10), keys, "Tree should contain two nodes with key 10")
    }

    @Test
    fun testRandomInsertionsDeletions() {
        val random = Random(42)
        val inserted = mutableSetOf<Int>()
        for (i in 1..1000) {
            val key = random.nextInt(0,100)
            tree.insert(key, key.toString())
            inserted.add(key)
            assertTrue(checker.checkTree(), "RBTree invariant violated after inserting $key")
        }
        for (key in inserted) {
            tree.erase(key)
            assertTrue(checker.checkTree(), "RBTree invariant violated after deleting $key")
        }
        assertFalse(tree.iterator().hasNext(), "Tree should be empty after deleting all inserted keys")
    }


}