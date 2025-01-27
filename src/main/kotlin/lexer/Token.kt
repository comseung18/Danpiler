package lexer

import toNFA

const val aToz = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)"
const val AToZ = "(A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)"
const val zeroToNine = "(0|1|2|3|4|5|6|7|8|9)"
const val whiteSpace = "( |\t|\n|\r|\u000C)"
private val _keywords = listOf("for", "while", "if", "else", "int", "float")


enum class Token(
    val tokenName: String,
    val regex: String,
    val isIgnoreWhenParsing: Boolean = false,
) {
    IntNumberToken("NUMBER", "$zeroToNine+"),
    FloatNumberToken("NUMBER", "$zeroToNine+\\.$zeroToNine+"),
    KeywordToken("KEYWORD", _keywords.joinToString(separator = "|")),
    OperatorToken("OPERATOR", "\\+|\\-|\\*|\\/|=|==|!=|<|>|<=|>=|\\+\\+|\\-\\-"),
    IdentifierToken("IDENTIFIER", "($aToz|$AToZ|_)($aToz|$AToZ|_|$zeroToNine)*"),
    LParenToken("LPAREN", "\\("),
    RParenToken("RPAREN", "\\)"),
    LBraceToken("LBRACE", "{"),
    RBraceToken("RBRACE", "}"),
    SemicolonToken("SEMICOLON", ";"),

    WhiteSpaceToken(
        "WHITESPACE",
        "$whiteSpace+",
        isIgnoreWhenParsing = true
    ),

    SingleLineCommentToken(
        "SINGLE_LINE_COMMENT",
        "//.*\n",
        isIgnoreWhenParsing = true
    ),

    MultiLineCommentToken(
        "MULTI_LINE_COMMENT",
        "/\\*.*\\*/",
        isIgnoreWhenParsing = true
    ),

    InvalidToken(
        "INVALID",
        "$zeroToNine+(" +
                "(($aToz|$AToZ|_).*)|" + // 숫자 뒤에 알파벳/언더스코어가 오는 경우
                "(\\.$zeroToNine*($aToz|$AToZ|_).*)" + // FLOAT 뒤에 잘못된 문자가 오는 경우
                ")"
    )

    ;

    val nfa by lazy {
        toNFA(this.regex, this)
    }

    val dfa by lazy {
        DFA.stateMinimizedDFA(nfa.toDFA())
    }
}

