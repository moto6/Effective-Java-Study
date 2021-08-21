# 아이템 18 상속보다는 컴포지션을 사용하라

- 왜냐하면 부모클래스가 변경되면 상속받은 자식클래스는 무조건 변경되어야 하기 

## 상속의 단점
- 메서드 호출과 달리 상속은 캡슐화를 깨뜨린다. 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길
수 있다.

### 상속은 캡슐화를 깨트릴수 있다!
- 상속은 상위 클래스와 하위 클래스가 순수한 IS-A 관계일 때만 써야 한다. 그러나 순수한 IS-A 관계여도 안심해서는 안된다.
- 만약 하위 클래스의 패키지가 상위 클래스와 다르고 상위 클래스가 확장을 고려해서 설계되지 않았다면, 상속을 다시 고려해보
아야 한다
- 자바 라이브러리에 있는 HashTable이나, Vector도 어떤 상속받아 만든 클래스인데, 보안때문에 상위클래스가 (116페이지 아래 두개 단락)
  -  하지만 문제를 바로잡기에는 너무 늦어버려서 바꿀 수 없게 되었다.
- 상위 클래스가 확장을 고려했고, 문서화도 잘 된 클래스라면 안전하다.
  - 상속의 취약점을 피하려면 상속 대신 Composition과 Forwarding을 사용해보는걸 고려하자
  - 상속을 고려했고, 문서화도 잘 된 클래스라면 당연히 그렇지...

### 어쩔수 없이 상속을 써야겠다면

- 메서드를 재정의하면 어떤 일이 일어나는지 정확히 정리하여 문서로 남겨야한다.
- 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지 문서로 남겨야한다.
- 클래스 내부에서 스스로의 메서드를 어떻게 사용하는지 문서로 남겨야한다.(어떤 순서로 호출하는지, 각각의 호출 결과는 어떻게
되는지)
- 일단 문서화 한 것은 클래스가 쓰이는 한 반드시 지켜야한다. (그렇지 않으면 하위 클래스의 오동작을 만들 수 있다.)

. 상속용으로 설계한 클래스는 하위 클래스를 만들어서 검증해야 한다.
컴포지션
- 상속용

### 내생각

- 아무래도 관점이 자바언어를 만들거나 프레임워크를 만드는 사람들의 관점에서, 내가 만든 클래스를 다른사람들이 상속받아 만드는 구조일때 이야기고
  - 혼자북치고 장구치는 상황이라면 상속하든 뭘하든 괜찮다. 동작하게만 만들자..



## 예제코드


```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;
    
    public InstrumentedHashSet(){}
    
    public InstrumentedHashSet(int initCap, float loadFactor){
    	super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}

InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
s.addAll(List.of("a", "b", "c"));
```
위와 같이 InstrumentedHashSet을 구현하고, addAll을 해주면 getAddCount()의 결과가 3을 반환하리라 생각하겠지만 6을 반환한다.

그 이유는 기존 HashSet의 addAll메서드 내부에서 add메서드를 호출한다는 것이다.

그 결과 addAll에서 3이 증가하고, 각 원소 마다 add가 호출되어 총 3번의 add가 실행되고 총 6이 되는 것이다.

 

그렇다고 addAll을 상위 HashSet의 addAll을 호출하지 않고 별도로 구현하여 이를 방지할 수 있을 것이다.

그러나 이러한 방식은 어렵고, 시간도 더 소요될 뿐더러 자칫 오류를 내거나 성능을 저하시킬 수 있다.

 

위의 문제는 재정의에서 문제가 발생하였다. 그렇다면 재정의 대신 새로운 메서드를 추가하면 괜찮을까?

훨씬 안전한 방식은 맞지만, 위험은 여전히 있다.

다음 릴리스에서 상위 클래스에 새 메서드가 추가되었는데, 우연히도 하위 클래스에 추가한 메서드와 동일하지만 반환 타입이 다르다면 컴파일조차 되지 않을 것이다. 이 외에도 다양하게 추가한 메서드는 상위 클래스의 메서드가 요구하는 규약을 만족하지 못할 가능성이 크다.

 

이러한 문제를 피하기 위한 컴포지션에 대해 알아보자.






### 상속을 대신한 컴포지션
기존 클래스를 확장하는 대신, 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게한다.
컴포지션을 통해 새 클래스의 인스턴스 메서드들은 기존 클래스에 대응하는 메서드를 호출해 그 결과를 반환하게하자.
이러한 방식을 전달(forwarding)이라 하며, 새로운 클래스는 기존 클래스의 내부 구현 방식의 영향에서 벗어나며, 심지어 기존 클래스에 새로운 메서드가 추가되더라도 전혀 영향을 받지 않는다.
아래의 코드를 통해 살펴보면 우선 재사용할 수 있는 전달 클래스가 아래와 같이 있다.

```java
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s = s; }

    public void clear()               { s.clear();            }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty();   }
    public int size()                 { return s.size();      }
    public Iterator<E> iterator()     { return s.iterator();  }
    public boolean add(E e)           { return s.add(e);      }
    public boolean remove(Object o)   { return s.remove(o);   }
    public boolean containsAll(Collection<?> c)
                                   { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c)
                                   { return s.addAll(c);      }
    public boolean removeAll(Collection<?> c)
                                   { return s.removeAll(c);   }
    public boolean retainAll(Collection<?> c)
                                   { return s.retainAll(c);   }
    public Object[] toArray()          { return s.toArray();  }
    public <T> T[] toArray(T[] a)      { return s.toArray(a); }
    @Override public boolean equals(Object o)
                                       { return s.equals(o);  }
    @Override public int hashCode()    { return s.hashCode(); }
    @Override public String toString() { return s.toString(); }
}
```
- 이후 재사용 클래스를 상속받아 구현된 집합 클래스이다.
```java
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
    public int getAddCount() {
        return addCount;
    }
}
```


### 컴포지션 초간단
```java

class Engine {} // The Engine class.

class Automobile {} // Automobile class which is parent to Car class.

class Car extends Automobile { // Car is an Automobile, so Car class extends Automobile 클래스
    private Engine engine; // Car has an Engine so, Car class has an instance of Engine class as its member.
}
```
- Car IS-A Automobile 이기 때문에 상속 관계, Automobile HAS-A Engine 이기 때문에 컴포지션(구성?) 관계라고 할 수 있
다. 


만약 컴포지션 대신 상속을 사용하기로 결정한다면, 다음과 같은 질문을 해봐야 한다.
- 확장하려는 클래스의 API에 아무런 결함이 없는가?
- 결함이 있다면, 이 결함이 여러분 클래스의 API까지 전파돼도 괜찮은가?
- 컴포지션으로는 이런 결함을 숨기는 새로운 API를 설계할 수 있지만, 상속은 상위 클래스의 결함을 그대로 상속 받는다.


- 상속과 컴포지션은 정확하게 분리해서 사용한다면 아예 다른 의미로 사용할 수 있는 것 같습니다만. 

## 의견한스푼
- 클린코드

## 출처
- https://happy-playboy.tistory.com/entry/Item-18-%EC%83%81%EC%86%8D%EB%B3%B4%EB%8B%A4%EB%8A%94-%EC%BB%B4%ED%8F%AC%EC%A7%80%EC%85%98%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC
