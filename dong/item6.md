# 아이템 6. 불필요한 객체 생성을 피하라	
- 똑같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 나 을 때가 많다.
- 재사용은 빠르고 세련되다. 특히 불변 객체(아이템 17)는 언제든 재사용할 수 있다.
- 파이썬의 뮤터블 이뮤터블 객체

## 아이디어
- 시간측정해서 문자열클래스 
- 뭐 프로그래머스 문제 있었던듯

## String 불필요한 객체 생성 막기
- 스트링클래스 불필요한 객체생성 막기 코드부터 보고 가실께요

### 1 - bad sample in book

```java
System.out.println("1 - bad sample in book");
String s1 = new String("Don't follow me! This is stupid code!!");
System.out.println(s1);
```

```java
L1
    LINENUMBER 8 L1
    NEW java/lang/String
    DUP
    LDC "Don't follow me! This is stupid code!!"
    INVOKESPECIAL java/lang/String.<init> (Ljava/lang/String;)V
    ASTORE 1

L2
    LINENUMBER 9 L2
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 1
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
```
- `NEW java/lang/String` : String 객체 생성
- `DUP` : 메모리를 어딘가로 복제함
- `INVOKESPECIAL java/lang/String.<init> (Ljava/lang/String;)V` : 

- invokespecial 명령어
```txt
- invokespecial 명령어는 현재 클래스의 슈퍼클래스의 private 메서드와 메서드뿐만 아니라 인스턴스 초기화 메서드를 호출하는 데 사용됩니다.
- invokespecial 은 특정 클래스 버전의 메서드를 호출하기 위해 동적 바인딩에 대한 걱정 없이 메서드를 호출하는 데 사용됩니다.
- 
```
- invokevirtual 명령어
```txt
- invokevirtual 명령어는 객체의 클래스를 기반으로 메소드를 호출한다는 것입니다. 
```
- invokeStatic 명령어 : 정적 메서드 호출 

- 출처 : https://www.jrebel.com/blog/using-objects-and-calling-methods-in-java-bytecode


### 2 - good sample in book

- 소스코드

```java
System.out.println("2 - good sample in book");
String s2 = "enhanced version";
System.out.println(s2);
```

- 바이트코드

```java
L4
    LINENUMBER 13 L4
    LDC "enhanced version"
    ASTORE 2

L5
    LINENUMBER 14 L5
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 2
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
```


### 3 - bad String

- 소스코드

```java
System.out.println("3 - bad String");
String[] hello = {"안"+"녕"+"하"+"세"+"요"+" "+"만"+"나"+"서"+"반"+"갑습니다"+"!!!"};
String result = "";
for (String s3 : hello) {
    result += s3;
}
System.out.println(result);
```

- 바이트코드

```java
L6
LINENUMBER 18 L6
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
LDC "3 - bad String"
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V

L7
LINENUMBER 19 L7
ICONST_1
ANEWARRAY java/lang/String
DUP
ICONST_0
LDC "\uc548\ub155\ud558\uc138\uc694 \ub9cc\ub098\uc11c\ubc18\uac11\uc2b5\ub2c8\ub2e4!!!!"
AASTORE
ASTORE 3

L8
LINENUMBER 20 L8
LDC ""
ASTORE 4

L9
LINENUMBER 21 L9
ALOAD 3
ASTORE 5
ALOAD 5
ARRAYLENGTH
ISTORE 6
ICONST_0
ISTORE 7

L10
FRAME FULL [[Ljava/lang/String; java/lang/String java/lang/String [Ljava/lang/String; java/lang/String [Ljava/lang/String; I I] []
ILOAD 7
ILOAD 6
IF_ICMPGE L11
ALOAD 5
ILOAD 7
AALOAD
ASTORE 8

L12
LINENUMBER 22 L12
ALOAD 4
ALOAD 8
INVOKEDYNAMIC makeConcatWithConstants(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; [
    // handle kind 0x6 : INVOKESTATIC
    java/lang/invoke/StringConcatFactory.makeConcatWithConstants(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;
    // arguments:
    "\u0001\u0001"
]
ASTORE 4

L13
LINENUMBER 21 L13
IINC 7 1
GOTO L10

L11
LINENUMBER 24 L11
FRAME CHOP 3
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 4
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
```
- 너무 길어서 해석하긴 좀 그런데 아무든 비효율적이다

### 4 - good String

- 소스코드

```java
System.out.println("4 - good String");
System.out.println("만나면 반갑다고 뽀뽀뽀 헤어져도 또만나요 뽀뽀뽀");

```

- 바이트코드

```java
L14
LINENUMBER 27 L14
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
LDC "4 - good String"
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V

L15
LINENUMBER 28 L15
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
LDC "\ub9cc\ub098\uba74 \ubc18\uac11\ub2e4\uace0 \ubf40\ubf40\ubf40 \ud5e4\uc5b4\uc838\ub3c4 \ub610\ub9cc\ub098\uc694 \ubf40\ubf40\ubf40"
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
```



### 5 - How smart is the Java compiler

- 소스코드
```java
System.out.println("5 - How smart is the Java compiler?");
System.out.println("안"+"녕"+"하"+"세"+"요"+" "+"만"+"나"+"서"+"반"+"갑습니다"+"!!!!");
```

- 바이트코드

```java
L16
LINENUMBER 31 L16
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
LDC "5 - How smart is the Java compiler?"
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V

L17
LINENUMBER 32 L17
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
LDC "\uc548\ub155\ud558\uc138\uc694 \ub9cc\ub098\uc11c\ubc18\uac11\uc2b5\ub2c8\ub2e4"
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
```

