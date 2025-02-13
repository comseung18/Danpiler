package tc

import lexer.Token
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parser.*

class FirstFollowCalculatorTest {

    @Test
    fun testFirstSet_SimpleGrammar() {
        val bnf = """
            <S> ::= <A> "b"
            <A> ::= "a" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar)

        val expectedFirstSets = mapOf(
            "S" to setOf(ConstTerminalItem("a"), ConstTerminalItem("b")),
            "A" to setOf(ConstTerminalItem("a")) // "ε"는 필요 없음 (자동 처리)
        )

        expectedFirstSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFirstSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "First set for <$nonTerminal> is incorrect")
        }
    }

    @Test
    fun testFirstSet_ChainedGrammar() {
        val bnf = """
            <X> ::= <Y> "c"
            <Y> ::= <Z>
            <Z> ::= "z" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar)

        val expectedFirstSets = mapOf(
            "X" to setOf(ConstTerminalItem("z"), ConstTerminalItem("c")),
            "Y" to setOf(ConstTerminalItem("z")), // "ε"는 자동 처리되므로 없어도 됨
            "Z" to setOf(ConstTerminalItem("z")) // "ε"는 자동 처리됨
        )

        expectedFirstSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFirstSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "First set for <$nonTerminal> is incorrect")
        }
    }

    @Test
    fun testFirstSet_ExpressionGrammar() {
        val bnf = """
            <E> ::= <T> "+" <E> | <T>
            <T> ::= <F> "*" <T> | <F>
            <F> ::= "n" | "(" <E> ")"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar)

        val expectedFirstSets = mapOf(
            "E" to setOf(ConstTerminalItem("n"), ConstTerminalItem("(")),
            "T" to setOf(ConstTerminalItem("n"), ConstTerminalItem("(")),
            "F" to setOf(ConstTerminalItem("n"), ConstTerminalItem("("))
        )

        expectedFirstSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFirstSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "First set for <$nonTerminal> is incorrect")
        }
    }

    @Test
    fun testFirstSet_UsingToken() {
        val bnf = """
            <Stmt> ::= IfToken LParenToken <Expr> RParenToken <Stmt> | IdentifierToken AssignmentOperatorToken <Expr> SemicolonToken
            <Expr> ::= IdentifierToken | IntNumberToken
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar)

        val expectedFirstSets = mapOf(
            "Stmt" to setOf(
                TokenTerminalItem("IfToken", Token.IfToken),
                TokenTerminalItem("IdentifierToken", Token.IdentifierToken)
            ),
            "Expr" to setOf(
                TokenTerminalItem("IdentifierToken", Token.IdentifierToken),
                TokenTerminalItem("IntNumberToken", Token.IntNumberToken)
            )
        )

        expectedFirstSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFirstSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "First set for <$nonTerminal> is incorrect")
        }
    }
}
