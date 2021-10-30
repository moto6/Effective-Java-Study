# 제네릭과 가변인수를 함께 쓸 때는 신중하라

제네릭 가변인수를 사용하는 것은 허용된다. 그렇다 해도 문제가 없는 것은 아니다.

```java
class item32 {
  static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList; // 힙 오염 발생
    String s = stringLists[0].get(0); // ClassCastException
  }
}
```

실무에서 유용하기 때문에, 모순을 수용하기로 했다.

`@SafeVarargs` 애너테이션을 이용하여 제네릭 가변인수 경고를 무시할 수 있다.

이때, 제네릭 가변인수는 다음 경우에는 안전하다.
- 메서드 내부에서 배열에 아무것도 저장하지 않고
- 그 배열의 참고가 밖으로 노출되지 않을 때
- 즉, 호출자가 그 메서드로 순수하게 인수들을 전달하는 일만 하는 경우 (varargs의 목적대로 쓰는 경우) 안전하다.

다음은 값을 변경하지 않지만 안전하지 않은 예이다.
```java
class item32 {
  static <T> T[] toArray(T... args) {
    return args; // 두번째 경우를 위반
  }

  static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
      case 0: return toArray(a, b);
      case 1: return toArray(b, c);
      case 2: return toArray(a, c);
    }
    throw new AssertionError();
  }

  public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
  }
}

```

위의 경우가 안전한 두가지 예외가 있다.

- `@SafeVarargs`로 제대로 설계된 또 다른 메서드에 넘기는 것은 괜찮다.
- 그저 배열 내용의 일부 함수를 호출만 하는 non-varargs 메서드에 넘기는 경우만 안전하다.

안전하지 않은 varargs 메서드는 절대로 작성하지 않는다. 즉, 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 `@Safevarargs`를 추가한다.

```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
  List<T> result = new ArrayList<>();
  for (List<? extends T> list : lists)
    result.addAll(list);
  return result;
}

```

Item28 에 따라서 배열보다 List를 사용하는 방법도 있다. 이 방법은 타입 안전성을 컴파일 시간에 검증할 수 있지만, 클라이언트 코드가 살짝 지저분해지고 속도가 느려질 수 있다.

```java
static <T> List<T> flatten(List<List<? extends T>> lists) {
  List<T> result = new ArrayList<>();
  for (List<? extends T> list : lists)
    result.addAll(list);
  return result;
}
```

pickTwo를 `List.of(T... args)`를 이용하여 개선한 메서드
```java
static <T> List<T> pickTwo(T a, T b, T c) {
  switch(rnd.nextInt(3)) {
    case 0: return List.of(a, b);
    case 1: return List.of(a, c);
    case 2: return List.of(b, c);
  }
  throw new AssertionError();
}
```
