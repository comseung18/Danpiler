package parser

const val danLangBNF = "<Program> ::= <ClassDeclarations> <FunctionDeclarations>\n" +

        "<ClassDeclarations> ::= <ClassDeclaration> <ClassDeclarations> | ε\n" +

        "<ClassDeclaration> ::= ClassToken IdentifierToken LBraceToken <ClassMembers> RBraceToken\n" +

        "<ClassMembers> ::= <ClassMember> <ClassMembers> | ε\n" +

        "<ClassMember> ::= <AccessModifier> <Type> IdentifierToken SemicolonToken | <AccessModifier> <FunctionDeclaration>\n" +

        "<AccessModifier> ::= PublicToken | PrivateToken\n" +

        "<FunctionDeclarations> ::= <FunctionDeclaration> <FunctionDeclarations> | ε\n" +

        "<FunctionDeclaration> ::= <Type> IdentifierToken LParenToken <ParameterListOpt> RParenToken LBraceToken <Statements> RBraceToken\n" +

        "<ParameterListOpt> ::= <ParameterList> | ε\n" +

        "<ParameterList> ::= <Parameter> | <Parameter> CommaToken <ParameterList>\n" +

        "<Parameter> ::= <Type> IdentifierToken\n" +

        "<Type> ::= TypeToken | TypeToken LSquareToken RSquareToken\n" +

        "<Statements> ::= <Statement> <Statements> | ε\n" +

        "<Statement> ::= <VariableDeclaration> | <Assignment> | <FunctionCall> SemicolonToken | <IfStatement> " +
        "| <LoopStatement> | <ReturnStatement> | <MemoryManagementStatement> | <Block>\n" +

        "<Block> ::= LBraceToken <Statements> RBraceToken\n" +

        "<VariableDeclaration> ::= <Type> IdentifierToken SemicolonToken | <Type> IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken\n" +

        "<Assignment> ::= IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken\n" +

        "<FunctionCall> ::= IdentifierToken LParenToken <ArgumentListOpt> RParenToken\n" +

        "<ArgumentListOpt> ::= <ArgumentList> | ε\n" +

        "<ArgumentList> ::= <Expression> | <Expression> CommaToken <ArgumentList>\n" +

        "<IfStatement> ::= IfToken LParenToken <Expression> RParenToken <Block> <ElseIfOpt> <ElseOpt>\n" +

        "<ElseIfOpt> ::= ElseToken IfToken LParenToken <Expression> RParenToken <Block> <ElseIfOpt> | ε\n" +

        "<ElseOpt> ::= ElseToken <Block> | ε\n" +

        "<LoopStatement> ::= ForToken LParenToken <VariableDeclaration> <Expression> SemicolonToken <Assignment> RParenToken <Block> | WhileToken LParenToken <Expression> RParenToken <Block>\n" +

        "<ReturnStatement> ::= ReturnToken <ExpressionOpt> SemicolonToken\n" +

        "<ExpressionOpt> ::= <Expression> | ε\n" +

        "<MemoryManagementStatement> ::= NewToken IdentifierToken SemicolonToken | DeleteToken IdentifierToken SemicolonToken\n" +

        "<Expression> ::= <Term> <ExpressionTail>\n" +

        "<ExpressionTail> ::= ArithmeticOperatorToken <Term> <ExpressionTail> | ε\n" +

        "<Term> ::= <Factor> <TermTail>\n" +

        "<TermTail> ::= ArithmeticOperatorToken <Factor> <TermTail> | ε\n" +

        "<Factor> ::= IdentifierToken | IntNumberToken | FloatNumberToken | LParenToken <Expression> RParenToken | <FunctionCall>\n" +

        "<ComparisonExpression> ::= <Expression> ComparisonOperatorToken <Expression>\n"

