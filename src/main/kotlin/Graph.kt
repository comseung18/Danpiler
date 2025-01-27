import lexer.Token

sealed interface Symbol {
    object EmptySymbol: Symbol
    data class CharSymbol(val c: Char): Symbol
}

data class Edge(
    val v: Symbol,
    val from: Int,
    val to: Int,
)


class Node {
    fun getResolvedTokenType(tokenString: String, lastMatchingTokens: Set<Token>?): Token {
        return lastMatchingTokens?.firstOrNull {
            it.nfa.match(tokenString)
        } ?: Token.InvalidToken
    }

    val i: Int = nextNodeId++
    var matchingTokens : Set<Token> = emptySet()

    companion object {
        private var nextNodeId = 0
    }
}

open class Graph {
    protected val nodes: MutableMap<Int, Node> = mutableMapOf()
    protected val edges: MutableMap<Int, MutableSet<Edge>> = mutableMapOf()
    private val reverseEdges: MutableMap<Int, MutableSet<Edge>> = mutableMapOf()

    fun getNodesForTest(): Map<Int, Node> = nodes
    fun getEdgesForTest(): Map<Int, List<Edge>> = edges.mapValues { it.value.toList() }

    fun getTransitions(here: Node, symbol: Symbol): List<Node> {
        return  this.edges[here.i]?.mapNotNull { edge ->
            if(edge.v == symbol) {
                nodes[edge.to]
            } else null
        }?.distinct() ?: emptyList()
    }

    fun addNode(node: Node) {
        nodes[node.i] = node
    }

    fun addEdge(edge: Edge) {
        edges.computeIfAbsent(edge.from) { mutableSetOf() }.add(edge)
        if(edge.to != edge.from) {
            reverseEdges.computeIfAbsent(edge.to) { mutableSetOf() }.add(edge)
        }
    }

    private fun removeEdge(edge: Edge) {
        edges[edge.from]?.remove(edge)
        reverseEdges[edge.to]?.remove(edge)
    }

    fun addOtherGraphNodesAndEdges(other: Graph) {
        nodes.putAll(other.nodes)
        edges.putAll(other.edges)
        reverseEdges.putAll(other.reverseEdges)
    }


}