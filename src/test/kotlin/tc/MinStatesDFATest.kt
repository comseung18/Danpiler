package tc

import DFA
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MinStatesDFATest {

    // Helper function to create and minimize DFA
    private fun createAndMinimizeDFA(regex: String): Pair<DFA, DFA> {
        val originalDFA = DFA.toDirectDFA(regex)
        val minimizedDFA = DFA.stateMinimizedDFA(originalDFA)
        return Pair(originalDFA, minimizedDFA)
    }

    // Helper function to test that both DFAs accept/reject the same strings
    private fun assertDFAEquality(originalDFA: DFA, minimizedDFA: DFA, testStrings: List<String>) {
        for (input in testStrings) {
            val originalMatch = originalDFA.match(input)
            val minimizedMatch = minimizedDFA.match(input)
            assertEquals(
                originalMatch, minimizedMatch,
                "DFA and Minimized DFA should have the same match result for input '$input'"
            )
        }
    }

    @Test
    fun testSingleCharacter() {
        val regex = "a"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("a", "b", "", "aa", "ab")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        // Optional: Check state counts
        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testMultipleCharacters() {
        val regex = "abc"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("abc", "ab", "abcd", "aabc", "", "abcabc")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testUnion() {
        val regex = "a|b"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("a", "b", "c", "", "ab", "ba")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testConcatenation() {
        val regex = "ab"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("ab", "a", "b", "abc", "aab", "")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testKleeneStar() {
        val regex = "a*"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("", "a", "aa", "aaa", "b", "aab", "aba")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testPlus() {
        val regex = "a+"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("", "a", "aa", "aaa", "b", "aab", "aba")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testOptional() {
        val regex = "a?"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("", "a", "aa", "b", "ab")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testComplexPattern() {
        val regex = "(a|b)*c(d|e)*"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "c", "ac", "bc", "aabccddd", "abccdeee",
            "aabcccdddeee", "abccx", "abccc", "ccdd", "abcd"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testNestedAlternationAndRepetition() {
        val regex = "a(b|c)*d"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "ad", "abd", "acbd", "abcbcd", "aabccddd",
            "a", "abcdx", "abcbac", "abcdabcd"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testMultipleAlternations() {
        val regex = "a|b|c|d"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("a", "b", "c", "d", "e", "ab", "abc", "")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testAlternationWithRepetition() {
        val regex = "(ab|cd)+ef"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "abef", "abcdabef", "cdabef",
            "ab", "abefg", "abefcd", "ef"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testMultipleQuantifiers() {
        val regex = "a*b+c?d+"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("aaabccccd")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testComplexNestedGroups() {
        val regex = "a(bc|de(f|g)h)*i"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "ai", "abci", "adefhhi", "abciadefghi",
            "a", "ab", "abcdefg", "abciadefghx", "abccc"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testNestedRepetitionAndAlternation() {
        val regex = "((a|b)+c)*d"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "d", "acd", "bacd", "aabcbacd",
            "aabcbac", "aabcbacdxx"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testRepetitionWithMultipleGroups() {
        val regex = "(ab|cd)*ef(g|h)+"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "efg", "abcdabefgh", "efghh",
            "ef", "abcdabef", "efgx", "efgh", "cdabefg"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testLongPattern() {
        val regex = "a(b|c)*d(e|f)+g?h"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf(
            "adh", "abbdfeh", "acbcdefgh", "abccdeefgh",
            "abccdeefg", "aebh", "abccdeeghx",
            "aabccdeefgh", "abbdfegh"
        )
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testEmptyRegex() {
        val regex = ""
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("", "a", "b")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testRepeatedCloners() {
        val regex = "a*a*"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("", "a", "aa", "aaa", "b", "aab", "aba")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testMultipleClosureOperators() {
        val regex = "a*+b*"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("", "a", "aa", "b", "bb", "aab", "aaabbb", "ab", "aabbab")
        assertDFAEquality(originalDFA, minimizedDFA, testStrings)

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testInvalidRegex_MismatchedParentheses() {
        val regex = "(a|b"

        // Expecting an exception due to mismatched parentheses
        assertThrows<IllegalArgumentException> {
            DFA.toDirectDFA(regex)
        }
    }


    @Test
    fun testEscapedSpecialCharacters() {
        val regex = "\\*\\+\\?"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("*+?", "*+", "+?", "", "*+?extra")
        val expectedMatches = listOf(true, false, false, false, false)

        for ((input, expected) in testStrings.zip(expectedMatches)) {
            assertEquals(expected, originalDFA.match(input), "Original DFA should match input '$input' as $expected")
            assertEquals(expected, minimizedDFA.match(input), "Minimized DFA should match input '$input' as $expected")
        }

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testEscapedParentheses() {
        val regex = "\\(a\\|b\\)"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("(a|b)", "a|b", "(a|b", "", "(a|b)extra")
        val expectedMatches = listOf(true, false, false, false, false)

        for ((input, expected) in testStrings.zip(expectedMatches)) {
            assertEquals(expected, originalDFA.match(input), "Original DFA should match input '$input' as $expected")
            assertEquals(expected, minimizedDFA.match(input), "Minimized DFA should match input '$input' as $expected")
        }

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testEscapedBackslash() {
        val regex = "a\\\\b"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("a\\b", "ab", "a\\bb", "a\\b\\b")
        val expectedMatches = listOf(true, false, false, false)

        for ((input, expected) in testStrings.zip(expectedMatches)) {
            assertEquals(expected, originalDFA.match(input), "Original DFA should match input '$input' as $expected")
            assertEquals(expected, minimizedDFA.match(input), "Minimized DFA should match input '$input' as $expected")
        }

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testComplexEscapedPattern() {
        val regex = "\\[a\\-z\\]\\*"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("[a-z]*", "[a-z]", "a-z*", "[a-z]*extra")
        val expectedMatches = listOf(true, false, false, false)

        for ((input, expected) in testStrings.zip(expectedMatches)) {
            assertEquals(expected, originalDFA.match(input), "Original DFA should match input '$input' as $expected")
            assertEquals(expected, minimizedDFA.match(input), "Minimized DFA should match input '$input' as $expected")
        }

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }

    @Test
    fun testEscapedCharactersWithConcatenation() {
        val regex = "\\(a\\)b\\*"
        val (originalDFA, minimizedDFA) = createAndMinimizeDFA(regex)

        val testStrings = listOf("(a)b*", "(a)b", "(a)b**", "(a)", "(a)b*extra")
        val expectedMatches = listOf(true, false, false, false, false)

        for ((input, expected) in testStrings.zip(expectedMatches)) {
            assertEquals(expected, originalDFA.match(input), "Original DFA should match input '$input' as $expected")
            assertEquals(expected, minimizedDFA.match(input), "Minimized DFA should match input '$input' as $expected")
        }

        assertTrue(minimizedDFA.getNodesForTest().size <= originalDFA.getNodesForTest().size, "Minimized DFA should have fewer or equal states")
    }
}