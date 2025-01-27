package lexer

import DFA
import DFA.Companion.stateMinimizedDFA
import NFA
import Node
import union

object Tokenizer {

    private fun makeTokenizingDFA(): DFA {
        // step 1. 각 토큰별 NFA 생성
        val nfaEachTokens = Token.values().map { token ->
            token.nfa
        }

        // step 2. NFA 병합
        var mergedNFA = NFA(Node(), mutableSetOf())
        nfaEachTokens.forEach {
            mergedNFA = union(mergedNFA, it)
        }
        return stateMinimizedDFA(mergedNFA.toDFA())
    }

    val dfa = makeTokenizingDFA()

    fun tokenize(
        input: String,
        dfa: DFA = this.dfa
    ): List<Pair<Token, String>> {
        val tokens = mutableListOf<Pair<Token, String>>() // (토큰 타입, 값)
        var currentIndex = 0
        val currentToken = StringBuilder()
        var currentState = dfa.startNode
        var lastMatchingTokens: Set<Token>? = null
        var lastAcceptingState: Node? = null
        var lastTokenStartIndex = 0

        while (currentIndex < input.length) {
            val char = input[currentIndex]
            val nextState = dfa.getTransitions(currentState, Symbol.CharSymbol(char)).firstOrNull()

            if (nextState != null) {
                currentToken.append(char)
                currentState = nextState
                ++currentIndex
                if (currentState.matchingTokens.isNotEmpty()) {
                    lastMatchingTokens = currentState.matchingTokens
                    lastAcceptingState = currentState
                    lastTokenStartIndex = currentIndex
                }
            } else {
                if (lastAcceptingState != null) {
                    val tokenType = lastAcceptingState.getResolvedTokenType(currentToken.toString(), lastMatchingTokens)
                    if (tokenType == Token.InvalidToken) {
                        throw IllegalArgumentException("Invalid token detected: '${currentToken}'")
                    }
                    if (tokenType.isIgnoreWhenParsing.not()) {
                        tokens.add(tokenType to currentToken.toString())
                    }
                    currentToken.clear()
                    currentIndex = lastTokenStartIndex
                    currentState = dfa.startNode
                    lastAcceptingState = null
                    lastMatchingTokens = null
                } else {
                    throw IllegalArgumentException("Invalid token starting at index $currentIndex")
                }
            }
        }

        // 마지막 남은 토큰 처리
        if (lastAcceptingState != null) {
            val tokenType = lastAcceptingState.getResolvedTokenType(currentToken.toString(), lastMatchingTokens)
            if (tokenType == Token.InvalidToken) {
                throw IllegalArgumentException("Invalid token detected: '${currentToken}'")
            }
            if (tokenType.isIgnoreWhenParsing.not()) {
                tokens.add(tokenType to currentToken.toString())
            }
        } else if (currentToken.isNotEmpty()) {
            throw IllegalArgumentException("Unrecognized token: $currentToken")
        }

        return tokens
    }

}