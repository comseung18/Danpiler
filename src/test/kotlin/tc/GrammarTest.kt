package tc

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parser.parseBNF

class GrammarTest {

    @Test
    fun `simple BNF parsing`() {
        val bnf = """
            <S> ::= <A> | "b"
            <A> ::= "a" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals(2, grammar.rules.size)

        val sRule = grammar.rules.find { it.nonTerminal.name == "S" }
        assertNotNull(sRule)
        assertFalse(sRule!!.canEmpty)
        assertEquals(2, sRule.productions.size)

        val aRule = grammar.rules.find { it.nonTerminal.name == "A" }
        assertNotNull(aRule)
        assertTrue(aRule!!.canEmpty)
        assertEquals(1, aRule.productions.size)
    }

    @Test
    fun `complex BNF parsing`() {
        val bnf = """
            <Expr> ::= <Term> "+" <Expr> | <Term>
            <Term> ::= <Factor> "*" <Term> | <Factor>
            <Factor> ::= "(" <Expr> ")" | "id"
        """.trimIndent()

        val grammar = parseBNF(bnf)

        assertEquals(3, grammar.rules.size)

        val exprRule = grammar.rules.find { it.nonTerminal.name == "Expr" }
        assertNotNull(exprRule)
        assertFalse(exprRule!!.canEmpty)
        assertEquals(2, exprRule.productions.size)

        val termRule = grammar.rules.find { it.nonTerminal.name == "Term" }
        assertNotNull(termRule)
        assertFalse(termRule!!.canEmpty)
        assertEquals(2, termRule.productions.size)

        val factorRule = grammar.rules.find { it.nonTerminal.name == "Factor" }
        assertNotNull(factorRule)
        assertFalse(factorRule!!.canEmpty)
        assertEquals(2, factorRule.productions.size)
    }

    @Test
    fun `BNF with epsilon`() {
        val bnf = """
            <S> ::= <A> <B>
            <A> ::= "a" | ε
            <B> ::= "b"
        """.trimIndent()

        val grammar = parseBNF(bnf)

        val aRule = grammar.rules.find { it.nonTerminal.name == "A" }
        assertNotNull(aRule)
        assertTrue(aRule!!.canEmpty)

        val bRule = grammar.rules.find { it.nonTerminal.name == "B" }
        assertNotNull(bRule)
        assertFalse(bRule!!.canEmpty)

        val sRule = grammar.rules.find { it.nonTerminal.name == "S" }
        assertNotNull(sRule)
        assertFalse(sRule!!.canEmpty)
    }

    @Test
    fun `BNF with epsilon 2 depth`() {
        val bnf = """
            <S> ::= <A> <B>
            <A> ::= "a" | ε
            <B> ::= "b" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)

        val aRule = grammar.rules.find { it.nonTerminal.name == "A" }
        assertNotNull(aRule)
        assertTrue(aRule!!.canEmpty)

        val bRule = grammar.rules.find { it.nonTerminal.name == "B" }
        assertNotNull(bRule)
        assertTrue(bRule!!.canEmpty)

