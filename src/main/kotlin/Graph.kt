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
    val i: Int = nextNodeId++

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