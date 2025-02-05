package tc

import lexer.Token
import lexer.Tokenizer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TokenizerTest {

    @Test
    fun `test single valid token`() {
        val input = "123"
        val expectedTokens = listOf(Token.IntNumberToken to "123")
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test multiple valid tokens with whitespace`() {
        val input = "123 + 456"
        val expectedTokens = listOf(
            Token.IntNumberToken to "123",
            Token.ArithmeticOperatorToken to "+",
            Token.IntNumberToken to "456"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test complex valid input`() {
        val input = "for (i = 0; i < 10; i++) { print(i); }"
        val expectedTokens = listOf(
            Token.ForToken to "for",
            Token.LParenToken to "(",
            Token.IdentifierToken to "i",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "0",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "i",
            Token.ComparisonOperatorToken to "<",
            Token.IntNumberToken to "10",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "i",
            Token.IncrementDecrementToken to "++",
            Token.RParenToken to ")",
            Token.LBraceToken to "{",
            Token.IdentifierToken to "print",
            Token.LParenToken to "(",
            Token.IdentifierToken to "i",
            Token.RParenToken to ")",
            Token.SemicolonToken to ";",
            Token.RBraceToken to "}"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test invalid token`() {
        val input = "123abc" // Invalid token due to number followed by letters
        assertThrows(IllegalArgumentException::class.java) {
            Tokenizer.tokenize(input)
        }
    }

    @Test
    fun `test input with ignored whitespace`() {
        val input = "int x = 42; // Variable declaration \n"
        val expectedTokens = listOf(
            Token.TypeToken to "int",
            Token.IdentifierToken to "x",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "42",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test empty input`() {
        val input = ""
        val expectedTokens = emptyList<Pair<Token, String>>()
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test identifier starting with underscore`() {
        val input = "_myVariable = 123;"
        val expectedTokens = listOf(
            Token.IdentifierToken to "_myVariable",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "123",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test mixed operators`() {
        val input = "a += b-- * c;"
        val expectedTokens = listOf(
            Token.IdentifierToken to "a",
            Token.AssignmentOperatorToken to "+=",
            Token.IdentifierToken to "b",
            Token.IncrementDecrementToken to "--",
            Token.ArithmeticOperatorToken to "*",
            Token.IdentifierToken to "c",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test various whitespaces`() {
        val input = "int\tvar\n=\r42;"
        val expectedTokens = listOf(
            Token.TypeToken to "int",
            Token.IdentifierToken to "var",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "42",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test invalid complex input`() {
        val input = "42..34" // 잘못된 FLOAT 형태
        assertThrows(IllegalArgumentException::class.java) {
            Tokenizer.tokenize(input)
        }
    }

    @Test
    fun `test long valid input`() {
        val input = "while (i < 100000) { total += i; i++; }"
        val expectedTokens = listOf(
            Token.WhileToken to "while",
            Token.LParenToken to "(",
            Token.IdentifierToken to "i",
            Token.ComparisonOperatorToken to "<",
            Token.IntNumberToken to "100000",
            Token.RParenToken to ")",
            Token.LBraceToken to "{",
            Token.IdentifierToken to "total",
            Token.AssignmentOperatorToken to "+=",
            Token.IdentifierToken to "i",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "i",
            Token.IncrementDecrementToken to "++",
            Token.SemicolonToken to ";",
            Token.RBraceToken to "}"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test type tokens`() {
        val input = "int x = 42; float arr = 3.14;"
        val expectedTokens = listOf(
            Token.TypeToken to "int",
            Token.IdentifierToken to "x",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "42",
            Token.SemicolonToken to ";",
            Token.TypeToken to "float",
            Token.IdentifierToken to "arr",
            Token.AssignmentOperatorToken to "=",
            Token.FloatNumberToken to "3.14",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test new and delete keywords`() {
        val input = "new MyClass(); delete obj;"
        val expectedTokens = listOf(
            Token.NewToken to "new",
            Token.IdentifierToken to "MyClass",
            Token.LParenToken to "(",
            Token.RParenToken to ")",
            Token.SemicolonToken to ";",
            Token.DeleteToken to "delete",
            Token.IdentifierToken to "obj",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test operators`() {
        val input = "a += 10; b++; if (a == b) { print(a); }"
        val expectedTokens = listOf(
            Token.IdentifierToken to "a",
            Token.AssignmentOperatorToken to "+=",
            Token.IntNumberToken to "10",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "b",
            Token.IncrementDecrementToken to "++",
            Token.SemicolonToken to ";",
            Token.IfToken to "if",
            Token.LParenToken to "(",
            Token.IdentifierToken to "a",
            Token.ComparisonOperatorToken to "==",
            Token.IdentifierToken to "b",
            Token.RParenToken to ")",
            Token.LBraceToken to "{",
            Token.IdentifierToken to "print",
            Token.LParenToken to "(",
            Token.IdentifierToken to "a",
            Token.RParenToken to ")",
            Token.SemicolonToken to ";",
            Token.RBraceToken to "}"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test array declaration and access`() {
        val input = "int[] numbers = new int[5]; numbers[0] = 42;"
        val expectedTokens = listOf(
            Token.TypeToken to "int",
            Token.LSquareToken to "[",
            Token.RSquareToken to "]",
            Token.IdentifierToken to "numbers",
            Token.AssignmentOperatorToken to "=",
            Token.NewToken to "new",
            Token.TypeToken to "int",
            Token.LSquareToken to "[",
            Token.IntNumberToken to "5",
            Token.RSquareToken to "]",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "numbers",
            Token.LSquareToken to "[",
            Token.IntNumberToken to "0",
            Token.RSquareToken to "]",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "42",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test single-line and multi-line comments`() {
        val input = "// This is a single-line comment\n/* This is a multi-line comment */ int x = 10;"
        val expectedTokens = listOf(
            Token.TypeToken to "int",
            Token.IdentifierToken to "x",
            Token.AssignmentOperatorToken to "=",
            Token.IntNumberToken to "10",
            Token.SemicolonToken to ";"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }
}
