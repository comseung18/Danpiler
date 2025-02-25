package tc

import lexer.Token
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val calculator = FirstFollowCalculator(grammar, "S")

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
        val calculator = FirstFollowCalculator(grammar, "X")

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
        val calculator = FirstFollowCalculator(grammar, "E")

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
        val calculator = FirstFollowCalculator(grammar, "Stmt")

        val expectedFirstSets = mapOf(
            "Stmt" to setOf(
                TokenTerminalItem(Token.IfToken),
                TokenTerminalItem(Token.IdentifierToken)
            ),
            "Expr" to setOf(
                TokenTerminalItem(Token.IdentifierToken),
                TokenTerminalItem(Token.IntNumberToken)
            )
        )

        expectedFirstSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFirstSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "First set for <$nonTerminal> is incorrect")
        }
    }



    @Test
    fun testFollowSet_SimpleGrammar() {
        val bnf = """
            <S> ::= <A> "b"
            <A> ::= "a" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar, "S")

        val expectedFollowSets = mapOf(
            "S" to setOf(ConstTerminalItem("$")),
            "A" to setOf(ConstTerminalItem("b"))
        )

        expectedFollowSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFollowSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "FOLLOW set for <$nonTerminal> is incorrect")
        }
    }

    @Test
    fun testFollowSet_ChainedGrammar() {
        val bnf = """
            <X> ::= <Y> "c"
            <Y> ::= <Z>
            <Z> ::= "z" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar, "X")

        val expectedFollowSets = mapOf(
            "X" to setOf(ConstTerminalItem("$")),
            "Y" to setOf(ConstTerminalItem("c")),
            "Z" to setOf(ConstTerminalItem("c"))
        )

        expectedFollowSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFollowSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "FOLLOW set for <$nonTerminal> is incorrect")
        }
    }

    @Test
    fun testFollowSet_TransitiveFollow() {
        val bnf = """
            <A> ::= <B> <C> <D>
            <B> ::= ε | "b"
            <C> ::= ε | "c"
            <D> ::= "d"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar, "A")

        val expectedFollowSets = mapOf(
            "A" to setOf(ConstTerminalItem("$")),
            "B" to setOf(ConstTerminalItem("c"), ConstTerminalItem("d")),
            "C" to setOf(ConstTerminalItem("d")),
            "D" to setOf(ConstTerminalItem("$"))
        )

        expectedFollowSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFollowSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "FOLLOW set for <$nonTerminal> is incorrect")
        }
    }

    @Test
    fun testFollowSet_ExpressionGrammar() {
        val bnf = """
            <E> ::= <T> "+" <E> | <T>
            <T> ::= <F> "*" <T> | <F>
            <F> ::= "n" | "(" <E> ")"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar, "E")

        val expectedFollowSets = mapOf(
            "E" to setOf(ConstTerminalItem("$"), ConstTerminalItem(")")),
            "T" to setOf(ConstTerminalItem("+"), ConstTerminalItem("$"), ConstTerminalItem(")")),
            "F" to setOf(ConstTerminalItem("*"), ConstTerminalItem("+"), ConstTerminalItem("$"), ConstTerminalItem(")"))
        )

        expectedFollowSets.forEach { (nonTerminal, expectedSet) ->
            val actualSet = calculator.getFollowSet(NonTerminalItem(nonTerminal))
            assertEquals(expectedSet, actualSet, "FOLLOW set for <$nonTerminal> is incorrect")
        }
    }


    @Test
    fun `test First and Follow sets on complex grammar`() {
        val bnf = """
            <Program> ::= <Statements>
            <Statements> ::= <Statement> | <Statement> <Statements>
            <Statement> ::= <Assignment> ";" | <IfStatement> | <WhileLoop> | <FunctionCall> ";"
            <Assignment> ::= <Identifier> "=" <Expression>
            <IfStatement> ::= "if" "(" <Expression> ")" "{" <Statements> "}" <ElsePart>
            <ElsePart> ::= "else" "{" <Statements> "}" | ε
            <WhileLoop> ::= "while" "(" <Expression> ")" "{" <Statements> "}"
            <FunctionCall> ::= <Identifier> "(" <Arguments> ")"
            <Arguments> ::= <Expression> | <Expression> "," <Arguments> | ε
            <Expression> ::= <Term> <ExpressionPrime>
            <ExpressionPrime> ::= "+" <Term> <ExpressionPrime> | ε
            <Term> ::= <Factor> <TermPrime>
            <TermPrime> ::= "*" <Factor> <TermPrime> | ε
            <Factor> ::= "(" <Expression> ")" | <Identifier> | <Number>
            <Identifier> ::= "ID"
            <Number> ::= "NUM"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val calculator = FirstFollowCalculator(grammar, "Program")

        val firstProgram = calculator.getFirstSet(NonTerminalItem("Program"))
        assertTrue(firstProgram.contains(ConstTerminalItem("ID")) || firstProgram.contains(ConstTerminalItem("if")))

        val followProgram = calculator.getFollowSet(NonTerminalItem("Program"))
        assertTrue(followProgram.contains(ConstTerminalItem("$")))

        val firstExpression = calculator.getFirstSet(NonTerminalItem("Expression"))
        assertTrue(firstExpression.contains(ConstTerminalItem("ID")) || firstExpression.contains(ConstTerminalItem("NUM")))

        val followExpression = calculator.getFollowSet(NonTerminalItem("Expression"))
        assertTrue(followExpression.contains(ConstTerminalItem(")")) || followExpression.contains(ConstTerminalItem(";")))

        val firstIfStatement = calculator.getFirstSet(NonTerminalItem("IfStatement"))
        assertTrue(firstIfStatement.contains(ConstTerminalItem("if")))

        val followIfStatement = calculator.getFollowSet(NonTerminalItem("IfStatement"))
        assertTrue(followIfStatement.contains(ConstTerminalItem(";")) || followIfStatement.contains(ConstTerminalItem("}")))
    }
}
