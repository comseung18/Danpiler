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
