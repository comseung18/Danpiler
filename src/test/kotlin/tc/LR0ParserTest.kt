import lexer.Token
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parser.*

class LR0AutomataTest {

    @Test
    fun testClosure() {

        val bnf = """
            <S> ::= <A>
            <A> ::= "a" <A> | "b"
        """.trimIndent()

        val grammar = parseBNF(bnf)

        val parser = SLRParser(grammar, NonTerminalItem("S"))

        // 초기 항목 설정: S → .A
        val initialItem = LR0Item(NonTerminalItem("S"), listOf(NonTerminalItem("A")), 0)

        // When: closure 함수 호출
        val closureSet = parser.closure(setOf(initialItem))

        // Then: closure 결과 검증
        val expectedItems = setOf(
            LR0Item(NonTerminalItem("S"), listOf(NonTerminalItem("A")), 0),
            LR0Item(NonTerminalItem("A"), listOf(ConstTerminalItem("a"), NonTerminalItem("A")), 0),
            LR0Item(NonTerminalItem("A"), listOf(ConstTerminalItem("b")), 0)
        )

        assertEquals(expectedItems, closureSet)
    }

    @Test
    fun testClosure_MoreComplexGrammar() {
        val bnf = """
            <S> ::= <A> <B>
            <A> ::= "a" | "b"
            <B> ::= <C> "d"
            <C> ::= "c" | ε
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = SLRParser(grammar, NonTerminalItem("S"))

        // 초기 항목: S → .A B
        val initialItem = LR0Item(NonTerminalItem("S"), listOf(NonTerminalItem("A"), NonTerminalItem("B")), 0)

        val closureSet = parser.closure(setOf(initialItem))

        val expectedItems = setOf(
            LR0Item(NonTerminalItem("S"), listOf(NonTerminalItem("A"), NonTerminalItem("B")), 0),
            LR0Item(NonTerminalItem("A"), listOf(ConstTerminalItem("a")), 0),
            LR0Item(NonTerminalItem("A"), listOf(ConstTerminalItem("b")), 0)
        )

        assertTrue(closureSet.containsAll(expectedItems))
    }

    @Test
    fun testClosure_WithRecursion() {
        val bnf = """
            <E> ::= <E> "+" <T> | <T>
            <T> ::= <T> "*" <F> | <F>
            <F> ::= "(" <E> ")" | "id"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = SLRParser(grammar, NonTerminalItem("E"))

        // 초기 항목: E → .E + T | .T
        val initialItem = LR0Item(NonTerminalItem("E"), listOf(NonTerminalItem("E"), ConstTerminalItem("+"), NonTerminalItem("T")), 0)

        val closureSet = parser.closure(setOf(initialItem))

        val expectedItems = setOf(
            LR0Item(NonTerminalItem("E"), listOf(NonTerminalItem("E"), ConstTerminalItem("+"), NonTerminalItem("T")), 0),
            LR0Item(NonTerminalItem("T"), listOf(NonTerminalItem("T"), ConstTerminalItem("*"), NonTerminalItem("F")), 0),
            LR0Item(NonTerminalItem("T"), listOf(NonTerminalItem("F")), 0),
            LR0Item(NonTerminalItem("F"), listOf(ConstTerminalItem("("), NonTerminalItem("E"), ConstTerminalItem(")")), 0),
            LR0Item(NonTerminalItem("F"), listOf(ConstTerminalItem("id")), 0)
        )

        assertTrue(closureSet.containsAll(expectedItems))
    }

