# Public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

- [item 15] `클래스와 멤버의 접근 권한을 최소화하라` 와 같이 생각하면 좋은 내용
- 필드에 대해서 getter와 setter를 제공한다.
  - 그런데, getter와 setter는 캡슐화를 깨고 객체 내부를 공개할 가능성이 매우 크다고 생각함. 차라리 객체에 작업을 위임하는 것이 좋다고 생각한다.
  - 그리고 객체 내의 가변 객체를 getter로 제공하면 객체를 거치지 않으면서 객체의 상태를 변화시킬 수 있다.
  - 그래서, getter와 setter는 객체가 그것을 공개하는 것이 좋은 경우 (ex. Point 객체의 x 좌표, y 좌표 / DTO 등)에만 사용하는 것이 좋을 것 같다.
- 다만 package-private 클래스 또는 private 중첩 클래스라면 데이터 필드를 노출해도 상관 없다.
  - 어차피 외부 (public)에는 공개되지 않기 때문이다.

```java
class LinkedList {
  private Node head;
  
  // 이 객체의 필드는 노출해도 괜찮다. 어차피 LinkedList 내부에서만 사용하기 때문에 오히려 이 필드조차 getter / setter로 접근한다면 불편하다.
  // 그리고 private로 해도 LinkedList 내에서 접근 가능
  private static class Node {
    Object data;
    Node next;
  }
}

```

- 불변 필드의 경우는 가변 필드의 경우보다는 낫다. 하지만 여전히 API 변경이 어렵고, 부가 작업이 불가능하다.

```java
class List {
  private Object[] data;

  // 가변 객체를 getter로 노출하지 않는게 좋다고 생각
  public Object[] getData() {
    return data;
  }
}

...
private void foo() {
  List list = new List();
  Object[] data = list.getData();
  data[3] = Integer.valueOf(3);
}

```
