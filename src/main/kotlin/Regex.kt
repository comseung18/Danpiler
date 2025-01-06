import java.util.Stack

enum class Operator(val op: Char, val priority: Int) {

    Union('|', 1),
    Kleene('*', 3),
    Optional('?', 3),
    Plus('+', 3),
    Concat('.', 2);

    fun isClosure() : Boolean {
        return this == Kleene || this == Optional || this == Plus
    }

    companion object {
        fun fromOpCode(c: Char) : Operator? {
            return Operator.values().firstOrNull { it.op == c }
        }
    }

}

fun insertExplicitConcatOp(regex: String): String {

    val forbiddenChars = listOf(Operator.Concat.op)

    val result = StringBuilder()

    fun isLiteral(c: Char): Boolean {
        val operators = Operator.values().filterNot { it == Operator.Concat }.map { it.op }.plus(setOf('(', ')'))
        return !operators.contains(c)
    }

    fun isClosureOperator(c: Char): Boolean {
        return Operator.fromOpCode(c)?.isClosure() ?: false
    }

    for (i in regex.indices) {
        val c1 = regex[i]
        if(c1 in forbiddenChars) {
            throw IllegalArgumentException("forbiddenChars in regex : $c1 in $i")
        }

        result.append(c1)

        if (i + 1 < regex.length) {
            val c2 = regex[i + 1]

            if (
                (isLiteral(c1) || c1 == ')' || isClosureOperator(c1)) &&
                (isLiteral(c2) || c2 == '(')
            ) {
                result.append(Operator.Concat.op)
            }
        }
    }

    return result.toString()
}


fun toPostfix(regex: String): String {
    val output = StringBuilder()
    val stack = Stack<Char>()

    val operators = Operator.values().associate { it.op to it.priority }

    for (c in regex) {
        when (c) {

            '(' -> stack.push(c)

            ')' -> {
                while (stack.isNotEmpty() && stack.peek() != '(') {
                    output.append(stack.pop())
                }
                if (stack.isNotEmpty() && stack.peek() == '(') {
                    stack.pop()
                } else {
                    throw IllegalArgumentException("괄호가 맞지 않습니다.")
                }
            }

            in operators.keys -> {
                while (stack.isNotEmpty() &&
                    stack.peek() != '(' &&
                        operators.getOrDefault(stack.peek(), 0) >=
                        operators.getOrDefault(c, 0)
                ) {
                    output.append(stack.pop())
                }
                stack.push(c)
            }

            else -> output.append(c)
        }
    }

    while (stack.isNotEmpty()) {
        val op = stack.pop()
        if (op == '(' || op == ')') {
            throw IllegalArgumentException("괄호가 맞지 않습니다.")
        }
        output.append(op)
    }

    return output.toString()
}