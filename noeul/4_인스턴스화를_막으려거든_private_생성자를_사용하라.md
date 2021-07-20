## 아이템4. 인스턴스화를 막으려거든 private 생성자를 사용하라

- 정적 멤버만 담은 유틸리티 클래스는 인스턴스로 만들어 쓰라고 설계한게 아니다.



### 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어준다.

- 빈 클래스

```java
package effective_java.item4;

public class ParkSanHee {
}

```

- 컴파일 후, 기본 생성자가 생성됨.

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package effective_java.item4;

public class ParkSanHee {
    public ParkSanHee() {
   }
}

```



### 추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다.

- 하위 클래스를 만들어 인스턴스화하면 그만
- 상속해서 쓰라는 뜻으로 오해할 수 있다.

```java
abstract class ParentUtil {
    public static Map<String, String> parseQueryString(String queryString) {
        return new HashMap<String, String>(){{
            put("test","parsedString");
        }};
    }
}

------
    
public class ChildUtil extends ParentUtil {
    public ChildUtil() {
    }
}
```

```java
public class Application {
    public static void main(String[] args) throws URISyntaxException {

        URI uri = new URI("http://human.com/info?name=sanhee");

        // ⛔ ParentUtil' is abstract; cannot be instantiated
        //ParentUtil parentUtil = new ParentUtil();

        //Map<String, String> map = ParentUtil.parseQueryString(uri.getQuery());

        ChildUtil childUtil = new ChildUtil();
        Map<String, String> map2 = ChildUtil.parseQueryString(uri.getQuery());
    }
}
```



## private 생성자를 추가하면 클래스의 인스턴스화를 막을 수 있다.

- 하위 클래스가 상위 클래스의 생성자에 접근할 수 없으므로, `상속`을 `불가능`하게 하는 효과가 있음.

```java
public class UtilityClass {
    
    public static Map<String, String> parseQueryString(String queryString) {
        return new HashMap<String, String>(){{
            put("test","parsedString");
        }};
    }

    // 기본 생성자가 만들어지는 것을 막는다.(인스턴스화 방지용)
    private UtilityClass(){
        throw new AssertionError();
    }

}
```