### String class 의 인스턴스 쓸데없이 만들지마라 요약정리
- String 인스턴스를 새로 만드는건 쓸모없는 짓
- 기능적으로 완전히 똑같은 문자열 출력인데, 바이트코드가 두줄이나 늘어났고, 메모리도 더 차지한다
- 이런 바이트코드가 반복문이나 빈번히 호출되는 메서드 라면 쓸데없는 String 인스턴스가 많이 만들어질 수도 있다
- (복습)생성자는 호출할 때마다 새로운 객체를 만들지만, 팩터리 메서드 는 전혀 그렇지 않다.



## 정규표현식
- 생성 비용이 아주 비싼 객체도 더러 있다. 이런 ‘비싼 객체’가 반복해서 필요하다면 캐싱하여 재사용하길 권장
- 안타깝게도 자신이 만드는 객체가 비싼 객체인지를 매번 명확히 알 수는 없다. 예를 들어 주어진 문자열이 유효한 로 마 숫자인지를 확인하는 메서드를 작성한다고 해보자. 다음은 정규표현식을 활용한 가장 쉬운 해법

- 정구표현식 예제
```
```

- 정규표현식의 장점
  - 이 방식의 문제는 String.matches 메서드를 사용해서 문제
  - String.matches는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만... 단점은 성능
- 정규표현식의 단점
  - 성능이 중요한 상황에서 반복해 사용하기 부적절
  - 내부에서 만드는 정규표현식용 Pattern 인스턴스는 한 번 쓰고 버려져서 곧바로 가 비지 컬렉션 대상이 된다
  - Pattern은 입력받은 정규표현식에 해당하는 유한상태머신(finite state machine)을 만들기 때문에 인스턴스 생성 비용이 높다
  - 성능을 개선하려면 필요한 정규표현식을 표현하는 (불변인) Pattern 인스턴 스를 클래스 초기화(정적 초기화) 과정에서 직접 생성해 캐싱해두고, 나중에 isRomanNumeral 메서드가 호출될 때마다 이 인스턴스를 재사용한다.
- 토스 아죠씨도 객체 생성 비용 비싼거 테스트 느려지는 주범이라고 함
    - 토스 발표 : 

## 오토박싱
-   오토박싱(auto boxing)은 프로그래머가 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환해주는 기술
- 오토박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐려주지만, 완전히 없애주는 것은 아니다
- 의미 상으로는 별다를 것 없지만 성능에서는 그렇지 않다
-  박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.
```java
```


## 아이템 오해하지 말기
- “객체 생성은 비싸니 피해야 한다”로 오해하면 안 된다. 
- JVM에서는 별다른 일을 하지 않는 작은 객체를 생성하고 회수하는 일이 크게 부담되지 않음
- 프로그램의 명확성, 간결성, 기능을 위해서 객체 를 추가로 생성하는 것이라면 일반적으로 좋은 일이다!
- 아주 무거운 객체가 아닌 다음에야 단순히 객체 생성을 피하고자 여 러분만의 객체 풀(pool)을 만들지는 말자. 물론 객체 풀을 만드는 게 나은 예가 있긴 하다. 데이터베이스 연결 같은 경우 생성 비용이 워낙 비싸니 재사용하는 편이 낫다. 하지만 일반적으로는 자체 객체 풀은 코드를 헷갈리게 만들고 메모 리 사용량을 늘리고 성능을 떨어뜨린다. 요즘 JVM의 가비지 컬렉터는 상당히 잘 최적화되어서 가벼운 객체용을 다룰 때는 직접 만든 객체 풀보다 훨씬 빠 르다.


### 방어적 복사(defensive copy)
방어적 복사 [아이템 50] 과 대조적인데, 이번 아이템이 “기존 객체를 재사용해야 한다면 새로운 객체를 만들지 마 라”
- 아이템 50은 “새로운 객체를 만들어야 한다면 기존 객체를 재사용지 마라”
- 방어적 복사가 필요한 상황에서 객체를 재사용했을 때의 피해가, 필요 없는 객체를 반복 생성했을 때의 피해보다 훨씬 크다는 사실을 기억하자. 
- 방어적 복사에 실패하면 언제 터져 나올지 모르는 버그와 보안 구멍으로 이어 지지만, 불필요한 객체 생성은 그저 코드 형태와 성능에만 영향을 준
> ``쓰레기차 피하려다 똥차에 치인다``

### TMI 객체 생성시, 클래스 실체화시 일어나는일 좀 공부해보기


### TMI 자바 바이트코드 대략 정리??
- https://iamsang.com/blog/2012/08/19/introduction-to-java-bytecode/
- https://www.jrebel.com/blog/using-objects-and-calling-methods-in-java-bytecode
- - [Java bytecode From Wikipedia](http://en.wikipedia.org/wiki/Java_bytecode#Support_for_dynamic_languages)
- [The Java Virtual Machine Specification](http://docs.oracle.com/javase/specs/jvms/se7/html/index.html)
- [Java bytecode: Understanding bytecode makes you a better programmer](http://www.ibm.com/developerworks/ibm/library/it-haggar_bytecode/)
- [Java Bytecode Fundamentals](http://arhipov.blogspot.kr/2011/01/java-bytecode-fundamentals.html)
- [Java / Eclipse : How does Eclipse compile classes with only a JRE?](http://stackoverflow.com/questions/1642338/java-eclipse-how-does-eclipse-compile-classes-with-only-a-jre)