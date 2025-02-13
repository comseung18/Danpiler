package parser


class FirstFollowCalculator(private val grammar: Grammar) {

    private val firstSet = mutableMapOf<NonTerminalItem, MutableSet<TerminalItem>>()

    init {
        firstSet.clear()
        calculateFirst()
    }

    fun getFirstSet(item: GrammarItem): Set<TerminalItem> {
        return when(item) {
            is NonTerminalItem -> firstSet[item] ?: throw IllegalArgumentException("no firstSet for ${item.name}")
            is TerminalItem -> setOf(item)
        }
    }

    private fun calculateFirst() {
        var changed: Boolean
        do {
            changed = false
            for(rule in grammar.rules) {
                val beforeSize = firstSet[rule.nonTerminal]?.size ?: 0
                for(production in rule.productions) {
                    for(item in production) {
                        when(item) {
                            is NonTerminalItem -> {
                                firstSet.getOrPut(rule.nonTerminal) { mutableSetOf() }.addAll(
                                    firstSet[item] ?: emptySet()
                                )

                                val itemCanEmpty = grammar.nonTerminalItemToProductions[item]?.canEmpty
                                    ?: throw IllegalArgumentException("unknown for rule: ${rule.nonTerminal.name} item ${item.name}")

                                if(!itemCanEmpty) break
                            }
                            is TerminalItem -> {
                                firstSet.getOrPut(rule.nonTerminal) { mutableSetOf() }.add(item)
                                break
                            }
                        }
                    }
                }
                val afterSize = firstSet[rule.nonTerminal]?.size ?: 0
                if(beforeSize != afterSize) changed = true
            }
        } while(changed)
    }

}