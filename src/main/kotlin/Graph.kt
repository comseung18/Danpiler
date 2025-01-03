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
    private val nodes: MutableMap<Int, Node> = mutableMapOf()
    protected val edges: MutableMap<Int, MutableSet<Edge>> = mutableMapOf()
    private val reverseEdges: MutableMap<Int, MutableSet<Edge>> = mutableMapOf()

    fun getNodesForTest(): Map<Int, Node> = nodes
    fun getEdgesForTest(): Map<Int, List<Edge>> = edges.mapValues { it.value.toList() }

    fun addNode(node: Node) {
        nodes[node.i] = node
    }

    // src 노드를 제거함
    // src 노드로 향하는 모든 Edge 를 dst 노드로 변경함.
    // src 노드에서 나가는 모든 Edge 를 dst 가 출발하도록 변경함.
    fun replaceNode(src: Node, dst: Node) {
        nodes.remove(src.i)
        addNode(dst)

        val edgesFromSrc = edges[src.i].orEmpty()
        val edgesToSrc = reverseEdges[src.i].orEmpty()

        edges.remove(src.i)
        reverseEdges.remove(src.i)

        edgesFromSrc.forEach {

            val nextTo = if(it.to == src.i) {
                dst.i
            } else {
                it.to
            }
            removeEdge(it)
            addEdge(Edge(
                it.v,
                dst.i,
                nextTo
            ))
        }

        edgesToSrc.forEach {
            if(it.from == src.i) return@forEach
            removeEdge(it)
            addEdge(Edge(
                it.v,
                it.from,
                dst.i
            ))
        }
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