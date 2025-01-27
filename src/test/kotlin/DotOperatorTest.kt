import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TokenizerDotOperatorTest {

    @Test
    fun `test dot operator`() {
        // 단일 '.' 매칭
        var regex = "."
        var dfa = DFA.toDirectDFA(regex)

        assertEquals(true, dfa.match("a"))
        assertEquals(true, dfa.match("1"))
        assertEquals(true, dfa.match("$"))
        assertEquals(false, dfa.match(""))
        assertEquals(false, dfa.match("ab"))

        // '.'과 다른 연산자 조합
        regex = "a.b*"
        dfa = DFA.toDirectDFA(regex)
        assertEquals(true, dfa.match("ab"))
        assertEquals(true, dfa.match("acb"))
        assertEquals(true, dfa.match("abb"))
        assertEquals(false, dfa.match("a"))
        assertEquals(false, dfa.match("abcde"))

        // 문자열 길이 확인
        regex = "...a"
        dfa = DFA.toDirectDFA(regex)
        assertEquals(false, dfa.match("xyzabc"))
        assertEquals(true, dfa.match("abca"))
        assertEquals(false, dfa.match("abcd"))
        assertEquals(false, dfa.match("xyz"))

        // 여러 '.' 연속 사용
        regex = "..."
        dfa = DFA.toDirectDFA(regex)
        assertEquals(true, dfa.match("abc"))
        assertEquals(true, dfa.match("123"))
        assertEquals(false, dfa.match("ab"))
        assertEquals(false, dfa.match("abcd"))

        // 특수 문자와 매칭
        regex = "a.c"
        dfa = DFA.toDirectDFA(regex)
        assertEquals(true, dfa.match("abc"))
        assertEquals(true, dfa.match("a c"))
        assertEquals(false, dfa.match("ac"))
        assertEquals(false, dfa.match("ab"))

        // 복합 조합
        regex = "a.b.c*"
        dfa = DFA.toDirectDFA(regex)
        assertEquals(false, dfa.match("abc"))
        assertEquals(true, dfa.match("a!bccc"))
        assertEquals(false, dfa.match("a1b"))
        assertEquals(false, dfa.match("ab"))
        assertEquals(true, dfa.match("a.bb"))

        // Kleene 및 Union과 조합
        regex = "(a|b).c*"
        dfa = DFA.toDirectDFA(regex)
        assertEquals(true, dfa.match("abc"))
        assertEquals(true, dfa.match("bc"))
        assertEquals(true, dfa.match("a!"))
        assertEquals(true, dfa.match("ac"))
        assertEquals(true, dfa.match("bc"))

        // 빈 문자열
        regex = "."
        dfa = DFA.toDirectDFA(regex)
        assertEquals(false, dfa.match(""))
        assertEquals(true, dfa.match("a"))
    }

    @Test
    fun `test NFA with dot operator`() {
        val regex = "a.c"
        val nfa = toNFA(regex)

        // NFA에서 '.' 연산자 테스트
        assertTrue(nfa.match("abc"))       // 'a', 임의 문자, 'c'
        assertTrue(nfa.match("a1c"))       // 숫자 포함
        assertTrue(nfa.match("a^c"))       // 특수 문자 포함
        assertEquals(false, nfa.match("ac"))   // 중간 문자가 없으므로 실패
        assertEquals(false, nfa.match("ab"))   // 마지막 'c'가 없으므로 실패
    }

    @Test
    fun `test DFA with dot operator`() {
        val regex = "a.*c"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()

        // DFA에서 '.' 및 Kleene(*) 연산자 테스트
        assertTrue(dfa.match("ac"))        // 'a', (0개 이상의 문자), 'c'
        assertTrue(dfa.match("abc"))       // 'a', 임의 문자, 'c'
        assertTrue(dfa.match("axyzc"))     // 여러 문자 포함
        assertEquals(false, dfa.match("a"))    // 'c'가 없으므로 실패
        assertEquals(false, dfa.match("ax"))   // 마지막 'c'가 없으므로 실패
    }

    @Test
    fun `test state minimized DFA with dot operator`() {
        val regex = "a.*b.c"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()
        val minimizedDFA = DFA.stateMinimizedDFA(dfa)

        // 축소된 DFA에서 '.' 및 복합 패턴 테스트
        assertTrue(minimizedDFA.match("abxc"))     // 'a', (0개 이상의 문자), 'b', 임의 문자, 'c'
        assertTrue(minimizedDFA.match("axyb^c"))   // 다양한 문자 조합
        assertTrue(minimizedDFA.match("abbbc"))    // 'b'가 여러 번 반복 가능
        assertEquals(false, minimizedDFA.match("ac"))   // 'b'가 없으므로 실패
        assertEquals(false, minimizedDFA.match("ab"))   // 'c'가 없으므로 실패
    }

    @Test
    fun `test complex regex with dot operator across all graphs`() {
        val regex = "a.(b|c)+.*d"
        val nfa = toNFA(regex)
        val dfa = nfa.toDFA()
        val minimizedDFA = DFA.stateMinimizedDFA(dfa)

        // NFA에서 테스트
        assertTrue(nfa.match("a1bccd"))
        assertEquals(false, nfa.match("ad"))

        // DFA에서 테스트
        assertTrue(dfa.match("a!bbbd"))
        assertEquals(false, dfa.match("abc"))

        // 상태 축소된 DFA에서 테스트
        assertFalse(minimizedDFA.match("axyzbccd"))
        assertEquals(true, minimizedDFA.match("abbd"))
    }
}
