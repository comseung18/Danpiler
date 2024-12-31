import java.util.Stack

fun insertExplicitConcatOp(regex: String): String {
    val result = StringBuilder()

    fun isLiteral(c: Char): Boolean {
        val operators = setOf('|', '*', '(', ')', '?', '+')
        return !operators.contains(c)
    }

    fun isClosureOperator(c: Char): Boolean {
        return c == '*' || c == '?' || c == '+'
    }

    for (i in regex.indices) {
        val c1 = regex[i]
        if(c1 == '.') {
            throw IllegalArgumentException("regex 에 '.' 이 포함되어 있습니다.")
        }

        result.append(c1)

        if (i + 1 < regex.length) {
            val c2 = regex[i + 1]

            if (
                (isLiteral(c1) || c1 == ')' || isClosureOperator(c1)) &&
                (isLiteral(c2) || c2 == '(')
            ) {
                result.append('.')
            }
        }
    }

    return result.toString()
}


fun toPostfix(regex: String): String {
    val output = StringBuilder()
    val stack = Stack<Char>()
    val operators = mapOf(
        '|' to 1,
        '.' to 2,
        '*' to 3,
        '?' to 3,
        '+' to 3
    )

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

            in operators -> {
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