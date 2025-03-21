# Danpiler
This is the Danpiler project, where I implement everything from lexical analysis to intermediate code generation and optimization based on "Compilers: Principles, Techniques & Tools."

## Lexical Analyzer

### **1. NFA**
Given a **regular expression**, it is converted into an **NFA (Nondeterministic Finite Automaton)**, which can then be visualized graphically.

The input regular expression can be converted into an [NFA](src/main/kotlin/NFA.kt). The resulting NFA can be used for regular expression matching. Refer to [NFATest.kt](src/test/kotlin/tc/NFATest.kt) for details.

#### Example: Regular Expression `(a|b)*abb`
NFA construction process and result:
![NFA Example](src/test/kotlin/docsimage/nfa_example.png)

### **2. DFA**
A given **regular expression** is converted into an **NFA** and then transformed into a **DFA (Deterministic Finite Automaton)**, which can be visualized graphically.
DFA provides more efficient regular expression matching than NFA by minimizing unnecessary states.

#### Example: Conversion from NFA to DFA
DFA transformation process and result:
![DFA Example](src/test/kotlin/docsimage/dfa_example.png)

### **3. Direct DFA**
A **regular expression** is converted directly into a DFA without passing through an NFA. This approach can result in fewer states compared to the traditional NFA-to-DFA transformation.

### **4. Tokenizer**
The **tokenizer** is responsible for breaking down an input string into **tokens**. It utilizes **DFA-based tokenization**, ensuring efficient lexical analysis.

#### **4.1. Token Definitions**
Each token is defined using regular expressions in `Token.kt`. Examples include:

- **Integer (IntNumberToken):** `[0-9]+`
- **Identifier (IdentifierToken):** `([a-zA-Z_][a-zA-Z0-9_]*)`
- **Operators (ArithmeticOperatorToken):** `(+|\-|\*|\/)`
- **Control Structures (ForToken, IfToken, WhileToken, etc.):** `"for"`, `"if"`, `"while"`

These regular expressions are converted into **NFA → DFA**, resulting in an optimized state machine.

#### **4.2. Tokenization Process**
The tokenizer follows these steps, as implemented in `Tokenizer.kt`:

1. **Read input string**
2. **Use the DFA to find the longest matching token**
3. **Convert the matched string into a token**
4. **Ignore whitespace and comments, returning valid tokens only**

Example:
```kotlin
val input = "int x = 42;"
val tokens = Tokenizer.tokenize(input)
println(tokens) 
// Output: [(TypeToken, "int"), (IdentifierToken, "x"), (AssignmentOperatorToken, "="), (IntNumberToken, "42"), (SemicolonToken, ";")]
```

#### **4.3. DFA-Based Tokenization**
Tokenization is performed using a **DFA-based approach** for efficiency:

- **NFA Generation:** Convert each token into an NFA
- **NFA Merging:** Combine all NFAs into one
- **DFA Conversion:** Transform the NFA into a DFA
- **DFA Minimization:** Remove redundant states for optimization

This results in a highly efficient tokenizer.

#### **4.4. Tokenization Example**
Below is a test case from `TokenizerTest.kt`:

```kotlin
@Test
fun `test multiple valid tokens with whitespace`() {
    val input = "123 + 456"
    val expectedTokens = listOf(
        Token.IntNumberToken to "123",
        Token.ArithmeticOperatorToken to "+",
        Token.IntNumberToken to "456"
    )
    val actualTokens = Tokenizer.tokenize(input)
    assertEquals(expectedTokens, actualTokens)
}
```

#### **4.5. Tokenization Visualization**
DFA-based tokenization process:
[Tokenizer DFA.pdf](src/test/kotlin/tokenizer.pdf)

---

## **Parser Implementation (LR(0) → SLR(1) → LR(1) → LALR(1))**

### **1. LR(1) Parsing Implementation**
LR(1) parsing extends SLR(1) by incorporating **Lookahead symbols**, allowing for more precise parsing decisions.
This approach prevents conflicts that occur in SLR(1) when FollowSets are not sufficient.

#### Example: LR(1) Parsing State Graph
To demonstrate LR(1) parsing, the following expression grammar is used:

```
<E> ::= <E> "+" <T> | <T>
<T> ::= <T> "\*" <F> | <F>
<F> ::= "(" <E> ")" | "id"
```

Generated LR(1) state graph:

![LR(1) Parsing State Graph](src/test/kotlin/docsimage/lr1_example.png)

The LR(1) parsing state graph contains a large number of states, making it complex and difficult to interpret.

### **2. LALR(1) Optimization**
Although LR(1) parsing is powerful, it introduces a large number of states due to its use of Lookaheads.
LALR(1) parsing optimizes this by merging LR(1) states that share the same LR(0) core while combining Lookaheads.

This allows for a more efficient parsing table while maintaining LR(1) parsing accuracy.

#### Example: LALR(1) Parsing State Graph
![LALR(1) Parsing State Graph](src/test/kotlin/docsimage/lalr_example.png)
As you can see, the number of states is significantly reduced.

### **3. Action & Goto Table Construction**
LALR(1) operates using **compressed LR(1) tables**, maintaining strong parsing capabilities while reducing state complexity.

```kotlin
override fun action(s: Int, terminalItem: TerminalItem): Action {
    var j = goto[s to terminalItem]
    if (j != null) {
        return Action.Shift(j)
    }

    val c = lr1CollectionMap[s]!!
    val reduceItems = c.items.filter { it.dotIndex == it.production.size &&
            it.lookAhead.contains(terminalItem)
    }
    if (reduceItems.size > 1) {
        throw IllegalArgumentException("grammar is not LALR(1)")
    }

    val reduceItem = reduceItems.firstOrNull()

    if (reduceItem != null) {
        if (reduceItem.nonTerminal == NonTerminalItem(root.name + "`") && terminalItem == endTerminalItem) {
            return Action.Accept
        }
        return Action.Reduce(reduceItem.nonTerminal, reduceItem.production)
    }

    return Action.Error
}
```
This function enables **Shift, Reduce, Accept, and Error actions** efficiently in the LALR(1) parsing model.

---

## **Abstract Syntax Tree (AST) Implementation**

### **DanLang: A Custom Programming Language**
DanLang is a C-like programming language designed for educational purposes. It includes:
- Primitive data types: `int`, `float`, `string`, `boolean`
- Statements ending with `;`
- Control structures: `for`, `while`, `if`, `else if`, `else`
- Functions with a return type, parameters, and block scope
- Classes with member variables and functions (no inheritance)
- Object creation using `new` and memory deallocation with `delete`
- Operator precedence: `*`, `/` have higher precedence than `+`, `-`

### **AST Representation in DanLang**
DanLang's AST is built using `DanLangASTNode` structures, where each non-terminal has a corresponding node type.

#### Example: AST for
```
int main() {
    int x = 3 + 4 * 5;
    return 0;
}
```

#### **Example AST Visualization**
![ast example](src/test/kotlin/docsimage/ast_example.png)

---

## **Next Steps**
- **Semantic Analysis**: Type checking, variable scope validation
- **Intermediate Code Generation**: Convert AST to three-address code (TAC)
- **Optimization & Code Generation**: Dead code elimination, register allocation

DanLang is now a fully functional language, with parsing and AST generation successfully implemented! 🚀