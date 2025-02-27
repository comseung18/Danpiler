package parser

import lexer.Token

const val EPSILON = "ε"

data class Production(
    val items: List<GrammarItem>,
    val semanticAction: (() -> Unit)? = null
)

data class GrammarRule(
    val nonTerminal: NonTerminalItem, // 비종료 기호
    var canEmpty: Boolean,
    val productions: List<Production> // 생산 규칙: 여러 토큰 리스트
)

sealed interface GrammarItem {
    val name: String
}

interface TerminalItem : GrammarItem {
    val value: String
}

data class TokenTerminalItem(
    val token: Token,
) : TerminalItem {
    override val name: String = token.tokenName
    override val value: String = token.tokenName
}

data class ConstTerminalItem(
    override val name: String,
) : TerminalItem {
    override val value: String = name
}

val emptyTerminalItem = ConstTerminalItem("ε")
val endTerminalItem = ConstTerminalItem("$")

data class NonTerminalItem(
    override val name: String,
) : GrammarItem

data class Grammar(
    val rules: List<GrammarRule> // 모든 문법 규칙
) {
    val nonTerminalItemToProductions: Map<NonTerminalItem, GrammarRule> by lazy {
        mutableMapOf<NonTerminalItem, GrammarRule>().apply {
            for(rule in rules) {
                this@apply.put(rule.nonTerminal, rule)
            }
        }
    }
}


fun parseBNF(bnf: String): Grammar {

    // # 이후는 무시
    // 문법은 <NonTerminal> ::= (<Terminal> or <NonTerminal>)+ | .. 의 형태이다.
    val lines = bnf.lines().filter { it.isNotBlank() }.map { line ->
        line.trim().substringBefore("#")
    }

    val grammarRules = mutableSetOf<GrammarRule>()

    // step 1. parse Rule
    lines.forEach { line ->

        val parts = line.split("::=").map { it.trim() }

        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid BNF format: $line")
        }

        val nonTerminalName = parts[0].removeSurrounding("<", ">")
        val productions = mutableListOf<Production>()
        var hasEpsilon = false

        val definitionPart = parts[1].split("|")
        definitionPart.forEach { rule ->

            val grammarItems = mutableListOf<GrammarItem>()

            val ruleSplit = rule.trim().split(" ").filter { it.isNotBlank() }
            ruleSplit.forEach { item ->
                when {
                    item.startsWith("<") && item.endsWith(">") -> {
                        grammarItems.add(
                            NonTerminalItem(
                                item.removeSurrounding("<", ">"),
                            )
                        )
                    }

                    Token.values().any { it.name == item } -> {
                        grammarItems.add(
                            TokenTerminalItem(Token.valueOf(item))
                        )
                    }

                    item.startsWith("\"") && item.endsWith("\"") -> {
                        grammarItems.add(
                            ConstTerminalItem(
                                item.removeSurrounding("\"", "\""),
                            )
                        )
                    }

                    item == EPSILON && ruleSplit.size == 1 -> hasEpsilon = true
                }
            }

            if (grammarItems.isNotEmpty()) {
                productions.add(Production(grammarItems))
            }

        }

        grammarRules.add(
            GrammarRule(
                NonTerminalItem(nonTerminalName),
                hasEpsilon,
                productions
            )
        )

    }


    // step 2. calculate canEmpty Iterative
    var changed: Boolean
    do {
        changed = false

        grammarRules.forEach { grammarRule ->
            if (!grammarRule.canEmpty) {
                var allCanEmpty = true
                grammarRule.productions.forEach l1@{ production ->
                    production.items.forEach l2@{ item ->
                        when (item) {
                            is NonTerminalItem -> {
                                val childCanEmpty = grammarRules.find { it.nonTerminal.name == item.name }?.canEmpty
                                    ?: throw IllegalArgumentException("unknown NonTerminalItem ${item.name} ")

                                if (!childCanEmpty) {
                                    allCanEmpty = false
                                    return@l2
                                }
                            }

                            is TerminalItem -> {
                                allCanEmpty = false
                                return@l2
                            }
                        }
                    }
                    if (allCanEmpty) return@l1
                }
                if (allCanEmpty) {
                    grammarRule.canEmpty = true
                    changed = true
                }
            }
        }

    } while (changed)


    return Grammar(grammarRules.toList())
}

