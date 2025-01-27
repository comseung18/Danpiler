import java.util.*

sealed class SyntaxTree {
    abstract var nullable: Boolean
    val firstPos = mutableSetOf<Int>()
    val lastPos = mutableSetOf<Int>()

    abstract fun computeNullable()
    abstract fun computeFirstPos()
    abstract fun computeLastPos()

    open fun computeProperties() {
        this.computeNullable()
        this.computeFirstPos()
        this.computeLastPos()
    }

    companion object {
        fun fromRegex(regex: String): SyntaxTree = buildSyntaxTree(regex)

        class FollowPosCalculator(private val syntaxTree: SyntaxTree) {
            val followPos: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
            val posToSymbol: MutableMap<Int, Symbol.NonEmptySymbol> = mutableMapOf()

            fun calculate() {
                dfs(syntaxTree)
            }

            private fun dfs(n: SyntaxTree) {
                if(n is OperatorNode) {
                    n.left?.let{ dfs(it) }
                    n.right?.let{ dfs(it) }

                    when(n.operator) {
                        Operator.Concat -> {
                            val leftLastPos = n.left?.lastPos.orEmpty()
                            val rightFirstPos = n.right?.firstPos.orEmpty()
                            for(p in leftLastPos) {
                                followPos.computeIfAbsent(p) { mutableSetOf() }.addAll(rightFirstPos)
                            }
                        }

                        Operator.Kleene, Operator.Plus -> {
                            val nLastPos = n.left?.lastPos.orEmpty()
                            val nFirstPos = n.left?.firstPos.orEmpty()
                            for(p in nLastPos) {
                                followPos.computeIfAbsent(p){ mutableSetOf() }.addAll(nFirstPos)
                            }
                        }

                        else -> {}
                    }
                } else if(n is SymbolNode) {
                    posToSymbol[n.position] = n.symbol
                }
            }
        }
    }
}

data class SymbolNode(val symbol: Symbol.NonEmptySymbol) : SyntaxTree() {
    var position: Int = 0
    override var nullable: Boolean = false

    override fun computeNullable() {
        nullable = false
    }

    override fun computeFirstPos() {
        firstPos.add(position)
    }

    override fun computeLastPos() {
        lastPos.add(position)
    }
}

data class OperatorNode(
    val operator: Operator,
    val left: SyntaxTree?,
    val right: SyntaxTree?
) : SyntaxTree() {
    override var nullable: Boolean = false

    override fun computeProperties() {
        this.left?.computeProperties()
        this.right?.computeProperties()
        super.computeProperties()
    }

    override fun computeNullable() {
        nullable = when (operator) {
            Operator.Union -> left?.nullable == true || right?.nullable == true
            Operator.Concat  -> left?.nullable == true && right?.nullable == true
            Operator.Kleene  -> true
            Operator.Plus  -> left?.nullable == true
            Operator.Optional  -> true
        }
    }

    override fun computeFirstPos() {
        when (operator) {
            Operator.Union -> {
                left?.firstPos?.let { firstPos.addAll(it) }
                right?.firstPos?.let { firstPos.addAll(it) }
            }
            Operator.Concat -> {
                left?.firstPos?.let { firstPos.addAll(it) }
                if (left?.nullable == true) {
                    right?.firstPos?.let { firstPos.addAll(it) }
                }
            }
            Operator.Kleene, Operator.Plus, Operator.Optional -> {
                left?.firstPos?.let { firstPos.addAll(it) }
            }
        }
    }

    override fun computeLastPos() {
        when (operator) {
            Operator.Union -> {
                left?.lastPos?.let { lastPos.addAll(it) }
                right?.lastPos?.let { lastPos.addAll(it) }
            }
            Operator.Concat -> {
                right?.lastPos?.let { lastPos.addAll(it) }
                if (right?.nullable == true) {
                    left?.lastPos?.let { lastPos.addAll(it) }
                }
            }
            Operator.Kleene, Operator.Plus, Operator.Optional -> {
                left?.lastPos?.let { lastPos.addAll(it) }
            }
        }
    }
}

private fun buildSyntaxTree(postFixRegex: String): SyntaxTree {
    val stack = Stack<SyntaxTree>()
    var position = 1
    var i = 0
    while(i< postFixRegex.length) {
        when (val c = postFixRegex[i++]) {
            in Operator.values().map{ it.op } -> {
                when (val op = Operator.fromOpCode(c.toString())) {
                    Operator.Union, Operator.Concat -> {
                        val right = stack.pop()
                        val left = stack.pop()
                        val opNode = OperatorNode(op, left, right)
                        stack.push(opNode)
                    }
                    Operator.Kleene, Operator.Plus, Operator.Optional -> {
                        val left = stack.pop()
                        val opNode = OperatorNode(op, left, null)
                        stack.push(opNode)
                    }
                    null -> {}
                }
            }
            else -> {

                val symNode = when (c) {
                    '\\' -> {
                        SymbolNode(Symbol.CharSymbol(postFixRegex[i++])).apply { this.position = position++ }
                    }
                    AnySymbolChar -> {
                        SymbolNode(Symbol.AnySymbol).apply { this.position = position++ }
                    }
                    else -> {
                        SymbolNode(Symbol.CharSymbol(c)).apply { this.position = position++ }
                    }
                }

                stack.push(symNode)
            }
        }
    }


    if (stack.size != 1) {
        throw IllegalArgumentException("Invalid regular expression")
    }

    val syntaxTree = stack.pop()

    syntaxTree.computeProperties()

    return syntaxTree
}
