import java.util.*

open class NFA(
    val startNode: Node,
    val endNodes: MutableSet<Node>
): Graph() {

    constructor(s: Node, e: Node) : this(s, mutableSetOf(e))

    init {
        addNode(startNode)
        endNodes.forEach {
            addNode(it)
        }
    }

    private fun epsilonClosure(states: Set<Int>) : Set<Int> {

        val st = Stack<Int>().apply {
            addAll(states)
        }

        val closure = mutableSetOf<Int>()

        while(st.isNotEmpty()) {
            val here = st.pop()
            if(closure.add(here)) {
                for(e in this.edges[here].orEmpty()) {
                    if(e.v is Symbol.EmptySymbol) {
                        st.add(e.to)
                    }
                }
            }
        }

        return closure
    }

    fun match(pattern: String) : Boolean {
        var currentStates = epsilonClosure(setOf(startNode.i))

        for(c in pattern) {
            val nextStates = mutableSetOf<Int>()
            for(state in currentStates) {
                for (e in this.edges[state].orEmpty()) {
                    if (e.v is Symbol.CharSymbol && e.v.c == c) {
                        nextStates.add(e.to)
                    }
                }
            }

            currentStates = epsilonClosure(nextStates)
        }

        return endNodes.any { it.i in currentStates }
    }

    // DOT 형식으로 그래프 출력
    fun toDot(): String {
        val dot = StringBuilder("digraph NFA {\n")

        // 그래프 방향과 크기 설정 (선택 사항)
        dot.append("  rankdir=LR;\n")
        dot.append("  size=\"8,5\";\n\n")

        // 시작점을 나타내는 비활성 노드 정의
        dot.append("  start [shape=point];\n")

        // 종료 노드를 이중 원으로 정의
        endNodes.forEach {
            dot.append("  ${it.i} [shape=doublecircle];\n")
        }

        // 시작점에서 실제 시작 노드로 ε 전이 추가
        startNode.let { start ->
            dot.append("  start -> ${start.i} [ label = \"ε\" ];\n")
        }

        // 모든 엣지 추가
        for ((_, edgeList) in edges) {
            for (edge in edgeList) {
                val label = when(edge.v) {
                    is Symbol.EmptySymbol -> "ε"
                    is Symbol.CharSymbol -> edge.v.c.toString()
                }
                dot.append("  ${edge.from} -> ${edge.to} [ label = \"$label\" ];\n")
            }
        }

        dot.append("}")
        return dot.toString()
    }

    fun toDFA() : DFA {
        val newStartNode = Node()
        val newEndNodes = mutableSetOf<Node>()

        val stateSetToDfaNodeNum = mutableMapOf<Set<Int>, Int>()
        val queue = ArrayDeque<Set<Int>>()

        val startClosure = this@NFA.epsilonClosure(setOf(this@NFA.startNode.i))
        stateSetToDfaNodeNum[startClosure] = newStartNode.i

        queue.add(startClosure)

        return DFA(
            newStartNode,
            newEndNodes
        ).apply {


            while(queue.isNotEmpty()) {
                val currentSet = queue.removeFirst()
                val currentDfaNodeNum = stateSetToDfaNodeNum[currentSet] ?: throw IllegalArgumentException("toDFA no dfa state")

                if(this@NFA.endNodes.any {it.i in currentSet }) {
                    val n = this@apply.nodes[currentDfaNodeNum] ?: throw IllegalArgumentException("toDFA no dfa state node for $currentDfaNodeNum")
                    newEndNodes.add(n)
                }

                val edgesFromCurrentSet = mutableListOf<Edge>().apply {
                    currentSet.forEach { i ->
                        addAll(this@NFA.edges[i]?.filter { edge -> edge.v !is Symbol.EmptySymbol } ?: emptyList())
                    }
                }

                val moveSetEachSymbol = edgesFromCurrentSet.groupBy { edge -> edge.v }.map { (k, v) -> k to (v.map { it.to }).toSet() }

                for((symbol, moveSet) in moveSetEachSymbol) {
                    val u = this@NFA.epsilonClosure(moveSet)

                    if(u.isEmpty()) continue

                    val dfaNodeExist = stateSetToDfaNodeNum[u]

                    val dfaNodeNum: Int
                    if(dfaNodeExist == null) {

                        queue.add(u)

                        val dfaNum = run {
                            // 새로운 DFA 노드 생성
                            val newNode = Node()
                            this@apply.addNode(newNode)
                            newNode.i
                        }

                        stateSetToDfaNodeNum[u] = dfaNum
                        dfaNodeNum = dfaNum
                    } else {
                        dfaNodeNum = dfaNodeExist
                    }

                    this@apply.addEdge(Edge(
                        symbol,
                        currentDfaNodeNum,
                        dfaNodeNum
                    ))
                }
            }


        }
    }

    companion object {
        fun fromSymbol(s : Symbol): NFA {
            val startNode = Node()
            val endNode = Node()

            return NFA(
                startNode,
                endNode
            ).apply {
                addEdge(Edge(s, startNode.i , endNode.i))
            }
        }
    }
}