    @Test
    fun testClosure_IndirectRecursion() {
        val bnf = """
            <S> ::= <A>
            <A> ::= <B>
            <B> ::= <C>
            <C> ::= "c"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = SLRParser(grammar, NonTerminalItem("S"))

        // 초기 항목: S → .A
        val initialItem = LR0Item(NonTerminalItem("S"), listOf(NonTerminalItem("A")), 0)

        val closureSet = parser.closure(setOf(initialItem))

        val expectedItems = setOf(
            LR0Item(NonTerminalItem("S"), listOf(NonTerminalItem("A")), 0),
            LR0Item(NonTerminalItem("A"), listOf(NonTerminalItem("B")), 0),
            LR0Item(NonTerminalItem("B"), listOf(NonTerminalItem("C")), 0),
            LR0Item(NonTerminalItem("C"), listOf(ConstTerminalItem("c")), 0)
        )

        assertEquals(expectedItems, closureSet)
    }

    @Test
    fun testClosure_ComplexLanguage() {
        val bnf = """
            <Program> ::= <ClassDeclarations> <FunctionDeclarations>
            <ClassDeclarations> ::= <ClassDeclaration> <ClassDeclarations> | ε
            <ClassDeclaration> ::= "class" IdentifierToken "{" <ClassMembers> "}"
            <ClassMembers> ::= <ClassMember> <ClassMembers> | ε
            <ClassMember> ::= <AccessModifier> <Type> IdentifierToken ";" | <AccessModifier> <FunctionDeclaration>
            <AccessModifier> ::= "public" | "private"
            
            <FunctionDeclarations> ::= <FunctionDeclaration> <FunctionDeclarations> | ε
            <FunctionDeclaration> ::= <Type> IdentifierToken "(" <ParameterListOpt> ")" "{" <Statements> "}"
            <ParameterListOpt> ::= <ParameterList> | ε
            <ParameterList> ::= <Parameter> | <Parameter> "," <ParameterList>
            <Parameter> ::= <Type> IdentifierToken
            <Type> ::= "int" | "float" | "string" | "boolean"
            
            <Statements> ::= <Statement> <Statements> | ε
            <Statement> ::= <VariableDeclaration> | <Assignment> | <FunctionCall> ";" | <IfStatement>  | <LoopStatement> | <ReturnStatement> | <Block>
            <Block> ::= "{" <Statements> "}"
            <VariableDeclaration> ::= <Type> IdentifierToken ";" | <Type> IdentifierToken "=" <Expression> ";"
            <Assignment> ::= IdentifierToken "=" <Expression> ";"
            <FunctionCall> ::= IdentifierToken "(" <ArgumentListOpt> ")"
            <ArgumentListOpt> ::= <ArgumentList> | ε
            <ArgumentList> ::= <Expression> | <Expression> "," <ArgumentList>
            
            <IfStatement> ::= "if" "(" <Expression> ")" <Block> <ElseIfOpt> <ElseOpt>
            <ElseIfOpt> ::= "else if" "(" <Expression> ")" <Block> <ElseIfOpt> | ε
            <ElseOpt> ::= "else" <Block> | ε
            
            <LoopStatement> ::= "for" "(" <VariableDeclaration> <Expression> ";" <Assignment> ")" <Block>  | "while" "(" <Expression> ")" <Block>
                              
            <ReturnStatement> ::= "return" <ExpressionOpt> ";"
            <ExpressionOpt> ::= <Expression> | ε
            
            <Expression> ::= <Term> <ExpressionTail>
            <ExpressionTail> ::= ArithmeticOperatorToken <Term> <ExpressionTail> | ε
            <Term> ::= <Factor> <TermTail>
            <TermTail> ::= ArithmeticOperatorToken <Factor> <TermTail> | ε
            <Factor> ::= IdentifierToken | IntNumberToken | FloatNumberToken | "(" <Expression> ")" | <FunctionCall>
            
            <ComparisonExpression> ::= <Expression> ComparisonOperatorToken <Expression>
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = SLRParser(grammar, NonTerminalItem("Program"))

        // 초기 항목: Program → .ClassDeclarations FunctionDeclarations
        val initialItem = LR0Item(
            NonTerminalItem("Program"),
            listOf(NonTerminalItem("ClassDeclarations"), NonTerminalItem("FunctionDeclarations")),
            0
        )

        val closureSet = parser.closure(setOf(initialItem))

        // Closure가 포함해야 할 예상 항목들
        val expectedItems = setOf(
            LR0Item(NonTerminalItem("Program"), listOf(NonTerminalItem("ClassDeclarations"), NonTerminalItem("FunctionDeclarations")), 0),
            LR0Item(NonTerminalItem("ClassDeclarations"), listOf(NonTerminalItem("ClassDeclaration"), NonTerminalItem("ClassDeclarations")), 0),
            LR0Item(NonTerminalItem("ClassDeclaration"), listOf(ConstTerminalItem("class"), TokenTerminalItem(Token.IdentifierToken), ConstTerminalItem("{"), NonTerminalItem("ClassMembers"), ConstTerminalItem("}")), 0),
        )

        assertTrue(closureSet.containsAll(expectedItems))
    }
}