class DFA(
    startNode: Node,
    endNodes: MutableSet<Node>
) : NFA(startNode, endNodes) {

    companion object {
        fun toDirectDFA(regex: String) : DFA {
            val additionalRegex = "($regex)#"

            val postFix = toPostfix(insertExplicitConcatOp(additionalRegex))
            val syntaxTree = SyntaxTree.fromRegex(postFix)
            val followPosCalculator =  SyntaxTree.Companion.FollowPosCalculator(syntaxTree).apply {
                calculate()
            }
            val newStartNode = Node()
            val newEndNodes = mutableSetOf<Node>()

            fun isEnd(state: Set<Int>): Boolean {
                // 첨가정규식 (r)# 에서 SyntaxTree 의 lastPos.first() 는 항상 # 의 position 이다.
                return syntaxTree.lastPos.first() in state
            }

            return DFA(
                newStartNode,
                endNodes = newEndNodes
            ).apply {
                val stateNodeNum: MutableMap<Set<Int>, Int> = mutableMapOf()
                stateNodeNum[syntaxTree.firstPos] = newStartNode.i

                val queue = ArrayDeque<Set<Int>>()
                queue.add(syntaxTree.firstPos)
                while(queue.isNotEmpty()) {
                    val nowState = queue.removeFirst()
                    val stateNum = stateNodeNum[nowState] ?: throw IllegalArgumentException("dfa no state in queue!")
                    val stateNode = this@apply.nodes[stateNum] ?: throw IllegalArgumentException("no state node in dfa")
                    if(isEnd(nowState)) newEndNodes.add(stateNode)


                    val transitions = mutableMapOf<Symbol.CharSymbol, MutableSet<Int>>()
                    for(p in nowState) {
                        transitions.computeIfAbsent(followPosCalculator.posToSymbol[p]!!){ mutableSetOf() }.addAll(
                            followPosCalculator.followPos[p].orEmpty()
                        )
                    }

                    for((symbol, targetSet) in transitions) {
                        val targetNodeNum = stateNodeNum[targetSet] ?: run {
                            val newDFANode = Node()
                            stateNodeNum[targetSet] = newDFANode.i
                            queue.add(targetSet)
                            this@apply.addNode(newDFANode)
                            newDFANode.i
                        }

                        this@apply.addEdge(Edge(
                            symbol,
                            stateNum,
                            targetNodeNum
                        ))
                    }

                }
            }
        }
    }

}