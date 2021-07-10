public 생성자를 사용해서 객체를 생성하는 전통적인 방법 말고, 이렇게 public static 팩토리 메소드를 사용해서 해당 클래스의 인스턴스를 만드는 방법도 있다.

이런 방법에는 각각 장단점이 있는데 다음과 같다.

## 장점 1: 이름을 가질 수 있다.

- 생성자에 제공하는 파라미터가, 생성자에서 반환하는 객체를 잘 설명하지 못할 경우
  - 잘 만든 이름을 가진 static 팩토리를 사용하는 것이 사용하기 보다 더 쉽고
  - 팩토리 메소드의 코드는 읽기 편함
    - 예) `BigInteger.probblePrime`

- 생성자는 시그니처에 제약이 있는데
  - 똑같은 타입을 파라미터로 받는 생성자 두개를 만들 수 없다
  - 그런 경우에도 public static 팩토리 메소드를 사용하는것이 유용하다.

## 장점 2: 반드시 새로운 객체를 만들 필요가 없다.

매번 새로운 객체를 만들 필요가 없는 경우에 미리 만들어둔 인스턴스 또는 캐시해둔 인스턴스를 반환할 수 있다. `Boolean.valueOf(boolean)` 메소드도 그 경우에 해당한다.

```java
public final class Boolean implements java.io.Serializable,
                                      Comparable<Boolean>
{
    /**
     * The {@code Boolean} object corresponding to the primitive
     * value {@code true}.
     */
    public static final Boolean TRUE = new Boolean(true);

    /**
     * The {@code Boolean} object corresponding to the primitive
     * value {@code false}.
     */
    public static final Boolean FALSE = new Boolean(false);
                                        
    .....(중략)
      
    @HotSpotIntrinsicCandidate
    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }


```





