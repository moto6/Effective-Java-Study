

# 아이템 8 - finalizer와 cleaner 사용을 피하라

> 저에겐 너무 너무 어려운 주차라서...그냥 정독을 해봤다는 것에 의의를 두려합니다.
> 하나도 이해를 못한 것 같네요.



### finalizer

- https://starplaying.tistory.com/129
  - 이 클래스의 객체가 `GC` 과정에서 가지고 있던 리소스가 반환될 때 해야 수행할 일
  - 이것은 사용자가 정의한 클래스에서 재정의할 수 있다. 만일 사용자의 클래스에 이 메서드를 재정의했다면, 그 finalize메서드를 객체의 `Finalizer`라고 부른다. 

### 사용되는 곳

- 자바 라이브러리 일부 클래스
  - FileInputSTream, FileOutputStream, ThreadPoolExcecutor ..
- Native peer와 연결된 객체
  - 네이티브 피어란 일반 자바 객체가 `네이티브 메서드` 를 통해 기능을 위임한 네이티브 객체?
  - 네이티브 피어는 자바 객체가 아니니 GC가 알지 못해, 네이티브 객체 회수를 못함.

### 쓰면 안되는 이유

> 하나도 모르겠지만, 일단 요약 및 블로그 탐색...

- 즉시 수행된다는 보장이 없고, 예측 할 수 없다.
  - 관련 예제 : 
    - https://codingdog.tistory.com/entry/java-finalize-%EB%A9%94%EC%86%8C%EB%93%9C-%EC%96%B8%EC%A0%9C-%EC%8B%A4%ED%96%89%EB%90%A0%EC%A7%80-%EB%AA%A8%EB%A5%B8%EB%8B%A4
- finalizer 동작 중 발생한 예외는 무시되며, 처리할 작업이 남았더라도 그 순간 종료
- 심각한 성능 문제

- finalizer 공격에 취약
  - 관련 예제 :
    - https://yangbongsoo.tistory.com/8

### 대안

- 예외가 발생해도 제대로 종료되도록 `try-with-resources` 사용



### 자바 가비지 컬렉션

- https://d2.naver.com/helloworld/1329
- 내용이 상세하고 좋은 것 같은데, 뒤늦게 찾아서 다 보지 못했습니다.