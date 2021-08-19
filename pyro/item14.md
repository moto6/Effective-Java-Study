# Item14: Comparable을 구현할지 고려하라

## Comparable 인터페이스를 구현해야는 이유

- Collections 인터페이스를 활용하기 위해
- 쉽게 이야기하자면, 객체의 정렬, 검색, 비교를 하기 위해

## Comparable 인터페이스를 구현하는 꿀팁

- `implements Comparable<>` 를 할 때 generic 을 명시해주자
- 인터페이스를 구현할 때 IDE 의 자동완성 기능 (Alt + Enter) 를 애용하자
- 이미 제공되고 있는 compare 메서드 혹은 구현된 Comparator 인터페이스를 재활용하자
    - 제공을 하는 주체는 바닐라 언어일 수도, 개발 환경일 수도, 개발 라이브러리 일수도 있다.

## compareTo 메서드의 규약

equals 메서드를 재정의할 때와 비슷한 규약들을 지키면 된다.<br>
수학적인 이론 설명은 생략하겠다.<br>
집합론을 공부해보았다면, 본능적으로 지키려고 하는 규칙들이다.<br>
솔직히 바로 위의 꿀팁 내용들을 따르면 굳이 신경쓸 필요가 없는 내용이다.<br>
수학적으로 증명하려 하지 말고, **테스트 코드를 짜려고 하라** (과학적 방법론)

- 규약 1. `sgn(x.compareTo(y)) == -sgn(y.comparetTo(x))` for all x and y
    - x 가 y 보다 크면, y 는 x 보다 작아야한다.
- 규약 2. Transitive (삼단논법, 추이성)
    - if `x.compareTo(y) > 0 && y.compareTo(z) > 0` then `x.compareTo(z) > 0`
    - x < y 이고, y < z 라면 x < z 이다.
- 규약 3. `x.compareTo(y) == 0` iff `sgn(x.compareTo(z)) == sgn(y.compareTo(z))`
    - 수학쟁이들은 매우 흥분할만 규칙이다.
    - x 와 y 가 같다면, 다른 차원으로 이동되어도 같아야한다는 의미이다.
    - 사랑하는 사람은 다시 태어나더라도 다시 사랑에 빠지게 된다는 식으로, 수학과에서 개드립을 하고는 한다.
- 규약 4. `x.compareTo(y) == 0` iff `x.equals(y)`
    - 필수 규약이 아니라고 하지만, 개인적으로는 필수적으로 같이 지켜줘야한다고 생각한다.
    - 다른 사람들은 규약 4를 지키지 않는 경우도 있다고 생각하자.

## 구현 예시

- 목표: Number 객체가 정렬 가능하도록 하자

### Comparable 을 구현해서 Collections 활용

```java
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Number implements Comparable<Number> {
    private final int number;

    public Number(int number) {
        this.number = number;
    }

    @Override
    public int compareTo(Number o) {
        return Integer.compare(number, o.number);
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public static void main(String[] args) {
        List<Number> numbers = Stream.of(5, 7, 2, 1, 9, 6, 4)
                .map(Number::new)
                .collect(Collectors.toList());
        System.out.println(numbers);
        Collections.sort(numbers);
        System.out.println(numbers);
    }
}
```

### Comparator 를 람다로 직접 재정의

개인적으로 선호하는 방식

즉 Comparable 을 구현해야할지 고려하는 것이지, 항상 만드시 Comparable 인터페이스를 구현할 필요는 없다.

```java
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Number {
    private final int number;

    public Number(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public static void main(String[] args) {
        List<Number> numbers = Stream.of(5, 7, 2, 1, 9, 6, 4)
                .map(Number::new)
                .collect(Collectors.toList());
        System.out.println(numbers);
        numbers.sort(Comparator.comparingInt(o -> o.number));
        System.out.println(numbers);
    }
}
```

### 비교해야하는 내부 멤버가 여러개 일때

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingInt;

public class Numbers implements Comparable<Numbers> {
    private int n1;
    private int n2;
    private static final Comparator<Numbers> COMPARATOR = comparingInt((Numbers o) -> o.n1)
            .thenComparing(o -> o.n2);

    public Numbers(int n1, int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    @Override
    public String toString() {
        return String.format("%d %d", n1, n2);
    }

    @Override
    public int compareTo(Numbers o) {
        return COMPARATOR.compare(this, o);
    }

    public static void main(String[] args) {
        List<Numbers> numbers = Arrays.asList(
                new Numbers(9, 8),
                new Numbers(5, 6),
                new Numbers(1, 2)
        );
        System.out.println(numbers);
        Collections.sort(numbers);
        System.out.println(numbers);
    }
}
```

### 내부 맴버가 여러개일 때 Comparator 를 람다로 재정의

Comparable 을 직접 구현해야하는 것보다 훨씬 간단하다. <br>
본인이 이 방식을 더 선호하는 이유는 보통 정렬하는 기준은, 정렬하는 순간마다 다른 경우가 많기 때문이다.<br>
따라서 정렬 기준은 객체 내부가 아니라, 객체 외부에 지역변수로 선언되어야 한다고 생각한다.

```java
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingInt;

public class Numbers {
    private int n1;
    private int n2;

    public Numbers(int n1, int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    @Override
    public String toString() {
        return String.format("%d %d", n1, n2);
    }

    public static void main(String[] args) {
        List<Numbers> numbers = Arrays.asList(
                new Numbers(9, 8),
                new Numbers(5, 6),
                new Numbers(1, 2)
        );
        System.out.println(numbers);
        numbers.sort(comparingInt((Numbers o) -> o.n1)
                .thenComparing(o -> o.n2));
        System.out.println(numbers);
    }
}
```

### 비교해야하는 내부 멤버의 갯수가 정해져 있지 않을 때

파이썬 마렵다.<br>
그리 예쁜 코드라고 생각되지 않아서, 더 좋은 방법에 대한 조언을 구한다.

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Numbers implements Comparable<Numbers> {
    private List<Integer> numbers;

    public Numbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public int size() {
        return numbers.size();
    }

    @Override
    public String toString() {
        return numbers.toString();
    }

    @Override
    public int compareTo(Numbers o) {
        int size = Math.min(size(), o.size());
        Iterator<Integer> itr1 = this.numbers.iterator();
        Iterator<Integer> itr2 = o.numbers.iterator();
        for (int i = 0; i < size; i++) {
            int compared = itr1.next().compareTo(itr2.next());
            if (compared != 0) {
                return compared;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        List<Numbers> numbers = Stream.of(
                Arrays.asList(9),
                Arrays.asList(5, 4),
                Arrays.asList(1, 2, 3)
        ).map(Numbers::new).collect(Collectors.toList());
        System.out.println(numbers);
        Collections.sort(numbers);
        System.out.println(numbers);
    }
}
```

hashCode 를 비교하는 이펙티브 자바 예시는, 실용성이 없다고 생각해서 따라하지 않았다.

## 자꾸 헷갈리는 내용

- `compare(Object o1, Object o2)` 이랑 `o1.compareTo(o2)` 이 같다.
- o1 이 작으면 음수가 나온다.

## 질문

- 매번 `import java.util.List` 치는게 귀찮은데 쉽게 자동완성할 수 있는 방법이 있나? 자꾸 `java.awt.List` 의 List 가 자동완성된다.
