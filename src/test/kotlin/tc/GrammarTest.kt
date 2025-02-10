package tc

import org.junit.jupiter.api.Test
import parser.Grammar
import parser.danLangGrammar
import parser.parseBNF

class GrammarTest {

    @Test
    fun test() {
        println(parseBNF(danLangGrammar))

    }
}
