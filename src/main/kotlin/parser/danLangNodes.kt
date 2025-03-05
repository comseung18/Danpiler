package parser

interface NodeHasId : ASTNode {
    val id: String?
}

interface DanLangASTNode : ASTNode {
    val children: List<ASTNode>
    val dotDesc: String
        get() = ""
}

fun DanLangASTNode.toDot(): String {
    fun buildDot(node: DanLangASTNode, sb: StringBuilder, parentId: String) {
        val nodeId = System.identityHashCode(node).toString() // 유일한 ID 생성
        var nodeLabel = node.javaClass.simpleName // 노드의 클래스 이름을 라벨로 사용

        // 만약 id를 가진 노드라면, id 값도 포함
        if (node is NodeHasId && node.id != null) {
            nodeLabel += "\\n(id: ${node.id})"
        }
        if(node.dotDesc.isNotEmpty()) {
            nodeLabel += "\\n${node.dotDesc}"
        }

        sb.append("  \"$nodeId\" [label=\"$nodeLabel\"];\n")
        sb.append("  \"$parentId\" -> \"$nodeId\";\n")

        for (child in node.children) {
            if (child is DanLangASTNode) {
                buildDot(child, sb, nodeId)
            }
        }
    }

    val sb = StringBuilder()
    sb.append("digraph AST {\n")
    sb.append("  node [shape=ellipse, fontname=\"Courier\"];\n") // 노드 스타일 지정
    buildDot(this, sb, "root")
    sb.append("}")
    return sb.toString()
}

data class Program(
    val classDeclarations: DanLangASTNode? = null,
    val functionDeclarations: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        classDeclarations, functionDeclarations
    )
}

data class ClassDeclarations(
    val classDeclaration: DanLangASTNode? = null,
    val classDeclarations: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        classDeclaration, classDeclarations
    )
}

data class ClassDeclaration(
    override val id: String? = null,
    val classMembers: DanLangASTNode? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(classMembers)
}

data class ClassMembers(
    val classMember: DanLangASTNode? = null,
    val classMembers: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(classMember, classMembers)
}

data class ClassMember(
    override val id: String? = null,
    val access: DanLangASTNode? = null,
    val type: DanLangASTNode? = null,
    val functionDeclaration: DanLangASTNode? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(
        access, type, functionDeclaration
    )
}

data class AccessModifier(
    val access: String? = null
) : DanLangASTNode {
    override val children: List<ASTNode> = emptyList()
}

data class FunctionDeclarations(
    val functionDeclaration: DanLangASTNode? = null,
    val functionDeclarations: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        functionDeclaration, functionDeclarations
    )
}

data class FunctionDeclaration(
    override val id: String? = null,
    val params: DanLangASTNode? = null,
    val stats: DanLangASTNode? = null,
    val type: DanLangASTNode? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(
        type, params, stats
    )
}

data class ParameterListOpt(
    val params: DanLangASTNode? = null
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(params)
}

data class ParameterList(
    val param: DanLangASTNode? = null,
    val params: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        param, params
    )
}

data class Parameter(
    val type: DanLangASTNode? = null,
    override val id: String? = null
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(type)
}

data class Type(
    val type: String,
    val isArray: Boolean
) : DanLangASTNode {
    override val children: List<ASTNode> = emptyList()
    override val dotDesc: String
        get() = type + if(isArray) "[]" else ""
}

data class Statements(
    val stat: DanLangASTNode? = null,
    val stats: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        stat, stats
    )
}

data class Statement(
    val variableDeclaration: DanLangASTNode? = null,
    val assignment: DanLangASTNode? = null,
    val functionCall: DanLangASTNode? = null,
    val ifStat: DanLangASTNode? = null,
    val loopStat: DanLangASTNode? = null,
    val returnStat: DanLangASTNode? = null,
    val memoryStat: DanLangASTNode? = null,
    val block: DanLangASTNode? = null
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        variableDeclaration, assignment, functionCall,
        ifStat, loopStat, returnStat, memoryStat, block
    )
}

data class Block(
    val stats: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        stats
    )
}

data class VariableDeclaration(
    val type: DanLangASTNode? = null,
    override val id: String? = null,
    val assign: String? = null,
    val expression: DanLangASTNode? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(
        type, expression
    )
}

data class Assignment(
    override val id: String? = null,
    val assign: String? = null,
    val expression: DanLangASTNode? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(
        expression
    )
}

data class FunctionCall(
    override val id: String? = null,
    val argumentListOpt: DanLangASTNode? = null
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(
        argumentListOpt
    )
}

data class ArgumentListOpt(
    val argumentList: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        argumentList
    )
}

data class ArgumentList(
    val expression: DanLangASTNode? = null,
    val argumentList: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        expression, argumentList
    )
}

data class IfStatement(
    val condition: DanLangASTNode? = null,
    val ifBlock: DanLangASTNode? = null,
    val elseIfOpt: DanLangASTNode? = null,
    val elseOpt: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        condition, ifBlock, elseIfOpt, elseOpt
    )
}

data class ElseIfOpt(
    val condition: DanLangASTNode? = null,
    val block : DanLangASTNode? = null,
    val elseIfOpt: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        condition, block, elseIfOpt
    )
}

data class ElseOpt(
    val block : DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(block)
}

data class ForLoopStatement(
    val initial: DanLangASTNode? = null,
    val condition: DanLangASTNode? = null,
    val assignment: DanLangASTNode? = null,
    val block: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        initial, condition, assignment, block
    )
}

data class WhileLoopStatement(
    val condition: DanLangASTNode? = null,
    val block: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        condition, block
    )
}

data class ReturnStatement(
    val expression: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        expression
    )
}

data class ExpressionOpt(
    val expression: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        expression
    )
}

data class MemoryNewStatement(
    override val id: String? = null,
    val count: Int? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = emptyList()
}


data class MemoryDeleteStatement(
    override val id: String? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = emptyList()
}

data class Expression(
    val term: DanLangASTNode? = null,
    val tail: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        term, tail
    )
}

data class ExpressionTail(
    val op: String? = null,
    val term: DanLangASTNode? = null,
    val tail: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(term, tail)
}

data class Term(
    val factor: DanLangASTNode? = null,
    val tail: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(
        factor, tail
    )
}

data class TermTail(
    val op: String? = null,
    val factor: DanLangASTNode? = null,
    val tail: DanLangASTNode? = null,
) : DanLangASTNode {
    override val children: List<ASTNode> = listOfNotNull(factor, tail)
    override val dotDesc: String
        get() = "$op"
}

data class Factor(
    override val id: String? = null,
    val intNumber: Int? = null,
    val floatNumber: Float? = null,
    val expression: DanLangASTNode? = null,
    val functionCall: DanLangASTNode? = null,
) : DanLangASTNode, NodeHasId {
    override val children: List<ASTNode> = listOfNotNull(
        expression, functionCall
    )

    override val dotDesc: String
        get() = intNumber?.toString() ?: floatNumber?.toString() ?: ""
}