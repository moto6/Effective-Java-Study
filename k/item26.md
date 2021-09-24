# 로 타입은 사용하지 말라

## 정리

제네릭을 생략하고 사용하지 않는다. 제네릭 타입을 명시해서 컴파일 타임에 관련 에러들을 쉽게 해결할 수 있도록 한다.

## 내용

- 과거에는 컬렉션에 객체를 넣으면 이후에는 type casting을 해야하는 번거로움이 있었다.
- 제네릭이 도입된 지금은 type casting 할 필요도 없어졌고, 다른 타입의 객체를 넣었을 때도 컴파일 타임에 해당 에러를 잡을 수 있다.
- 제네릭을 사용하지 않으면 제네릭이 안겨주는 안전성과 표현력을 모두 잃게 된다.

- bad eg. `List list = new ArrayList();` type hint 가 없다.
- 이는 `List<Object> list` 와는 다르다. 이는 Object 타입을 받는다고 명시한 것이다.
- 원소타입을 몰라도 받고 싶은 경우는 비한정적 와일드카드 타입을 대신 사용한다.
- `List<?> list`

```java
import java.util.*;

public class Item26 {
  public static void main(String[] args) {
    final List<String> list = new ArrayList<>();

    list.add("Item26");
    doWithWildCard(list);
  }

  // 패러미터로 임의의 타입을 받고 싶다면 와일드 카드를 사용한다.
  public static void doWithWildCard(List<?> list) {
    list.add("wild card"); // compile error
    list.add(null); // 실행 가능
    list.forEach(e -> {
      if (e instanceof String) {
        String s = (String) e;
        System.out.println(s);
      }
    });
  }
}


```

로 타입을 사용해도 되는 경우는 다음 두가지 이다.
1. class 리터럴 (eg. `List.class`, `String[].class`, `int.class`)
2. instanceof
