import lexer.Token
import java.io.File
import java.io.PrintWriter
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

    private fun escapeString(input: String): String {
        val escapeMap = mapOf(
            '\t' to "\\t",
            '\n' to "\\n",
            '\r' to "\\r",
            '\b' to "\\b",
            '\u000C' to "\\f", // 폼 피드
            '\\' to "\\\\",    // 백슬래시
            '"' to "\\\"",     // 큰따옴표
            '\'' to "\\\'"     // 작은따옴표
        )

        val builder = StringBuilder()
        for (char in input) {
            builder.append(escapeMap[char] ?: char)
        }
        return builder.toString()
    }

    // DOT 형식으로 그래프 출력
    private fun toDot(): String {
        val dot = StringBuilder("digraph ${javaClass.name} {\n")

        // 그래프 방향과 크기 설정
        dot.append("  rankdir=LR;\n")
        dot.append("  size=\"15,10\";\n  dpi=300;\n")

        // 시작점을 나타내는 비활성 노드 정의
        dot.append("  start [shape=point];\n")

        // 모든 노드에 토큰 정보 추가
        nodes.values.forEach { node ->
            val token = node.matchingTokens.joinToString { it.tokenName }
            val shape = if (endNodes.contains(node)) "doublecircle" else "circle"
            if(token.isNotEmpty()) {
                dot.append("  ${node.i} [shape=$shape, label=\"${node.i}\\n$token\"];\n")
            } else if(endNodes.contains(node)) {
                dot.append("  ${node.i} [shape=$shape];\n")
            }
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
                dot.append("  ${edge.from} -> ${edge.to} [ label = \"${escapeString(label)}\" ];\n")
            }
        }

        dot.append("}")
        return dot.toString()
    }


    fun printToFile(
        outputFileName: String = "test1",
        extension: String = "pdf"
    ) {
        val dotContent = this.toDot() // DOT 형식 문자열 생성

        // 출력 파일 경로 설정
        val outputDirectory = File("/Users/kakao/IdeaProjects/compiler/src/test/kotlin")
        val outputPdfFile = File(outputDirectory, "$outputFileName.$extension")

        // 임시 DOT 파일 생성
        val dotFile = File(outputDirectory, "$outputFileName.dot")

        kotlin.runCatching {
            // DOT 내용을 임시 파일에 작성
            PrintWriter(dotFile).use { writer ->
                writer.write(dotContent)
            }

            // Graphviz dot 명령어를 실행하여 PDF 생성
            val process =
                ProcessBuilder("dot", "-T${extension}", dotFile.absolutePath, "-o", outputPdfFile.absolutePath)
                    .redirectErrorStream(true) // 오류를 표준 출력으로 리디렉션
                    .start()

            process.waitFor() // 명령어 실행 완료 대기
        }
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

                this@apply.nodes[currentDfaNodeNum]?.matchingTokens = currentSet.mapNotNull {
                    this@NFA.nodes[it]?.matchingTokens
                }.flatten().toSet()


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

fun toNFA(regex: String, token: Token? = null) : NFA {
    if(regex.isEmpty()) {
        val st = Node()
        val ed = Node()
        return NFA(st, ed).apply {
            this@apply.addEdge(Edge(
                Symbol.EmptySymbol,
                st.i,
                ed.i
            ))
        }
    }

    val explicitConcat = insertExplicitConcatOp(regex)
    val postFix = toPostfix(explicitConcat)

    val nfaStack = Stack<NFA>()

    var i = 0
    while(i < postFix.length) {
        when(val c = postFix[i++]) {
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
                val symbol: Symbol = if(c == '\\') {
                    Symbol.CharSymbol(postFix[i++])
                } else {
                    Symbol.CharSymbol(c)
                }

                val symbolNFA = NFA.fromSymbol(symbol)
                nfaStack.push(symbolNFA)
            }
        }

    }
    if (nfaStack.size != 1) {
        throw IllegalArgumentException("잘못된 정규 표현식입니다.")
    }

    return nfaStack.pop().apply {
        this@apply.endNodes.forEach {
            if(token != null) {
                it.matchingTokens = setOf(token)
            }
        }
    }
}

