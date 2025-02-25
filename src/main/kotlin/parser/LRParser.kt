package parser

import java.io.File
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayDeque

abstract class LRParser(
    val grammar: Grammar,
    val root: NonTerminalItem
) {
    sealed interface Action {
        data class Shift(val j: Int) : Action
        data class Reduce(
            val n: NonTerminalItem,
            val production: List<GrammarItem>
        ) : Action
        data object Accept : Action
        data object Error : Action
    }

    abstract val goto: MutableMap<Pair<Int, GrammarItem>, Int>

    abstract fun exportToDot() : String

    abstract fun action(
        s: Int,
        terminalItem: TerminalItem
    ): Action

    fun parse(input: List<TerminalItem>) : Boolean {
        val stack: Stack<Int> = Stack<Int>()
        stack.push(0)

        var index = 0
        while(index < input.size) {
            val a = input[index]
            when(val act = action(stack.peek(), a)) {
                Action.Accept -> return true
                Action.Error -> return false
                is Action.Reduce -> {
                    repeat(act.production.size) {
                        println("stack pop: ${stack.pop()}")
                    }
                    stack.push(goto[Pair(stack.peek(), act.n)])
                    println("reduce(${stack.peek()}) : ${act.n.name} ::= ${act.production}")
                }
                is Action.Shift -> {
                    println("shift ${stack.peek()} to ${act.j} by $a ")
                    stack.push(act.j)
                    ++index

                }
            }
        }

        return true
    }
}

data class LR0Item(
    val nonTerminal: NonTerminalItem,
    val production: List<GrammarItem>, // 생산 규칙: 여러 토큰 리스트
    val dotIndex: Int // range [0, production.size]
)

data class LR0ItemCollection(
    val items: Set<LR0Item>
)

class SLRParser(grammar: Grammar, root: NonTerminalItem) : LRParser(grammar, root) {

    private var collectionCounter = 0

    private val reverseLr0CollectionMap : MutableMap<LR0ItemCollection, Int> = mutableMapOf()
    private val lr0CollectionMap: MutableMap<Int, LR0ItemCollection> = mutableMapOf()
    override val goto: MutableMap<Pair<Int, GrammarItem>, Int> = mutableMapOf()

    private val firstFollowCalculator = FirstFollowCalculator(grammar, root.name)

    init {
        lr0CollectionMap.clear()
        goto.clear()

        lr0CollectionMap[0] = LR0ItemCollection(
            closure(
                setOf(
                    LR0Item(
                        NonTerminalItem(root.name + "`"),
                        listOf(root),
                        0
                    )
                )
            )
        )
        reverseLr0CollectionMap[lr0CollectionMap[0]!!] = 0

        collectionCounter = 1
        val q = ArrayDeque<Int>()
        q.add(0)

        while (q.isNotEmpty()) {
            val here = q.removeFirst()
            val lr0Collection = lr0CollectionMap[here] ?: continue

            lr0Collection.items.filter { it.dotIndex in it.production.indices }.groupBy {
                LR0Item(it.nonTerminal, it.production, it.dotIndex + 1)
                it.production[it.dotIndex]
            }.forEach { (x, rules) ->
                val nextItems = rules.map { LR0Item(it.nonTerminal, it.production, it.dotIndex + 1) }
                val nextLr0Collection = LR0ItemCollection(closure(nextItems.toSet()))

                if(nextLr0Collection !in reverseLr0CollectionMap) {
                    lr0CollectionMap[collectionCounter] = nextLr0Collection
                    reverseLr0CollectionMap[nextLr0Collection] = collectionCounter

                    goto[Pair(here, x)] = collectionCounter

                    q.add(collectionCounter)

                    ++collectionCounter
                } else {
                    goto[Pair(here, x)] = reverseLr0CollectionMap[nextLr0Collection]!!
                }

            }
        }
    }

    fun closure(items: Set<LR0Item>): Set<LR0Item> {
        val ret = mutableSetOf<LR0Item>()
        ret.addAll(items)

        val q = ArrayDeque<NonTerminalItem>()

        // NonTerminalItem ::= 형태를 closure 에 포함했는 지 여부
        val visited = mutableSetOf<NonTerminalItem>()

        items.forEach {
            if (it.dotIndex < it.production.size) {
                val gItem = it.production[it.dotIndex]
                if (gItem is NonTerminalItem && !visited.contains(gItem)) {
                    val rules = grammar.nonTerminalItemToProductions[gItem] ?: return@forEach
                    q.add(rules.nonTerminal)
                    visited.add(gItem)
                }
            }
        }

        while (q.isNotEmpty()) {
            val here = q.removeFirst()
            val rules = grammar.nonTerminalItemToProductions[here] ?: continue
            rules.productions.forEach { production ->
                if (production.isNotEmpty()) {
                    ret.add(
                        LR0Item(
                            here,
                            production,
                            0
                        )
                    )

                    val r = production.first()
                    if(r is NonTerminalItem && !visited.contains(r)) {
                        q.add(r)
                        visited.add(r)
                    }
                }
            }
        }

        return ret
    }

    override fun action(s: Int, terminalItem: TerminalItem): Action {
        val j = goto[s to terminalItem]
        if(j != null) {
            return Action.Shift(j)
        }

        // j is null
        val c = lr0CollectionMap[s]!!

        val reduceItems = c.items.filter { it.dotIndex == it.production.size &&
                firstFollowCalculator.getFollowSet(it.nonTerminal).contains(terminalItem)
        }
        if(reduceItems.size > 1) {
            throw IllegalArgumentException("grammar is not SLR(1)")
        }

        val reduceItem = reduceItems.firstOrNull()

        if(reduceItem != null) {
            if(reduceItem.nonTerminal == NonTerminalItem(root.name + "`")){
                return Action.Accept
            }
            return Action.Reduce(reduceItem.nonTerminal, reduceItem.production)
        }

        return Action.Error
    }

    override fun exportToDot(): String {
        val sb = StringBuilder()
        sb.append("digraph LR0_Automaton {\n")
        sb.append("  rankdir=LR;\n") // 왼쪽에서 오른쪽 방향 그래프

        // 상태 노드 정의
        for ((index, collection) in lr0CollectionMap) {
            val label = collection.items.joinToString("\\n") { item ->
                val beforeDot = item.production.take(item.dotIndex).joinToString(" ") { formatGrammarItem(it) }
                val afterDot = item.production.drop(item.dotIndex).joinToString(" ") { formatGrammarItem(it) }
                "<${item.nonTerminal.name}> -> $beforeDot • $afterDot"
            }
            sb.append("  $index [label=\"$index:\\n$label\", shape=box];\n")
        }

        // GOTO 간선 정의
        for ((key, value) in goto) {
            val (fromState, symbol) = key
            sb.append("  $fromState -> $value [label=\"${formatGrammarItem(symbol)}\"];\n")
        }

        sb.append("}")

        return sb.toString()
    }

    // 🔥 논터미널은 <>, 터미널은 그대로 출력하는 함수
    private fun formatGrammarItem(item: GrammarItem): String {
        return when (item) {
            is NonTerminalItem -> "<${item.name}>" // 논터미널이면 <>로 감싸기
            is TokenTerminalItem -> item.token.name // 토큰 터미널이면 그대로
            is ConstTerminalItem -> item.name // 상수 터미널이면 그대로
            else -> item.toString() // 예외적인 경우
        }
    }
}