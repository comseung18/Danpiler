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
            Token.OperatorToken to "+",
            Token.IntNumberToken to "456"
        )
        val actualTokens = Tokenizer.tokenize(input)
        assertEquals(expectedTokens, actualTokens)
    }

    @Test
    fun `test complex valid input`() {
        val input = "for (i = 0; i < 10; i++) { print(i); }"
        val expectedTokens = listOf(
            Token.KeywordToken to "for",
            Token.LParenToken to "(",
            Token.IdentifierToken to "i",
            Token.OperatorToken to "=",
            Token.IntNumberToken to "0",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "i",
            Token.OperatorToken to "<",
            Token.IntNumberToken to "10",
            Token.SemicolonToken to ";",
            Token.IdentifierToken to "i",
            Token.OperatorToken to "++",
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
            Token.KeywordToken to "int",
            Token.IdentifierToken to "x",
            Token.OperatorToken to "=",
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
}
