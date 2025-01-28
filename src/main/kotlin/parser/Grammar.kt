data class GrammarRule(
    val nonTerminal: String, // 비종료 기호
    val productions: List<List<String>> // 생산 규칙: 여러 토큰 리스트
)

data class Grammar(
    val startSymbol: String, // 시작 기호
    val rules: List<GrammarRule> // 모든 문법 규칙
)


fun parseBNF(bnf: String): Grammar {
    val rules = mutableListOf<GrammarRule>()
    var startSymbol: String? = null

    bnf.lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() } // 빈 줄 제외
        .forEach { line ->
            val sanitizedLine = line.split("#").first().trim() // 주석 제거
            if (sanitizedLine.isNotEmpty()) {
                val (lhs, rhs) = sanitizedLine.split("::=").map { it.trim() }
                val productions = rhs.split("|").map { s -> s.trim().split(" ").filter { it.isNotEmpty() } }

                if (startSymbol == null) startSymbol = lhs // 첫 번째 규칙을 시작 기호로 설정
                rules.add(GrammarRule(nonTerminal = lhs, productions = productions))
            }
        }

    val nonTerminals = rules.map { it.nonTerminal }.toSet()
    val undefinedNonTerminals = rules
        .flatMap { it.productions.flatten() }
        .filter { it.startsWith("<") && it.endsWith(">") } // 논터미널 필터링
        .filter { it !in nonTerminals } // 정의되지 않은 논터미널 찾기
        .toSet()

    if (undefinedNonTerminals.isNotEmpty()) {
        throw IllegalArgumentException("Undefined non-terminals: $undefinedNonTerminals")
    }

    return Grammar(
        startSymbol = startSymbol ?: throw IllegalArgumentException("No start symbol defined in BNF"),
        rules = rules
    )
}

