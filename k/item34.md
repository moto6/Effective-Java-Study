# int 상수 대신 열거 타입을 사용하라

정수 열거 패턴 - 과거의 C enum에서 가져왔는데, 매우 좋지 않다.
```java
Calendar.DAY_OF_MONTH // 1

calendar.add(Calendar.SECOND, 2);
calendar.add(33, 2); //???

calendar.set(1582, Calendar.OCTOBER , 4);  // 1582년 10월 4일
calendar.set(1582, 10 , 4);  // 1582년 11월 4일

```
https://d2.naver.com/helloworld/645609

- 상수의 값이 바뀌면 클라이언트도 컴파일을 다시 해야한다.
- 해당 상수를 문자열로 출력하면 의미가 없어진다.
- 문자열을 열거형으로 사용하는 경우는 더 나쁘다. 오타가 발생할 수도 있고, 문자열 비교 때문에 성능도 하락한다.

```java
public enum Fruit {
  APPLE,
  ORANGE
}
```

- 자바의 열거타입은 완전한 클래스이다. 그러므로 기능을 확장할 수 있다.
- 열거 타입은 외부에서 사용할 수 있는 생성자가 없으므로 final이다.
- 열거 타입의 인스턴스는 싱글턴이다. 그러므로 == 등을 통해 직접 비교도 가능하다.
- 컴파일 시간의 타입 안전성을 보장한다. Fruit의 경우, APPLE과 ORANGE가 아닌 경우에 컴파일 오류가 발생한다.
- 다음과 같이 인스턴스 이름이 같은 enum도 namespace로 분류할 수 있기 때문에 공존할 수 있다.

```java
public enum Fruit {
  APPLE,
  ORANGE
}

public enum MyFavoriteFruit {
  APPLE,
  ORANGE
}
```

- 새로운 상수를 추가하거나 순서를 바꿔도 클라이언트를 새로 컴파일할 필요 없다.
- toString() 메소드를 이용해서 적절한 문자열을 사용할 수 있다.
- 임의의 메서드나 필드를 추가할 수 있다.
- Object 메서드들 (equals, toString, hashCode...) 등을 매우 잘 구현했다.
- Comparable 과 Serializable 을 매우 잘 구현했다.

enum 확장 기능
```java
public enum Planet {
  MERCURY(3.302e+23, 2.439e6),
  VENUS  (4.869e+24, 6.052e6),
  EARTH  (5.975e+24, 6.378e6),
  MARS   (6.419e+23, 3.393e6),
  JUPITER(1.899e+27, 7.149e7),
  SATURN (5.685e+26, 6.027e7),
  URANUS (8.683e+25, 2.556e7),
  NEPTUNE(1.024e+26, 2.477e7),
  PLUTO  (1.303e+22, 1.188e6);

  private final double mass; // In kilograms
  private final double radius; // In meters
  private final double surfaceGravity; // In m / s^
  // Universal gravitational constant in m^3 / kg s^2
  private static final double G = 6.67300E-11;
  // Constructor
  Planet(double mass, double radius) {
    this.mass = mass;
    this.radius = radius;
    surfaceGravity = G * mass / (radius * radius);
  }
  public double mass() { return mass; }
  public double radius() { return radius; }
  public double surfaceGravity() { return surfaceGravity; }
  public double surfaceWeight(double mass) {
    return mass * surfaceGravity; // F = ma
  }
}

public class WeightTable {
  public static void main(String[] args) {
    double earthWeight = Double.parseDouble(args[0]);
    double mass = earthWeight / Planet.EARTH.surfaceGravity();
    for (Planet p : Planet.values())
      System.out.printf("Weight on %s is %f%n",
      p, p.surfaceWeight(mass));
  }
}
```

``` // 185인 경우
Weight on MERCURY is 69.912739
Weight on VENUS is 167.434436
Weight on EARTH is 185.000000
Weight on MARS is 70.226739
Weight on JUPITER is 467.990696
Weight on SATURN is 197.120111
Weight on URANUS is 167.398264
Weight on NEPTUNE is 210.208751
Weight on PLUTO is ...
```

명왕성은 2006년에 행성에서 빠졌는데, 실제 프로그램에서도 이를 뺀다면 클라이언트에서는 유용한 컴파일 오류가 발생한다.

enum instance에 따라서 동작이 달라지는 경우에도 enum은 유용하다. switch 로 구현하는 한가지 방법은 다음과 같다.

```java
public enum Operation {
PLUS, MINUS, TIMES, DIVIDE;
// Do the arithmetic operation represented by this constant
  public double apply(double x, double y) {
    switch(this) {
      case PLUS: return x + y;
      case MINUS: return x - y;
      case TIMES: return x * y;
      case DIVIDE: return x / y;
    }
    throw new AssertionError("Unknown op: " + this);
  }
}
```

다만 이 방법은 이후 연산자가 추가될 때마다 코드를 수정해야하는 단점이 있다.

이를 추상 메서드와 인스턴스별 구현을 하면 다음과 같이 작성할 수 있다.

```java
public enum Operation {
  PLUS {public double apply(double x, double y){return x + y;}},
  MINUS {public double apply(double x, double y){return x - y;}},
  TIMES {public double apply(double x, double y){return x * y;}},
  DIVIDE{public double apply(double x, double y){return x / y;}};
  public abstract double apply(double x, double y);
}
```

