아이템6: 다 쓴 객체 참조를 해제하라



가비지 컬렉션이 다 쓴 객체를 알아서 정리해주더라도 메모리 관리에 신경써야한다.

아래의 세가지 경우에 memory leak이 발생할 수 있다.



### 메모리 관리를 직접 하는 클래스 

```java
public class Stack {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

위의 코드에서 memory leak이 발생할 수 있다. 

elements 배열로 저장소 풀을 만들어서 원소를 관리하는데 배열의 **비활성 영역**을 GC가 알 수 없다. 왜냐하면 GC가 보기에는 비활성영역에서 참조하는 객체도 유효한 객체로 보기 때문이다. 

여기서 비활성 영역이란?

-> Stack에 push하고 pop 과정을 거친 후에, <u>elements 배열의 size보다 큰 인덱스 범위</u>를 비활성 영역이라고 볼 수 있다. 



해당 참조를 다 썼을 때 null을 대입함으로서 참조를 해제하면 이 문제가 해결된다.

위의 스택 예시를 아래와 같이 구현하면 된다.

```java
public Object pop() {
		if (size == 0) {
		throw new EmptyStackException();
		}
		Object result = elements[--size];
		elements[size] = null;      // 다 쓴 참조 해제
		return result;
}
```

다 쓴 참조를 null 처리하게 되면 실수로 이 참조를 사용하는 것을 방지할 수 있다. (NullPointerException 발생)

하지만 모든 다 쓴 참조에 대해서 null처리하는 것은 코드를 지저분하게 만들기 때문에, 예제인 Stack 클래스와 같이 자기 메모리를 직접 관리하는 클래스의 경우에 직접 null 처리하는 방법을 사용한다. 



### 캐시

캐시 역시 memory leak의 원인이 된다.

객체 참조를 캐시에 넣어두고 잊어버릴 수 있기 때문이다.

```java
Object key1 = new Object();
Object value1 = new Object();

Map<Object, Object> cache = new HashMap<>();
cache.put(key1, value1);
```

여기서 key1은 Object 객체를 strong reference하고 있다.

이 경우에는 다른 곳에서 key1을 더이상 참조하지 않더라도, 이 cache가 key1을 갖고 있기 때문에 key1이 갖고 있는 Object 객체는 GC의 대상이 될 수 없다.

- strong referece? `Integer prime = 1;`  과 같은 일반적인 참조 유형, 변수 prime은 값이 1인 Integer 객체에 대한 강한 참조를 가진다. 

[참고]: http://blog.breakingthat.com/2018/08/26/java-collection-map-weakhashmap/

 

해결법은 HashMap대신 WeakHashMap을 사용하는 것이다.

WeakHashMap을 사용하게 되면, 다른곳에서 key1에 대한 참조가 더이상 없다면, cache에 들어있는 key1에 대한 엔트리가 GC가 될 수 있다. (key1에 대한 엔트리가 무슨 뜻인지 아직 완벽히 이해하지 못했다, key1에 대한 value1까지 GC의 대상이 된다는 뜻 같음)

WeakHashpMap은 key의 값을 weak reference로 바꿔서 넣기 때문이다.

- weak reference란?

  -> GC 대상이 되려면 그 객체를 참조하는 것이 없어야하는데,

  weak reference는 strong reference만 없다면 이 weak reference 자체도 GC의 대상이 될 수 있다. 

  아래의 예시를 보자.

  ```java
  WeakReference weakWidget = new WeakReference(widget);
  ```

  widget을 가르키는 reference가 쓸모가 없어지면, 이 weakwidget이라는 변수가 갖고 있는 메모리도 GC의 대상이 된다. 

  widget에 대한 strong reference가 없다면 weakwidget도 GC의 대상이 되는 것이다. 



(reference와 GC에 관해선 [해당 글](https://d2.naver.com/helloworld/329631)을 보고 더 학습해보자.)



### 콜백

콜백을 쌓아두기만 하고 비우지 않는 경우에는 캐시와 같은 memory leak 문제가 발생한다.

해결법은 캐시와 마찬가지로 콜백을 모아두는 Map을 WeakHashMap으로 사용하는 것이다. 



**결론**: 메모리 누수는 잘 드러나지 않는 문제이기 때문에 미리 예방하는 것이 중요하다. 
