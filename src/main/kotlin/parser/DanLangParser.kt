package parser

import java.util.*



// AST 생성 및 감축 시 semanticAction 재정의를 위해 파서는 언어별로 만드는게 맞다.
class DanLangParser : LALRParser(danLangGrammar, NonTerminalItem("Program")) {

    override fun parse(input: List<TerminalItem>) : Boolean {
        val stack: Stack<Pair<Int, GrammarItem>> = Stack<Pair<Int, GrammarItem>>()
        stack.push(stackStartInt to emptyTerminalItem)

        var index = 0
        while(index < input.size) {
            val a = input[index]
            when(val act = action(stack.peek().first, a)) {
                Action.Accept -> return true
                Action.Error -> return false
                is Action.Reduce -> {

                    val popTerminals = mutableListOf<GrammarItem>()
                    repeat(act.production.size) {
                        popTerminals.add(stack.pop().second)
                    }
                    popTerminals.reverse()
                    stack.push(goto[Pair(stack.peek().first, act.n)]!! to act.n)
                    println("reduce(${stack.peek().first}) : ${act.n.name} ::= $popTerminals")
                }
                is Action.Shift -> {
                    stack.push(act.j to a)
                    ++index

                }

                is Action.EmptyShift -> {
                    stack.push(act.j to emptyTerminalItem)
                }
            }
        }

        return true
    }
}