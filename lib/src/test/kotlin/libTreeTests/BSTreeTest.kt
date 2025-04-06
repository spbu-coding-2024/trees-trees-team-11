package libTreeTests

import libTree.trees.BSTree
import libTreeTestsUtils.BSTreeUtilsFunctions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.random.Random

class BSTreeTest {
    private lateinit var checker: BSTreeUtilsFunctions<Int, String>
    private lateinit var tree: BSTree<Int, String>

    @BeforeEach
    fun setUp() {
        tree = BSTree()
        checker = BSTreeUtilsFunctions(tree)
    }

    companion object {
        private val random = Random.Default

        @JvmStatic
        fun heightTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Empty tree
            Arguments.of(
                emptyList<Pair<Int, String>>(),
                0
            ),
            // Test 2: Tree with 1 element
            Arguments.of(
                listOf(10 to "A"),
                1
            ),
            // Test 3: Tree with 2 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B"),
                2
            ),
            // Test 4: Tree with 3 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                2
            ),
            // Test 5: Tree with 4 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C", 1 to "D"),
                3
            )
        )

        @JvmStatic
        fun containKeyTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Empty tree
            Arguments.of(
                emptyList<Pair<Int, String>>(),
                88,
                false
            ),
            // Test 2: Tree with 1 element
            Arguments.of(
                listOf(10 to "A"),
                10,
                true
            ),
            // Test 3: Tree with 2 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B"),
                99,
                false
            )
        )

        @JvmStatic
        fun eraseTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Tree with 1 element
            Arguments.of(
                listOf(10 to "A"),
                10,
                emptyList<Int>()
            ),
            // Test 2: Tree with 2 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B"),
                5,
                listOf(10)
            ),
            // Test 3: Tree with 3 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                10,
                listOf(5, 15)
            ),
            // Test 4: Tree with 4 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C", 1 to "D"),
                5,
                listOf(1, 10, 15)
            )
        )

        @JvmStatic
        fun insertTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Insert 1 element
            Arguments.of(
                listOf(10 to "A"),
                listOf(10)
            ),
            // Test 2: Insert 2 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B"),
                listOf(5, 10)
            ),
            // Test 3: Insert 3 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C"),
                listOf(5, 10, 15)
            ),
            // Test 4: Insert 4 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C", 1 to "D"),
                listOf(1, 5, 10, 15)
            ),
            // Test 5: Insert 5 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C", 1 to "D", 7 to "E"),
                listOf(1, 5, 7, 10, 15)
            )
        )

        @JvmStatic
        fun cleanTestCases(): Stream<Arguments> = Stream.of(
            // Test 1: Tree with 1 element
            Arguments.of(
                listOf(10 to "A")
            ),
            // Test 2: Tree with 3 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C")
            ),
            // Test 3: Tree with 5 elements
            Arguments.of(
                listOf(10 to "A", 5 to "B", 15 to "C", 1 to "D", 7 to "E")
            )
        )
    }

    @ParameterizedTest
    @MethodSource("heightTestCases")
    fun testHeight(inserts: List<Pair<Int, String>>, expectedHeightOfTree: Int) {
        inserts.forEach { (k, v) -> tree.insert(k, v) }
        assertEquals(expectedHeightOfTree, tree.height())
        assertTrue(checker.checkTree())
    }

    @ParameterizedTest
    @MethodSource("containKeyTestCases")
    fun testContainKey(inserts: List<Pair<Int, String>>, keyToCheck: Int, expected: Boolean) {
        inserts.forEach { (k, v) -> tree.insert(k, v) }
        assertEquals(expected, tree.containsKey(keyToCheck))
        assertTrue(checker.checkTree())
    }

    @ParameterizedTest
    @MethodSource("eraseTestCases")
    fun testErase(inserts: List<Pair<Int, String>>, keyToDelete: Int, expected: List<Int>) {
        inserts.forEach { (k, v) -> tree.insert(k, v) }
        tree.erase(keyToDelete)
        assertEquals(expected, checker.toKeyList())
        assertTrue(checker.checkTree())
    }

    @ParameterizedTest
    @MethodSource("insertTestCases")
    fun testInsert(inserts: List<Pair<Int, String>>, expected: List<Int>) {
        inserts.forEach { (k, v) -> tree.insert(k, v) }
        assertEquals(expected, checker.toKeyList())
        assertTrue(checker.checkTree())
    }

    @ParameterizedTest
    @MethodSource("cleanTestCases")
    fun testClean(inserts: List<Pair<Int, String>>) {
        inserts.forEach { (k, v) -> tree.insert(k, v) }
        tree.clean()
        assertEquals(0, tree.height())
        assertTrue(checker.toKeyList().isEmpty())
    }

    @Test
    fun `erase node with two children`() {
        tree.insert(10, "A")
        tree.insert(5, "B")
        tree.insert(15, "C")
        tree.erase(10)
        assertFalse(tree.containsKey(10))
        assertTrue(checker.checkTree())
    }

    @Test
    fun `tree should contain duplicate inserts correctly`() {
        tree.insert(66, "A")
        tree.insert(66, "B")
        assertEquals(1, checker.countNodes())
    }

    @Test
    fun `should return keys in sorted order`() {
        tree.insert(50, "A")
        tree.insert(30, "B")
        tree.insert(70, "C")
        tree.insert(20, "D")
        tree.insert(40, "E")

        val expected = listOf(20, 30, 40, 50, 70)
        assertEquals(expected, checker.toKeyList())
    }

    @Test
    fun `iterator on empty tree`() {
        assertFalse(tree.iterator().hasNext())
    }

    @Test
    @Tag("slow")
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun randomOperations() {
        val countOfInsertions = Random.nextInt(0, 10_000)
        repeat(countOfInsertions) {
            tree.insert(Random.nextInt(0, 999), Random.nextInt(0, 999).toString())
        }

        val countOfDeletions = Random.nextInt(0, countOfInsertions)
        repeat(countOfDeletions) {
            tree.erase(Random.nextInt(0, 999))
        }

        assertTrue(checker.checkTree(), "BST properties violated")
    }
}
