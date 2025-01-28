package lexer

import toNFA

const val aToz = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)"
const val AToZ = "(A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)"
const val zeroToNine = "(0|1|2|3|4|5|6|7|8|9)"
const val whiteSpace = "( |\t|\n|\r|\u000C)"
const val identifierRegex = "($aToz|$AToZ|_)($aToz|$AToZ|_|$zeroToNine)*"

private val types = listOf(
    "int",
    "float",
    "string",
    "boolean",
    "void",
)


enum class Token(
    val regex: String,
    val isIgnoreWhenParsing: Boolean = false,
) {
    IntNumberToken( "$zeroToNine+"),
    FloatNumberToken("$zeroToNine+\\.$zeroToNine+"),

    // control-token
    ForToken("for"),
    WhileToken("while"),
    IfToken("if"),
    ElseToken("else"),
    ContinueToken("continue"),
    BreakToken("break"),

    // 그 외 특별한 역할을 하는 토큰
    ClassToken("class"),
    NewToken("new"),
    DeleteToken("delete"),

    TypeToken(types.joinToString(separator = "|")),
    OperatorToken("\\+|\\-|\\*|\\/|=|==|!=|<|>|<=|>=|\\+\\+|\\-\\-|\\+="),
    IdentifierToken(identifierRegex),
    LParenToken("\\("),
    RParenToken("\\)"),
    LBraceToken( "{"),
    RBraceToken( "}"),
    LSquareToken("["),
    RSquareToken("]"),
    SemicolonToken( ";"),

    WhiteSpaceToken(
        "$whiteSpace+",
        isIgnoreWhenParsing = true
    ),

    SingleLineCommentToken(
        "//.*\n",
        isIgnoreWhenParsing = true
    ),

    MultiLineCommentToken(
        "/\\*.*\\*/",
        isIgnoreWhenParsing = true
    ),

    InvalidToken(
        "$zeroToNine+(" +
                "(($aToz|$AToZ|_).*)|" + // 숫자 뒤에 알파벳/언더스코어가 오는 경우
                "(\\.$zeroToNine*($aToz|$AToZ|_).*)" + // FLOAT 뒤에 잘못된 문자가 오는 경우
                ")"
    )

    ;

    val tokenName: String = this::class.java.name

    val nfa by lazy {
        toNFA(this.regex, this)
    }

    val dfa by lazy {
        DFA.stateMinimizedDFA(nfa.toDFA())
    }
}

