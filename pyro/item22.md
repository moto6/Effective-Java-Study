# Item 22: 인터페이스는 타입을 정의하는 용도로만 사용하라

아이템 22가 **typing** 과 관련된 가이드라면, <br>
아이템 23은 **subtyping** 과 관련된 가이드이다.

타입스크립트처럼 타입이 존재하는 모든 언어에서 사용되는 테크닉이므로 읽어둘 가치가 있다.

- typing: 타입은 state 와 logic 을 가지지 않는다.
- subtyping: 리스코프 치환 법칙, LSP(Liskov Substitution Principle)

## 요약

인터페이스에 제발 static 좀 넣지 말자. <br>
인터페이스는 타입을 정의하는 역할이지, 유틸을 제공하는 역할이 아니다.


### 유틸 클래스

```java
public class PhysicalConstants {
    private PhysicalConstants() {}

    public static final double AVOGADROS_NUMBER = 6.022;
    public static final double BOLTZMANN_CONST = 1.38;
    public static final double ELECTRON_MASS = 9.109;
}
```

### 바보 같은 클래스

interface 는 implements 하기 전까지는 객체를 구현할 수 없다고, <br>
이걸 싱글톤같은 용도로 사용하려는 요상한 개발자들이 있다. <br>
지금은 이런분이 없겠지만, 옛날에는 이런분들이 자신이 천재적인 발상을 해냈다고 여겼다.

```java
public interface PhysicalConstants {
    public static final double AVOGADROS_NUMBER = 6.022;
    public static final double BOLTZMANN_CONST = 1.38;
    public static final double ELECTRON_MASS = 9.109;
}
```
