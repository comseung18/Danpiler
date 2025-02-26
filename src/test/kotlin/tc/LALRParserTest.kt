import lexer.Token
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parser.*

class LALRParserTest {
    @Test
    fun testSimpleGrammar() {
        val bnf = """
            <S> ::= <A> "b"
            <A> ::= "a"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = LALRParser(grammar, NonTerminalItem("S"))

        val input = listOf(ConstTerminalItem("a"), ConstTerminalItem("b"), endTerminalItem)
        val result = parser.parse(input)



        assertTrue(result, "Simple grammar should be parsed successfully")
    }

    @Test
    fun testExpressionGrammar() {
        val bnf = """
            <E> ::= <E> "+" <T> | <T>
            <T> ::= <T> "*" <F> | <F>
            <F> ::= "(" <E> ")" | "id"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = LALRParser(grammar, NonTerminalItem("E"))

        val input = listOf(ConstTerminalItem("id"), ConstTerminalItem("+"), ConstTerminalItem("id"), endTerminalItem)
        val result = parser.parse(input)

        assertTrue(result, "Expression grammar should be parsed successfully")
    }

    @Test
    fun testIfStatement() {
        val bnf = """
            <S> ::= "if" "(" <E> ")" <S> | "a"
            <E> ::= "id"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = LALRParser(grammar, NonTerminalItem("S"))

        val input = listOf(ConstTerminalItem("if"), ConstTerminalItem("("), ConstTerminalItem("id"), ConstTerminalItem(")"), ConstTerminalItem("a"), endTerminalItem)
        val result = parser.parse(input)

        assertTrue(result, "If statement should be parsed successfully")
    }


    @Test
    fun testComplexGrammar() {
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
            <Statement> ::= <VariableDeclaration> | <Assignment> | <FunctionCall> ";" | <Block>
            <Block> ::= "{" <Statements> "}"
            <VariableDeclaration> ::= <Type> IdentifierToken ";" | <Type> IdentifierToken "=" <Expression> ";"
            <Assignment> ::= IdentifierToken "=" <Expression> ";"
            <FunctionCall> ::= IdentifierToken "(" <ArgumentListOpt> ")"
            <ArgumentListOpt> ::= <ArgumentList> | ε
            <ArgumentList> ::= <Expression> | <Expression> "," <ArgumentList>
            <Expression> ::= IdentifierToken | IntNumberToken | FloatNumberToken
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = LALRParser(grammar, NonTerminalItem("Program"))

        val input = listOf(
            ConstTerminalItem("class"), TokenTerminalItem(Token.IdentifierToken), ConstTerminalItem("{"),
            ConstTerminalItem("public"), ConstTerminalItem("int"), TokenTerminalItem(Token.IdentifierToken), ConstTerminalItem(";"),
            ConstTerminalItem("}"),
            ConstTerminalItem("int"), TokenTerminalItem(Token.IdentifierToken), ConstTerminalItem("("), ConstTerminalItem(")"),
            ConstTerminalItem("{"), TokenTerminalItem(Token.IdentifierToken), ConstTerminalItem("="), TokenTerminalItem(Token.IntNumberToken), ConstTerminalItem(";"),
            ConstTerminalItem("}"),
            endTerminalItem
        )
        val result = parser.parse(input)

        assertTrue(result, "Complex class-based grammar should be parsed successfully")
    }

    @Test
    fun testInvalidInput() {
        val bnf = """
            <S> ::= <A> "b"
            <A> ::= "a"
        """.trimIndent()

        val grammar = parseBNF(bnf)
        val parser = LALRParser(grammar, NonTerminalItem("S"))

        val input = listOf(ConstTerminalItem("b"), ConstTerminalItem("a"), endTerminalItem) // 잘못된 순서
        val result = parser.parse(input)

        assertFalse(result, "Invalid input should not be parsed successfully")
    }
}