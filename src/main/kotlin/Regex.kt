import java.util.Stack

enum class Operator(val op: Char, val priority: Int) {

    Union('|', 1),
    Kleene('*', 3),
    Optional('?', 3),
    Plus('+', 3),
    Concat( '볡', 2);

    fun isClosure() : Boolean {
        return this == Kleene || this == Optional || this == Plus
    }

    companion object {
        fun fromOpCode(c: String) : Operator? {
            return Operator.values().firstOrNull { it.op.toString() == c }
        }
    }

}

fun insertExplicitConcatOp(regex: String): String {

    val forbiddenChars = listOf(Operator.Concat.op.toString())

    val result = StringBuilder()

    fun isLiteral(c: String): Boolean {
        val operators = Operator.values().filterNot { it == Operator.Concat }.map { it.op.toString() }
            .plus(setOf("(", ")"))
        return !operators.contains(c)
    }

    fun isClosureOperator(c: String): Boolean {
        return Operator.fromOpCode(c)?.isClosure() ?: false
    }

    val emptyParenthesesRemoved = removeEmptyParentheses(regex)

    var i = 0
    while(i < emptyParenthesesRemoved.length) {

        val c1: String

        if(emptyParenthesesRemoved[i] == '\\' && i + 1 < emptyParenthesesRemoved.length) {
            c1 = emptyParenthesesRemoved.substring(i..i+1)
            i += 2
        } else {
            c1 = emptyParenthesesRemoved[i].toString()
            ++i
        }

        if(c1 in forbiddenChars) {
            throw IllegalArgumentException("forbiddenChars in regex : $c1 $regex")
        }

        result.append(c1)

        if(i < emptyParenthesesRemoved.length) {
            val c2 : String = if(emptyParenthesesRemoved[i] == '\\' && i + 1 < emptyParenthesesRemoved.length) {
                emptyParenthesesRemoved.substring(i..i+1)
            } else {
                emptyParenthesesRemoved[i].toString()
            }

            if (
                (isLiteral(c1) || c1 == ")" || isClosureOperator(c1)) &&
                (isLiteral(c2) || c2 == "(")
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

    var i = 0
    while(i < regex.length) {
        when (val c = regex[i++]) {
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

            else -> {
                if(c == '\\') {
                    output.append(c).append(regex[i++])
                } else {
                    output.append(c)
                }
            }
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

private fun removeEmptyParentheses(s: String): String {
    val stack = mutableListOf<Char>()

    for (char in s) {
        when (char) {
            ')' -> {
                if (stack.isNotEmpty() && stack.last() == '(') {
                    stack.removeLast()
                } else {
                    stack.add(char)
                }
            }
            else -> stack.add(char)
        }
    }

    return stack.reversed().joinToString(separator = "").reversed()
}