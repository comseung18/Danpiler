import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DFATest {
    @Test
    fun testSingleCharacter() {
        val regex = "a"
        val nfa = NFA.fromSymbol(Symbol.CharSymbol('a'))
        val dfa = nfa.toDFA() // 단일 토큰 유형 사용

        assertTrue(dfa.match("a"), "Pattern 'a' should match 'a'")
        assertFalse(dfa.match("b"), "Pattern 'a' should not match 'b'")
        assertFalse(dfa.match(""), "Pattern 'a' should not match empty string")
    }

    @Test
    fun testMultipleCharacters() {
        val regex = "abc"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        assertTrue(dfa.match("abc"), "Pattern 'abc' should match 'abc'")
        assertFalse(dfa.match("ab"), "Pattern 'abc' should not match 'ab'")
        assertFalse(dfa.match("abcd"), "Pattern 'abc' should not match 'abcd'")
    }

    @Test
    fun testUnion() {
        val regex = "a|b"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()


        assertTrue(dfa.match("a"), "Pattern 'a|b' should match 'a'")
        assertTrue(dfa.match("b"), "Pattern 'a|b' should match 'b'")
        assertFalse(dfa.match("c"), "Pattern 'a|b' should not match 'c'")
        assertFalse(dfa.match(""), "Pattern 'a|b' should not match empty string")
    }

    @Test
    fun testConcatenation() {
        val regex = "ab"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        assertTrue(dfa.match("ab"), "Pattern 'ab' should match 'ab'")
        assertFalse(dfa.match("a"), "Pattern 'ab' should not match 'a'")
        assertFalse(dfa.match("abc"), "Pattern 'ab' should not match 'abc'")
    }

    @Test
    fun testKleeneStar() {
        val regex = "a*"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        println(dfa.toDot())

        assertTrue(dfa.match(""), "Pattern 'a*' should match empty string")
        assertTrue(dfa.match("a"), "Pattern 'a*' should match 'a'")
        assertTrue(dfa.match("aaa"), "Pattern 'a*' should match 'aaa'")
        assertFalse(dfa.match("aaab"), "Pattern 'a*' should not match 'aaab'")
    }

    @Test
    fun testPlus() {
        val regex = "a+"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        assertFalse(dfa.match(""), "Pattern 'a+' should not match empty string")
        assertTrue(dfa.match("a"), "Pattern 'a+' should match 'a'")
        assertTrue(dfa.match("aaaa"), "Pattern 'a+' should match 'aaaa'")
        assertFalse(dfa.match("aaab"), "Pattern 'a+' should not match 'aaab'")
    }

    @Test
    fun testOptional() {
        val regex = "a?"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()
        println(dfa.toDot())

        assertTrue(dfa.match(""), "Pattern 'a?' should match empty string")
        assertTrue(dfa.match("a"), "Pattern 'a?' should match 'a'")
        assertFalse(dfa.match("aa"), "Pattern 'a?' should not match 'aa'")
    }

    @Test
    fun testComplexPattern() {
        val regex = "(a|b)*c(d|e)*"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        assertTrue(dfa.match("c"), "Pattern '(a|b)*c(d|e)*' should match 'c'")
        assertTrue(dfa.match("ac"), "Pattern '(a|b)*c(d|e)*' should match 'ac'")
        assertTrue(dfa.match("bc"), "Pattern '(a|b)*c(d|e)*' should match 'bc'")
        assertFalse(dfa.match("aabccddd"), "Pattern '(a|b)*c(d|e)*' should match 'aabccddd'")
        assertFalse(dfa.match("abccdeee"), "Pattern '(a|b)*c(d|e)*' should match 'abccdeee'")
        assertFalse(dfa.match("aabcccdddeee"), "Pattern '(a|b)*c(d|e)*' should not match 'aabcccdddeee'")
        assertFalse(dfa.match("abccx"), "Pattern '(a|b)*c(d|e)*' should not match 'abccx'")
        assertFalse(dfa.match("abccc"), "Pattern '(a|b)*c(d|e)*' should not match 'abccc'")
    }

    @Test
    fun testNumbers() {
        val regex = "(0|1|2|3|4|5|6|7|8|9)+"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        assertTrue(dfa.match("0"), "Pattern '[0-9]+' should match '0'")
        assertTrue(dfa.match("12345"), "Pattern '[0-9]+' should match '12345'")
        assertFalse(dfa.match("123a"), "Pattern '[0-9]+' should not match '123a'")
        assertFalse(dfa.match(""), "Pattern '[0-9]+' should not match empty string")
    }

}