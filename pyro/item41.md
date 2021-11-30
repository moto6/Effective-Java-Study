# Item 41: 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라

## 마커 인터페이스란?
  - Marker Interface
  - interface that contains no method
  - 예시: Serializable

## Marker annotation 과 비교

- Marker annotation
  - 특정 class 나 interface 에 종속되지 않은 프로그램 element 에 쓰인다.
    - 예시: Spring 의 DI 는 class 와 interface 에 상관없이 동작해야한다.
    - 따라서 Spring 은 annotation 을 쓴다.
- Marker Interface
  - 아무 class 나 interface 가 아니라, 타입 자체를 제한하고 싶다.
  - 그러면 Marker Interface 로 미리 컴파일 에러를 일으킬 수 있다.

즉 Marker annotation 고 Marker Interface 둘다 그게 그거 아닌가요? 라고 생각하기 쉽지만 전혀 다르다.

Marker annotation 은 특정 class 나 interface 에 종속되지 않은 프로그램 혹은 로직에서 쓰이는 타입을 정의하고 싶을 떄 사용한다.

반면, Marker annotation 은 프로그램 혹은 로직에서 쓰이는 타입을 특정 class 혹은 interface 의 하위로 제한하고 싶을 때 사용한다.

즉 둘다 어찌 보면 타입을 정의하려 하지만 그 방향성이 전혀 다르다.

## 타입 정의가 중요한 이유

type casting 및, type checking 은 Rice's Theorem 에 따라,

현대의 컴퓨터에서 알고리즘을 만들 수 없는 코딩 불가능한 요구사항이다.

따라서, 우리가 직접 타입을 미리미리 정의해 주는게 정말로 중요하다.
