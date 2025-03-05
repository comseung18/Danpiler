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

        "<MemoryManagementStatement> ::= NewToken IdentifierToken SemicolonToken | DeleteToken IdentifierToken SemicolonToken " +
        "| NewToken IdentifierToken LSquareToken IntNumberToken RSquareToken SemicolonToken\n" +

        "<Expression> ::= <Term> <ExpressionTail>\n" +

        "<ExpressionTail> ::= ArithmeticOperatorToken <Term> <ExpressionTail> | ε\n" +

        "<Term> ::= <Factor> <TermTail>\n" +

        "<TermTail> ::= PriorityArithmeticOperatorToken <Factor> <TermTail> | ε\n" +

        "<Factor> ::= IdentifierToken | IntNumberToken | FloatNumberToken | LParenToken <Expression> RParenToken | <FunctionCall>\n"




private val danLangBNFWithSDT: List<BNFWithSDT> = listOf(
    BNFWithSDT("Program", true,
        listOf(
            "<ClassDeclarations> <FunctionDeclarations>" to { stack ->
                val y = stack.pop()
                val x = stack.pop()
                Program(x.second as? DanLangASTNode, y.second as? DanLangASTNode)
            }
        )
    ),
    BNFWithSDT("ClassDeclarations", true,
        listOf(
            "<ClassDeclaration> <ClassDeclarations>" to { stack ->
                val y = stack.pop()
                val x = stack.pop()
                ClassDeclarations(x.second as? DanLangASTNode, y.second as? DanLangASTNode)
            }
        )
    ),
    BNFWithSDT("ClassDeclaration", false,
        listOf(
            "ClassToken IdentifierToken LBraceToken <ClassMembers> RBraceToken" to { stack ->
                stack.pop()
                val members = stack.pop()
                stack.pop()
                val id = stack.pop()
                stack.pop()
                ClassDeclaration(
                    (id.second as? TokenTerminalItem)?.value,
                    members.second as? DanLangASTNode
                )

            }
        )
    ),
    BNFWithSDT("ClassMembers", true,
        listOf(
            "<ClassMember> <ClassMembers>" to { stack ->
                val y = stack.pop()
                val x = stack.pop()
                ClassMembers(
                    x.second as? DanLangASTNode,
                    y.second as? DanLangASTNode
                )
            }
        )
    ),
    BNFWithSDT("ClassMember", false,
        listOf(
            "<AccessModifier> <Type> IdentifierToken SemicolonToken" to { stack ->
                stack.pop()
                val id = stack.pop()
                val type = stack.pop()
                val access = stack.pop()
                ClassMember(
                    id = (id.second as? TokenTerminalItem)?.value,
                    type = type.second as? DanLangASTNode,
                    access = access.second as? DanLangASTNode
                )
            },
            "<AccessModifier> <FunctionDeclaration>" to { stack ->
                val y = stack.pop()
                val x = stack.pop()
                ClassMember(
                    access = x.second as? DanLangASTNode,
                    functionDeclaration = y.second as? DanLangASTNode
                )
            }
        )
    ),
    BNFWithSDT("AccessModifier", false,
        listOf(
            "PublicToken" to { stack ->
                val access = stack.pop()
                AccessModifier((access.second as? TokenTerminalItem)?.value)
            },
            "PrivateToken" to { stack ->
                val access = stack.pop()
                AccessModifier((access.second as? TokenTerminalItem)?.value)
            }
        )
    ),
    BNFWithSDT("FunctionDeclarations", true,
        listOf(
            "<FunctionDeclaration> <FunctionDeclarations>" to { stack ->
                val y = stack.pop().second as? DanLangASTNode
                val x = stack.pop().second as? DanLangASTNode
                FunctionDeclarations(x, y)
            }
        )
    ),
    BNFWithSDT("FunctionDeclaration", false,
        listOf(
            "<Type> IdentifierToken LParenToken <ParameterListOpt> RParenToken LBraceToken <Statements> RBraceToken" to { stack ->
                stack.pop()
                val stats = stack.pop().second as? DanLangASTNode
                stack.pop()
                stack.pop()
                val params = stack.pop().second as? DanLangASTNode
                stack.pop()
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                val type = stack.pop().second as? DanLangASTNode
                FunctionDeclaration(
                    id = id,
                    params = params,
                    stats = stats,
                    type = type
                )
            }
        )
    ),
    BNFWithSDT("ParameterListOpt", true,
        listOf(
            "<ParameterList>" to { stack ->
                ParameterListOpt(stack.pop().second as? DanLangASTNode)
            }
        )
    ),
    BNFWithSDT("ParameterList", false,
        listOf(
            "<Parameter>" to { stack ->
                ParameterList(
                    param = stack.pop().second as? DanLangASTNode
                )
            },
            "<Parameter> CommaToken <ParameterList>" to { stack ->
                ParameterList(
                    params = stack.pop().second as? DanLangASTNode,
                    param = stack.pop().second as? DanLangASTNode
                )
            }
        )
    ),
    BNFWithSDT("Parameter", false,
        listOf(
            "<Type> IdentifierToken" to { stack ->
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                val type = stack.pop().second as? DanLangASTNode
                Parameter(type = type, id = id)
            }
        )
    ),
    BNFWithSDT("Type", false,
        listOf(
            "TypeToken" to { stack ->
                Type((stack.pop().second as TokenTerminalItem).value, false)
            },
            "TypeToken LSquareToken RSquareToken" to { stack ->
                stack.pop()
                stack.pop()
                Type((stack.pop().second as TokenTerminalItem).value, true)
            }
        )
    ),
    BNFWithSDT("Statements", true,
        listOf(
            "<Statement> <Statements>" to { stack ->
                val y = stack.pop().second as? DanLangASTNode
                val x = stack.pop().second as? DanLangASTNode
                Statements(stat = x, stats = y)
            }
        )
    ),
    BNFWithSDT("Statement", false,
        listOf(
            "<VariableDeclaration>" to { stack ->
                Statement(
                    variableDeclaration = stack.pop().second as? DanLangASTNode
                )
            },
            "<Assignment>" to { stack ->
                Statement(
                    assignment = stack.pop().second as? DanLangASTNode
                )
            },
            "<FunctionCall> SemicolonToken" to { stack ->
                stack.pop()
                Statement(
                    functionCall = stack.pop().second as? DanLangASTNode
                )
            },
            "<IfStatement>" to { stack ->
                Statement(
                    ifStat = stack.pop().second as? DanLangASTNode
                )
            },
            "<LoopStatement>" to { stack ->
                Statement(
                    loopStat = stack.pop().second as? DanLangASTNode
                )
            },
            "<ReturnStatement>" to { stack ->
                Statement(
                    returnStat = stack.pop().second as? DanLangASTNode
                )
            },
            "<MemoryManagementStatement>" to { stack ->
                Statement(
                    memoryStat = stack.pop().second as? DanLangASTNode
                )
            },
            "<Block>" to { stack ->
                Statement(
                    block = stack.pop().second as? DanLangASTNode
                )
            }
        )
    ),
    BNFWithSDT("Block", false,
        listOf(
            "LBraceToken <Statements> RBraceToken" to { stack ->
                stack.pop()
                val stats = stack.pop().second as? DanLangASTNode
                stack.pop()

                Block(
                    stats = stats
                )
            }
        )
    ),
    BNFWithSDT("VariableDeclaration", false,
        listOf(
            "<Type> IdentifierToken SemicolonToken" to { stack ->
                stack.pop()
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                val type = stack.pop().second as? DanLangASTNode
                VariableDeclaration(type = type, id = id)
            },
            "<Type> IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken" to { stack ->
                stack.pop()
                val exp = stack.pop().second as? DanLangASTNode
                val assign = (stack.pop().second as? TokenTerminalItem)?.value
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                val type = stack.pop().second as? DanLangASTNode
                VariableDeclaration(type = type, id = id, assign = assign, expression = exp)
            }
        )
    ),
    BNFWithSDT("Assignment", false,
        listOf(
            "IdentifierToken AssignmentOperatorToken <Expression> SemicolonToken" to { stack ->
                stack.pop()
                val exp = stack.pop().second as? DanLangASTNode
                val assign = (stack.pop().second as? TokenTerminalItem)?.value
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                Assignment(
                    id = id,
                    assign = assign,
                    expression = exp
                )
            }
        )
    ),
    BNFWithSDT("FunctionCall", false,
        listOf(
            "IdentifierToken LParenToken <ArgumentListOpt> RParenToken" to { stack ->
                stack.pop()
                val arguments = stack.pop().second as? DanLangASTNode
                stack.pop()
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                FunctionCall(
                    id = id,
                    argumentListOpt = arguments
                )
            }
        )
    ),
    BNFWithSDT("ArgumentListOpt", true,
        listOf(
            "<ArgumentList>" to { stack ->
                ArgumentListOpt(stack.pop().second as? DanLangASTNode)
            }
        )
    ),
    BNFWithSDT("ArgumentList", false,
        listOf(
            "<Expression>" to { stack ->
                ArgumentList(
                    stack.pop().second as? DanLangASTNode
                )
            },
            "<Expression> CommaToken <ArgumentList>" to { stack ->
                val arguments = stack.pop().second as? DanLangASTNode
                stack.pop()
                val exp = stack.pop().second as? DanLangASTNode
                ArgumentList(
                    expression = exp,
                    argumentList = arguments
                )
            }
        )
    ),
    BNFWithSDT("IfStatement", false,
        listOf(
            "IfToken LParenToken <Expression> RParenToken <Block> <ElseIfOpt> <ElseOpt>" to { stack ->
                val elseOpt = stack.pop().second as? DanLangASTNode
                val elseIfOpt = stack.pop().second as? DanLangASTNode
                val ifBlock = stack.pop().second as? DanLangASTNode
                stack.pop()
                val condition = stack.pop().second as? DanLangASTNode
                stack.pop()
                stack.pop()
                IfStatement(
                    condition = condition,
                    ifBlock = ifBlock,
                    elseIfOpt = elseIfOpt,
                    elseOpt = elseOpt
                )
            }
        )
    ),
    BNFWithSDT("ElseIfOpt", true,
        listOf(
            "ElseToken IfToken LParenToken <Expression> RParenToken <Block> <ElseIfOpt>" to { stack ->
                val elseIfOpt = stack.pop().second as? DanLangASTNode
                val block = stack.pop().second as? DanLangASTNode
                stack.pop()
                val condition = stack.pop().second as? DanLangASTNode
                stack.pop()
                stack.pop()
                stack.pop()
                ElseIfOpt(
                    condition = condition,
                    block = block,
                    elseIfOpt = elseIfOpt
                )
            }
        )
    ),
    BNFWithSDT("ElseOpt", true,
        listOf(
            "ElseToken <Block>" to { stack ->
                val block = stack.pop().second as? DanLangASTNode
                stack.pop()
                ElseOpt(
                    block = block
                )
            }
        )
    ),
    BNFWithSDT("LoopStatement", false,
        listOf(
            "ForToken LParenToken <VariableDeclaration> <Expression> SemicolonToken <Assignment> RParenToken <Block>" to { stack ->
                val block = stack.pop().second as? DanLangASTNode
                stack.pop()
                val assignment = stack.pop().second as? DanLangASTNode
                stack.pop()
                val condition = stack.pop().second as? DanLangASTNode
                val initial = stack.pop().second as? DanLangASTNode
                stack.pop()
                stack.pop()
                ForLoopStatement(
                    initial = initial,
                    condition = condition,
                    assignment = assignment,
                    block = block
                )
            },
            "WhileToken LParenToken <Expression> RParenToken <Block>" to { stack ->
                val block = stack.pop().second as? DanLangASTNode
                stack.pop()
                val condition = stack.pop().second as? DanLangASTNode
                stack.pop()
                stack.pop()
                WhileLoopStatement(
                    condition = condition,
                    block = block
                )
            }
        )
    ),
    BNFWithSDT("ReturnStatement", false,
        listOf(
            "ReturnToken <ExpressionOpt> SemicolonToken" to { stack ->
                stack.pop()
                val expression = stack.pop().second as? DanLangASTNode
                stack.pop()
                ReturnStatement(
                    expression = expression
                )
            }
        )
    ),
    BNFWithSDT("ExpressionOpt", true,
        listOf(
            "<Expression>" to { stack ->
                ExpressionOpt(
                    stack.pop().second as? DanLangASTNode
                )
            }
        )
    ),
    BNFWithSDT("MemoryManagementStatement", false,
        listOf(
            "NewToken IdentifierToken SemicolonToken" to { stack ->
                stack.pop()
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                stack.pop()
                MemoryNewStatement(id = id)
            },
            "DeleteToken IdentifierToken SemicolonToken" to { stack ->
                stack.pop()
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                stack.pop()
                MemoryDeleteStatement(id = id)
            },
            "NewToken IdentifierToken LSquareToken IntNumberToken RSquareToken SemicolonToken" to { stack ->
                stack.pop()
                stack.pop()
                val count = (stack.pop().second as? TokenTerminalItem)?.value?.toInt()
                stack.pop()
                val id = (stack.pop().second as? TokenTerminalItem)?.value
                stack.pop()
                MemoryNewStatement(id = id, count = count)
            }
        )
    ),
    BNFWithSDT("Expression", false,
        listOf(
            "<Term> <ExpressionTail>" to { stack ->
                val tail = stack.pop().second as? DanLangASTNode
                val term = stack.pop().second as? DanLangASTNode
                Expression(term = term, tail = tail)
            }
        )
    ),
    BNFWithSDT("ExpressionTail", true,
        listOf(
            "ArithmeticOperatorToken <Term> <ExpressionTail>" to { stack ->
                val tail = stack.pop().second as? DanLangASTNode
                val term = stack.pop().second as? DanLangASTNode
                val op = (stack.pop().second as? TokenTerminalItem)?.value
                ExpressionTail(op, term, tail)
            }
        )
    ),
    BNFWithSDT("Term", false,
        listOf(
            "<Factor> <TermTail>" to { stack ->
                val tail = stack.pop().second as? DanLangASTNode
                val factor = stack.pop().second as? DanLangASTNode
                Term(factor, tail)
            }
        )
    ),
    BNFWithSDT("TermTail", true,
        listOf(
            "PriorityArithmeticOperatorToken <Factor> <TermTail>" to { stack ->
                val tail = stack.pop().second as? DanLangASTNode
                val factor = stack.pop().second as? DanLangASTNode
                val op = (stack.pop().second as? TokenTerminalItem)?.value
                TermTail(op, factor, tail)
            }
        )
    ),
    BNFWithSDT("Factor", false,
        listOf(
            "IdentifierToken" to { stack ->
                val x = stack.pop()
                Factor(id = (x.second as? TokenTerminalItem)?.value)
            },
            "IntNumberToken" to { stack ->
                val x = stack.pop()
                Factor(intNumber = (x.second as? TokenTerminalItem)?.value?.toInt())
            },
            "FloatNumberToken" to { stack ->
                val x = stack.pop()
                Factor(floatNumber = (x.second as? TokenTerminalItem)?.value?.toFloat())
            },
            "LParenToken <Expression> RParenToken" to { stack ->
                stack.pop()
                val e = stack.pop().second
                stack.pop()
                Factor(expression = e as? DanLangASTNode)
            },
            "<FunctionCall>" to { stack ->
                Factor(functionCall = stack.pop().second as? DanLangASTNode)
            }
        )
    ),
)



val danLangGrammar by lazy {
    danLangBNFWithSDT.toGrammar()
}