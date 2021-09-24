# 톱레벨 클래스는 한 파일에 하나만 담으라

- 소스 파일 하나에는 반드시 톱레벨 클래스를 하나만 담자.

```java
// Main.java
public class Main {
  public static void main(String[] args) {
    System.out.println(Utensil.NAME + Dessert.NAME);
  }
}

// Utensil.java
class Utensil {
  static final String NAME = "pan";
}

class Dessert {
  static final String NAME = "cake";
}

// Dessert.java
class Utensil {
  static final String NAME = "pot";
}

class Dessert {
  static final String NAME = "pie";
}
```
- `javac Main.java Dessert.java`
- `javac Main.java Utensil.java`
- `javac Main.java`

컴파일할 때 같이 사용하는 파일에 따라서 결과물이 달라진다. 이를 수정하려면 Utensil과 Dessert를 다른 파일로 분리한다.

정 같이 사용하고 싶다면 정적 멤버 클래스를 사용한다.

```java
public class Utensil {
  static final String NAME = "pan";
}

public class Dessert {
  static final String NAME = "cake";
}

public class Test {
  public static class Utensil {
    static final String NAME = "pan";
  }

  public static class Dessert {
    static final String NAME = "cake";
  }
}

```

## 참고
- jdk 11를 쓰면 패키지 없이 컴파일 할 때 에러 없이 컴파일 된다.
- 그 대신 컴파일을 같이 하는 것에 따라 결과가 달라진다.