        val sRule = grammar.rules.find { it.nonTerminal.name == "S" }
        assertNotNull(sRule)
        assertTrue(sRule!!.canEmpty)
    }

    @Test
    fun `BNF with tokens`() {
        val bnf = """
            <Statement> ::= <Assignment> | <Expression>
            <Assignment> ::= IdentifierToken "=" <Expression>
            <Expression> ::= IdentifierToken | IntNumberToken
        """.trimIndent()

        val grammar = parseBNF(bnf)

        val statementRule = grammar.rules.find { it.nonTerminal.name == "Statement" }
        assertNotNull(statementRule)
        assertEquals(2, statementRule!!.productions.size)

        val assignmentRule = grammar.rules.find { it.nonTerminal.name == "Assignment" }
        assertNotNull(assignmentRule)
        assertEquals(1, assignmentRule!!.productions.size)

        val exprRule = grammar.rules.find { it.nonTerminal.name == "Expression" }
        assertNotNull(exprRule)
        assertEquals(2, exprRule!!.productions.size)
    }

    @Test
    fun `test canEmpty with direct epsilon rule`() {
        val bnf = """
            <S> ::= ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val ruleS = grammar.rules.find { it.nonTerminal.name == "S" }!!

        assertEquals(true, ruleS.canEmpty, "S should be nullable because it directly produces ε")
    }

    @Test
    fun `test canEmpty with multiple production including epsilon`() {
        val bnf = """
            <S> ::= "a" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val ruleS = grammar.rules.find { it.nonTerminal.name == "S" }!!

        assertEquals(true, ruleS.canEmpty, "S should be nullable because it has an epsilon production")
    }

    @Test
    fun `test canEmpty propagation through depth`() {
        val bnf = """
            <S> ::= <A> <B>
            <A> ::= "a" | ε
            <B> ::= "b" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val ruleS = grammar.rules.find { it.nonTerminal.name == "S" }!!
        val ruleA = grammar.rules.find { it.nonTerminal.name == "A" }!!
        val ruleB = grammar.rules.find { it.nonTerminal.name == "B" }!!

        assertEquals(true, ruleA.canEmpty, "A should be nullable because it has an epsilon production")
        assertEquals(true, ruleB.canEmpty, "B should be nullable because it has an epsilon production")
        assertEquals(true, ruleS.canEmpty, "S should be nullable because both A and B are nullable")
    }

    @Test
    fun `test canEmpty with nested dependencies`() {
        val bnf = """
            <S> ::= <A>
            <A> ::= <B>
            <B> ::= "b" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val ruleS = grammar.rules.find { it.nonTerminal.name == "S" }!!
        val ruleA = grammar.rules.find { it.nonTerminal.name == "A" }!!
        val ruleB = grammar.rules.find { it.nonTerminal.name == "B" }!!

        assertEquals(true, ruleB.canEmpty, "B should be nullable because it has an epsilon production")
        assertEquals(true, ruleA.canEmpty, "A should be nullable because B is nullable")
        assertEquals(true, ruleS.canEmpty, "S should be nullable because A is nullable")
    }

    @Test
    fun `test canEmpty with non-nullable dependency`() {
        val bnf = """
            <S> ::= <A> <B>
            <A> ::= "a"
            <B> ::= "b" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val ruleS = grammar.rules.find { it.nonTerminal.name == "S" }!!
        val ruleA = grammar.rules.find { it.nonTerminal.name == "A" }!!
        val ruleB = grammar.rules.find { it.nonTerminal.name == "B" }!!

        assertEquals(false, ruleA.canEmpty, "A should not be nullable because it does not produce ε")
        assertEquals(true, ruleB.canEmpty, "B should be nullable because it has an epsilon production")
        assertEquals(false, ruleS.canEmpty, "S should not be nullable because A is not nullable")
    }

    @Test
    fun `test canEmpty with deep recursive dependencies`() {
        val bnf = """
            <S> ::= <A> <B>
            <A> ::= <C>
            <B> ::= <D>
            <C> ::= <E>
            <D> ::= "d" | ε
            <E> ::= "e" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val ruleS = grammar.rules.find { it.nonTerminal.name == "S" }!!
        val ruleA = grammar.rules.find { it.nonTerminal.name == "A" }!!
        val ruleB = grammar.rules.find { it.nonTerminal.name == "B" }!!
        val ruleC = grammar.rules.find { it.nonTerminal.name == "C" }!!
        val ruleD = grammar.rules.find { it.nonTerminal.name == "D" }!!
        val ruleE = grammar.rules.find { it.nonTerminal.name == "E" }!!

        assertEquals(true, ruleD.canEmpty, "D should be nullable because it has an epsilon production")
        assertEquals(true, ruleE.canEmpty, "E should be nullable because it has an epsilon production")
        assertEquals(true, ruleC.canEmpty, "C should be nullable because E is nullable")
        assertEquals(true, ruleA.canEmpty, "A should be nullable because C is nullable")
        assertEquals(true, ruleB.canEmpty, "B should be nullable because D is nullable")
        assertEquals(true, ruleS.canEmpty, "S should be nullable because A and B are nullable")
    }

}
