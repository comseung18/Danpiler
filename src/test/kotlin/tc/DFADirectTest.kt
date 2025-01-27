package tc

import DFA
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DFADirectTest {
    @Test
    fun testSingleCharacter() {
        val regex = "a"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("a"), "Pattern 'a' should match 'a'")
        assertFalse(dfa.match("b"), "Pattern 'a' should not match 'b'")
        assertFalse(dfa.match(""), "Pattern 'a' should not match empty string")
    }

    @Test
    fun testMultipleCharacters() {
        val regex = "abc"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("abc"), "Pattern 'abc' should match 'abc'")
        assertFalse(dfa.match("ab"), "Pattern 'abc' should not match 'ab'")
        assertFalse(dfa.match("abcd"), "Pattern 'abc' should not match 'abcd'")
    }

    @Test
    fun testUnion() {
        val regex = "a|b"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("a"), "Pattern 'a|b' should match 'a'")
        assertTrue(dfa.match("b"), "Pattern 'a|b' should match 'b'")
        assertFalse(dfa.match("c"), "Pattern 'a|b' should not match 'c'")
        assertFalse(dfa.match(""), "Pattern 'a|b' should not match empty string")
    }

    @Test
    fun testConcatenation() {
        val regex = "ab"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("ab"), "Pattern 'ab' should match 'ab'")
        assertFalse(dfa.match("a"), "Pattern 'ab' should not match 'a'")
        assertFalse(dfa.match("abc"), "Pattern 'ab' should not match 'abc'")
    }

    @Test
    fun testKleeneStar() {
        val regex = "a*"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match(""), "Pattern 'a*' should match empty string")
        assertTrue(dfa.match("a"), "Pattern 'a*' should match 'a'")
        assertTrue(dfa.match("aaa"), "Pattern 'a*' should match 'aaa'")
        assertFalse(dfa.match("aaab"), "Pattern 'a*' should not match 'aaab'")
    }

    @Test
    fun testPlus() {
        val regex = "a+"
        val dfa = DFA.toDirectDFA(regex)
        assertFalse(dfa.match(""), "Pattern 'a+' should not match empty string")
        assertTrue(dfa.match("a"), "Pattern 'a+' should match 'a'")
        assertTrue(dfa.match("aaaa"), "Pattern 'a+' should match 'aaaa'")
        assertFalse(dfa.match("aaab"), "Pattern 'a+' should not match 'aaab'")
    }

    @Test
    fun testOptional() {
        val regex = "a?"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match(""), "Pattern 'a?' should match empty string")
        assertTrue(dfa.match("a"), "Pattern 'a?' should match 'a'")
        assertFalse(dfa.match("aa"), "Pattern 'a?' should not match 'aa'")
    }

    @Test
    fun testComplexPattern() {
        val regex = "(a|b)*c(d|e)*"
        val dfa = DFA.toDirectDFA(regex)

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
    fun testNestedAlternationAndRepetition() {
        val regex = "a(b|c)*d"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("ad"), "Pattern 'a(b|c)*d' should match 'ad'")
        assertTrue(dfa.match("abd"), "Pattern 'a(b|c)*d' should match 'abd'")
        assertTrue(dfa.match("acbd"), "Pattern 'a(b|c)*d' should match 'acbd'")
        assertTrue(dfa.match("abcbcd"), "Pattern 'a(b|c)*d' should match 'abcbcd'")
        assertFalse(dfa.match("a"), "Pattern 'a(b|c)*d' should not match 'a'")
        assertFalse(dfa.match("abcdx"), "Pattern 'a(b|c)*d' should not match 'abcdx'")
    }

    @Test
    fun testMultipleAlternations() {
        val regex = "a|b|c|d"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("a"), "Pattern 'a|b|c|d' should match 'a'")
        assertTrue(dfa.match("b"), "Pattern 'a|b|c|d' should match 'b'")
        assertTrue(dfa.match("c"), "Pattern 'a|b|c|d' should match 'c'")
        assertTrue(dfa.match("d"), "Pattern 'a|b|c|d' should match 'd'")
        assertFalse(dfa.match("e"), "Pattern 'a|b|c|d' should not match 'e'")
        assertFalse(dfa.match("ab"), "Pattern 'a|b|c|d' should not match 'ab'")
    }

    @Test
    fun testAlternationWithRepetition() {
        val regex = "(ab|cd)+ef"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("abef"), "Pattern '(ab|cd)+ef' should match 'abef'")
        assertTrue(dfa.match("abcdabef"), "Pattern '(ab|cd)+ef' should match 'abcdabef'")
        assertTrue(dfa.match("cdabef"), "Pattern '(ab|cd)+ef' should match 'cdabef'")
        assertFalse(dfa.match("ab"), "Pattern '(ab|cd)+ef' should not match 'ab'")
        assertFalse(dfa.match("abefg"), "Pattern '(ab|cd)+ef' should not match 'abefg'")
        assertFalse(dfa.match("abefcd"), "Pattern '(ab|cd)+ef' should not match 'abefcd'")
    }

    @Test
    fun testMultipleQuantifiers() {
        val regex = "a*b+c?d+"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("bbd"), "Pattern 'a*b+c?d+' should match 'bbd'")
        assertTrue(dfa.match("abbbd"), "Pattern 'a*b+c?d+' should match 'abbbd'")
        assertFalse(dfa.match("aaabccccd"), "Pattern 'a*b+c?d+' should match 'aaabccccd'")
        assertTrue(dfa.match("bcd"), "Pattern 'a*b+c?d+' should match 'bcd'")
        assertFalse(dfa.match("aab"), "Pattern 'a*b+c?d+' should not match 'aab'")
        assertFalse(dfa.match("abbdx"), "Pattern 'a*b+c?d+' should not match 'abbdx'")
        assertFalse(dfa.match("aabbc"), "Pattern 'a*b+c?d+' should not match 'aabbc'")
    }

    @Test
    fun testComplexNestedGroups() {
        val regex = "a(bc|de(f|g)h)*i"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("ai"), "Pattern 'a(bc|de(f|g)h)*i' should match 'ai'")
        assertTrue(dfa.match("abci"), "Pattern 'a(bc|de(f|g)h)*i' should match 'abci'")
        assertFalse(dfa.match("adefhhi"), "Pattern 'a(bc|de(f|g)h)*i' should match 'adefhhi'")
        assertFalse(dfa.match("abciadefghi"), "Pattern 'a(bc|de(f|g)h)*i' should match 'abciadefghi'")
        assertFalse(dfa.match("a"), "Pattern 'a(bc|de(f|g)h)*i' should not match 'a'")
        assertFalse(dfa.match("ab"), "Pattern 'a(bc|de(f|g)h)*i' should not match 'ab'")
        assertFalse(dfa.match("abcdefg"), "Pattern 'a(bc|de(f|g)h)*i' should not match 'abcdefg'")
        assertFalse(dfa.match("abciadefghx"), "Pattern 'a(bc|de(f|g)h)*i' should not match 'abciadefghx'")
    }

    @Test
    fun testNestedRepetitionAndAlternation() {
        val regex = "((a|b)+c)*d"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("d"), "Pattern '((a|b)+c)*d' should match 'd'")
        assertTrue(dfa.match("acd"), "Pattern '((a|b)+c)*d' should match 'acd'")
        assertTrue(dfa.match("bacd"), "Pattern '((a|b)+c)*d' should match 'bacd'")
        assertTrue(dfa.match("aabcbacd"), "Pattern '((a|b)+c)*d' should match 'aabcbacd'")
        assertFalse(dfa.match("aabcbac"), "Pattern '((a|b)+c)*d' should not match 'aabcbac'")
        assertFalse(dfa.match("aabcbacdxx"), "Pattern '((a|b)+c)*d' should not match 'aabcbacdxx'")
    }

    @Test
    fun testRepetitionWithMultipleGroups() {
        val regex = "(ab|cd)*ef(g|h)+"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("efg"), "Pattern '(ab|cd)*ef(g|h)+' should match 'efg'")
        assertTrue(dfa.match("abcdabefgh"), "Pattern '(ab|cd)*ef(g|h)+' should match 'abcdabefgh'")
        assertTrue(dfa.match("efghh"), "Pattern '(ab|cd)*ef(g|h)+' should match 'efghh'")
        assertFalse(dfa.match("ef"), "Pattern '(ab|cd)*ef(g|h)+' should not match 'ef'")
        assertFalse(dfa.match("abcdabef"), "Pattern '(ab|cd)*ef(g|h)+' should not match 'abcdabef'")
        assertFalse(dfa.match("efgx"), "Pattern '(ab|cd)*ef(g|h)+' should not match 'efgx'")
    }

    @Test
    fun testLongPattern() {
        val regex = "a(b|c)*d(e|f)+g?h"
        val dfa = DFA.toDirectDFA(regex)

        assertFalse(dfa.match("adh"), "Pattern 'a(b|c)*d(e|f)+g?h' should match 'adh'")
        assertTrue(dfa.match("abbdfeh"), "Pattern 'a(b|c)*d(e|f)+g?h' should match 'abbdfeh'")
        assertTrue(dfa.match("acbcdefgh"), "Pattern 'a(b|c)*d(e|f)+g?h' should match 'acbcdefgh'")
        assertTrue(dfa.match("abccdeefgh"), "Pattern 'a(b|c)*d(e|f)+g?h' should match 'abccdeefgh'")
        assertFalse(dfa.match("abccdeefg"), "Pattern 'a(b|c)*d(e|f)+g?h' should not match 'abccdeefg'")
        assertFalse(dfa.match("aebh"), "Pattern 'a(b|c)*d(e|f)+g?h' should not match 'aebh'")
        assertFalse(dfa.match("abccdeeghx"), "Pattern 'a(b|c)*d(e|f)+g?h' should not match 'abccdeeghx'")
    }


    @Test
    fun testComplexExpression() {
        val regex = "a|b*c|d(e|f)*g"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("a"), "Pattern 'a|b*c|d(e|f)*g' should match 'a'")
        assertTrue(dfa.match("bc"), "Pattern 'a|b*c|d(e|f)*g' should match 'bc'")
        assertTrue(dfa.match("bbbc"), "Pattern 'a|b*c|d(e|f)*g' should match 'bbbc'")
        assertTrue(dfa.match("deg"), "Pattern 'a|b*c|d(e|f)*g' should match 'deg'")
        assertTrue(dfa.match("dfg"), "Pattern 'a|b*c|d(e|f)*g' should match 'dfg'")
        assertTrue(dfa.match("deefg"), "Pattern 'a|b*c|d(e|f)*g' should match 'deefg'")
        assertFalse(dfa.match("b"), "Pattern 'a|b*c|d(e|f)*g' should not match 'b'")
        assertFalse(dfa.match("bdg"), "Pattern 'a|b*c|d(e|f)*g' should not match 'bdg'")
        assertTrue(dfa.match("dg"), "Pattern 'a|b*c|d(e|f)*g' should not match 'dg'")
        assertFalse(dfa.match("dgh"), "Pattern 'a|b*c|d(e|f)*g' should not match 'dgh'")
        assertFalse(dfa.match("abcd"), "Pattern 'a|b*c|d(e|f)*g' should not match 'abcd'")
    }


    @Test
    fun testEscapedSpecialCharacters() {
        val regex = "\\*\\+\\?"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("*+?"), "Pattern '\\*\\+\\?' should match '*+?'")
        assertFalse(dfa.match("*+"), "Pattern '\\*\\+\\?' should not match '*+'")
        assertFalse(dfa.match("+?"), "Pattern '\\*\\+\\?' should not match '+?'")
        assertFalse(dfa.match(""), "Pattern '\\*\\+\\?' should not match empty string")
        assertFalse(dfa.match("*+?extra"), "Pattern '\\*\\+\\?' should not match '*+?extra'")
    }

    @Test
    fun testEscapedParentheses() {
        val regex = "\\(a\\|b\\)"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("(a|b)"), "Pattern '\\(a\\|b\\)' should match '(a|b)'")
        assertFalse(dfa.match("a|b"), "Pattern '\\(a\\|b\\)' should not match 'a|b'")
        assertFalse(dfa.match("(a|b"), "Pattern '\\(a\\|b\\)' should not match '(a|b'")
        assertFalse(dfa.match(""), "Pattern '\\(a\\|b\\)' should not match empty string")
    }

    @Test
    fun testEscapedBackslash() {
        val regex = "a\\\\b"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("a\\b"), "Pattern 'a\\\\b' should match 'a\\b'")
        assertFalse(dfa.match("ab"), "Pattern 'a\\\\b' should not match 'ab'")
        assertFalse(dfa.match("a\\bb"), "Pattern 'a\\\\b' should not match 'a\\bb'")
        assertFalse(dfa.match("a\\b\\b"), "Pattern 'a\\\\b' should not match 'a\\b\\b'")
    }

    @Test
    fun testEscapedCharactersWithConcatenation() {
        val regex = "\\(a\\)b\\*"
        val dfa = DFA.toDirectDFA(regex)

        assertTrue(dfa.match("(a)b*"), "Pattern '\\(a\\)b\\*' should match '(a)b*'")
        assertFalse(dfa.match("(a)b"), "Pattern '\\(a\\)b\\*' should not match '(a)b'")
        assertFalse(dfa.match("(a)b**"), "Pattern '\\(a\\)b\\*' should not match '(a)b**'")
        assertFalse(dfa.match("(a)"), "Pattern '\\(a\\)b\\*' should not match '(a)'")
        assertFalse(dfa.match("(a)b*extra"), "Pattern '\\(a\\)b\\*' should not match '(a)b*extra'")
    }

}