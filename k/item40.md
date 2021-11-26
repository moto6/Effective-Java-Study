# @Override 애너테이션을 일관되게 사용하라

```java
import java.util.HashSet;
import java.util.Set;

public class Bigram {
  private final char first;
  private final char second;

  public Bigram(char first, char second) {
    this.first = first;
    this.second = second;
  }

  public boolean equals(Bigram b) {
    return b.first == first && b.second == second;
  }

  public int hashCode() {
    return 31 * first + second;
  }

  public static void main(String[] args) {
    Set<Bigram> s = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      for (char ch = 'a'; ch <= 'z'; ch++) {
        s.add(new Bigram(ch, ch));
      }
    }
    System.out.println(s.size());
  }
}

```

위 객체는 equals와 hashCode를 재정의하려고 했다. 그러나 안타깝게도 equals를 재정의한 것이 아니라 overloading을 하게 되어, Set에서 이 메소드가 아닌 `Object.equals`를 사용하여 결과값이 260이 나오게 되었다.

이 의도를 명확히 하려면 재정의하려는 메소드에 `@Override` 애너테이션을 추가한다.

```java
import java.util.HashSet;
import java.util.Set;

public class Bigram {
  private final char first;
  private final char second;

  public Bigram(char first, char second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean equals(Bigram b) {
    return b.first == first && b.second == second;
  }

  public int hashCode() {
    return 31 * first + second;
  }

  public static void main(String[] args) {
    Set<Bigram> s = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      for (char ch = 'a'; ch <= 'z'; ch++) {
        s.add(new Bigram(ch, ch));
      }
    }
    System.out.println(s.size());
  }
}

```
그러면 해당 메소드에서 이 함수가 super type에 없으므로 `Object.equals(Object obj)` 컴파일 에러를 발생한다. 그래서 이를 수정하면 다음과 같다.

```java
import java.util.HashSet;
import java.util.Set;

public class Bigram {
  private final char first;
  private final char second;

  public Bigram(char first, char second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(b instanceof Bigram)) return false;
    Bigram b = (Bigram) obj;
    return b.first == first && b.second == second;
  }

  public int hashCode() {
    return 31 * first + second;
  }

  public static void main(String[] args) {
    Set<Bigram> s = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      for (char ch = 'a'; ch <= 'z'; ch++) {
        s.add(new Bigram(ch, ch));
      }
    }
    System.out.println(s.size());
  }
}

```

**상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 추가하자.** 예외는 구체 클래스에서 상위 클래스의 추상 메서드를 재정의 할때 뿐이다. 이유는 추상 메서드를 구현하지 않으면 컴파일 에러로 찾을 수 있기 때문이다. 물론 예외 상관없이 달아도 상관없다.

인터페이스를 재정의할 때에도 사용할 수 있다. 디폴트 인터페이스 메소드가 있는 경우에 `@Override` 애너테이션을 사용하면 시그니쳐가 올바른지 재차 확인할 수 있다. 만약 디폴트 메서드가 없다는 것을 알고 있다면 위의 예외와 마찬가지로 생략할 수는 있다.

한편, 추상 클래스나 인터페이스에서는 상위 클래스나 상위 인터페이스의 메서드를 재정의하는 모든 메서드에 `@Override`를 다는 것이 좋다. 상위 클래스가 구체 클래스든 추상 클래스든 마찬가지이다. `Set`의 경우 `Collection` 인터페이스를 확장했지만, 새로 추가한 메서드는 없다. 그래서 모든 메서드 선언에 `@Override`를 달아 실수로 추가한 메서드가 없음을 보장했다.

- `@Override` 가 없다면, 상위 인터페이스에 존재하는 메서드인지를 확인할 수 없어, 실수로 메서드를 추가할 수도 있다.
- 비록 Collection과 Set은 같은 메서드를 갖지만, 메서드의 계약이 달라지므로 확장을 하면서도 모든 메서드를 재정의하는 것 같다.
