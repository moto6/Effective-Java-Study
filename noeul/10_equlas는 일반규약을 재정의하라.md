

# equals는 일반 규약을 지켜 재정의하라

- 재정의하려면 지켜야할 `equlas 규약`이 있다.
  - 대칭성, 추이성, 일관성, 반사성, null-아님(표준용어X)
  - 일반적은, 대칭성, 추이성, 일관성만 검사하면 된다고 함. (나머진 발생할일이 적어서?)
- 구체 클래스를 확장해 새로운 값을 추가 하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.



- 대칭성 오류
  - ex) java.sql.Timestamp
    - java.util.Date를 확장한 후 nanoseconds 필드 추가
      - Timestamp, Date 서로 섞어 사용하면 엉뚱하게 동작할 수 있음.
- 일관성 오류
  - ex) java.net.URL의 equals
    - 호스트 이름을 IP 주소로 바꾸려면 네트워크를 통해야 하는데, 그 결과가 항상 같다고 보장할 수 없음.
  - 문제를 피하려면 eqauls는 항시 메모리에 존재하는 객체만을 사용한 결정적(deterministic) 계산만 수행해야함.

- null-아님
  - 모든 객체가 null과 같지 않아야 함.
  - null 체크는 필요하지 않음.
  - eqauls는 건네받은 객체를 적절히 형변환 후 필수 필드들의 값을 알아내야 함.
    - 형변환에 앞서 `insanceof` 연산자로 타입 검사해야함.
    - `insanceof`는 첫 번째 피연산자가 `null`이면 false를 반환함.
      - 즉, null 체크를 명시적으로 안해도됨.

---

## 몰랐던 것

- float와 double은 `==` 연산자로 비교 X
  - Float.compare(float, float) 또는 Double.compare(double, double) 로 비교
  - 특수한 `부동소수` 값등을 다뤄야 하기 때문임.

- 어떤 필드를 먼저 비교하느냐가 eqauls 성능을 좌우하기도 한다.
  - 다를 가능성이 더 크거나 비교하는 비용이 싼(혹은 둘다 해당) 필드를 먼저 비교



---

## 결론 

## - 규약 지키기 힘들면 IDE 자동완성 쓰거나 AutoValue 프레임워크를 쓰자?