불변(immutable) 클래스([아이템 17](https://github.com/keesun/study/blob/master/effective-java/item17.md))인 경우는 나중에

- 토비의봄 라이브영상



## 장점 3: 리턴 타입의 하위 타입 인스턴스를 만들 수도 있다.

- 객체를 만들어 줄(실체화, 인스턴스화) 구체적인 클래스를 선택해서 쓸 수 있다

  - 사용의 유연함 : 내마음대로 ArrayList, LinkedList 등등.. 

- 타입은 인터페이스로 지정하고, 인터페이스의 구현체는 API로 노출 시키지 않지만

  ```java
  public interface List<E> extends Collection<E>
  ```

- 인스턴스는 interface를 implements하는 하위 클래스의 인스턴스로 만들어서 넣어줘도 되니까! 

```java
List<Integer> integers2 ;
integers2 = new ArrayList<>();
integers2 = new LinkedList<>();
integers2 = new Vector<>();
integers2 = new Stack<>();
integers2 = new CopyOnWriteArrayList<>();
// 동시성 보장하는 리스트 >> 트랜잭션의 리피터블 리드 수준의 동시성 보장
//public interface List<E> extends Collection<E>

//List 는 인터페이스라서 아래같은 코드는 불가능
//List<Integer> integers1 = new List<>();
```

-  `java.util.Collections`가 그 예시
  - 위 코드는 List 뿐이지만, Set, Map등등 모두 기본은 비슷
  - `java.util.Collections`는 45개에 달하는 인터페이스의 구현체의 인스턴스를 제공하지만 그 구현체들은 전부 non-public이다.
  - 인터페이스 뒤에 감쳐줘 있고 그럼으로서 public으로 제공해야 할 API를 줄임
  - 개념적인 무게(conceptual weight)까지 줄일 수 있었다.
    - List를 자바언어가 미리 만들어주고 싶은데, ArrayList를 쓰고싶을지, LinkedList를 쓰고싶을지 모르겠지만, 다 준비해봤어!
      - ArrayList는 read시 원소접근 속도에 강점이, LinkedList는 리스트 중간에서 삽입, 삭제에서의 강점
    - 프로그래머가 어떤 인터페이스가 제공하는 API를 사용할 때 알아야 할 개념의 개수와 난이도를 말한다.

- SOLID원칙 중 : DIP (Dependency inversion principle)
  - 프로그래머는 추상화에 의존하며 구체화에 의존하면 안된다.
  - 구현 클래스에 의존하지 말고, 인터페이스에 의존해야한다.
  - 클라이언트 코드가 인터페이스를 바라보며 그 구현체를 직접 바라보지 않게해야한다.

- 로미오와 줄리엣 연극 감독이 공연을 구현하기 위한 대본에 의존을 해야지
  - 로미오 역할에 미리 캐스팅된 최준에 빠져서 공연을 구상하고, 최준에 의존적인 공연이 되면 나중에 로미오 역할의 남주를 변경하기란 상당히 어렵고 곤란해짐
  - 로미오라는 역할에만 의존을 해야됨. 고로 마동석이든 최준이든, 누구든 로미오 역할만 할수있으면 연극이 진행되도록 구성
  - 반대급부 : 친절한 금자씨라는 영화는 처음 기획부터 금자역에 이영애님을 염두해 두고 시나리오를 작성





## 장점 4: 리턴하는 객체의 클래스가 입력 매개변수에 따라 매번 다를 수 있다.

- `EnumSet` 클래스 [아이템 36 학습예정] 는 생성자 없이 public static 메소드, `allOf()`, `of()` 등을 제공한다. 그 안에서 리턴하는 객체의 타입은 enum 타입의 개수에 따라 `RegularEnumSet` 또는 `JumboEnumSet`으로 달라진다.

- 이런 객체 타입은 노출하지 않고 감춰져 있어서 변경과 확장이 용이하다 캡슐화  JDK의 변화에 따라 새로운 타입을 만들거나 기존 타입을 없애도 문제가 되지 않는다.



- 모르겠숴,,





## 장점 5: 리턴하는 객체의 클래스가 public static 팩토리 메소드를 작성할 시점에 반드시 존재하지 않아도 된다.

### 서비스 프로바이더

- static 팩토리 메소드는 `서비스 프로바이더` 프레임워크의 근본 예시로는
  - `서비스 프로바이더` 프레임워크는 3가지가 필요하다
    1. `서비스 인터페이스` : 서비스의 구현체를 대표
    2. `프로바이더 등록 API` : 구현체를 등록하는데 사용하는 
    3. `서비스 엑세스 API`클라이언트가 해당 서비스의 인스턴스를 가져갈 때 사용하는 
- 서비스 프로바이더 : https://velog.io/@jihoson94/Service-Provider-Framework-Interface
- 스프링에서 예시 : Logger 클래스
  - 찾다가.. ㅎㅎ;
- 책의 예시 : `JDBC`
  - `JDBC`의 경우, `DriverManager.registerDriver()`가 `프로바이더 등록 API` 이고
  - `DriverManager.getConnection()`이 `서비스 엑세스 API`
  - `Driver`가 `서비스 프로바이더 인터페이스` 역할을 한다.

- 자바언어가 제공하는 서비스 프로바이더 클래스 : java.util.ServiceLoader
  - 자바 5부터는 `java.util.ServiceLoader`라는 일반적인 용도의 서비스 프로바이더를 제공
  -  하지만`JDBC`는 자바 5 이전에 만들어졌기 때문에 `JDBC`는 `ServiceLoader`를 사용하진 않는다.
- 서비스 인터페이스의 인스턴스를 제공하는 `서비스 프로바이더 인터페이스`를 만들 수도 있는데, 없는 경우에는 리플랙션[아이템 65]을 사용해서 구현체를 만들어 준다.

## 단점 1: public 또는 protected 생성자 없이 static public 메소드만 제공하는 클래스는 상속할 수 없다.
- `Collections 프레임워크`에서 제공하는 편의성 구현체(`java.util.Collections`)는 상속할 수 없다. 
- 오히려 장점 : 불변 타입[아이템 17]인 경우나 상속 대신 컴포지션을 권장[아이템 18]하기 때문(면접?)

## 단점 2: 프로그래머가 static 팩토리 메소드를 찾는게 어렵다.
- 생성자처럼 API 설명에 명확하게 드러나지 않는다
  - 그냥 생성자만 보고 무지성으로 찾을수는 없고 메서드까지 찾아봐야 한다
  - 클래스 사용자가 정적 팩토리메서드를 사용해서 인스턴스화 하는 방법을 알아내야함
- Javadoc이 알아서 정적 팩토리메서드를 사용해 인스턴스 화 하라고 친절하게 좋을텐데 그러지 못한다!
  - 생성자는 Javadoc 상단에 모아서 보여주나
  - static 팩토리 메소드는 API Javadoc으로 자동 생성한 문서에서 다뤄주지 않음
  - 따라서 클래스나 인터페이스 문서 상단에 팩토리 메소드에 대한 설명을 하고 싶으면 DIY!


## TIM 1 : 팩토리 메서드 디자인패턴

- Factory Design 패턴은 **[Encapsulation](http://javarevisited.blogspot.com/2012/03/what-is-encapsulation-in-java-and-oops.html)** 객체 지향 개념을 기반으로 합니다.
-  Factory 메소드는 종종 Item이라고 불리는 Factory와 다른 객체를 생성하는데 사용되며 생성 코드를 캡슐화합니다.
-  따라서 클라이언트 측에서 객체 생성 코드를 사용하는 대신 **Java의 Factory 메소드** 내부에 캡슐화 합니다.
  - 사용자가 new Class() 하지 말고 메서드로 받아온다.
-  Java에서 팩토리 패턴의 가장 좋은 예 중 하나는 Spring Logger의 LoggerFactory클래스 (책 예제는 Swing API의 BorderFactory 클래스)지만

- 데코레이터 디자인 패턴과 함께 JDK 뿐만 아니라 Spring, Struts, Apache 등 다양한 오픈소스 프레임워크에서 많이 사용되는 핵심 디자인 패턴
  - Read more: https://javarevisited.blogspot.com/2011/12/factory-design-pattern-java-example.html#ixzz708cMRDnC

## TIM 2 : 정적 팩토리메서드 패턴 vs 팩토리 메서드 디자인패턴
- 차이가 궁금해..
