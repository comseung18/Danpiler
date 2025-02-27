package tc

import lexer.Tokenizer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parser.*
import printToFile

class DanLangParseTest {
    @Test
    fun test() {

        val grammar = parseBNF(danLangBNF)
        val parser = LALRParser(grammar, NonTerminalItem("Program"))

        val input = """
            int main() {
                int x = 1;
                int y = 2;
                println(x+y);
            }
        """.trimIndent()

        val inputTokenized = Tokenizer.tokenize(input).map { TokenTerminalItem(it.first, it.second)} + endTerminalItem

        assertTrue(parser.parse(inputTokenized))

    }
}