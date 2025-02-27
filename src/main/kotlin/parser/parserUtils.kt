package parser

private const val END_OF_INPUT = "$"

class FirstFollowCalculator(private val grammar: Grammar, private val startSymbol: String) {

    private val firstSet = mutableMapOf<NonTerminalItem, MutableSet<TerminalItem>>()
    private val followSet = mutableMapOf<NonTerminalItem, MutableSet<TerminalItem>>()
    init {
        firstSet.clear()
        followSet.clear()
        calculateFirst()
        calculateFollow()
    }

    fun getFollowSet(item: GrammarItem) : Set<TerminalItem> {
        return when(item) {
            is NonTerminalItem -> followSet[item] ?: throw IllegalArgumentException("no followSet for ${item.name}")
            is TerminalItem -> emptySet()
        }
    }

    fun getFirstSet(item: GrammarItem): Set<TerminalItem> {
        return when(item) {
            is NonTerminalItem -> firstSet[item] ?: throw IllegalArgumentException("no firstSet for ${item.name}")
            is TerminalItem -> setOf(item)
        }
    }

    fun getFirstSetTwo(a: GrammarItem, b: GrammarItem) : Set<TerminalItem> {
        return if(a is NonTerminalItem && grammar.nonTerminalItemToProductions[a]?.canEmpty == true) {
            getFirstSet(a) + getFirstSet(b)
        } else {
            getFirstSet(a)
        }
    }

    private fun calculateFirst() {
        var changed: Boolean
        do {
            changed = false
            for(rule in grammar.rules) {
                val beforeSize = firstSet[rule.nonTerminal]?.size ?: 0
                for(production in rule.productions) {
                    for(item in production.items) {
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

    private fun calculateFollow() {

        followSet.getOrPut(NonTerminalItem(startSymbol)) { mutableSetOf() }.add(ConstTerminalItem(END_OF_INPUT))

        // A -> aBpD 에서 FIRST(p) 가 FOLLOW(B) 에 들어간다. 추가로 p 가 empty 이면 FIRST(D) 도 FOLLOW(B) 에 들어간다.
        for(rule in grammar.rules) {
            for(production in rule.productions) {
                for(i in production.items.indices) {
                    val B = production.items[i]
                    if(B is NonTerminalItem) {
                        for(j in (i+1) until production.items.size) {
                            val p = production.items[j]
                            val firstOfP = getFirstSet(p)
                            followSet.getOrPut(B) { mutableSetOf() }.addAll(firstOfP)
                            val pCanEmpty = when(p) {
                                is NonTerminalItem -> {
                                    grammar.nonTerminalItemToProductions[p]?.canEmpty ?: throw IllegalArgumentException("unknown NonTerminal P ${p.name}")
                                }
                                is TerminalItem -> {
                                    break
                                }
                            }
                            if(!pCanEmpty) break
                        }

                    }
                }
            }
        }

        var changed: Boolean
        do {
            changed = false

            for(rule in grammar.rules) {

                val A = rule.nonTerminal

                for(production in rule.productions) {

                    for(i in production.items.indices) {
                        val B = production.items[i]

                        if(B !is NonTerminalItem) continue

                        var canAllEmptyAfterB = true
                        for(j in (i+1) until production.items.size) {

                            val pCanEmpty = when(val p = production.items[j]) {
                                is NonTerminalItem -> {
                                    grammar.nonTerminalItemToProductions[p]?.canEmpty ?: throw IllegalArgumentException("unknown NonTerminal P ${p.name}")
                                }
                                is TerminalItem -> {
                                    false
                                }
                            }

                            if(!pCanEmpty) {
                                canAllEmptyAfterB = false
                                break
                            }
                        }

                        if(canAllEmptyAfterB) {
                            val beforeSize = followSet.getOrPut(B) { mutableSetOf() }.size
                            followSet[B]?.addAll(followSet.getOrPut(A) { mutableSetOf() })  // FOLLOW(A) → FOLLOW(B)
                            if (beforeSize != followSet[B]?.size) changed = true
                        }
                    }
                }
            }

        } while(changed)
    }
}