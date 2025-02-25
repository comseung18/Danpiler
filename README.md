# Danpiler
Compilers : Principles, Techniques & Tools 를 읽고 어휘 분석기부터 중간 코드 생성과 코드 최적화까지 가능한 만큼 직접 구현해봅니다. 그 이름하야 단파일러 프로젝트!

## 어휘 분석기

### NFA
**정규식**을 입력받아 이를 **NFA (Nondeterministic Finite Automaton)**로 변환하고, 이를 그래픽적으로 시각화할 수 있습니다.

입력된 정규식을 [NFA](src/main/kotlin/NFA.kt)로 변환할 수 있습니다. 변환한 NFA로 정규식 매칭을 할 수 있고, [NFATest.kt](src/test/kotlin/tc/NFATest.kt)를 참조하세요.

#### 예시: 정규식 `(a|b)*abb`
NFA 생성 과정과 결과:
![NFA Example](src/test/kotlin/docsimage/nfa_example.png)

### DFA
**정규식**을 입력받아 생성된 **NFA**를 **DFA (Deterministic Finite Automaton)**로 변환하고, 이를 그래픽적으로 시각화할 수 있습니다.
DFA는 NFA에 비해 더 효율적으로 정규식을 매칭할 수 있으며, 불필요한 상태를 제거하여 최적화된 상태로 동작합니다.

#### 예시: NFA에서 DFA로 변환된 결과
DFA 변환 과정과 결과:
![DFA Example](src/test/kotlin/docsimage/dfa_example.png)

### Direct DFA
**정규식**을 입력받아 NFA를 거치지 않고 곧바로 DFA를 생성합니다. 이 방식은 NFA를 생성한 뒤 DFA로 변환하는 방식보다 더 적은 상태를 가질 수 있습니다.

---

## **파서 구현 (LR(0) → SLR(1))**

### **1. LR(0) 오토마타 생성**
BNF 문법을 입력받아 **LR(0) 오토마타를 생성**합니다.  
각 상태(State)와 이동(Transition)을 그래픽으로 출력할 수 있으며,  
DOT(Graphviz) 파일을 이용해 **오토마타를 시각적으로 확인**할 수 있습니다.

- `closure()`를 이용해 **LR(0) 상태 집합을 계산**하고,
- `goto()`를 통해 **각 상태에서의 전이(Transition)를 구성**합니다.

### **2. SLR(1) 확장**
SLR(1) 파서는 **LR(0) 파서에서 FollowSet을 추가로 고려하여 충돌을 줄인 버전**입니다.  
이를 위해 [FirstFollowCalculator](src/main/kotlin/parser/parserUtils.kt)를 이용해 **First/Follow 집합을 계산**하고,  
Shift/Reduce 충돌을 해결하여 보다 정교한 파싱이 가능합니다.

#### 예시: SLR(1) 파싱 과정
SLR(1) 파서가 어떻게 동작하는지 보여주기 위해, 다음과 같은 간단한 수식(Expression) 문법을 사용합니다:

```
<E> ::= <E> "+" <T> | <T>
<T> ::= <T> "*" <F> | <F>
<F> ::= "(" <E> ")" | "id"
```

이 문법을 기반으로 생성된 SLR(1) 상태 그래프는 다음과 같습니다:

![SLR Graph](/src/test/kotlin/docsimage/slr_example.png)

### **3. Action & Goto 테이블 구성**
SLR(1) 파서는 **Action / Goto 테이블을 기반으로 동작**합니다.  
각 상태에서 입력을 확인하고, **Shift, Reduce, Accept, Error** 처리를 수행합니다.

```kotlin
override fun action(s: Int, terminalItem: TerminalItem): Action {
    val j = goto[s to terminalItem]
    if (j != null) {
        return Action.Shift(j)
    }

    val reduceItems = lr0CollectionMap[s]!!.items.filter { it.dotIndex == it.production.size &&
        firstFollowCalculator.getFollowSet(it.nonTerminal).contains(terminalItem)
    }

    if (reduceItems.size > 1) {
        throw IllegalArgumentException("grammar is not SLR(1)")
    }

    val reduceItem = reduceItems.firstOrNull()
    if (reduceItem != null) {
        if (reduceItem.nonTerminal == NonTerminalItem(root.name + "`")) {
            return Action.Accept
        }
        return Action.Reduce(reduceItem.nonTerminal, reduceItem.production)
    }

    return Action.Error
}
```
위 코드처럼 **Shift, Reduce, Accept, Error를 처리**하며,  
SLR(1)에서는 FollowSet을 고려하여 Reduce를 수행합니다.

### **4. 테스트 및 검증**
SLR(1) 파서가 정상적으로 동작하는지 **여러 문법을 테스트하여 검증**하였습니다.
[SLRParserTest.kt](src/test/kotlin/tc/SLRParserTest.kt) 에서 확인할 수 있습니다.

#### ✅ **테스트 케이스**
- **간단한 문법 테스트**
- **수식(Expression) 문법 테스트**
- **제어문 (if, while) 테스트**
- **클래스/함수 선언을 테스트하여 복잡한 문법도 올바르게 파싱되는지 확인**

