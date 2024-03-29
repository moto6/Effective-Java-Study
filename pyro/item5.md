# Item 5: 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

- 의존성 역전: import 없애기
- 의존성 주입: new 없애기

### 개인 경험담:

Item 4 처럼 static 유틸 클래스를 만든다면, Mockito로도 테스트가 불가능하다. <br>
싱글톤으로 만든다면, Mockito 로 테스트가 가능하다. <br>
Item 5 규칙을 잘 지켰다면 테스트 코드를 작성할 때, Mockito 를 필요로 하지 않는다. <br>
테스트 코드를 짜던 도중 Mockito 가 필요해 진다면, Item 5 를 다시 읽어보자.

## 바닐라

의존성 역전과 의존성 주입 둘다 없는 예시

```Main.java
public class Main {
    public static void main(String[] args) {
        Chef chef = new Chef();
        chef.cook();
    }
}
```

```Chef.java
public class Chef {
    public void cook() {
        Pizza pizza = new Pizza();
        String message = String.format("셰프가 %s를 만듭니다.", pizza);
        System.out.println(message);
    }
}
```

```Pizza.java
public class Pizza {
    public String toString() {
        return "피자";
    }
}
```

## 의존성 주입

의존성 역전 없이, 의존성 주입만 있는 예시

Main 이 Chef 에게 Pizza 의존 객체를 주입해주고 있다.<br>
스프링을 쓰면 개발자가 Main.java 를 구현하지 않아도, 프레임워크가 알아서 주입을 해준다.

별거 아니네요! 할 수도 있지만, 상당히 어려운것을 프레임워크가 대신해주고 있다. <br>
왜냐하면 프로젝트 규모가 커질 수록 객체의 생명주기를 관리하기가 점점 더 어려워주기 떄문이다.

```Main.java
public class Main {
    public static void main(String[] args) {
        Chef chef = new Chef();
        chef.cook(new Pizza());
    }
}
```

```Chef.java
public class Chef {
    public void cook(Pizza pizza) {
        String message = String.format("셰프가 %s를 만듭니다.", pizza);
        System.out.println(message);
    }
}
```

```Pizza.java
public class Pizza {
    public String toString() {
        return "피자";
    }
}
```

## 의존성 역전

의존성 역전이 일어난 예시

Chef 는 Pizza 를 실행하고 있음에도 불구하고, (Control Flow) <br>
코드 상으로 Chef 는 Pizza 를 전혀 모르고 의존하지 않는다. (Dependency Flow)

이처럼 Control Flow 와 Dependency Flow 가 일치하지 않을때, <br>
의존성 역전이 일어났다고 한다.

```Main.java
public class Main {
    public static void main(String[] args) {
        Chef chef = new Chef();
        chef.cook(new food());
    }
}
```

```Chef.java
public class Chef {
    public void cook(Food food) {
        String message = String.format("셰프가 %s를 만듭니다.", food);
        System.out.println(message);
    }
}
```

```Pizza.java
public class Pizza implements Food {
    public String toString() {
        return "피자";
    }
}
```

```Food.java
public interface Food {
    String toString();
}
```

## 결론

의존성 주입이 되었음에도, 의존성 역전이 일어나지 않았을 수도 있다.

하지만 의존성 역전이 일어나려면, 추상화(Abstraction)와 의존성 주입이 꼭 필요하다. (이제와서야 K 교수님의 큰 뜻을 깨달았습니다.)

추상화된 interface 만으로는 결코 객체를 생성할 수 없기에, 실제로 구현된 Concrete 클래스를 누군가 객체로 만들어서 주입을 해주어야 한다.

주입을 해주는 누군가는 스프링과 같은 프레임워크가 될수도 있고, 개발자가 직접 의존성을 주입하는 코드를 짤 수도 있다.
