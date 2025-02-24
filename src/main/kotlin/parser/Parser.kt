package parser

abstract class Parser(
    val grammar: Grammar,
    val root: NonTerminalItem
)

data class LR0Item(
    val nonTerminal: NonTerminalItem,
    val production: List<GrammarItem>, // 생산 규칙: 여러 토큰 리스트
    val dotIndex: Int // range [0, production.size]
)

data class LR0ItemCollection(
    val items: Set<LR0Item>
)

class LR0Parser(grammar: Grammar, root: NonTerminalItem) : Parser(grammar, root) {

    private val lr0CollectionSet = mutableSetOf<LR0ItemCollection>()

    init {
        lr0CollectionSet.clear()
        lr0CollectionSet.add(
            closure(
                LR0ItemCollection(
                    setOf(
                        LR0Item(
                            NonTerminalItem(root.name + "`"),
                            listOf(root),
                            0
                        )
                    )
                )
            )
        )
    }

    fun closure(collection: LR0ItemCollection): LR0ItemCollection {
        val ret = mutableSetOf<LR0Item>()
        ret.addAll(collection.items)

        val q = ArrayDeque<NonTerminalItem>()

        // NonTerminalItem ::= 형태를 closure 에 포함했는 지 여부
        val visited = mutableSetOf<NonTerminalItem>()

        collection.items.forEach {
            if(it.dotIndex < it.production.size) {
                val gItem = it.production[it.dotIndex]
                if(gItem is NonTerminalItem && !visited.contains(gItem)) {
                    val rules = grammar.nonTerminalItemToProductions[gItem] ?: return@forEach
                    q.add(rules.nonTerminal)
                    visited.add(gItem)
                }
            }
        }

        while(q.isNotEmpty()) {
            val here = q.removeFirst()
            val rules = grammar.nonTerminalItemToProductions[here] ?: continue
            rules.productions.forEach { production ->
                if(production.isNotEmpty()) {
                    ret.add(LR0Item(
                        here,
                        production,
                        0
                    ))

                    val r = production.first()
                    if(r is NonTerminalItem && !visited.contains(r)) {
                        q.add(r)
                        visited.add(r)
                    }
                }
            }
        }

        return LR0ItemCollection(ret)
    }

}