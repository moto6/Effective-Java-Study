# Singleton
 - 하나만 생성할 수 있는 클래스

# 1. public static final 필드인 방식
 - 장점
   - API에 명백히 드러남.
   - 간결함

```java
public class SanHee {
    public static final SanHee INSTANCE = new SanHee();
    private SanHee(){}
}
```

```java
class SanHeeTest {

    @Test
    @DisplayName("싱글턴 인스턴스 동일한지 비교")
    public void compareInstance(){
        SanHee sanHee = SanHee.INSTANCE;
        SanHee sanHee2 = SanHee.INSTANCE;
        assertThat(sanHee).isEqualTo(sanHee2);
    }
}
```
![](https://images.velog.io/images/san/post/2265c81e-6fba-49e6-a9b1-2f041f943421/image.png)

### 리플렉션을 통한 private 생성자 호출

```java
    @Test
    @DisplayName("싱글턴 인스턴스와 리플렉션으로 생성한 인스턴스 비교")
    public void compareReflectionInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SanHee sanHee = SanHee.INSTANCE;
        
        Constructor<SanHee> constructor = (Constructor<SanHee>) sanHee.getClass().getDeclaredConstructor(); // 리플렉션
        constructor.setAccessible(true);

        SanHee sanHee1 = constructor.newInstance();

        assertThat(sanHee).isEqualTo(sanHee1);
    }
```

- 새로운 인스턴스가 생성된 것을 확인할 수 있음.
![](https://images.velog.io/images/san/post/1b1bc36f-2fb5-41e5-8b91-c9b662e53470/image.png)

<BR>

### private 생성자 호출 방어

```java
public class SanHee {
    public static final SanHee INSTANCE = new SanHee();
    private SanHee(){
        if(INSTANCE != null){
            try {
                throw new InstanceAlreadyExistsException("싱글턴 객체는 새로운 인스턴스를 생성할 수 없습니다.");
            } catch (InstanceAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }
}
```

![](https://images.velog.io/images/san/post/d5c2f80e-cc9c-406e-9275-eaec0a13a176/image.png)

# 2. 정적 팩토리 메서드
  - 장점
    - API를 바꾸지 않고도 Singleton이 아니게 변경할 수 있음.
    - 정적 팩토리를 제네릭 싱글톤 팩토리로 만들 수 있음
    - 정적 팩토리의 메서드 참조를 `Supplier` 로 사용 할 수 있음.


```java
  public class SanHee {
    private static final SanHee INSTANCE = new SanHee();
    private SanHee(){
        ...
    }
    public static SanHee getInstance(){
        return INSTANCE;
    }
  }
```

```java
...
  
Supplier<SanHee> sanHeeSupplier = SanHee::getInstance;
SanHee sanHee = sanHeeSupplier.get();
  
```

----
## 키워드 정리
# 직렬화(Serialization)
- `객체`에 저장된 데이터를 `스트림`에 쓰기위해 연속적인(serial) 데이터로 `변환` 하는 것을 의미
- `ObjectInputStream(InputStream in)`
# 역직렬화(Deserialization)
- `스트림`으로부터 데이터를 읽어서 `객체`를 만드는 것
- `ObjectOutputStream(OutputStream in)`

# 직렬화 가능한 클래스
- **Serializable** 인터페이스 구현

# 직렬화 대상 제외
- 제어자 **transient**를 필드에 붙이면 됨.
- 보안상 직렬화되면 안되는 값에 대해서 사용
- transient가 붙은 인스턴스 변수의 값은 그 타입의 기본 값으로 직렬화

# 직렬화/역직렬화 예제

```java
public class UserInfo implements Serializable {

    static final long serialVersionUID = 6893L; // 직렬화 가능한 클래스의 버전관리

    String name;
    transient int age;  // 직렬화 대상 제외

    public UserInfo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

```

<BR>

```java
@Test
@DisplayName("역직렬화 테스트")
public void deserialize() throws IOException, ClassNotFoundException {
        byte[] serializedUserInfo = {-84, -19, 0, 5, 115, 114, 0, 25, 99, 111, 109, 46, 101, 120, 97, 109, 112, 108, 101, 46, 100, 101, 109, 111, 46, 85, 115, 101, 114, 73, 110, 102, 111, 0, 0, 0, 0, 0, 0, 26, -19, 2, 0, 1, 76, 0, 4, 110, 97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 6, -20, -126, -80, -19, -99, -84};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedUserInfo);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        UserInfo findUserInfo = (UserInfo)objectInputStream.readObject();
        logger.debug("역직렬화 결과: {}",findUserInfo);

        objectInputStream.close();
        byteArrayInputStream.close();
}
```

```text
- 직렬화 결과: [-84, -19, 0, 5, 115, 114, 0, 25, 99, 111, 109, 46, 101, 120, 97, 109, 112, 108, 101, 46, 100, 101, 109, 111, 46, 85, 115, 101, 114, 73, 110, 102, 111, 0, 0, 0, 0, 0, 0, 26, -19, 2, 0, 1, 76, 0, 4, 110, 97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 6, -20, -126, -80, -19, -99, -84]
05:52:35.408 [Test worker] DEBUG com.example.demo.SanHeeTest 

- 직렬화 결과 (인코딩): rO0ABXNyABljb20uZXhhbXBsZS5kZW1vLlVzZXJJbmZvAAAAAAAAGu0CAAFMAARuYW1ldAASTGphdmEvbGFuZy9TdHJpbmc7eHB0AAbsgrDtnaw=

```

<BR>
<BR>

  

```java
@Test
@DisplayName("역직렬화 테스트")
public void deserialize() throws IOException, ClassNotFoundException {
       
        String serialized = "rO0ABXNyABljb20uZXhhbXBsZS5kZW1vLlVzZXJJbmZvAAAAAAAAGu0CAAFMAARuYW1ldAASTGphdmEvbGFuZy9TdHJpbmc7eHB0AAbsgrDtnaw=";
        byte[] serializedUserInfo = Base64.getDecoder().decode(serialized);   
      //byte[] serializedUserInfo = {-84, -19, 0, 5, 115, 114, 0, 25, 99, 111, 109, 46, 101, 120, 97, 109, 112, 108, 101, 46, 100, 101, 109, 111, 46, 85, 115, 101, 114, 73, 110, 102, 111, 0, 0, 0, 0, 0, 0, 26, -19, 2, 0, 1, 76, 0, 4, 110, 97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 6, -20, -126, -80, -19, -99, -84};
  
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedUserInfo);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        UserInfo findUserInfo = (UserInfo)objectInputStream.readObject();
        logger.debug("역직렬화 결과: {}",findUserInfo);

        objectInputStream.close();
        byteArrayInputStream.close();
    }
```
- 아래 역직렬화 결과에서, age 필드에 transient가 잘 적용된 것을 확인할 수 있음
```text
역직렬화 결과: UserInfo{name='산희', age=0}

```

# Singleson 클래스 직렬화
- 단순히 `Serializable`을 구현한다고 선언하는 것만으로는 부족함.
- 모든 인스턴스 필드를 `transient`이라고 선언하고, `readResolve` 메서드를 제공해야한다.
  - 만약, 위 조건을 지키지 않으면 `직렬화`된 인스턴스를 `역직렬화`할 때마다 새로운 인스턴스가 생성된다.

```java
public class SingleTon implements Serializable {
    public static final SingleTon INSTANCE = new SingleTon();
    private SingleTon(){}
}
```

```java
@Test
@DisplayName("직렬화된 인스턴스를 역직렬화했을 때, 기존 싱글톤 인스턴스와 동일한가")
    public void CompareSingleTone() throws IOException, ClassNotFoundException {
        byte[] serialized = serialize(SingleTon.INSTANCE);
        SingleTon findSingleTon = deserialize(serialized);
        assertThat(findSingleTon).isEqualTo(singleTon);
}
```

- 역직렬화할 때 새로운 인스턴스가 생성된 것을 확인할 수 있음.
![](https://images.velog.io/images/san/post/af0ed831-3ae6-431d-83ae-3736434eca5e/image.png)


## transient 제어자, readResolve() 메서드 추가
```java
public class SingleTon implements Serializable {
    public static final transient SingleTon INSTANCE = new SingleTon();
  // public static final SingleTon INSTANCE = new SingleTon();
  // ❓ transient가 없어도, 새로운 인스턴스가 정의되지 않음.
    private SingleTon(){}


    private Object readResolve() {
        return INSTANCE;
    }
}
```
![](https://images.velog.io/images/san/post/6249d08c-b99f-4800-b0a2-fe804a8418f9/image.png)

<BR>

-----

- (추가) 위 예제에서 사용한 메서드
```java
private SingleTon deserialize(byte[] serialized) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                return (SingleTon) objectInputStream.readObject();
            }
        }
    }

private byte[] serialize(SingleTon singleTon) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(singleTon);

                return byteArrayOutputStream.toByteArray();
            }
        }
    }  
```
<br>

-----

## 찾아 볼 것

- 객체, 클래스에 정의된 인스턴스 변수의 집합
  - 객체는 오직 인스턴스 변수들로만 구성돼있음
  
- Singleson 클래스 직렬화는 왜 모든 인스턴스 필드를 transient로 선언을 해야할까? 자료형 기본값(0,null..)등이 들어가지 않으려나??..
   - 책에서는 모든 인스턴스 필드에 선언 해둬야 한다고 했는데, readResolve 메서드만 정의해도 정상적으로 되는 것 같음.

----

## Reference.

- https://github.com/java-squid/effective-java/tree/master/chapter02/item03
- https://javabom.tistory.com/13
- 자바의 정석
- 이펙티브 자바 3판