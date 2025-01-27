
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

## 토크나이저 (Tokenizer)
### 개요
토크나이저는 소스 코드를 분석하여 입력된 문자열을 **토큰(Token)** 단위로 나누는 역할을 합니다.
각 토큰 타입은 정규식으로 정의되어 있으며, **NFA**를 통해 정의된 정규식을 **DFA**로 변환하여 토크나이징 성능을 최적화했습니다.

### 주요 구현 사항
1. **토큰 타입 정의**:
   - 각 토큰 타입을 [Token.kt](src/main/kotlin/lexer/Token.kt) 파일에 정의했습니다.
   - 정규식을 기반으로 각 토큰 타입에 대한 NFA를 생성합니다.

2. **DFA 병합 및 상태 최소화**:
   - 모든 토큰 타입의 NFA를 병합하여 단일 DFA로 변환한 후, 상태를 최소화합니다.
   - 상태 최소화를 통해 메모리 사용량을 줄이고 성능을 향상시켰습니다.
   - 완성된 그래프는 [PDF](src/test/kotlin/tokenizer.pdf)를 참고하세요. #감동 #실화 ( 브라우저에서 보면 깨져서 다운받아서 보세요 )

3. **토큰 타입 해석**:
   - DFA 상태에 매칭된 토큰 타입 중 우선순위를 통해 가장 적합한 토큰을 선택합니다.

### 예시 코드
#### 입력 코드
```kotlin
val input = "int x = 42;"
val tokens = Tokenizer.tokenize(input)
println(tokens)
```

#### 출력 결과
```
[
    (KEYWORD, "int"),
    (IDENTIFIER, "x"),
    (OPERATOR, "="),
    (NUMBER, "42"),
    (SEMICOLON, ";")
]
```

### 테스트
- [TokenizerTest.kt](src/test/kotlin/tc/TokenizerTest.kt)에서 다양한 입력에 대한 테스트를 확인할 수 있습니다.
- 예외 처리와 다양한 토큰 타입 매칭이 포함된 케이스를 테스트했습니다.

---

이 프로젝트는 지속적으로 발전 중이며, 다음 목표는 **파서(Parser)** 구현입니다.
