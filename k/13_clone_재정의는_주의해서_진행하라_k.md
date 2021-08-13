# clone 재정의는 주의해서 진행하라

## `java.lang.Cloneable`

- https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Cloneable.html
- Cloneable을 구현한 객체는 그 객체에서 `Object.clone()` 메소드를 부르면 field-for-field copy 가 가능하도록 하는 interface이다.
- 컨벤션에 의하면 Cloneable을 구현하는 클래스는 `Object.clone()`을 `public` 으로 `override` 한다.
- `Cloneable`에 사실 `clone()` 메소드는 없다.

## Object.clone() 의 규약

- https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#clone()
- x.clone() != x
- x.clone().getClass() == x.getClass()
- x.clone().equals(x)
- 위 세가지 조건은 반드시 만족할 필요 없다.
- 반환 객체는 `super.clone()`을 통해서 얻어야 한다. 이 클래스와 그 수퍼클래스가 이를 만족한다면, `x.clone().getClass() == x.getClass()`인 경우가 될 것이다.
- `clone()` 메소드로 얻은 객체는 원본 객체와 독립적이어야 한다.

## Effective Java

```java
class A implements Cloneable {
  @Override
  public A clone() {
    return new A(); // 일단 문제는 없다.
  }
}

class B extends A implements Cloneable {
  @Override
  public B clone() {
    return super.clone(); // class A를 반환한다. 문제가 발생
  }
}

```

- 위와 같은 상황을 방지하려면, 될 수 있으면 clone()을 사용할 때 super.clone()을 이용하도록 한다.
- `super.clone()`를 이용하지 않을 거라면, Cloneable을 구현할 이유도 없다.

----

- 사실 굳이 `Cloneable`을 구현한 필요는 없다.

```java
class Person implements Cloneable {
  private final String firstName;
  private final String lastName;

  Person(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
  
  @Override
  public Person clone() {
    try {
      return (Person) super.clone();
    } catch (CloneNotSupportedException e) { // unchecked exception 이어야 했다.
      throw new AsserstionError();
    }
  }
}

```

------

```java
class MyList implements Cloneable{
  private int[] elements; // final을 사용할 수 없다.

  @Override
  public MyList clone() {
    try {
      MyList result = (MyList) super.clone();
      result.elements = elements.clone();
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}

```

-------

- Array의 clone()은 런타임 시간의 타입과 컴파일 시간의 `타입` 모두가 원본과 똑같은 배열을 반환한다. 다만 객체 배열이라면 얕은 복사가 된다.

```java
class MyList implements Cloneable{
  private MyEntry[] elements; // final을 사용할 수 없다.

  private static class MyEntry {
    private int value;
    MyEntry(int value) { this.value = value; }

  }
  @Override
  public MyList clone() {
    try {
      MyList result = (MyList) super.clone();
      result.elements = new MyEntry[elements.length];
      for (int i = 0; i < elements.length; i++) {
        result.elements[i] = new MyEntry(elements[i].value); // 깊은 복사를 실행
      }
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}

```

- clone에서는 재정의 될수 있는 메서드를 호출하지 말아야 한다. 하위 클래스에서 동작이 변경된 메서드가 불려 상위 클래스에서의 clone의 동작이 바뀔 수 있기 때문이다. (iten19) 
- 재정의한 clone에서는 `CloneNotSupportedException`를 던질 필요 없다.
- 상속용 클래스는 `Cloneable`을 구현하면 안된다.
  - `Object.clone()`과 같이 제대로 동작하는 메소드를 구현하고, `CloneNotSupportedException` 던질수 있는 것을 선언하거나
  - 동작하지도 않고, 재정의할 수도 없도록 한다.

- 스레드 안전한 클래스의 경우, `clone`도 또한 스레드 안전하도록 구현해야 한다.
- 그냥 복사 생성자와 복사 팩터리를 이용한다.

```java
class Foo {
  int a;
  public Foo(Foo foo) {
    this.a = foo.a;
  }

  public static Foo newInstance(Foo foo) {
    return new Foo(foo);
  }
}
```
