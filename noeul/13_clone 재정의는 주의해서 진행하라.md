# Clone 재정의는 주의해서 진행하라

## 객체 복사

- [객체 지향 프로그래밍](https://ko.wikipedia.org/wiki/객체_지향_프로그래밍)에서 **객체 복사**(object copying)는 객체 지향 프로그램에서 말하는 데이터 단위인 기존의 객체의 사본을 [생성](https://ko.wikipedia.org/w/index.php?title=객체_생성&action=edit&redlink=1)하는 것

- 방식
  - 얕은 복사
  - 깊은 복사
    -  복사 생성자 (변환 생성자)
    -  복사 팩터리 (변환 팩터리)
    -  Cloneable

## 얕은 복사

-  field-by-field copy 라고 부르며, field-for-field copy, field copy 라고도 한다

- 필드 값이 객체 참조인 경우, 참조를 복사하므로  동일한 객체를 참조
- 필드 값이 원시 자료형인 경우 원시 자료형의 값을 복사

```java
public class Sample {

    static class Obj{
        private String name;

        public Obj(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        Obj obj = new Obj("객체1");
        System.out.println("obj = " + obj.name);

        Obj obj2 = obj;
        obj2.name = "이름변경";

        System.out.println("obj2 = " + obj2.name);
        System.out.println("obj = " + obj.name);

        int a = 5;
        int b = a;

        b = 3;

        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}

----
    
obj = 객체1
obj2 = 이름변경
obj = 이름변경
a = 5
b = 3
```

---



> 미리 결론: ''복제 기능은 생성자와 팩터리를 이용하는게 최고..''  - effective java

## 깊은 복사

- 복사되는 객체에 대한 참조 대신, 참조가 되는 모든 객체를 위한 새 사본의 객체가 만들어진다.

### 1. 복사 생성자 (변환 생성자)

- 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자

```java
public class Sample {

    static class Obj{
        private String name;

        public Obj(String name) {
            this.name = name;
        }

        public Obj(Obj obj){
            this.name = obj.name;
        }
    }

    public static void main(String[] args) {
        Obj obj = new Obj("객체1");
        Obj obj2 = new Obj(obj);
        System.out.println("복사한 obj2 = " + obj2.name);
        
        obj2.name = "객체2";
        System.out.println("수정한 obj2 = " + obj2.name);
        System.out.println("원본 obj = " + obj.name);
    }
}

----
    
복사한 obj2 = 객체1
수정한 obj2 = 객체2
원본 obj = 객체1
```

### 2. 복사 팩터리 (변환 팩터리)

- 이름을 지정할 수 있어서 복사 생성자보다 명확하다.

```java

private Obj(Obj obj) {
    this.name = obj.name;
}

public static Obj newInstance(Obj obj){
    return new Obj(obj);
}

```

### 3. Cloneable

- 배열의 clone은 런타임 타임과 컴파일 타임 타입 모두가 원본 배열과 똑같은 배열을 반환한다.
- `배열`을 `복제`할 때는 배열의 `clone` 메서드를 사용하는 것이 `권장`됨.

- Cloneable 인터페이스는 Object의 protected 메서드인 clone의 동작방식을 결정한다.
  - Cloneable을 구현하지 않고 clone을 호출하면 `CloneNotSupportedException` 예외 발생
- **Cloneable은 지양하는 것이 바람직하다.**
  - 언어모순적이다.
  - `clone()`의 규약이 허술하다.
  - 고려해야할 사항이 많다.
    - 공변 반환 타이핑
    - 참조타입, 가변타입
    - 멀티스레드

```java
package java.lang;

public class Object {
...
protected native Object clone() throws CloneNotSupportedException;
```

```java
package java.lang;

public interface Cloneable {
}
```

```java
static class Obj implements Cloneable{
        private String name;
        public Obj(String name) {
            this.name = name;
        }

        @Override
        public Obj clone() throws CloneNotSupportedException {
            return (Obj) super.clone();
        }
}
```

```java
public class Sample {
    public static void main(String[] args) throws CloneNotSupportedException {
        Integer[] a = new Integer[]{1,2,3,4,5};
        Integer[] b = a.clone();

        System.out.println("복사 직후 b:"+Arrays.toString(b));
        Arrays.sort(b, Comparator.reverseOrder());
        System.out.println("내림차순 직후 b:"+Arrays.toString(b));
        System.out.println("원본 a:"+Arrays.toString(a));
    }
}

----
  
복사 직후 b:[1, 2, 3, 4, 5]
내림차순 직후 b:[5, 4, 3, 2, 1]
원본 a:[1, 2, 3, 4, 5]
```



---



### Reference.



- https://github.com/im-d-team/Dev-Docs/blob/master/Java/copy-object.md

- https://ko.wikipedia.org/wiki/객체_복사#복사_방식

- effective java
