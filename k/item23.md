# 태그 달린 클래스보다는 클래스 계층구조를 활용하라

하나의 객체에 태그를 달아 내부 구현이 다른 클래스를 사용하지 말고, 클래스의 계층 구조를 이용하자!

## 태그 달린 클래스

```java
class Figure {
  enum Shape { RECTANGLE, CIRCLE };

  // 태그 필드
  final Shape shape;
  // CIRCLE일 때만 사용
  private double radius;
  // RECTANGLE일 때만 사용
  private double width;
  private double length;

  Figure(double radius) {
    this.shape = Shape.CIRCLE;
    this.radius = radius;
  }

  Figure(double length, double width) {
    this.shape = Shape.RECTANGLE;
    this.length = length;
    this.width = width;
  }

  double area() {
    switch (shape) {
      case RECTANGLE: 
        return length *width;
      case CIRCLE:
        return Math.PI * radius * radius;
      default:
        throw new AssertionError(shape);
    }
  }
}

```

문제점이 너무나도 많다.

1. 쓸데없는 코드가 많다.
2. 여러 구현이 한 클래스에 있어 가독성도 나쁘다.
3. 다른 의미를 위한 코드가 함께 있어 메모리도 많이 사용한다.
4. 컴파일러 친화적이지 않다.
5. 필드를 final로 사용하려면 불필요하게 초기화해야 한다.
6. 변경이 필요할 때마다 많은 부분을 수정해야한다.

장황하고, 오류를 내기 쉽고, 비효율적이다.

이런 경우, 클래스 계층 구조를 활용하는 서브타이핑을 사용한다.

```java
interface Figure {
  double area();
}

class Circle implements Figure {
  final double radius;
  Circle(double radius) {
    this.radius = radius;
  }

  @Override
  public double area() {
    return Math.PI * radius * radius;
  }
}

class Rectangle implements Figure {
  final double length;
  final double width;
  Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }

  @Override
  public double area() {
    return length * width;
  }
}

class Square extends Rectangle {
  Square(double side) {
    super(side, side);
  }
}

```

1. 간결하다
2. 쓸데없는 코드도 사라졌다.
3. final 필드를 효율적으로 사용할 수 있다.
4. 컴파일러가 메서드를 구현했는 지 확인해준다.
5. 기존 코드를 수정하지 않으면서 확장하기 쉽다.
6. 타입 사이의 자연스러운 계층 관계를 반영할 수 있다.
