package tc

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parseBNF

class GrammarTest {

    @Test
    fun `test parseBNF`() {
        val bnf = """
    <Expr> ::= <Term> "+" <Expr> | <Term>
    <Term> ::= <Factor> "*" <Term> | <Factor>
    <Factor> ::= "(" <Expr> ")" | IDENTIFIER
    """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals("<Expr>", grammar.startSymbol)
        assertEquals(3, grammar.rules.size)
        assertEquals("<Expr>", grammar.rules[0].nonTerminal)
        assertEquals(listOf(listOf("<Term>", "\"+\"", "<Expr>"), listOf("<Term>")), grammar.rules[0].productions)
    }

    @Test
    fun `test simple grammar`() {
        val bnf = """
        <Expr> ::= <Term> "+" <Expr> | <Term>
        <Term> ::= <Factor> "-" <Term> | <Factor>
        <Factor> ::= "(" <Expr> ")" | IDENTIFIER
        """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals("<Expr>", grammar.startSymbol)
        assertEquals(3, grammar.rules.size)

        val exprRule = grammar.rules[0]
        assertEquals("<Expr>", exprRule.nonTerminal)
        assertEquals(listOf(
            listOf("<Term>", "\"+\"", "<Expr>"),
            listOf("<Term>")
        ), exprRule.productions)

        val termRule = grammar.rules[1]
        assertEquals("<Term>", termRule.nonTerminal)
        assertEquals(listOf(
            listOf("<Factor>", "\"-\"", "<Term>"),
            listOf("<Factor>")
        ), termRule.productions)

        val factorRule = grammar.rules[2]
        assertEquals("<Factor>", factorRule.nonTerminal)
        assertEquals(listOf(
            listOf("\"(\"", "<Expr>", "\")\""),
            listOf("IDENTIFIER")
        ), factorRule.productions)
    }

    @Test
    fun `test single rule grammar`() {
        val bnf = """
        <Start> ::= IDENTIFIER
        """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals("<Start>", grammar.startSymbol)
        assertEquals(1, grammar.rules.size)
        assertEquals("<Start>", grammar.rules[0].nonTerminal)
        assertEquals(listOf(listOf("IDENTIFIER")), grammar.rules[0].productions)
    }

    @Test
    fun `test empty grammar`() {
        val bnf = ""

        val exception = kotlin.runCatching {
            parseBNF(bnf)
        }.exceptionOrNull()

        assertEquals(true, exception is IllegalArgumentException)
    }

    @Test
    fun `test grammar with comments and whitespace`() {
        val bnf = """
        # This is a comment
        <Start> ::= IDENTIFIER | "123" # Another comment
        """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals("<Start>", grammar.startSymbol)
        assertEquals(1, grammar.rules.size)
        assertEquals(listOf(
            listOf("IDENTIFIER"),
            listOf("\"123\"")
        ), grammar.rules[0].productions)
    }

    @Test
    fun `test undefined non-terminals`() {
        val bnf = """
    <Expr> ::= <Term> "+" <Expr>
    <Term> ::= <Factor> "*" <Term> # Term uses <Factor>
    """.trimIndent()

        val exception = kotlin.runCatching {
            parseBNF(bnf)
        }.exceptionOrNull()

        assertEquals(true, exception is IllegalArgumentException)
        assertEquals("Undefined non-terminals: [<Factor>]", exception?.message)
    }

    @Test
    fun `test mid-line comments`() {
        val bnf = """
    <Expr> ::= <Term> "+" <Expr> | <Term> # This is a comment
    <Term> ::= <Factor> "*" <Term> | <Factor>
    <Factor> ::= "(" <Expr> ")" | IDENTIFIER # Final comment
    """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals("<Expr>", grammar.startSymbol)
        assertEquals(3, grammar.rules.size)
    }

}