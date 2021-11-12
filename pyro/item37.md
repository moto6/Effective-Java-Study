# Item 37: ordinal 인덱싱 대신 EnumMap을 사용하라

ordinal 이 무엇인지 아는가? <br>
모르겠다고? 정상이다.

앞으로도 기억속에서 지워버린채로 평생 쓰지 않도록 하자.

item37 요약: ordinal 을 쓰지 말자

## Ordinal 을 사용한 거지같은 코드

보통 신입은 이런 코드를 작성안하고, 쉰내나는 옛날 개발자가 이런 코드를 작성한다.

아래 코드가 끔찍한 이유들

1. 배열이 `Set<Plant>` 제네릭과 호환되지 않아서 비검사 형변환을 수행하고 있음
2. 배열이 각 인덱스의 의미를 몰라서 일일히 주석을 달아야 함
3. 범위밖의 ordinal 을 입력받을 때 컴파일 타임에 체크가 불가능함

말로 하면 어려우니깐 그냥 바로 코드를 보자.

```java
import java.util.HashSet;
import java.util.Set;

public class Plant {
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }

    public static void main(String[] args) {
        Plant[] garden = new Plant[]{
                new Plant("소나무", LifeCycle.ANNUAL),
                new Plant("초코나무", LifeCycle.PERENNIAL),
                new Plant("치킨나무", LifeCycle.PERENNIAL),
                new Plant("코틀린나무", LifeCycle.BIENNIAL),
                new Plant("파이썬나무", LifeCycle.BIENNIAL),
                new Plant("포트란나무", LifeCycle.BIENNIAL)
        };

        // FIXME: 강제 형변환이 필요한 시점에서 이미 망했다.
        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];

        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }

        for (Plant p : garden) {
            // FIXME: 코드가 읽히질 않아서 아래와 같은 끔찍한 주석을 달아야 한다.
            /**
             * ordinal 을 기준으로 식물들의 group 을 만듬
             * ordinal == 0 이면 ANNUAL
             * ordinal == 1 이면 PERENNIAL
             * ordinal == 2 이면 BIENNIAL 이다.
             */
            plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
            System.out.println(p + ": " + p.lifeCycle + ", " + p.lifeCycle.ordinal());
        }

        System.out.println();
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            // FIXME: i 가 0,1,2 중 하나라는 것을 어떻게 보장할 것인가?
            // 실수로 Plant.LifeCycle.values()[3] 을 호출하더라도 컴파일 단계에서 체크를 해줄 수가 없다.
            System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
```

```txt
소나무: ANNUAL, 0
초코나무: PERENNIAL, 1
치킨나무: PERENNIAL, 1
코틀린나무: BIENNIAL, 2
파이썬나무: BIENNIAL, 2
포트란나무: BIENNIAL, 2

ANNUAL: [소나무]
PERENNIAL: [초코나무, 치킨나무]
BIENNIAL: [포트란나무, 파이썬나무, 코틀린나무]
```

## 개선된 코드

Map 자료 구조를 사용한다.

일일히 초기화하는게 번거롭다면, plantsByLifeCycle3 에서처럼 stream 의 groupingBy 를 이용할 수도 있다.

```java
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class Plant {
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }

    public static void main(String[] args) {
        Plant[] garden = new Plant[]{
                new Plant("소나무", LifeCycle.ANNUAL),
                new Plant("초코나무", LifeCycle.PERENNIAL),
                new Plant("치킨나무", LifeCycle.PERENNIAL),
                new Plant("코틀린나무", LifeCycle.BIENNIAL),
                new Plant("파이썬나무", LifeCycle.BIENNIAL),
                new Plant("포트란나무", LifeCycle.BIENNIAL)
        };

        // Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];
        Map<LifeCycle, Set<Plant>> plantsByLifeCycle1 = new HashMap<>();
        Map<LifeCycle, List<Plant>> plantsByLifeCycle2 = Arrays.stream(garden)
                .collect(groupingBy(plant -> plant.lifeCycle));
        Map<LifeCycle, Set<Plant>> plantsByLifeCycle3 = Arrays.stream(garden)
                .collect(groupingBy(
                        plant -> plant.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class),
                        toSet()
                ));

        for (LifeCycle lifeCycle : LifeCycle.values()) {
            plantsByLifeCycle1.put(lifeCycle, new HashSet<>());
        }
        for (Plant p : garden) {
            plantsByLifeCycle1.get(p.lifeCycle).add(p);
        }

        System.out.println(plantsByLifeCycle1);
        System.out.println(plantsByLifeCycle2);
        System.out.println(plantsByLifeCycle3);
    }
}
```

```txt
{ANNUAL=[소나무], PERENNIAL=[치킨나무, 초코나무], BIENNIAL=[파이썬나무, 코틀린나무, 포트란나무]}
{ANNUAL=[소나무], PERENNIAL=[초코나무, 치킨나무], BIENNIAL=[코틀린나무, 파이썬나무, 포트란나무]}
{ANNUAL=[소나무], PERENNIAL=[치킨나무, 초코나무], BIENNIAL=[파이썬나무, 코틀린나무, 포트란나무]}
```

## 마지막 예제

### Before

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;


        // FIXME: null 이 명시적으로 사용되었다는 점에서 뭔가 망했다는 걸 느껴야 한다.
        private static final Transition[][] TRANSITIONS = {
                {null, MELT, SUBLIME},
                {FREEZE, null, BOIL},
                {DEPOSIT, CONDENSE, null}
        };

        // FIXME: ordinal 을 직접 사용하고 있는 시점에서 망했다.
        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

### After

```java
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // NOTE: Map 을 만들었다. Stream 을 이해하려하지 말고, 이렇게 groupingBy 를 활용할 수 있다는 것만 기억하자.
        private static final Map<Phase, Map<Phase, Transition>> map = Stream.of(values())
                .collect(groupingBy(
                        transition -> transition.from,
                        () -> new EnumMap<>(Phase.class), // NOTE: 요런걸 telescoping factory 라고 한다.
                        toMap(
                                transition -> transition.to,
                                transition -> transition,
                                (__, ___) -> __, // NOTE: 사용되지 않는 코드.
                                () -> new EnumMap<>(Phase.class)
                        )
                ));

        public static Transition from(Phase from, Phase to) {
            return map.get(from).get(to);
        }
    }
}
```