private val danLangBNFWithSDT: List<BNFWithSDT> = listOf(
    BNFWithSDT("Program", true,
        listOf(
            "<ClassDeclarations> <FunctionDeclarations>" to { }
        )
    ),
    BNFWithSDT("ClassDeclarations", true,
        listOf(
            "<ClassDeclaration> <ClassDeclarations>" to { }
        )
    ),
    BNFWithSDT("ClassDeclaration", false,
        listOf(
            "ClassToken IdentifierToken LBraceToken <ClassMembers> RBraceToken" to { }
        )
    ),
    BNFWithSDT("ClassMembers", true,
        listOf(
            "<ClassMember> <ClassMembers>" to { }
        )
    ),
    BNFWithSDT("ClassMember", false,
        listOf(
            "<AccessModifier> <Type> IdentifierToken SemicolonToken" to { },
            "<AccessModifier> <FunctionDeclaration>" to { }
        )
    ),
    BNFWithSDT("AccessModifier", false,
        listOf(
            "PublicToken" to { },
            "PrivateToken" to { }
        )
    ),
    BNFWithSDT("FunctionDeclarations", true,
        listOf(
            "<FunctionDeclaration> <FunctionDeclarations>" to { }
        )
    ),
    BNFWithSDT("FunctionDeclaration", false,
        listOf(
            "<Type> IdentifierToken LParenToken <ParameterListOpt> RParenToken LBraceToken <Statements> RBraceToken" to { }
        )
    ),
    BNFWithSDT("ParameterListOpt", true,
        listOf(
            "<ParameterList>" to { }
        )
    ),
    BNFWithSDT("ParameterList", false,
        listOf(
            "<Parameter>" to { },
            "<Parameter> CommaToken <ParameterList>" to { }
        )
    ),
    BNFWithSDT("Parameter", false,
        listOf(
            "<Type> IdentifierToken" to { }
        )
    ),
    BNFWithSDT("Type", false,
        listOf(
            "TypeToken" to { },
            "TypeToken LSquareToken RSquareToken" to { }
        )
    ),
    BNFWithSDT("Statements", true,
        listOf(
            "<Statement> <Statements>" to { }
        )
    ),
    BNFWithSDT("Statement", false,
        listOf(
            "<VariableDeclaration>" to { },
            "<Assignment>" to { },
            "<FunctionCall> SemicolonToken" to { },
            "<IfStatement>" to { },
            "<LoopStatement>" to { },
            "<ReturnStatement>" to { },
            "<MemoryManagementStatement>" to { },
            "<Block>" to { }
        )
    ),
    BNFWithSDT("Block", false,
        listOf(
            "LBraceToken <Statements> RBraceToken" to { }
        )
    ),
    BNFWithSDT("VariableDeclaration", false,
        listOf(
            "<Type> IdentifierToken SemicolonToken" to { },
            "<Type> IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken" to { }
        )
    ),
    BNFWithSDT("Assignment", false,
        listOf(
            "IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken" to { }
        )
    ),
    BNFWithSDT("FunctionCall", false,
        listOf(
            "IdentifierToken LParenToken <ArgumentListOpt> RParenToken" to { }
        )
    ),
    BNFWithSDT("ArgumentListOpt", true,
        listOf(
            "<ArgumentList>" to { }
        )
    ),
    BNFWithSDT("ArgumentList", false,
        listOf(
            "<Expression>" to { }, "<Expression> CommaToken <ArgumentList>" to { }
        )
    ),
    BNFWithSDT("IfStatement", false,
        listOf(
            "IfToken LParenToken <Expression> RParenToken <Block> <ElseIfOpt> <ElseOpt>" to { }
        )
    ),
    BNFWithSDT("ElseIfOpt", true,
        listOf(
            "ElseToken IfToken LParenToken <Expression> RParenToken <Block> <ElseIfOpt>" to { }
        )
    ),
    BNFWithSDT("ElseOpt", true,
        listOf(
            "ElseToken <Block>" to { }
        )
    ),
    BNFWithSDT("LoopStatement", false,
        listOf(
            "ForToken LParenToken <VariableDeclaration> <Expression> SemicolonToken <Assignment> RParenToken <Block>" to { },
            "WhileToken LParenToken <Expression> RParenToken <Block>" to { }
        )
    ),
    BNFWithSDT("ReturnStatement", false,
        listOf(
            "ReturnToken <ExpressionOpt> SemicolonToken" to { }
        )
    ),
    BNFWithSDT("ExpressionOpt", true,
        listOf(
            "<Expression>" to { }
        )
    ),
    BNFWithSDT("MemoryManagementStatement", false,
        listOf(
            "NewToken IdentifierToken SemicolonToken" to { }, "DeleteToken IdentifierToken SemicolonToken" to { }
        )
    ),
    BNFWithSDT("Expression", false,
        listOf(
            "<Term> <ExpressionTail>" to { }
        )
    ),
    BNFWithSDT("ExpressionTail", true,
        listOf(
            "ArithmeticOperatorToken <Term> <ExpressionTail>" to { }
        )
    ),
    BNFWithSDT("Term", false,
        listOf(
            "<Factor> <TermTail>" to { }
        )
    ),
    BNFWithSDT("TermTail", true,
        listOf(
            "ArithmeticOperatorToken <Factor> <TermTail>" to { }
        )
    ),
    BNFWithSDT("Factor", false,
        listOf(
            "IdentifierToken" to { },
            "IntNumberToken" to { },
            "FloatNumberToken" to { },
            "LParenToken <Expression> RParenToken" to { },
            "<FunctionCall>" to { }
        )
    ),
    BNFWithSDT("ComparisonExpression", false,
        listOf(
            "<Expression> ComparisonOperatorToken <Expression>" to { }
        )
    )
)



val danLangGrammar by lazy {
    danLangBNFWithSDT.toGrammar()
}