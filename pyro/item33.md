# Item 33: 타입 안전 이종 컨테이너를 고려하라

- Typesafe Heterogeneous Container 패턴
- 타입 안전 이종 컨테이너 패턴

컨테이너 대신 키를 매개변수화 한 다음, <br>
컨테이너에 값을 넣거나 뺄 때 <br>
매개변수화한 키를 함께 제공하는 디자인 패턴

어렵게 생각할 필요 없이, `Class<T> type` 타입 그 자체를 Map 의 키로 설정하고, <br>
함수의 파라미터로 `Class<T> type` 를 넘겨서 객체를 put 하거나 get 하는 디자인 패턴이다.

Spring 의 IoC 컨테이너가 이렇게 구현되어 있다. <br>
책에 나온 컨테이너라는 용어는 IoC 컨테이너라고 생각해도 무방하다.

파이로가 바닐라 자바로 Spring 직접 구현해보기를 테크톡 했을 때 <br>
Item33 의 디자인 패턴을 활용했다.

대신 파이로는 wildcard 를 더 적극적으로 활용해서, <br>
파이로가 짱 싫어하는 `type.cast` 가 없도록 하였다.

- [Before](https://github.com/ghojeong/playground/blob/main/dependency/src/main/java/dip/v2/Server.java)
- [After](https://github.com/ghojeong/playground/blob/main/dependency/src/main/java/ioc/framework/SpringApplication.java)
- [BeanFactory](https://github.com/ghojeong/playground/blob/main/dependency/src/main/java/ioc/framework/BeanFactory.java)

## 타임 안전 이종 컨테이너 패턴 예시

### 코드

```Item33.java
import java.util.*;

public class Item33 {
    // API
    public interface Favorites {
        <T> void putFavorite(Class<T> type, T instance);
        <T> T getFavorite(Class<T> type);
    }

    // API의 구현
    public static class FavoritesImpl implements Favorites {
        private Map<Class<?>, Object> favorites = new HashMap<>();

        @Override
        public <T> void putFavorite(Class<T> type, T instance) {
            // type.cast 를 한번 더 함으로써, raw 타입 instance 가 put 되지 않도록 방지한다.
            favorites.put(Objects.requireNonNull(type), type.cast(instance));
        }

        @Override
        public <T> T getFavorite(Class<T> type) {
            return type.cast(favorites.get(type));
        }
    }

    // 클라이언트
    public static void main(String[] args) {
        Favorites f = new FavoritesImpl();

        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebabe);
        f.putFavorite(Class.class, Favorites.class);

        String favoriteString = f.getFavorite(String.class);
        int favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);

        System.out.println("favoriteString: "+ favoriteString);
        System.out.println("favoriteInteger: "+ favoriteInteger);
        System.out.println("favoriteClass: "+ favoriteClass.getName());
    }
}
```

### 실행결과

```txt
favoriteString: Java
favoriteInteger: -889275714
favoriteClass: Item33$Favorites
```

## 슈퍼 타입 토큰(Super Type Token) 패턴

- 타입 토큰(Type Token)이란?
  -  `Map<Class<?>, Object>` 자료구조에서 Key 가 되는 `Class<T> type` 를 타입 토큰이라고 한다.
- 그럼 슈퍼 타입 토큰은 뭔가?
  - 파이로도 잘 모른다. 제대로 활용해본 적이 없다.
  - 일단 슈퍼 타입 토큰이 해결하려고 한 문제 부터 알아보자.

### 슈퍼 타입 토큰이 해결하려고 한 문제

`String.class` 와 `String[].class` 는 Class 객체로 만들 수 있어서 타입 토큰으로 사용할 수 있다. <br>
하지만 `List<String>.class` 는 Class 객체로 만들 수가 없어서, 타입 토큰으로 사용할 수 없다. <br>
(참고 Item28: 배열보다는 리스트를 사용하라)

우리가 하고 싶은 것은 비한정적인 `List.class` 타입 토큰이 아니라, 한정적인 `List<String>.class` 타입 토큰을 사용하고 싶다. <br>
대체 어떻게 하면 좋을까? <br>
닐 개프터에 따르면 이럴 때 슈퍼 타입 토큰을 사용하면 된다. <br>
기존의 타입 토큰에서 불가능했던 `List<String>.class` 를 가능하게 하므로, <br>
굉장하다는 뜻에서 슈퍼라는 이름을 붙였나보다. 우리말로 하면 "짱짱 타입 토큰"이다. 참 유치한 네이밍이다.

하지만 조슈아 블로크에 따르면 닐 개프터의 해결책도 충분히 만족스럽지 않다고 한다. <br>
내 생각에 이건 자바 언어 자체의 문제라서 C#, 타입스크립트, 코틀린과 같은 언어로 갈아타면 해결된다.

바닐라 자바에서 슈퍼 타입 토큰을 사용하고 싶다면, 리플렉션의 `ParameterizedType` 를 활용하면 된다.<br>
Spring 에서 슈퍼 타입 토큰을 사용하고 싶다면 `ParameterizedTypeReference` 를 활용하면 된다.

### 코드

Spring 까지 공부하기에는 귀찮아서, 리플렉션의 `Type` 과 `ParameterizedType` 을 예시코드로 정리해보았다.

```java
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class Item33 {
    static class SuperList extends ArrayList<String> {}

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        SuperList superList = new SuperList();

        Type type = list.getClass().getGenericSuperclass();
        Type superType = superList.getClass().getGenericSuperclass();

        System.out.println("평범 타입 토큰: " + type);
        System.out.println("슈퍼 타입 토큰: " + superType);

        // ParameterizedType 으로 generic 의 타입 파라미터에 어떤 Argument 가 들어갔는지 확인할 수 있다.
        System.out.println("\n평범 타입 토큰 파라미터: " + ((ParameterizedType) type).getActualTypeArguments()[0]);
        System.out.println("슈퍼 타입 토큰 파라미터: " + ((ParameterizedType) superType).getActualTypeArguments()[0]);
    }
}
```

### 실행결과

```txt
평범 타입 토큰: java.util.AbstractList<E>
슈퍼 타입 토큰: java.util.ArrayList<java.lang.String>

평범 타입 토큰 파라미터: E
슈퍼 타입 토큰 파라미터: class java.lang.String
```

## 어노테이션 API 의 한정적 타입 토큰

어렵게 써놨지만, 그냥 어노테이션의 generic 에다가 extends 를 사용한다는 뜻이다.

어노테이션에서 타입 토큰 쓰는게 어려운게 아니라, reflection 을 능숙하게 쓰는게 어렵다.

### 코드

```java
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class Item33 {
    @Controller
    static class ApiController {}

    @Service
    static class ApiService {}

    static Class<? extends Annotation> Name2Type(String annotationTypeName) throws ClassNotFoundException {
        return Class.forName(annotationTypeName).asSubclass(Annotation.class);
    }

    static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return element.getAnnotation(annotationType);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ApiController controller = new ApiController();
        ApiService service = new ApiService();

        Class<? extends Annotation> controllerType = Name2Type("org.springframework.stereotype.Controller");
        Class<? extends Annotation> serviceType = Name2Type("org.springframework.stereotype.Service");

        System.out.println("## 토큰 타입 비교 ##");
        System.out.println("컨트롤러 타입 from Class: " + Controller.class);
        System.out.println("컨트롤러 타입 from Name:  " + controllerType);
        System.out.println("서비스 타입 from Class: " + Service.class);
        System.out.println("서비스 타입 from Name:  " + serviceType);

        System.out.println("\n## 어노테이션 비교## ");
        System.out.println("컨트롤러 클래스: " + controller.getClass());
        System.out.println("서비스 클래스:  " + service.getClass());
        System.out.println("컨트롤러 어노테이선: " + getAnnotation(controller.getClass(), controllerType));
        System.out.println("서비스 어노테이선:  " + getAnnotation(service.getClass(), serviceType));
    }
}
```

### 실행결과

```txt
## 토큰 타입 비교 ##
컨트롤러 타입 from Class: interface org.springframework.stereotype.Controller
컨트롤러 타입 from Name:  interface org.springframework.stereotype.Controller
서비스 타입 from Class: interface org.springframework.stereotype.Service
서비스 타입 from Name:  interface org.springframework.stereotype.Service

## 어노테이션 비교##
컨트롤러 클래스: class Item33$ApiController
서비스 클래스:  class Item33$ApiService
컨트롤러 어노테이선: @org.springframework.stereotype.Controller("")
서비스 어노테이선:  @org.springframework.stereotype.Service("")
```
