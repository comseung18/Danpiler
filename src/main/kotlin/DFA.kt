class DFA(
    startNode: Node,
    endNodes: MutableSet<Node>
) : NFA(startNode, endNodes) {

    companion object {
        fun stateMinimizedDFA(dfa: DFA) : DFA {
            // collect input symbols
            val symbols = dfa.edges.flatMap { it.value }.mapNotNull { (it.v as? Symbol.CharSymbol) }.distinct()
            // collect node to node each symbol
            val nodeToNodeMapEachSymbol: MutableMap<Pair<Int, Symbol.CharSymbol>, Int> = mutableMapOf()
            dfa.edges.forEach { (i, edges) ->
                for(edge in edges) {
                    if(edge.v !is Symbol.CharSymbol) continue
                    nodeToNodeMapEachSymbol[i to edge.v] = edge.to
                }
            }

            // initial partition = S, E
            val s0 = (dfa.nodes.values.toSet() - dfa.endNodes).map { it.i }.toSet()
            val e = dfa.endNodes.map{ it.i }.toSet()
            var nextPartitionId = 0
            val nodeToPartitionNum: MutableMap<Int, Int> = mutableMapOf()

            fun addNewPartition(s: Iterable<Int>) {
                for(n in s) {
                    nodeToPartitionNum[n] = nextPartitionId
                }
                ++nextPartitionId
            }

            val partitions: MutableSet<Set<Int>> = mutableSetOf<Set<Int>>().apply {
                if(s0.isNotEmpty()) {
                    addNewPartition(s0)
                    add(s0)
                }
                if(e.isNotEmpty()) {
                    addNewPartition(e)
                    add(e)
                }
            }

            var hasNewPartition = true
            val checkedPartitionMap: MutableSet<Int> = mutableSetOf()
            while(hasNewPartition) {
                hasNewPartition = false
                val newPartition : MutableSet<Set<Int>> = mutableSetOf()
                for(partition in partitions) {
                    val partitionNum = nodeToPartitionNum[partition.first()]

                    if(partition.size == 1 || checkedPartitionMap.contains(partitionNum)) {
                        newPartition.add(partition)
                        continue
                    }

                    checkedPartitionMap.add(partitionNum!!)
                    var foundPartitionHere = false
                    for(c in symbols) {
                        val targetPartitionNum: MutableMap<Int, Int> = mutableMapOf()

                        for(n in partition) {
                            targetPartitionNum[n] = nodeToPartitionNum[nodeToNodeMapEachSymbol[n to c]] ?: -1
                        }
                        val partitioned = targetPartitionNum
                            .keys.groupBy { k -> targetPartitionNum[k]!! }
                        if(partitioned.size != 1) {
                            hasNewPartition = true
                            foundPartitionHere = true
                            partitioned.values.forEach {
                                val newPartSet = it.toSet()
                                addNewPartition(newPartSet)
                                newPartition.add(newPartSet)
                            }
                            break
                        }
                    }

                    if(!foundPartitionHere) {
                        newPartition.add(partition)
                    }

                }
                partitions.clear()
                partitions.addAll(newPartition)

            }
            // make state-minimized DFA
            val partitionNumToNewDFANode: MutableMap<Int, Node> = mutableMapOf()
            partitions.forEach {
                partitionNumToNewDFANode.computeIfAbsent(nodeToPartitionNum[it.first()]!!) { Node() }
            }

            return DFA(partitionNumToNewDFANode[nodeToPartitionNum[dfa.startNode.i]]!!,
                dfa.endNodes.mapNotNull{ partitionNumToNewDFANode[nodeToPartitionNum[it.i]] }.toMutableSet()
            ).apply {

                partitionNumToNewDFANode.values.forEach {
                    this@apply.addNode(it)
                }

                dfa.edges.forEach { (from, edges) ->
                    edges.forEach {edge ->
                        val p1 = partitionNumToNewDFANode[nodeToPartitionNum[from]]!!.i
                        val p2 = partitionNumToNewDFANode[nodeToPartitionNum[edge.to]]!!.i
                        this@apply.addEdge(Edge(
                            edge.v,
                            p1,
                            p2
                        ))
                    }
                }
            }
        }

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