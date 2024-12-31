# Danpiler
Compilers : Principles, Techniques &amp; Tools 를 읽고 어휘 분석기부터 중간 코드 생성과 코드 최적화까지 가능한 만큼 직접 구현해봅니다. 그이름하야 단파일러 프로젝트


## 어휘분석기

### NFA
**정규식**을 입력받아 이를 **NFA (Nondeterministic Finite Automaton)**로 변환하고, 이를 그래픽적으로 시각화할 수 있습니다.

입력된 정규식을 [NFA](src/main/kotlin/NFA.kt)로 변환할 수 있습니다. 변환한 NFA 로 정규식 매칭을 할 수 있고 [NFATest.kt](src/test/kotlin/NFATest.kt) 를 참조하세요.

변환된 NFA는 **Dot 파일** 형식으로 출력되고, 이를 **Graphviz**를 사용하여 그래프 형태로 시각화할 수 있습니다.

#### 동작 예시
#### 1. 정규식 → NFA 변환

입력 정규식:"a|b*c|d(e|f)*g"
#### 2. NFA Graph

변환된 NFA를 **Dot 파일**로 출력하고, 이를 Graphviz를 사용하여 시각화한 이미지입니다. Dot 파일의 내용은 다음과 같습니다:
( toDot 함수로 출력가능 )
```dot
digraph NFA {
  rankdir=LR;
  size="8,5";

  start [shape=point];
  23 [shape=doublecircle];
  start -> 22 [ label = "ε" ];
  10 -> 18 [ label = "d" ];
  14 -> 15 [ label = "f" ];
  12 -> 13 [ label = "e" ];
  16 -> 14 [ label = "ε" ];
  16 -> 12 [ label = "ε" ];
  15 -> 17 [ label = "ε" ];
  13 -> 17 [ label = "ε" ];
  18 -> 16 [ label = "ε" ];
  18 -> 20 [ label = "ε" ];
  17 -> 16 [ label = "ε" ];
  17 -> 20 [ label = "ε" ];
  20 -> 21 [ label = "g" ];
  2 -> 3 [ label = "b" ];
  4 -> 2 [ label = "ε" ];
  4 -> 6 [ label = "ε" ];
  3 -> 2 [ label = "ε" ];
  3 -> 6 [ label = "ε" ];
  6 -> 7 [ label = "c" ];
  0 -> 1 [ label = "a" ];
  8 -> 4 [ label = "ε" ];
  8 -> 0 [ label = "ε" ];
  7 -> 9 [ label = "ε" ];
  1 -> 9 [ label = "ε" ];
  22 -> 10 [ label = "ε" ];
  22 -> 8 [ label = "ε" ];
  21 -> 23 [ label = "ε" ];
  9 -> 23 [ label = "ε" ];
}
```

위의 Dot 파일을 Graphviz로 시각화한 결과는 다음과 같습니다:
![image](src/test/kotlin/nfa.png)
#### ( ε 은 빈 문자 간선 )


#### 3. NFA 그래프 출력
위의 Dot 파일을 Graphviz를 사용하여 NFA 그래프를 생성할 수 있습니다. 예시로 nfa.dot 파일을 사용하여 NFA 그래프를 PNG 이미지로 생성하는 방법은 다음과 같습니다:
```
dot -Tpng nfa.dot -o nfa_graph.png
```
이 명령어를 실행하면 nfa_graph.png라는 이미지 파일이 생성됩니다.


### DFA

### Tokenizing


