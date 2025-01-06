import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class NFATest {

    @Test
    fun testSingleCharacter() {
        val regex = "a"
        val nfa = NFA.fromSymbol(Symbol.CharSymbol('a'))

        assertTrue(nfa.match("a"), "Pattern 'a' should match 'a'")
        assertFalse(nfa.match("b"), "Pattern 'a' should not match 'b'")
        assertFalse(nfa.match(""), "Pattern 'a' should not match empty string")
    }

    @Test
    fun testUnion() {
        val regex = "a|b"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("a"), "Pattern 'a|b' should match 'a'")
        assertTrue(nfa.match("b"), "Pattern 'a|b' should match 'b'")
        assertFalse(nfa.match("ab"), "Pattern 'a|b' should not match 'ab'")
        assertFalse(nfa.match(""), "Pattern 'a|b' should not match empty string")
    }

    @Test
    fun testConcatenation() {
        val regex = "ab"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("ab"), "Pattern 'ab' should match 'ab'")
        assertFalse(nfa.match("a"), "Pattern 'ab' should not match 'a'")
        assertFalse(nfa.match("b"), "Pattern 'ab' should not match 'b'")
        assertFalse(nfa.match("abc"), "Pattern 'ab' should not match 'abc'")
    }

    @Test
    fun testKleeneStar() {
        val regex = "a*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match(""), "Pattern 'a*' should match empty string")
        assertTrue(nfa.match("a"), "Pattern 'a*' should match 'a'")
        assertTrue(nfa.match("aa"), "Pattern 'a*' should match 'aa'")
        assertTrue(nfa.match("aaa"), "Pattern 'a*' should match 'aaa'")
        assertFalse(nfa.match("b"), "Pattern 'a*' should not match 'b'")
        assertFalse(nfa.match("ab"), "Pattern 'a*' should not match 'ab'")
    }

    @Test
    fun testComplexPattern() {
        val regex = "a(b|c)*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("a"), "Pattern 'a(b|c)*' should match 'a'")
        assertTrue(nfa.match("ab"), "Pattern 'a(b|c)*' should match 'ab'")
        assertTrue(nfa.match("ac"), "Pattern 'a(b|c)*' should match 'ac'")
        assertTrue(nfa.match("abbc"), "Pattern 'a(b|c)*' should match 'abbc'")
        assertTrue(nfa.match("abcbcbc"), "Pattern 'a(b|c)*' should match 'abcbcbc'")
        assertFalse(nfa.match("b"), "Pattern 'a(b|c)*' should not match 'b'")
        assertFalse(nfa.match("abcx"), "Pattern 'a(b|c)*' should not match 'abcx'")
    }

    @Test
    fun testAnotherComplexPattern() {
        val regex = "(a|b)*c"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("c"), "Pattern '(a|b)*c' should match 'c'")
        assertTrue(nfa.match("ac"), "Pattern '(a|b)*c' should match 'ac'")
        assertTrue(nfa.match("bc"), "Pattern '(a|b)*c' should match 'bc'")
        assertTrue(nfa.match("aabbc"), "Pattern '(a|b)*c' should match 'aabbc'")
        assertTrue(nfa.match("ababababc"), "Pattern '(a|b)*c' should match 'ababababc'")
        assertFalse(nfa.match("ab"), "Pattern '(a|b)*c' should not match 'ab'")
        assertTrue(nfa.match("abc"), "Pattern '(a|b)*c' should match 'abc'")
    }

    @Test
    fun testMultipleUnions() {
        val regex = "a|b|c"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("a"), "Pattern 'a|b|c' should match 'a'")
        assertTrue(nfa.match("b"), "Pattern 'a|b|c' should match 'b'")
        assertTrue(nfa.match("c"), "Pattern 'a|b|c' should match 'c'")
        assertFalse(nfa.match("d"), "Pattern 'a|b|c' should not match 'd'")
        assertFalse(nfa.match("ab"), "Pattern 'a|b|c' should not match 'ab'")
    }

    @Test
    fun testNestedGroups() {
        val regex = "(a|b)(c|d)"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("ac"), "Pattern '(a|b)(c|d)' should match 'ac'")
        assertTrue(nfa.match("ad"), "Pattern '(a|b)(c|d)' should match 'ad'")
        assertTrue(nfa.match("bc"), "Pattern '(a|b)(c|d)' should match 'bc'")
        assertTrue(nfa.match("bd"), "Pattern '(a|b)(c|d)' should match 'bd'")
        assertFalse(nfa.match("a"), "Pattern '(a|b)(c|d)' should not match 'a'")
        assertFalse(nfa.match("c"), "Pattern '(a|b)(c|d)' should not match 'c'")
        assertFalse(nfa.match("abcd"), "Pattern '(a|b)(c|d)' should not match 'abcd'")
    }

    @Test
    fun testMultipleKleeneStars() {
        val regex = "a*b*c*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match(""), "Pattern 'a*b*c*' should match empty string")
        assertTrue(nfa.match("a"), "Pattern 'a*b*c*' should match 'a'")
        assertTrue(nfa.match("aaabbbccc"), "Pattern 'a*b*c*' should match 'aaabbbccc'")
        assertTrue(nfa.match("aaaccc"), "Pattern 'a*b*c*' should match 'aaaccc'")
        assertTrue(nfa.match("bbb"), "Pattern 'a*b*c*' should match 'bbb'")
        assertFalse(nfa.match("abcx"), "Pattern 'a*b*c*' should not match 'abcx'")
        assertFalse(nfa.match("abca"), "Pattern 'a*b*c*' should not match 'abca'")
    }

    @Test
    fun testMixedOperators() {
        val regex = "a|bc*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("a"), "Pattern 'a|bc*' should match 'a'")
        assertTrue(nfa.match("b"), "Pattern 'a|bc*' should match 'b'")
        assertTrue(nfa.match("bc"), "Pattern 'a|bc*' should match 'bc'")
        assertTrue(nfa.match("bccc"), "Pattern 'a|bc*' should match 'bccc'")
        assertFalse(nfa.match("ab"), "Pattern 'a|bc*' should not match 'ab'")
        assertFalse(nfa.match(""), "Pattern 'a|bc*' should not match empty string")
    }

    @Test
    fun testComplexNestedPattern() {
        val regex = "((a|b)*c)d"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("cd"), "Pattern '((a|b)*c)d' should match 'cd'")
        assertTrue(nfa.match("acd"), "Pattern '((a|b)*c)d' should match 'acd'")
        assertTrue(nfa.match("bbcd"), "Pattern '((a|b)*c)d' should match 'bbcd'")
        assertTrue(nfa.match("aabbbcd"), "Pattern '((a|b)*c)d' should match 'aabbbcd'")
        assertFalse(nfa.match("c"), "Pattern '((a|b)*c)d' should not match 'c'")
        assertFalse(nfa.match("abc"), "Pattern '((a|b)*c)d' should not match 'abc'")
        assertTrue(nfa.match("abcd"), "Pattern '((a|b)*c)d' should not match 'abcd'")
    }

    @Test
    fun testMultipleGroupsAndOperators() {
        val regex = "(a|b)*c(d|e)*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("c"), "Pattern '(a|b)*c(d|e)*' should match 'c'")
        assertTrue(nfa.match("ac"), "Pattern '(a|b)*c(d|e)*' should match 'ac'")
        assertTrue(nfa.match("bc"), "Pattern '(a|b)*c(d|e)*' should match 'bc'")
        assertFalse(nfa.match("aabcccdddeee"), "Pattern '(a|b)*c(d|e)*' should match 'aabcccdddeee'")
        assertFalse(nfa.match("abcbcdde"), "Pattern '(a|b)*c(d|e)*' should match 'abcbcdde'")
        assertFalse(nfa.match("abcbcb"), "Pattern '(a|b)*c(d|e)*' should not match 'abcbcb'")
        assertFalse(nfa.match("abcx"), "Pattern '(a|b)*c(d|e)*' should not match 'abcx'")
    }

    @Test
    fun testAlternationWithConcatenation() {
        val regex = "a|bc"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("a"), "Pattern 'a|bc' should match 'a'")
        assertTrue(nfa.match("bc"), "Pattern 'a|bc' should match 'bc'")
        assertFalse(nfa.match("b"), "Pattern 'a|bc' should not match 'b'")
        assertFalse(nfa.match("c"), "Pattern 'a|bc' should not match 'c'")
        assertFalse(nfa.match("abc"), "Pattern 'a|bc' should not match 'abc'")
    }

    @Test
    fun testNestedAlternations() {
        val regex = "a|(b|c)*d"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("a"), "Pattern 'a|(b|c)*d' should match 'a'")
        assertTrue(nfa.match("bd"), "Pattern 'a|(b|c)*d' should match 'bd'")
        assertTrue(nfa.match("cd"), "Pattern 'a|(b|c)*d' should match 'cd'")
        assertTrue(nfa.match("bbcd"), "Pattern 'a|(b|c)*d' should match 'bbcd'")
        assertTrue(nfa.match("bcccd"), "Pattern 'a|(b|c)*d' should match 'bcccd'")
        assertFalse(nfa.match(""), "Pattern 'a|(b|c)*d' should not match empty string")
        assertFalse(nfa.match("b"), "Pattern 'a|(b|c)*d' should not match 'b'")
        assertFalse(nfa.match("c"), "Pattern 'a|(b|c)*d' should not match 'c'")
        assertFalse(nfa.match("abc"), "Pattern 'a|(b|c)*d' should not match 'abc'")
    }

    @Test
    fun testComplexExpression() {
        val regex = "a|b*c|d(e|f)*g"
        val nfa = toNFA(regex)
        assertTrue(nfa.match("a"), "Pattern 'a|b*c|d(e|f)*g' should match 'a'")
        assertTrue(nfa.match("bc"), "Pattern 'a|b*c|d(e|f)*g' should match 'bc'")
        assertTrue(nfa.match("bbbc"), "Pattern 'a|b*c|d(e|f)*g' should match 'bbbc'")
        assertTrue(nfa.match("deg"), "Pattern 'a|b*c|d(e|f)*g' should match 'deg'")
        assertTrue(nfa.match("dfg"), "Pattern 'a|b*c|d(e|f)*g' should match 'dfg'")
        assertTrue(nfa.match("deefg"), "Pattern 'a|b*c|d(e|f)*g' should match 'deefg'")
        assertFalse(nfa.match("b"), "Pattern 'a|b*c|d(e|f)*g' should not match 'b'")
        assertFalse(nfa.match("bdg"), "Pattern 'a|b*c|d(e|f)*g' should not match 'bdg'")
        assertTrue(nfa.match("dg"), "Pattern 'a|b*c|d(e|f)*g' should not match 'dg'")
        assertFalse(nfa.match("dgh"), "Pattern 'a|b*c|d(e|f)*g' should not match 'dgh'")
        assertFalse(nfa.match("abcd"), "Pattern 'a|b*c|d(e|f)*g' should not match 'abcd'")
    }

    @Test
    fun testStarWithAlternation() {
        val regex = "(a|b)*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match(""), "Pattern '(a|b)*' should match empty string")
        assertTrue(nfa.match("a"), "Pattern '(a|b)*' should match 'a'")
        assertTrue(nfa.match("b"), "Pattern '(a|b)*' should match 'b'")
        assertTrue(nfa.match("ab"), "Pattern '(a|b)*' should match 'ab'")
        assertTrue(nfa.match("aabb"), "Pattern '(a|b)*' should match 'aabb'")
        assertTrue(nfa.match("ababab"), "Pattern '(a|b)*' should match 'ababab'")
        assertFalse(nfa.match("c"), "Pattern '(a|b)*' should not match 'c'")
        assertFalse(nfa.match("abc"), "Pattern '(a|b)*' should not match 'abc'")
    }

    @Test
    fun testOptionalOperator() {
        val regex = "a?b"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("b"), "Pattern 'a?b' should match 'b'")
        assertTrue(nfa.match("ab"), "Pattern 'a?b' should match 'ab'")
        assertFalse(nfa.match("a"), "Pattern 'a?b' should not match 'a'")
        assertFalse(nfa.match(""), "Pattern 'a?b' should not match empty string")
        assertFalse(nfa.match("abb"), "Pattern 'a?b' should not match 'abb'")
    }

    @Test
    fun testPlusOperator() {
        val regex = "a+b"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("ab"), "Pattern 'a+b' should match 'ab'")
        assertTrue(nfa.match("aab"), "Pattern 'a+b' should match 'aab'")
        assertTrue(nfa.match("aaab"), "Pattern 'a+b' should match 'aaab'")
        assertFalse(nfa.match("b"), "Pattern 'a+b' should not match 'b'")
        assertFalse(nfa.match("a"), "Pattern 'a+b' should not match 'a'")
        assertFalse(nfa.match("aabb"), "Pattern 'a+b' should not match 'aabb'")
    }

    @Test
    fun testComplexNestedAlternation() {
        val regex = "((a|b)|c)*d"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("d"), "Pattern '((a|b)|c)*d' should match 'd'")
        assertTrue(nfa.match("ad"), "Pattern '((a|b)|c)*d' should match 'ad'")
        assertTrue(nfa.match("bd"), "Pattern '((a|b)|c)*d' should match 'bd'")
        assertTrue(nfa.match("cd"), "Pattern '((a|b)|c)*d' should match 'cd'")
        assertTrue(nfa.match("aabd"), "Pattern '((a|b)|c)*d' should match 'aabd'")
        assertTrue(nfa.match("bccbd"), "Pattern '((a|b)|c)*d' should match 'bccbd'")
        assertFalse(nfa.match("a|b|c"), "Pattern '((a|b)|c)*d' should not match 'a|b|c'")
        assertFalse(nfa.match("abcx"), "Pattern '((a|b)|c)*d' should not match 'abcx'")
    }

    @Test
    fun testMultipleOperatorsCombination() {
        val regex = "(a|b)*c(d|e)*f"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("cf"), "Pattern '(a|b)*c(d|e)*f' should match 'cf'")
        assertTrue(nfa.match("acdf"), "Pattern '(a|b)*c(d|e)*f' should match 'acdf'")
        assertFalse(nfa.match("bbccdddf"), "Pattern '(a|b)*c(d|e)*f' should match 'bbccdddf'")
        assertFalse(nfa.match("aabcbdeef"), "Pattern '(a|b)*c(d|e)*f' should match 'aabcbdeef'")
        assertFalse(nfa.match("c"), "Pattern '(a|b)*c(d|e)*f' should not match 'c'")
        assertFalse(nfa.match("cde"), "Pattern '(a|b)*c(d|e)*f' should not match 'cde'")
        assertFalse(nfa.match("abcefg"), "Pattern '(a|b)*c(d|e)*f' should not match 'abcefg'")
        assertFalse(nfa.match("abcfef"), "Pattern '(a|b)*c(d|e)*f' should not match 'abcfef'")
    }

    @Test
    fun testNestedKleeneStars() {
        val regex = "a*(b*c*)*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match(""), "Pattern 'a*(b*c*)*' should match empty string")
        assertTrue(nfa.match("a"), "Pattern 'a*(b*c*)*' should match 'a'")
        assertTrue(nfa.match("aaa"), "Pattern 'a*(b*c*)*' should match 'aaa'")
        assertTrue(nfa.match("aabcc"), "Pattern 'a*(b*c*)*' should match 'aabcc'")
        assertTrue(nfa.match("aabbcc"), "Pattern 'a*(b*c*)*' should match 'aabbcc'")
        assertTrue(nfa.match("aabccc"), "Pattern 'a*(b*c*)*' should match 'aabccc'")
        assertTrue(nfa.match("aabbccbbcc"), "Pattern 'a*(b*c*)*' should match 'aabbccbbcc'")
        assertTrue(nfa.match("aabcbcc"), "Pattern 'a*(b*c*)*' should not match 'aabcbcc'")
        assertTrue(nfa.match("abc"), "Pattern 'a*(b*c*)*' should not match 'abc'")
    }

    @Test
    fun testAlternationWithKleeneStar() {
        val regex = "(a|b)*c*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match(""), "Pattern '(a|b)*c*' should match empty string")
        assertTrue(nfa.match("a"), "Pattern '(a|b)*c*' should match 'a'")
        assertTrue(nfa.match("b"), "Pattern '(a|b)*c*' should match 'b'")
        assertTrue(nfa.match("aaabbb"), "Pattern '(a|b)*c*' should match 'aaabbb'")
        assertTrue(nfa.match("aaabbbccc"), "Pattern '(a|b)*c*' should match 'aaabbbccc'")
        assertTrue(nfa.match("aabccc"), "Pattern '(a|b)*c*' should match 'aabccc'")
        assertFalse(nfa.match("abca"), "Pattern '(a|b)*c*' should not match 'abca'")
        assertFalse(nfa.match("abcb"), "Pattern '(a|b)*c*' should not match 'abcb'")
    }

    @Test
    fun testMultipleAlternationsAndConcatenations() {
        val regex = "(a|b)c|(d|e)f"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("ac"), "Pattern '(a|b)c|(d|e)f' should match 'ac'")
        assertTrue(nfa.match("bc"), "Pattern '(a|b)c|(d|e)f' should match 'bc'")
        assertTrue(nfa.match("df"), "Pattern '(a|b)c|(d|e)f' should match 'df'")
        assertTrue(nfa.match("ef"), "Pattern '(a|b)c|(d|e)f' should match 'ef'")
        assertFalse(nfa.match("a"), "Pattern '(a|b)c|(d|e)f' should not match 'a'")
        assertFalse(nfa.match("b"), "Pattern '(a|b)c|(d|e)f' should not match 'b'")
        assertFalse(nfa.match("c"), "Pattern '(a|b)c|(d|e)f' should not match 'c'")
        assertFalse(nfa.match("d"), "Pattern '(a|b)c|(d|e)f' should not match 'd'")
        assertFalse(nfa.match("e"), "Pattern '(a|b)c|(d|e)f' should not match 'e'")
        assertFalse(nfa.match("abcd"), "Pattern '(a|b)c|(d|e)f' should not match 'abcd'")
    }

    @Test
    fun testDeeplyNestedPattern() {
        val regex = "((a|b)(c|d))*e"
        val nfa = toNFA(regex)

        assertTrue(nfa.match("e"), "Pattern '((a|b)(c|d))*e' should match 'e'")
        assertTrue(nfa.match("ace"), "Pattern '((a|b)(c|d))*e' should match 'ace'")
        assertTrue(nfa.match("bde"), "Pattern '((a|b)(c|d))*e' should match 'bde'")
        assertFalse(nfa.match("acebde"), "Pattern '((a|b)(c|d))*e' should match 'acebde'")
        assertFalse(nfa.match("acebde"), "Pattern '((a|b)(c|d))*e' should match 'acebde'")
        assertFalse(nfa.match("aceacebdebde"), "Pattern '((a|b)(c|d))*e' should match 'aceacebdebde'")
        assertFalse(nfa.match("a"), "Pattern '((a|b)(c|d))*e' should not match 'a'")
        assertFalse(nfa.match("aceb"), "Pattern '((a|b)(c|d))*e' should not match 'aceb'")
        assertFalse(nfa.match("acebd"), "Pattern '((a|b)(c|d))*e' should not match 'acebd'")
        assertFalse(nfa.match("abcdef"), "Pattern '((a|b)(c|d))*e' should not match 'abcdef'")
    }

    @Test
    fun testOptionalAndKleeneStarCombination() {
        val regex = "a?b*"
        val nfa = toNFA(regex)

        assertTrue(nfa.match(""), "Pattern 'a?b*' should match empty string")
        assertTrue(nfa.match("a"), "Pattern 'a?b*' should match 'a'")
        assertTrue(nfa.match("b"), "Pattern 'a?b*' should match 'b'")
        assertTrue(nfa.match("ab"), "Pattern 'a?b*' should match 'ab'")
        assertTrue(nfa.match("abbb"), "Pattern 'a?b*' should match 'abbb'")
        assertFalse(nfa.match("c"), "Pattern 'a?b*' should not match 'c'")
        assertFalse(nfa.match("ac"), "Pattern 'a?b*' should not match 'ac'")
        assertFalse(nfa.match("abbc"), "Pattern 'a?b*' should not match 'abbc'")
    }

    @Test
    fun testPlusAndKleeneStarCombination() {
        val regex = "a+b*"
        val nfa = toNFA(regex)

        assertFalse(nfa.match(""), "Pattern 'a+b*' should not match empty string")
        assertTrue(nfa.match("a"), "Pattern 'a+b*' should match 'a'")
        assertTrue(nfa.match("ab"), "Pattern 'a+b*' should match 'ab'")
        assertTrue(nfa.match("aaabbb"), "Pattern 'a+b*' should match 'aaabbb'")
        assertTrue(nfa.match("aaab"), "Pattern 'a+b*' should match 'aaab'")
        assertTrue(nfa.match("aaabbbb"), "Pattern 'a+b*' should match 'aaabbbb'")
        assertFalse(nfa.match("b"), "Pattern 'a+b*' should not match 'b'")
        assertFalse(nfa.match("aabx"), "Pattern 'a+b*' should not match 'aabx'")
    }

}