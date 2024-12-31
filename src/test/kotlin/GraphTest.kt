import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReplaceNodeTest {

    private lateinit var graph: Graph
    private lateinit var nfa: NFA

    @BeforeEach
    fun setUp() {
        // NFA 초기화
        val start = Node()
        val end = Node()
        nfa = NFA(start, end)
        graph = nfa
    }

    @Test
    fun `replace node with single incoming and outgoing edge`() {
        // 노드 생성
        val src = Node()
        val dst = Node()
        val intermediary = Node()

        // 그래프에 노드 추가
        graph.addNode(intermediary)
        graph.addNode(src)
        graph.addNode(dst)

        // 엣지 추가: intermediary -> src -> end
        graph.addEdge(Edge(Symbol.CharSymbol('a'), intermediary.i, src.i))
        graph.addEdge(Edge(Symbol.CharSymbol('b'), src.i, nfa.endNode.i))

        // replaceNode 호출: src를 dst로 대체
        graph.replaceNode(src, dst)

        // 검증
        val nodes = graph.getNodesForTest()
        val edges = graph.getEdgesForTest()

        assertFalse(nodes.containsKey(src.i), "Source node should be removed from nodes.")
        assertTrue(nodes.containsKey(dst.i), "Destination node should be added to nodes.")

        // intermediary에서 dst로의 엣지가 있는지 확인
        val intermediaryEdges = edges[intermediary.i]
        assertNotNull(intermediaryEdges, "Intermediary should have outgoing edges.")
        assertTrue(intermediaryEdges!!.any {
            it.to == dst.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'a'
        }, "Intermediary should have an edge to destination node with symbol 'a'.")

        // dst에서 end로의 엣지가 있는지 확인
        val dstEdges = edges[dst.i]
        assertNotNull(dstEdges, "Destination node should have outgoing edges.")
        assertTrue(dstEdges!!.any {
            it.to == nfa.endNode.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'b'
        }, "Destination node should have an edge to end node with symbol 'b'.")

        // src에서 end로의 엣지가 더 이상 존재하지 않는지 확인
        val srcEdges = edges[src.i]
        assertTrue(srcEdges.isNullOrEmpty(), "Source node should have no outgoing edges.")
    }

    @Test
    fun `replace node with multiple incoming and outgoing edges`() {
        // 노드 생성
        val src = Node()
        val dst = Node()
        val node1 = Node()
        val node2 = Node()
        val node3 = Node()
        val node4 = Node()

        // 그래프에 노드 추가
        graph.addNode(node1)
        graph.addNode(node2)
        graph.addNode(src)
        graph.addNode(node3)
        graph.addNode(node4)
        graph.addNode(dst)

        // 엣지 추가
        // node1 -> src
        // node2 -> src
        // src -> node3
        // src -> node4
        graph.addEdge(Edge(Symbol.CharSymbol('a'), node1.i, src.i))
        graph.addEdge(Edge(Symbol.CharSymbol('b'), node2.i, src.i))
        graph.addEdge(Edge(Symbol.CharSymbol('c'), src.i, node3.i))
        graph.addEdge(Edge(Symbol.CharSymbol('d'), src.i, node4.i))

        // replaceNode 호출: src를 dst로 대체
        graph.replaceNode(src, dst)

        // 검증
        val nodes = graph.getNodesForTest()
        val edges = graph.getEdgesForTest()

        assertFalse(nodes.containsKey(src.i), "Source node should be removed from nodes.")
        assertTrue(nodes.containsKey(dst.i), "Destination node should be added to nodes.")

        // node1에서 dst로의 엣지가 있는지 확인
        val node1Edges = edges[node1.i]
        assertNotNull(node1Edges, "Node1 should have outgoing edges.")
        assertTrue(node1Edges!!.any {
            it.to == dst.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'a'
        }, "Node1 should have an edge to destination node with symbol 'a'.")

        // node2에서 dst로의 엣지가 있는지 확인
        val node2Edges = edges[node2.i]
        assertNotNull(node2Edges, "Node2 should have outgoing edges.")
        assertTrue(node2Edges!!.any {
            it.to == dst.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'b'
        }, "Node2 should have an edge to destination node with symbol 'b'.")

        // dst에서 node3으로의 엣지가 있는지 확인
        val dstEdges = edges[dst.i]
        assertNotNull(dstEdges, "Destination node should have outgoing edges.")
        assertTrue(dstEdges!!.any {
            it.to == node3.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'c'
        }, "Destination node should have an edge to node3 with symbol 'c'.")

        // dst에서 node4으로의 엣지가 있는지 확인
        assertTrue(dstEdges.any {
            it.to == node4.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'd'
        }, "Destination node should have an edge to node4 with symbol 'd'.")

        // src에서 node3과 node4로의 엣지가 더 이상 존재하지 않는지 확인
        val srcEdges = edges[src.i]
        assertTrue(srcEdges.isNullOrEmpty(), "Source node should have no outgoing edges.")
    }

    @Test
    fun `replace node with self-loop edge`() {
        // 노드 생성
        val src = Node()
        val dst = Node()

        // 그래프에 노드 추가
        graph.addNode(src)
        graph.addNode(dst)
        graph.addNode(nfa.endNode) // endNode는 이미 추가되어 있을 수 있습니다.

        // 엣지 추가: src -> src (self-loop), src -> end
        graph.addEdge(Edge(Symbol.CharSymbol('a'), src.i, src.i))
        graph.addEdge(Edge(Symbol.CharSymbol('b'), src.i, nfa.endNode.i))

        // replaceNode 호출: src를 dst로 대체
        graph.replaceNode(src, dst)

        // 검증
        val nodes = graph.getNodesForTest()
        val edges = graph.getEdgesForTest()

        assertFalse(nodes.containsKey(src.i), "Source node should be removed from nodes.")
        assertTrue(nodes.containsKey(dst.i), "Destination node should be added to nodes.")

        // dst에서 dst로의 self-loop 엣지가 있는지 확인
        val dstEdges = edges[dst.i]
        assertNotNull(dstEdges, "Destination node should have outgoing edges.")
        assertTrue(dstEdges!!.any {
            it.to == dst.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'a'
        }, "Destination node should have a self-loop with symbol 'a'.")

        // dst에서 end로의 엣지가 있는지 확인
        assertTrue(dstEdges.any {
            it.to == nfa.endNode.i && it.v is Symbol.CharSymbol && (it.v as Symbol.CharSymbol).c == 'b'
        }, "Destination node should have an edge to end node with symbol 'b'.")

        // src에서 나가는 엣지가 더 이상 존재하지 않는지 확인
        val srcEdges = edges[src.i]
        assertTrue(srcEdges.isNullOrEmpty(), "Source node should have no outgoing edges.")
    }

    @Test
    fun `replace node with no incoming and outgoing edges`() {
        // 노드 생성
        val src = Node()
        val dst = Node()

        // 그래프에 노드 추가
        graph.addNode(src)
        graph.addNode(dst)

        // src 노드에 엣지가 없다고 가정

        // replaceNode 호출: src를 dst로 대체
        graph.replaceNode(src, dst)

        // 검증
        val nodes = graph.getNodesForTest()
        val edges = graph.getEdgesForTest()

        assertFalse(nodes.containsKey(src.i), "Source node should be removed from nodes.")
        assertTrue(nodes.containsKey(dst.i), "Destination node should be added to nodes.")

        // dst 노드에 엣지가 없음을 확인
        val dstEdges = edges[dst.i]
        assertTrue(dstEdges.isNullOrEmpty(), "Destination node should have no outgoing edges.")
    }
}
