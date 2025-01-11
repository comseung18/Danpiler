import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InsertConcatOpTest {

    @Test
    fun testNoConcatWithEmptyGroup() {
        val regex = "()#"
        val expected = "#"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should not insert '.' between empty group '()' and '#'")
    }

    @Test
    fun testMultipleEmptyGroups() {
        val regex = "()()()a()b()c"
        val expected = "a.b.c"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should correctly handle multiple empty groups and insert '.' appropriately")
    }

    @Test
    fun testConcatBetweenLiteralAndGroup() {
        val regex = "a(b)c"
        val expected = "a.(b).c"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should insert '.' between literals and groups")
    }

    @Test
    fun testNoConcatWithinGroup() {
        val regex = "(ab)*"
        val expected = "(a.b)*"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should not insert '.' within a group")
    }

    @Test
    fun testConcatWithClosure() {
        val regex = "a*b"
        val expected = "a*.b"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should not insert '.' between '*' and 'b'")
    }

    @Test
    fun testMultipleConcats() {
        val regex = "a(b|c)d"
        val expected = "a.(b|c).d"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should insert '.' between 'a' and '(b|c)', and between '(b|c)' and 'd'")
    }

    @Test
    fun testConcatWithStartAndEnd() {
        val regex = "(a|b)*c#"
        val expected = "(a|b)*.c.#"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should insert '.' between '(a|b)*' and 'c', but not before '#'")
    }

    @Test
    fun testNoConcatWithOnlyOperators() {
        val regex = "*+?#"
        val expected = "*+?.#"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should not insert '.' between consecutive operators")
    }

    @Test
    fun testEmptyRegex() {
        val regex = ""
        val expected = ""
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Empty regex should remain unchanged")
    }

    @Test
    fun testSingleCharacter() {
        val regex = "a"
        val expected = "a"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Single character regex should remain unchanged")
    }

    @Test
    fun testConcatAfterEmptyGroup() {
        val regex = "()a"
        val expected = "a"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should not insert '.' before 'a' when preceded by empty group '()'")
    }

    @Test
    fun testConcatBeforeEmptyGroup() {
        val regex = "a()"
        val expected = "a"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should not insert '.' after 'a' when followed by empty group '()'")
    }

    @Test
    fun testNestedEmptyGroups() {
        val regex = "((())())a(b())c"
        val expected = "a.(b).c"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should correctly handle nested empty groups and insert '.' appropriately")
    }

    @Test
    fun testThrowExceptionForForbiddenChars() {
        val regex = ".a"
        assertThrows<IllegalArgumentException> {
            insertExplicitConcatOp(regex)
        }
    }

    @Test
    fun testConcatWithNonEmptyAndEmptyGroups() {
        val regex = "(a)b()c"
        val expected = "(a).b.c"
        val actual = insertExplicitConcatOp(regex)
        assertEquals(expected, actual, "Should correctly handle mix of non-empty and empty groups")
    }
}