// operator '|'
fun union(a:NFA, b: NFA) : NFA {
    val startNode = Node()
    val endNode = Node()

    return NFA(
        startNode,
        endNode
    ).apply {
        addOtherGraphNodesAndEdges(a)
        addOtherGraphNodesAndEdges(b)

        addEdge(Edge(
            Symbol.EmptySymbol,
            startNode.i,
            a.startNode.i
        ))

        addEdge(Edge(
            Symbol.EmptySymbol,
            startNode.i,
            b.startNode.i
        ))

        a.endNodes.forEach {
            addEdge(Edge(
                Symbol.EmptySymbol,
                it.i,
                endNode.i
            ))
        }

        b.endNodes.forEach {
            addEdge(Edge(
                Symbol.EmptySymbol,
                it.i,
                endNode.i
            ))
        }
    }
}

// operator '.'
fun concat(a: NFA, b: NFA): NFA {
    val startNode = a.startNode
    val endNode = b.endNodes

    return NFA(
        startNode,
        endNode
    ).apply {
        addOtherGraphNodesAndEdges(a)
        addOtherGraphNodesAndEdges(b)
        a.endNodes.forEach {
            this@apply.addEdge(Edge(
                Symbol.EmptySymbol,
                it.i,
                b.startNode.i
            ))
        }
    }
}

// a+ == a(a*)
fun plus(a: NFA): NFA {
    val kleeneA = kleene(a)
    return concat(a, kleeneA)
}

// a? == a|ε
fun optional(a: NFA): NFA {
    val epsilonNFA = NFA.fromSymbol(Symbol.EmptySymbol)
    return union(a, epsilonNFA)
}

// operator '*'
fun kleene(a: NFA): NFA {
    val startNode = Node()
    val endNode = Node()

    return NFA(
        startNode,
        endNode
    ).apply {
        addOtherGraphNodesAndEdges(a)

        addEdge(Edge(
            Symbol.EmptySymbol,
            startNode.i,
            a.startNode.i
        ))

        a.endNodes.forEach {
            addEdge(Edge(
                Symbol.EmptySymbol,
                it.i,
                endNode.i
            ))

            addEdge(Edge(
                Symbol.EmptySymbol,
                it.i,
                a.startNode.i
            ))
        }

        addEdge(Edge(
            Symbol.EmptySymbol,
            startNode.i,
            endNode.i
        ))
    }
}

fun toNFA(regex: String) : NFA {
    val explicitConcat = insertExplicitConcatOp(regex)
    val postFix = toPostfix(explicitConcat)

    val nfaStack = Stack<NFA>()
    for(c in postFix) {
        when(c) {
            Operator.Kleene.op -> {
                if(nfaStack.isEmpty()) {
                    throw IllegalArgumentException("'${Operator.Kleene.op}' 연산자 앞에 NFA가 없습니다.")
                }

                val subNfa = nfaStack.pop()
                nfaStack.push(kleene(subNfa))
            }

            Operator.Plus.op -> {
                if(nfaStack.isEmpty()) {
                    throw IllegalArgumentException("'${Operator.Plus.op}' 연산자 앞에 NFA가 없습니다.")
                }

                val subNfa = nfaStack.pop()
                nfaStack.push(plus(subNfa))
            }

            Operator.Optional.op -> {
                if(nfaStack.isEmpty()) {
                    throw IllegalArgumentException("'${Operator.Optional.op}' 연산자 앞에 NFA가 없습니다.")
                }

                val subNfa = nfaStack.pop()
                nfaStack.push(optional(subNfa))
            }

            Operator.Concat.op -> {
                if (nfaStack.size < 2) {
                    throw IllegalArgumentException("'${Operator.Concat.op}' 연산자 앞에 두 개의 NFA가 필요합니다.")
                }
                val a = nfaStack.pop()
                val b = nfaStack.pop()
                val concatenated = concat(b, a)
                nfaStack.push(concatenated)
            }

            Operator.Union.op -> {
                if (nfaStack.size < 2) {
                    throw IllegalArgumentException("'${Operator.Union.op}' 연산자 앞에 두 개의 NFA가 필요합니다.")
                }
                val a = nfaStack.pop()
                val b = nfaStack.pop()
                val unionNFA = union(a, b)
                nfaStack.push(unionNFA)
            }

            'ε' -> {
                val symbol = Symbol.EmptySymbol
                val symbolNFA = NFA.fromSymbol(symbol)
                nfaStack.push(symbolNFA)
            }

            else -> {
                val symbol = Symbol.CharSymbol(c)
                val symbolNFA = NFA.fromSymbol(symbol)
                nfaStack.push(symbolNFA)
            }
        }
    }

    if (nfaStack.size != 1) {
        throw IllegalArgumentException("잘못된 정규 표현식입니다.")
    }

    return nfaStack.pop()
}

