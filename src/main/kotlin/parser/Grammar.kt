package parser

import lexer.Token

const val danLangGrammar: String =
    "<Program> ::= <ProgramElement> | <ProgramElement> <Program>\n" +

            "<ProgramElement> ::= <FunctionDeclaration> | <ClassDeclaration> | <Statement>\n" +

            "<FunctionDeclaration> ::= TypeToken IdentifierToken LParenToken <ParameterList>? RParenToken LBraceToken <StatementList> RBraceToken\n" +

            "<ClassDeclaration> ::= ClassToken IdentifierToken LParenToken <ParameterList>? RParenToken LBraceToken <ClassMemberList> RBraceToken\n" +

            "<ParameterList> ::= <Parameter> | <Parameter> CommaToken <ParameterList>\n" +

            "<Parameter> ::= TypeToken IdentifierToken\n" +

            "<ClassMemberList> ::= <ClassMember> | <ClassMember> <ClassMemberList>\n" +

            "<ClassMember> ::= PublicToken TypeToken IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken " +
            "| PrivateToken TypeToken IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken" +
            "| PublicToken TypeToken IdentifierToken SemicolonToken " +
            "| PrivateToken TypeToken IdentifierToken SemicolonToken\n" +

            "<StatementList> ::= <Statement> | <Statement> <StatementList>\n" +

            "<Statement> ::= <VariableDeclaration> | <ExpressionStatement> | <IfStatement> | <WhileStatement> | <ForStatement> " +
            "| <ReturnStatement> | <BreakStatement> | <ContinueStatement>\n" +

            "<VariableDeclaration> ::= TypeToken IdentifierToken <AssignmentExpression>? SemicolonToken\n" +

            "<AssignmentExpression> ::= AssignmentOperatorToken <Expression>  | CompoundAssignmentOperator <Expression>  | IncrementDecrementToken\n" +

            "<ExpressionStatement> ::= <Expression> SemicolonToken\n" +

            "<IfStatement> ::= IfToken LParenToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken " +
            "| IfToken LParenToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken ElseToken LBraceToken <StatementList> RBraceToken " +
            "| IfToken LParenToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken <ElseIfList> ElseToken LBraceToken <StatementList> RBraceToken\n" +

            "<ElseIfList> ::= ElseToken IfToken LParenToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken " +
            "| ElseToken IfToken LParenToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken <ElseIfList>\n" +

            "<WhileStatement> ::= WhileToken LParenToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken\n" +

            "<ForStatement> ::= ForToken LParenToken <VariableDeclaration> <Expression> SemicolonToken <Expression> RParenToken LBraceToken <StatementList> RBraceToken\n" +

            "<ReturnStatement> ::= ReturnToken <Expression>? SemicolonToken\n" +

            "<BreakStatement> ::= BreakToken SemicolonToken\n" +

            "<ContinueStatement> ::= ContinueToken SemicolonToken\n" +

            "<Expression> ::= <LogicalExpression>\n" +

            "<LogicalExpression> ::= <EqualityExpression> | <LogicalExpression> ComparisonOperatorToken <EqualityExpression>\n" +

            "<EqualityExpression> ::= <ArithmeticExpression> | <EqualityExpression> ComparisonOperatorToken <ArithmeticExpression>\n" +

            "<ArithmeticExpression> ::= <Term> | <ArithmeticExpression> ArithmeticOperatorToken <Term>\n" +

            "<Term> ::= <Factor> | <Term> ArithmeticOperatorToken <Factor>\n" +

            "<Factor> ::= <PrimaryExpression> | IncrementDecrementToken <PrimaryExpression> | <PrimaryExpression> IncrementDecrementToken\n" +

            "<PrimaryExpression> ::= IntNumberToken | FloatNumberToken | IdentifierToken | LParenToken <Expression> RParenToken " +
            "| <FunctionCall>  | <MemberAccess> | <ArrayAccess> | <ClassInstanceCreation>\n" +

            "<FunctionCall> ::= IdentifierToken LParenToken <ArgumentList>? RParenToken\n" +

            "<ArgumentList> ::= <Expression> | <Expression> CommaToken <ArgumentList>\n" +

            "<DeleteStatement> ::= DeleteToken IdentifierToken SemicolonToken\n" +

            "<ClassInstanceCreation> ::= NewToken IdentifierToken LParenToken <ArgumentList>? RParenToken\n" +

            "<MemberAccess> ::= IdentifierToken DotToken IdentifierToken\n" +
            "<MemberAssignment> ::= <MemberAccess> AssignmentOperatorToken <Expression> SemicolonToken\n" +

            "<ArrayAccess> ::= IdentifierToken LSquareToken <Expression> RSquareToken\n"

data class GrammarRule(
    val nonTerminal: NonTerminalItem, // 비종료 기호
    val productions: List<List<GrammarItem>> // 생산 규칙: 여러 토큰 리스트
)

interface GrammarItem {
    val name: String
    val isOptional: Boolean
}

data class TerminalItem(
    override val name: String,
    val token: Token,
) : GrammarItem {
    override val isOptional: Boolean = false
}

data class NonTerminalItem(
    override val name: String,
    override val isOptional: Boolean = false
) : GrammarItem

data class Grammar(
    val startSymbol: NonTerminalItem, // 시작 기호
    val rules: List<GrammarRule> // 모든 문법 규칙
)


fun parseBNF(bnf: String): Grammar {
    val rules = mutableListOf<GrammarRule>()
    val nonTerminals = mutableMapOf<String, NonTerminalItem>()

    fun getOrCreateNonTerminal(name: String, isOpt: Boolean): NonTerminalItem {
        return nonTerminals.getOrPut(name) { NonTerminalItem(name) }
    }

    val lines = bnf.lines().filter { it.isNotBlank() }
    var startSymbol: NonTerminalItem? = null

    for (line in lines) {
        val parts = line.split("::=").map { it.trim() }
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid BNF format: $line")
        }

        val nonTerminalName = parts[0].removeSurrounding("<", ">")
        val nonTerminal = getOrCreateNonTerminal(nonTerminalName, false)

        if (startSymbol == null) startSymbol = nonTerminal

        val productionRules = parts[1].split("|").map { rule ->
            rule.trim().split(" ").filter { it.isNotBlank() }.map { itemOpt ->
                val isOptional = itemOpt.endsWith("?")
                val item = itemOpt.removeSuffix("?")

                when {
                    item.startsWith("<") && item.endsWith(">") ->
                        getOrCreateNonTerminal(
                            item.removeSurrounding("<", ">"),
                            isOptional
                        )

                    Token.values().any { it.name == item } ->
                        TerminalItem(item, Token.valueOf(item))

                    else -> throw IllegalArgumentException("Unknown token or non-terminal: $item")
                }
            }
        }

        rules.add(GrammarRule(nonTerminal, productionRules))
    }

    return Grammar(startSymbol ?: throw IllegalArgumentException("No start symbol found"), rules)
}

