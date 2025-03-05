package tc

import lexer.Tokenizer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import parser.*
import printToFile

class DanLangParseTest {

    private val parser = DanLangParser()

    @Test
    fun test() {

        val input = """
            int main() {
                int x = 1;
                int y = 2;
                println(x+y);
            }
        """.trimIndent()

        val inputTokenized = Tokenizer.tokenizeForLRParse(input)
        assertTrue(parser.parse(inputTokenized))

    }

    @Test
    fun astTest() {
        val input = """
            int main() {
                int x = 2*3 + 5 / 3 + 2 * 6;
            }
        """.trimIndent()

        val inputTokenized = Tokenizer.tokenizeForLRParse(input)
        val root = parser.parseAST(inputTokenized)
        assertNotNull(root)
        if(root != null) {
            (root as? DanLangASTNode)?.toDot()?.let {
                printToFile(it)
            }

        }
    }
}