# equals를 재정의하려거든 hashCode도 재정의하라

- equals를 재정의하면, hashCode도 재정의해야한다.
- 그렇지 않으면 `리스코프 치환 원칙`을 위반한다.
- 규약은 다음과 같다.
  - `equals` 비교에 사용하는 값이 변하지 않는다면, 앱이 실행되는 동안 `hashCode` 반환값도 변하지 말아야한다.
  - `equals`로 같다고 판단한다면, `hashCode`도 같은 값이어야 한다.
  - 다른 객체에 대해서 반드시 hashCode가 다를 필요는 없다.


## 해시코드 작성 간단 요령

1. int 변수 result를 선언한 후 값 c로 초기화한다. 이때 c는 첫번째 핵심 필드를 단계 2.a 방식으로 계산한 해시코드이다.
2. 해당 객체의 나머지 핵심 필드 f 각각에 대해 다음 작업을 수행한다.
  1. 해당 필드의 해시 코드 c를 계산한다.
    1. 기본타입 필드라면 Type.hashCode(f)를 수행한다. (박싱 클래스 Type)
    2. 참조타입이면서 이 객체의 equals가 이 필드의 equals를 재귀적으로 호출하여 비교한다면, 이 필드의 hashCode를 재귀적으로 호출한다. 계산이 복잡해질 것 같으면 이 필드의 표준형을 만들어 그 표준형의 hashCode를 호출한다. 필드의 값이 null이라면 0을 사용한다.
    3. 필드가 배열이라면 핵심 원소를 별도 필드로 다룬다. 핵심 원소가 없다면 단순 상수(ex. 0)로, 모든 원소가 핵심이라면 `Arrays.hashCode`를 사용한다.
  2. 단계 2.1에서 계산한 해시코드 c로 result를 계산한다.
    - `result = 31 * result + c`
3. result를 반환한다.

```java
class Person {
  private String first;
  private String last;
  private String phoneNumber;

  ...
  @Override public int hashCode() {
    int result = first.hashCode();
    result = 31 * result + last.hashCode();
    result = 31 * result + phoneNumber.hashCode();
    return result;
  }
  
}
```


- 파생 필드는 해시코드 계산에서 제외해도 된다.
- equals 비교에 사용되지 않은 필드는 반드시 제거한다.
- 해시 충돌이 더욱 적어야한다면 구아바의 com.google.common.hash.Hashing 을 참고한다.
- Objects.hash(...args); 도 한가지 방법이다.
- 매번 hashCode의 비용이 크다면 지연 초기화 전략을 쓰면서 캐싱한다.

- 해시코드를 계산할 때 핵심 필드를 생략하면 안된다.
- hashCode 생성 규칙을 API 사용자에게 자세히 공표하지 말자.

- equals와 hashCode 를 만들어주는 IDE 기능이나 AutoValue 프레임워크를 사용한다.