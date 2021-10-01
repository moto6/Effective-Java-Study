# 이왕이면 제네릭 타입으로 만들라

## 방법 1

- 담는 elements를 `E[]`으로 선언한다.
- 생성할 때 캐스팅을 한다.

```java
import java.util.Arrays;

// 방법 1
class MyArrayList<E> {
  private E[] elements;
  private int size = 0;
  private static final int DEFAULT_SIZE = 16;

  @SuppressWarnings("unchecked")
  public MyArrayList() {
    elements = (E[]) new Object[DEFAULT_SIZE];
  }

  public void add(E e) {
    ensureCapacity();
    elements[size++] = e;
  }

  public E get(int i) {
    if (i >= size) {
      throw new IndexOutOfBoundsException();
    }
    return elements[i];
  }

  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}
```

### 문제점
- [Heap Pollution](https://en.wikipedia.org/wiki/Heap_pollution)이 일어나서 실행 시간에 에러가 발생할 수 있다.
- 위 예제에선 실제로 문제가 일어나진 않는다.

### 장점
- 배열 생성시 타입 캐스팅을 한번만 하면 된다.
- 가독성이 더 좋고 배열 타입이 `E[]` 이므로 오직 E 타입의 인스턴스만 받는 다는 것을 확실히 어필한다.

## 방법 2

- 담는 객체를 `Object[]`로 선언한다.
- 담는 객체에서 꺼내서 사용할 때 매번 Type Casting을 한다.

```java
import java.util.Arrays;
// 방법 2
class MyArrayList<E> {
  private Object[] elements;
  private int size = 0;
  private static final int DEFAULT_SIZE = 16;

  public MyArrayList() {
    elements = new Object[DEFAULT_SIZE];
  }

  public void add(E e) {
    ensureCapacity();
    elements[size++] = e;
  }

  public E get(int i) {
    if (i >= size) {
      throw new IndexOutOfBoundsException();
    }

    @SuppressWarnings("unchecked")
    E result = (E) elements[i];

    return result;
  }

  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}

```

### 문제점
- 매번 꺼내서 사용할 때 Type Casting을 해야한다.

### 장점
- 힙 오염을 일으키지 않는다.

### 참고
- java.util.ArrayList 소스코드는 방법 2로 구현되어 있다.

### 정리

Item 28하고 모순이 있다. 하지만 제네릭 타입 안에서 List를 사용하는 것이 항상 좋지는 않다. 자바에 기본 List는 존재하지 않으므로 결국 어디선가에서는 이런 식으로 구현해야한다.

HashMap의 경우 성능 향상을 위해 배열을 사용한다.

일반적으로 타입에 제약을 두지 않는 경우가 있다. 원시 타입을 제외하면 모두 사용할 수 있다.

타입에 제약을 걸 수도 있다. 다음과 같이 선언한다.

```java
class DelayQueue<E extends Delayed> implements BlockingQueue<E>
```