여기서 toString을 오버라이드해서 구현해서 사용하면 다음과 같이 유용하게 사용할 수 있다.

```java
public enum Operation {
  PLUS("+") {
    public double apply(double x, double y) { return x + y; }
  },
  MINUS("-") {
    public double apply(double x, double y) { return x - y; }
  },
  TIMES("*") {
    public double apply(double x, double y) { return x * y; }
  },
  DIVIDE("/") {
    public double apply(double x, double y) { return x / y; }
  };
  private final String symbol;
  Operation(String symbol) { this.symbol = symbol; }
  @Override public String toString() { return symbol; }
  public abstract double apply(double x, double y);
}

public static void main(String[] args) {
  double x = Double.parseDouble(args[0]);
  double y = Double.parseDouble(args[1]);
  for (Operation op : Operation.values())
    System.out.printf("%f %s %f = %f%n",
      x, op, y, op.apply(x, y));
}
```

```
2.000000 + 4.000000 = 6.000000
2.000000 - 4.000000 = -2.000000
2.000000 * 4.000000 = 8.000000
2.000000 / 4.000000 = 0.500000
```

모든 경우에 사용할 수 있는 fromString 구현은 다음과 같다.
```java
private static final Map<String, Operation> stringToEnum =
  Stream.of(values()).collect(toMap(Object::toString, e -> e));
// Returns Operation for string, if any
public static Optional<Operation> fromString(String symbol) {
  return Optional.ofNullable(stringToEnum.get(symbol));
}
```

stringToEnum에 enum이 추가되는 시점은 먼저 Enum 인스턴스들이 생성된 후, static 변수들이 초기화될 때이다.

이 제약 때문에 열거타입 생성자에서 같은 열거타입의 다른 상수에도 접근할 수 없다.

한편, 상수별 메서드 구현에서 열거 타입 상수끼리 코드를 공유하기가 어렵다는 단점이 있다. 다음은 주말에 일하는 경우 잔업 수당을 계산하는 경우이다.

```java
enum PayrollDay {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
  SATURDAY, SUNDAY;
  private static final int MINS_PER_SHIFT = 8 * 60;
  int pay(int minutesWorked, int payRate) {
    int basePay = minutesWorked * payRate;
    int overtimePay;
    switch(this) {
      case SATURDAY: case SUNDAY: // Weekend
        overtimePay = basePay / 2;
        break;
      default: // Weekday
        overtimePay = minutesWorked <= MINS_PER_SHIFT ?
          0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
    }
    return basePay + overtimePay;
  }
}
```

간결하지만, 관리 관점에서 위험하다. 휴가와 같은 새로운 값을 열거타입에 추가하려면 switch 문을 수정해야 한다.
그 외에 방법으로는 
1. 상수마다 코드를 중복해서 넣거나
2. 내부 함수로 평일용과 주말용을 나눠서 도우미 메서드를 작성하고 각 상수가 자신에게 필요한 메서드를 적절히 호출한다.

결국 두가지 방법도 가독성이 떨어지고 오류 발생 가능성이 떨어진다.

평일 잔업 수당 계산용 메서드인 overtimePay를 구현하고, 주말 상수에서만 재정의해서 쓰면 장황한 부분은 줄일 수 있지만, 이후에 새로운 상수를 추가하면서 overtimePay를 적절히 재정의 해야하는 단점이 있다.

가장 깔끔한 방법은 전략 패턴을 이용하는 것이다.

```java
enum PayrollDay {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
  SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);
  private final PayType payType;
  PayrollDay(PayType payType) { this.payType = payType; }
  PayrollDay() { this(PayType.WEEKDAY); } // Default
  int pay(int minutesWorked, int payRate) {
    return payType.pay(minutesWorked, payRate);
  }
  // The strategy enum type
  private enum PayType {
    WEEKDAY {
      int overtimePay(int minsWorked, int payRate) {
        return minsWorked <= MINS_PER_SHIFT ? 0 :
          (minsWorked - MINS_PER_SHIFT) * payRate / 2;
      }
    },
    WEEKEND {
      int overtimePay(int minsWorked, int payRate) {
        return minsWorked * payRate / 2;
      }
    };
    abstract int overtimePay(int mins, int payRate);
    private static final int MINS_PER_SHIFT = 8 * 60;
    int pay(int minsWorked, int payRate) {
      int basePay = minsWorked * payRate;
      return basePay + overtimePay(minsWorked, payRate);
    }
  }
}

```

기존 열거타입에 상수별 동작을 혼합해 넣을 때는 switch 문이 좋은 선택이 될 수 있다.

```java
public static Operation inverse(Operation op) {
  switch(op) {
    case PLUS: return Operation.MINUS;
    case MINUS: return Operation.PLUS;
    case TIMES: return Operation.DIVIDE;
    case DIVIDE: return Operation.TIMES;
    default: throw new AssertionError("Unknown op: " + op);
  }
}
```

이 경우, 생성자 시점에 해당하는 객체를 넣어줄 수 없다.

열거타입은 **필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자.**

태양계 행성, 한 주의 요일, 체스 말과 같은 경우는 포함된다. 메뉴 아이템, 연산 코드, 명령줄 플래그 등 허용하는 값 모두를 컴파일 타임에 알 수 있을 때도 사용할 수 있다.

**열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.** 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계되었다.

