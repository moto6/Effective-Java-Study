### 아이템4: 인스턴스화를 막기 원하면 private 생성자를 사용해라



#### 정적 멤버만을 담은 클래스의 사용 

주로, 객체지향적으로 사고하지 않는 방식이다.

하지만 java.lang.Math와 java.util.Arrays와 같이 관련된 메서드를 모아둔 클래스도 있다.

또는 Collections와 같이 특정 인터페이스를 구현하는 객체를 생성하는 정적 메소드를 모은 클래스도 있다.

혹은 final 클래스와 관련한 메서드를 모을 때도 사용된다. -> final 클래스는 상속 불가능해서 하위 클래스에 메소드를 넣을 수 없기 때문이다. 



정적 멤버만 담은 util 클래스는 인스턴스를 사용하려고 설계된 것이 아니다.

**그런데 생성자를 명시하지 않으면 컴파일러가 기본생성자를 만들어주기 때문에 명시적으로 기본생성자를 private로 추가한다.**

이렇게 하면 상속을 불가능하게 하는 효과도 볼 수 있다. 

모든 생성자는 명시적 혹은 묵시적으로 상위 클래스의 생성자를 호출하는데, 기본 생성자를 private으로 만들면 하위 클래스가 상위 클래스의 생성자에 접근할 수 없기 때문이다. 



아래와 같이 생성자를 호출하면 AssertionError를 발생시킴으로서 클래스 내부에서도 생성자를 호출할 수 없도록 할 수 있다. 

```java
public class UtilClass {

    private UtilClass() {
        throw new AssertionError();
    }
}
```

꼭 이렇게 에러를 발생시킬 필요는 없지만 실수로 클래스 내부에서 생성자 호출하는 행위를 방지할 수 있다. 



추상클래스로 만들면 클래스 내부에서 인스턴스 생성을 방지할 수 있지만, 아래와 같이 이것의 자손 클래스가 인스턴스화 시킬 수 있어서 <u>단순히 추상클래스로 만드는 것은 인스턴스화를 방지할 수 없다.</u>

```java
public abstract class UtilClass {

    public static String getName() {
        return "yeon";
    }

    static class SubUtilClass extends UtilClass {

    }

    public static void main(String[] args) {
        UtilClass utilClass = new SubUtilClass();
    }
}
```





백기선님의 영상을 보니 spring의 유틸 클래스들은 abstract로 많이 구현되어 있다. 그래서 abstract로 만드는 것으로 충분히 인스턴스화를 막을 수 있다고 한다. 

