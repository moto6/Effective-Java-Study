# 인스턴스화를 막으려거든 private 생성자를 사용하라

static method / field만 존재하는 객체를 사용하고 싶을 때가 있다.

비록 객체지향적이지 않지만, 쓰는 경우가 있다.

1. java.lang.Math, java.util.Arrays 와 같이 primitive type 을 위한 메소드를 모아둔 객체
2. java.util.Collections 와 같이 객체를 생성해주는 메소드를 모아둔 객체
3. final 클래스와 관련한 메소드들을 모아둔 객체

이런 객체들은 인스턴스화 해서 사용하라는 객체가 아니다. 그러므로 인스턴스 생성을 막을 필요가 있다.

간단하게, 모든 생성자를 private로 만들면 된다.

```java
public class KUtil {
    private KUtil() {

    }

    public static String doSomething() {
        return "doSomething";
    }
}
```

비고)
- 추상 클래스로 만들어도 인스턴스화를 피할 수 없다.
- private 생성자만 존재하면 객체를 상속할 수도 없다.
